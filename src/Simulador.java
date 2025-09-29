

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal que orquestra a execução das simulações de escalonamento.
 * Configura e executa cada algoritmo com sua respectiva política de
 * seleção e preempção, e escreve os resultados em um arquivo.
 */
public class Simulador {

    private static final int QUANTUM = 4;
    private static final String NOME_ARQUIVO_SAIDA = "resultados_simulacao.txt";

    /**
     * Cria e retorna a lista de processos a serem usados nas simulações.
     * @return Uma lista de objetos Processo.
     */
    private static List<Processo> criarConjuntoDeProcessos() {
        List<Processo> processos = new ArrayList<>();
        final int MAX_PRIORIDADE = 5; // A mesma constante do seu algoritmo

        // Processo 1
        Processo p1 = new Processo(1, 0, 5, 2);
        p1.setBilhetes(MAX_PRIORIDADE - p1.getPrioridade() + 1);
        processos.add(p1);

        // Processo 2
        Processo p2 = new Processo(2, 2, 3, 1);
        p2.setBilhetes(MAX_PRIORIDADE - p2.getPrioridade() + 1);
        processos.add(p2);

        // Processo 3
        Processo p3 = new Processo(3, 4, 8, 3);
        p3.setBilhetes(MAX_PRIORIDADE - p3.getPrioridade() + 1);
        processos.add(p3);

        // Processo 4
        Processo p4 = new Processo(4, 5, 6, 2);
        p4.setBilhetes(MAX_PRIORIDADE - p4.getPrioridade() + 1);
        processos.add(p4);

        // Processo 5
        Processo p5 = new Processo(5, 11, 8, 1);
        p5.setBilhetes(MAX_PRIORIDADE - p5.getPrioridade() + 1);
        processos.add(p5);

        return processos;
    }

    public static void main(String[] args) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO_SAIDA))) {

            // --- 1. Algoritmos NÃO-PREEMPTIVOS ---
            // Todos usam a política PreempcaoNenhuma(), pois nunca interrompem um processo.
            System.out.println("=======================================================");
            Escalonador escalonadorFCFS = new Escalonador(
                    criarConjuntoDeProcessos(),
                    new AlgoritmoFCFS(),
                    "FCFS",
                    0, // Quantum não aplicável
                    new PreempcaoNenhuma()
            );
            escalonadorFCFS.executar(writer);

            System.out.println("=======================================================");
            Escalonador escalonadorSJF = new Escalonador(
                    criarConjuntoDeProcessos(),
                    new AlgoritmoSJF(),
                    "SJF (Não-Preemptivo)",
                    0,
                    new PreempcaoNenhuma()
            );
            escalonadorSJF.executar(writer);

            System.out.println("=======================================================");
            Escalonador escalonadorRR = new Escalonador(
                    criarConjuntoDeProcessos(),
                    new AlgoritmoRR(), // A seleção é FIFO
                    "Round Robin (Q=" + QUANTUM + ")",
                    QUANTUM, // Quantum é essencial aqui
                    new PreempcaoPorQuantum() // A preempção ocorre pelo quantum
            );
            escalonadorRR.executar(writer);

            System.out.println("=======================================================");
            Escalonador escalonadorPrioridade = new Escalonador(
                    criarConjuntoDeProcessos(),
                    new AlgoritmoPrioridade(),
                    "Prioridade (Não-Preemptivo)",
                    0,
                    new PreempcaoNenhuma()
            );
            escalonadorPrioridade.executar(writer);

            System.out.println("=======================================================");
            Escalonador escalonadorMultiplasFilasRR = new Escalonador(
                    criarConjuntoDeProcessos(),
                    new AlgoritmoMultiplasFilas(),                  // 1. Usa a seleção que prioriza a fila 1
                    "Múltiplas Filas (Prio 1 = RR, Demais = FCFS)",  // 2. Nome descritivo
                    QUANTUM,                                    // 3. Passamos o quantum a ser usado
                    new PreempcaoMultiplasFilas()                  // 4. A nova política de preempção inteligente!
            );
            escalonadorMultiplasFilasRR.executar(writer);

            System.out.println("=======================================================");
            Escalonador escalonadorLoteriaPreemptivo = new Escalonador(
                    criarConjuntoDeProcessos(),
                    new AlgoritmoLoteria(), // A seleção inicial é por Loteria
                    "Loteria (Preemptivo)",
                    0, // Quantum não é usado
                    new PreempcaoLoteria() // A preempção ocorre por uma nova loteria a cada ciclo
            );
            escalonadorLoteriaPreemptivo.executar(writer);





            System.out.println("\n\n>>> Todas as simulações foram concluídas! Verifique o arquivo '" + NOME_ARQUIVO_SAIDA + "' <<<");

        } catch (IOException e) {
            System.err.println("Ocorreu um erro ao escrever no arquivo de resultados: " + e.getMessage());
        }
    }
}