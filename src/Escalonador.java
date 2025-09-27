import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Escalonador {
    // ... (variáveis idênticas às anteriores)
    private final List<Processo> processosDeEntrada;
    private final List<Processo> filaDeProntos;
    private final List<Processo> processosFinalizados;
    private final List<String> ordemDeExecucao;
    private final int totalProcessos;
    private int tempoAtual;
    private Processo processoEmExecucao;
    private final AlgoritmoEscalonador estrategia;
    private final String nomeAlgoritmo;

    // --- NOVO: Variáveis para Round Robin ---
    private final int quantum;
    private int fatiasDeTempoExecutadas;

    // ALTERADO: Construtor agora aceita um quantum.
    public Escalonador(List<Processo> processos, AlgoritmoEscalonador estrategia, String nomeAlgoritmo, int quantum) {
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

        // NOVO: Inicialização das variáveis do RR
        this.quantum = quantum;
        this.fatiasDeTempoExecutadas = 0;
    }

    /**
     * Executa a simulação completa, do tempo 0 até o término de todos os processos.
     * Este método orquestra a chegada de processos, a seleção via estratégia, a execução
     * com lógica de preempção (se aplicável) e a finalização.
     *
     * @param writer O objeto Writer para onde os resultados finais serão escritos.
     * @throws IOException Se ocorrer um erro durante a escrita no arquivo.
     */
    public void executar(Writer writer) throws IOException {
        // 1. Mensagem inicial para feedback no console.
        System.out.println("--- Executando simulação com o algoritmo: " + this.nomeAlgoritmo + " ---");

        // 2. O loop principal da simulação. Continua enquanto houver processos a serem finalizados.
        while (processosFinalizados.size() < totalProcessos) {

            // 3. Verifica se algum processo novo chegou no tempo atual e o coloca na fila de prontos.
            verificarNovasChegadas();

            // 4. LÓGICA DE DECISÃO: Executada apenas se a CPU estiver ociosa.
            if (processoEmExecucao == null) {
                // Delega a decisão de qual processo escolher para a estratégia injetada (Strategy Pattern).
                Processo proximoProcesso = estrategia.selecionarProximoProcesso(filaDeProntos);

                // Se uma estratégia encontrou um processo válido...
                if (proximoProcesso != null) {
                    processoEmExecucao = proximoProcesso;
                    filaDeProntos.remove(proximoProcesso);
                    processoEmExecucao.setStatus(StatusProcesso.EXECUTANDO);

                    // Lógica para adicionar o processo à ordem de execução (evita duplicatas seguidas no log do RR).
                    if (ordemDeExecucao.isEmpty() || !ordemDeExecucao.get(ordemDeExecucao.size()-1).equals("P" + processoEmExecucao.getId())) {
                        ordemDeExecucao.add("P" + processoEmExecucao.getId());
                    }
                }
            }

            // 5. LÓGICA DE EXECUÇÃO: Executada se a CPU estiver ocupada com um processo.
            if (processoEmExecucao != null) {
                // Decrementa o tempo restante do processo.
                processoEmExecucao.setTempoRestante(processoEmExecucao.getTempoRestante() - 1);
                // Incrementa o contador da fatia de tempo (para o Round Robin).
                fatiasDeTempoExecutadas++;

                // CASO 5.1: O processo TERMINOU sua execução (tempo restante chegou a zero).
                if (processoEmExecucao.getTempoRestante() == 0) {
                    finalizarProcessoAtual();
                    fatiasDeTempoExecutadas = 0; // Zera o contador para o próximo processo.
                }
                // CASO 5.2: O processo NÃO terminou, mas seu QUANTUM ACABOU.
                // A verificação "quantum > 0" garante que esta lógica SÓ rode para algoritmos preemptivos.
                else if (quantum > 0 && fatiasDeTempoExecutadas >= quantum) {
                    System.out.println("Tempo " + (tempoAtual+1) + ": Processo " + processoEmExecucao.getId() + " sofreu preempção (quantum esgotado).");
                    processoEmExecucao.setStatus(StatusProcesso.PRONTO);
                    filaDeProntos.add(processoEmExecucao); // Devolve o processo para o FIM da fila de prontos.
                    processoEmExecucao = null; // Libera a CPU.
                    fatiasDeTempoExecutadas = 0; // Zera o contador.
                }
            }

            // 6. O clock da simulação avança uma unidade de tempo.
            tempoAtual++;
        }

        // 7. Após o fim do loop, chama o método para gravar os resultados no arquivo.
        imprimirResultados(writer);
    }

    // ... (O restante da classe Escalonador permanece igual)
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
     * CORRIGIDO: Imprime os resultados formatados, com a Prioridade como última coluna.
     * @param writer O objeto Writer para onde a saída será direcionada.
     * @throws IOException Se ocorrer um erro durante a escrita no arquivo.
     */
    private void imprimirResultados(Writer writer) throws IOException {
        processosFinalizados.sort(Comparator.comparingInt(Processo::getId));

        double somaTempoRetorno = 0;
        double somaTempoEspera = 0;

        writer.write("Resultados para o algoritmo: " + this.nomeAlgoritmo + "\n");
        writer.write("Ordem de Execução: " + String.join(" → ", ordemDeExecucao) + "\n");

        // ALTERADO: Ordem das colunas no cabeçalho
        writer.write(String.format("%-10s %-17s %-17s %-12s\n",
                "Processo", "Tempo de Espera", "Tempo de Retorno", "Prioridade"));
        writer.write("--------------------------------------------------------------\n");

        for (Processo p : processosFinalizados) {
            // ALTERADO: Ordem dos dados para corresponder ao novo cabeçalho
            writer.write(String.format("P%-9d %-17d %-17d %-12d\n",
                    p.getId(),
                    p.getTempoEspera(),
                    p.getTempoRetorno(),
                    p.getPrioridade() // <<< MOVIMOS PARA O FINAL
            ));
            somaTempoRetorno += p.getTempoRetorno();
            somaTempoEspera += p.getTempoEspera();
        }

        writer.write("--------------------------------------------------------------\n");
        writer.write(String.format("Tempo Médio de Espera: %.2f\n", somaTempoEspera / totalProcessos));
        writer.write(String.format("Tempo Médio de Retorno: %.2f\n", somaTempoRetorno / totalProcessos));

        writer.write("\n-------\n\n");

        System.out.println("Resultados para '" + this.nomeAlgoritmo + "' foram gravados no arquivo.");
    }
}