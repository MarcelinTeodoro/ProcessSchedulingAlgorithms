import java.util.List;
import java.util.Random;

/**
 * Implementação da estratégia de escalonamento por Loteria (Não-Preemptivo).
 * É um algoritmo probabilístico que aloca "bilhetes" aos processos com base
 * em sua prioridade e sorteia um vencedor para executar.
 */
public class AlgoritmoLoteria implements AlgoritmoEscalonador {

    private final Random random;
    private static final int MAX_PRIORIDADE = 5; // Define um teto para o cálculo de bilhetes

    public AlgoritmoLoteria() {
        this.random = new Random();
    }

    @Override
    public Processo selecionarProximoProcesso(List<Processo> filaDeProntos) {
        if (filaDeProntos.isEmpty()) {
            return null;
        }

        // --- Passo 1: Calcular o total de bilhetes na "pote" ---
        int totalDeBilhetes = 0;
        for (Processo p : filaDeProntos) {
            totalDeBilhetes += getBilhetesParaProcesso(p);
        }

        // Se por algum motivo não houver bilhetes, retorna o primeiro (para evitar divisão por zero)
        if (totalDeBilhetes == 0) {
            return filaDeProntos.get(0);
        }

        // --- Passo 2: Sortear um número vencedor ---
        // Sorteia um número entre 1 e o total de bilhetes (inclusive)
        int bilheteVencedor = random.nextInt(totalDeBilhetes) + 1;

        // --- Passo 3: Encontrar qual processo "possui" o bilhete sorteado ---
        int contadorDeBilhetes = 0;
        for (Processo p : filaDeProntos) {
            contadorDeBilhetes += getBilhetesParaProcesso(p);
            // Se o bilhete vencedor está dentro da faixa de bilhetes deste processo...
            if (bilheteVencedor <= contadorDeBilhetes) {
                // ...então ele é o vencedor!
                return p;
            }
        }

        // Linha de segurança, não deve ser alcançada
        return null;
    }

    /**
     * Define a regra para quantos bilhetes um processo recebe.
     * Processos com prioridade menor (mais alta) recebem mais bilhetes.
     * @param p O processo
     * @return O número de bilhetes
     */
    private int getBilhetesParaProcesso(Processo p) {
        // Fórmula: (Prioridade Máxima - Prioridade do Processo + 1)
        // Ex: Prio 1 -> (5-1+1) = 5 bilhetes
        //     Prio 2 -> (5-2+1) = 4 bilhetes
        //     Prio 3 -> (5-3+1) = 3 bilhetes
        return MAX_PRIORIDADE - p.getPrioridade() + 1;
    }
}