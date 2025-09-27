import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementação da estratégia de escalonamento por Prioridade (Não-Preemptivo).
 * Assume que um valor de prioridade MENOR significa uma prioridade MAIOR
 * (ex: 1 é mais prioritário que 2).
 */
public class AlgoritmoPrioridade implements AlgoritmoEscalonador {

    @Override
    public Processo selecionarProximoProcesso(List<Processo> filaDeProntos) {
        if (filaDeProntos.isEmpty()) {
            return null;
        }

        // A lógica é idêntica ao SJF, mas o critério de comparação agora é a prioridade.
        // Usamos .min() porque definimos que um número MENOR tem prioridade MAIOR.
        Optional<Processo> processoEscolhido = filaDeProntos.stream()
                .min(Comparator.comparingInt(Processo::getPrioridade));

        return processoEscolhido.orElse(null);
    }
}