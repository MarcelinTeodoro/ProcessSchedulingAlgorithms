import java.util.List;
import java.util.Random;

/**
 * Implementação da estratégia de escalonamento por Loteria.
 * Utiliza o número de bilhetes armazenado em cada objeto Processo.
 */
public class AlgoritmoLoteria implements AlgoritmoEscalonador {

    private final Random random;

    public AlgoritmoLoteria() {
        this.random = new Random();
    }

    @Override
    public Processo selecionarProximoProcesso(List<Processo> filaDeProntos) {
        if (filaDeProntos.isEmpty()) {
            return null;
        }

        // --- Passo 1: Calcular o total de bilhetes usando o getter ---
        int totalDeBilhetes = 0;
        for (Processo p : filaDeProntos) {
            totalDeBilhetes += p.getBilhetes(); // <-- MUDANÇA AQUI
        }

        if (totalDeBilhetes == 0) {
            return filaDeProntos.get(0);
        }

        // --- Passo 2: Sortear um número vencedor ---
        int bilheteVencedor = random.nextInt(totalDeBilhetes) + 1;

        // --- Passo 3: Encontrar qual processo "possui" o bilhete sorteado ---
        int contadorDeBilhetes = 0;
        for (Processo p : filaDeProntos) {
            contadorDeBilhetes += p.getBilhetes(); // <-- MUDANÇA AQUI
            if (bilheteVencedor <= contadorDeBilhetes) {
                return p;
            }
        }

        return null; // Linha de segurança
    }


}