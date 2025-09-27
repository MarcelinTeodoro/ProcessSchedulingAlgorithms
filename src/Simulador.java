
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

    public class Simulador {

        private static final int QUANTUM_RR = 4;
        private static final String NOME_ARQUIVO_SAIDA = "resultados_simulacao.txt";

        private static List<Processo> criarConjuntoDeProcessos() {
            List<Processo> processos = new ArrayList<>();

            processos.add(new Processo(1, 0, 5, 2));
            processos.add(new Processo(2, 2, 3, 1));
            processos.add(new Processo(3, 4, 8, 3));
            processos.add(new Processo(4, 5, 6, 2));
            processos.add(new Processo(5, 11, 8, 1));
            return processos;
        }

        // ALTERADO: O main agora lida com IOException
        public static void main(String[] args) throws IOException {

            // Usa try-with-resources para garantir que o writer seja fechado automaticamente
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO_SAIDA))) {

                // --- Simulação com FCFS ---
                System.out.println("=======================================================");
                Escalonador escalonadorFCFS = new Escalonador(criarConjuntoDeProcessos(), new AlgoritmoFCFS(), "FCFS", 0);
                escalonadorFCFS.executar(writer); // Passa o writer para o método

                // --- Simulação com SJF (Não Preemptivo) ---
                System.out.println("=======================================================");
                Escalonador escalonadorSJF = new Escalonador(criarConjuntoDeProcessos(), new AlgoritmoSJF(), "SJF (Não-Preemptivo)", 0);
                escalonadorSJF.executar(writer);

                // --- Simulação com Round Robin (RR) ---
                System.out.println("=======================================================");
                Escalonador escalonadorRR = new Escalonador(criarConjuntoDeProcessos(), new AlgoritmoRR(), "Round Robin (Q=" + QUANTUM_RR + ")", QUANTUM_RR);
                escalonadorRR.executar(writer);

                // --- Simulação com Prioridade (Não Preemptivo) ---
                System.out.println("=======================================================");
                Escalonador escalonadorPrioridade = new Escalonador(criarConjuntoDeProcessos(), new AlgoritmoPrioridade(), "Prioridade (Não-Preemptivo)", 0);
                escalonadorPrioridade.executar(writer);

                // --- Simulação com Múltiplas Filas ---
                System.out.println("=======================================================");
                Escalonador escalonadorMultiplasFilas = new Escalonador(criarConjuntoDeProcessos(), new AlgoritmoMultiplasFilas(), "Múltiplas Filas", 0);
                escalonadorMultiplasFilas.executar(writer);

                // --- Simulação com Loteria ---
                System.out.println("=======================================================");
                Escalonador escalonadorLoteria = new Escalonador(criarConjuntoDeProcessos(), new AlgoritmoLoteria(), "Loteria", 0);
                escalonadorLoteria.executar(writer);

                System.out.println("\n\n>>> Todas as simulações foram concluídas! Verifique o arquivo '" + NOME_ARQUIVO_SAIDA + "' <<<");

            } catch (IOException e) {
                System.err.println("Ocorreu um erro ao escrever no arquivo de resultados: " + e.getMessage());
            }
        }
    }