/**
 * Implementação de uma política de preempção mista para o Múltiplas Filas.
 * - Aplica preempção por QUANTUM se o processo for de ALTA prioridade (ex: Prio 1).
 * - NÃO aplica preempção (se comporta como FCFS) para as demais prioridades.
 */
public class PreempcaoMultiplasFilas implements PreempcaoStrategy {

    @Override
    public boolean devePreemptar(Escalonador escalonador) {
        Processo processoEmExecucao = escalonador.getProcessoEmExecucao();

        // Se não há processo, não há o que preemptar.
        if (processoEmExecucao == null) {
            return false;
        }

        // --- LÓGICA PRINCIPAL ---
        // Verifica a prioridade do processo que está na CPU.
        if (processoEmExecucao.getPrioridade() == 1) {
            // Se for de ALTA PRIORIDADE, aplicamos a lógica do Round Robin.
            if (escalonador.getQuantum() <= 0) {
                return false;
            }
            return escalonador.getFatiasDeTempoExecutadas() >= escalonador.getQuantum();

        } else {
            // Se for de qualquer outra prioridade, NUNCA sofre preempção por quantum.
            // (Ainda pode sofrer preempção se um processo de prioridade maior chegar,
            // mas isso é responsabilidade da PreempcaoPorPrioridade).
            return false;
        }
    }
}