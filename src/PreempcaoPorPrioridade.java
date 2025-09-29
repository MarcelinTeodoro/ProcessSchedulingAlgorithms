import java.util.List;

/**
 * Implementação da política de preempção baseada em prioridade.
 * Usada por algoritmos como Prioridade Preemptivo ou Múltiplas Filas Preemptivo.
 * A preempção ocorre se um processo na fila de prontos tiver uma prioridade
 * maior do que a do processo atualmente em execução.
 */
public class PreempcaoPorPrioridade implements PreempcaoStrategy {

    @Override
    public boolean devePreemptar(Escalonador escalonador) {
        Processo processoEmExecucao = escalonador.getProcessoEmExecucao();
        List<Processo> filaDeProntos = escalonador.getFilaDeProntos();

        // Se não há processo executando ou não há ninguém na fila, não há o que preemptar.
        if (processoEmExecucao == null || filaDeProntos.isEmpty()) {
            return false;
        }

        // Obtém a prioridade do processo que está na CPU.
        int prioridadeAtual = processoEmExecucao.getPrioridade();

        // Percorre a fila de prontos para ver se alguém tem prioridade maior.
        // Lembre-se: prioridade maior = número menor.
        for (Processo processoPronto : filaDeProntos) {
            if (processoPronto.getPrioridade() < prioridadeAtual) {
                // Encontrou um processo mais importante na fila de prontos.
                // O processo atual DEVE sofrer preempção.
                System.out.println("Tempo " + (escalonador.getTempoAtual() + 1) + ": PREEMPÇÃO! Processo P" + processoPronto.getId() +
                        " (Prio:" + processoPronto.getPrioridade() + ") chegou e é mais prioritário que P" +
                        processoEmExecucao.getId() + " (Prio:" + prioridadeAtual + ").");
                return true;
            }
        }

        // Se percorreu a fila inteira e ninguém era mais prioritário, não preempta.
        return false;
    }
}