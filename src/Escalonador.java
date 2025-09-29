import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Escalonador {
    // --- Variáveis de Instância ---
    private final List<Processo> processosDeEntrada;
    private final List<Processo> filaDeProntos;
    private final List<Processo> processosFinalizados;
    private final List<String> ordemDeExecucao;
    private final int totalProcessos;
    private int tempoAtual;
    private Processo processoEmExecucao;
    private final AlgoritmoEscalonador estrategia;
    private final String nomeAlgoritmo;
    private final int quantum;
    private final PreempcaoStrategy politicaPreempcao;
    private int fatiasDeTempoExecutadas;



    public Escalonador(List<Processo> processos, AlgoritmoEscalonador estrategia, String nomeAlgoritmo, int quantum, PreempcaoStrategy politicaPreempcao) {
        this.processosDeEntrada = new ArrayList<>(processos);
        this.processosDeEntrada.sort(Comparator.comparingInt(Processo::getTempoChegada));
        this.filaDeProntos = new ArrayList<>();
        this.processosFinalizados = new ArrayList<>();
        this.ordemDeExecucao = new ArrayList<>();
        this.totalProcessos = processos.size();
        this.tempoAtual = 0;
        this.processoEmExecucao = null;
        this.estrategia = estrategia;
        this.nomeAlgoritmo = nomeAlgoritmo;
        this.quantum = quantum;
        this.politicaPreempcao = politicaPreempcao;
        this.fatiasDeTempoExecutadas = 0;
    }

    public void executar(Writer writer) throws IOException {
        System.out.println("--- Executando simulação com o algoritmo: " + this.nomeAlgoritmo + " ---");

        while (processosFinalizados.size() < totalProcessos) {

            verificarNovasChegadas();
            gerenciarSelecaoDeProcesso();
            executarCicloDoProcessoAtual();
            tempoAtual++;
        }

        imprimirResultados(writer);
    }

    /**
     * Verifica a lista de processos de entrada e move para a fila de prontos
     * aqueles cujo tempo de chegada é menor ou igual ao tempo atual.
     */
    private void verificarNovasChegadas() {
        List<Processo> processosQueChegaram = processosDeEntrada.stream()
                .filter(p -> p.getTempoChegada() <= tempoAtual)
                .collect(Collectors.toList());

        for(Processo p : processosQueChegaram) {
            p.setStatus(StatusProcesso.PRONTO);
            filaDeProntos.add(p);
        }
        processosDeEntrada.removeAll(processosQueChegaram);
    }



    /**
     * Se a CPU estiver ociosa, tenta selecionar um novo processo usando a estratégia definida.
     */
    private void gerenciarSelecaoDeProcesso() {
        if (processoEmExecucao == null) {
            Processo proximoProcesso = estrategia.selecionarProximoProcesso(filaDeProntos);
            if (proximoProcesso != null) {
                processoEmExecucao = proximoProcesso;
                filaDeProntos.remove(proximoProcesso);
                processoEmExecucao.setStatus(StatusProcesso.EXECUTANDO);
                if (ordemDeExecucao.isEmpty() || !ordemDeExecucao.get(ordemDeExecucao.size() - 1).equals("P" + processoEmExecucao.getId())) {
                    ordemDeExecucao.add("P" + processoEmExecucao.getId());
                }
            }
        }
    }

    private void executarCicloDoProcessoAtual() {
        if (processoEmExecucao == null) {
            return; // Nada a fazer se a CPU está ociosa
        }

        processoEmExecucao.setTempoRestante(processoEmExecucao.getTempoRestante() - 1);
        fatiasDeTempoExecutadas++;

        // Caso 1: O processo terminou sua execução.
        if (processoEmExecucao.getTempoRestante() == 0) {
            finalizarProcessoAtual();
            // Resetar fatias de tempo, pois um novo processo será escolhido.
            fatiasDeTempoExecutadas = 0;
        }
        // Caso 2: A política de preempção decide que o processo deve ser interrompido.
        else if (politicaPreempcao.devePreemptar(this)) {
            sofrerPreempcao();
        }
    }

    /**
     * Lida com a preempção de um processo, devolvendo-o para a fila de prontos.
     */
    private void sofrerPreempcao() {
        System.out.println("Tempo " + (tempoAtual + 1) + ": Processo " + processoEmExecucao.getId() + " sofreu preempção (quantum esgotado).");
        processoEmExecucao.setStatus(StatusProcesso.PRONTO);
        filaDeProntos.add(processoEmExecucao);
        processoEmExecucao = null;
        fatiasDeTempoExecutadas = 0;
    }


    public int getTempoAtual() {
        return this.tempoAtual;
    }
    public int getQuantum() {
        return this.quantum;
    }

    public int getFatiasDeTempoExecutadas() {
        return this.fatiasDeTempoExecutadas;
    }

    public Processo getProcessoEmExecucao() {
        return this.processoEmExecucao;
    }

    public List<Processo> getFilaDeProntos() {
        return this.filaDeProntos;
    }



    /**
     * Finaliza o processo atualmente em execução, calcula suas métricas
     * e libera a CPU (processoEmExecucao = null).
     */
    private void finalizarProcessoAtual() {
        int tempoDeConclusao = tempoAtual + 1;
        processoEmExecucao.setStatus(StatusProcesso.FINALIZADO);
        processoEmExecucao.setTempoConclusao(tempoDeConclusao);

        int tempoRetorno = tempoDeConclusao - processoEmExecucao.getTempoChegada();
        processoEmExecucao.setTempoRetorno(tempoRetorno);

        int tempoEspera = tempoRetorno - processoEmExecucao.getTempoExecucao();
        processoEmExecucao.setTempoEspera(tempoEspera);

        processosFinalizados.add(processoEmExecucao);
        processoEmExecucao = null; // Libera a CPU
    }


    /**
     * Método principal refatorado. Agora ele apenas coordena as chamadas
     * para os métodos auxiliares de impressão.
     */
    private void imprimirResultados(Writer writer) throws IOException {
        processosFinalizados.sort(Comparator.comparingInt(Processo::getId));

        boolean isLoteria = this.nomeAlgoritmo.contains("Loteria");
        boolean usaPrioridade = this.nomeAlgoritmo.contains("Prioridade") ||
                this.nomeAlgoritmo.contains("Múltiplas Filas") ||
                isLoteria;

        // 1. Imprime o cabeçalho
        imprimirCabecalhoResultados(writer, isLoteria, usaPrioridade);

        // 2. Imprime o corpo da tabela e retorna as somas dos tempos
        double[] somas = imprimirCorpoTabelaResultados(writer, isLoteria, usaPrioridade);

        // 3. Imprime o rodapé com as médias, usando as somas retornadas
        imprimirRodapeResultados(writer, somas[0], somas[1]);

        System.out.println("Resultados para '" + this.nomeAlgoritmo + "' foram gravados no arquivo.");
    }


    /**
     * NOVO MÉTODO: Imprime a parte inicial dos resultados, incluindo o cabeçalho da tabela.
     */
    private void imprimirCabecalhoResultados(Writer writer, boolean isLoteria, boolean usaPrioridade) throws IOException {
        writer.write("Resultados para o algoritmo: " + this.nomeAlgoritmo + "\n");
        writer.write("Ordem de Execução: " + String.join(" → ", ordemDeExecucao) + "\n");

        if (isLoteria) {
            writer.write(String.format("%-10s %-17s %-17s %-12s %-12s\n",
                    "Processo", "Tempo de Espera", "Tempo de Retorno", "Prioridade", "Nº Bilhetes"));
        } else if (usaPrioridade) {
            writer.write(String.format("%-10s %-17s %-17s %-12s\n",
                    "Processo", "Tempo de Espera", "Tempo de Retorno", "Prioridade"));
        } else {
            writer.write(String.format("%-10s %-17s %-17s\n",
                    "Processo", "Tempo de Espera", "Tempo de Retorno"));
        }
        writer.write("--------------------------------------------------------------------------\n");
    }


    /**
     * NOVO MÉTODO: Itera sobre os processos, imprime cada linha da tabela e calcula as somas.
     * @return Um array de double onde a posição 0 é a soma do tempo de espera e a posição 1 é a soma do tempo de retorno.
     */
    private double[] imprimirCorpoTabelaResultados(Writer writer, boolean isLoteria, boolean usaPrioridade) throws IOException {
        double somaTempoEspera = 0;
        double somaTempoRetorno = 0;

        for (Processo p : processosFinalizados) {
            somaTempoEspera += p.getTempoEspera();
            somaTempoRetorno += p.getTempoRetorno();

            if (isLoteria) {
                writer.write(String.format("P%-9d %-17d %-17d %-12d %-12d\n",
                        p.getId(), p.getTempoEspera(), p.getTempoRetorno(), p.getPrioridade(), p.getBilhetes()));
            } else if (usaPrioridade) {
                writer.write(String.format("P%-9d %-17d %-17d %-12d\n",
                        p.getId(), p.getTempoEspera(), p.getTempoRetorno(), p.getPrioridade()));
            } else {
                writer.write(String.format("P%-9d %-17d %-17d\n",
                        p.getId(), p.getTempoEspera(), p.getTempoRetorno()));
            }
        }
        return new double[]{somaTempoEspera, somaTempoRetorno};
    }


    /**
     * NOVO MÉTODO: Imprime a parte final dos resultados, com as médias calculadas.
     */
    private void imprimirRodapeResultados(Writer writer, double somaTempoEspera, double somaTempoRetorno) throws IOException {
        writer.write("--------------------------------------------------------------------------\n");
        writer.write(String.format("Tempo Médio de Espera: %.2f\n", somaTempoEspera / totalProcessos));
        writer.write(String.format("Tempo Médio de Retorno: %.2f\n", somaTempoRetorno / totalProcessos));
        writer.write("\n-------\n\n");
    }
}