import java.util.List;

/**
 * Implementação da estratégia de escalonamento First-Come, First-Served (FCFS).
 */
public class AlgoritmoFCFS implements AlgoritmoEscalonador {

    @Override
    public Processo selecionarProximoProcesso(List<Processo> filaDeProntos) {
        // Se a fila de prontos estiver vazia, não há ninguém para selecionar.
        if (filaDeProntos.isEmpty()) {
            return null;
        }

        // No FCFS, o próximo processo é sempre o primeiro que entrou na fila (índice 0).
        return filaDeProntos.get(0);
    }
}