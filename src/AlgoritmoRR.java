import java.util.List;

/**
 * Implementação da estratégia de seleção para o Round Robin (RR).
 * A política de seleção do RR é First-In, First-Out, então ele simplesmente
 * escolhe o primeiro processo da fila de prontos.
 */
public class AlgoritmoRR implements AlgoritmoEscalonador {

    @Override
    public Processo selecionarProximoProcesso(List<Processo> filaDeProntos) {
        if (filaDeProntos.isEmpty()) {
            return null;
        }
        // A seleção é FIFO: pega o primeiro da lista.
        return filaDeProntos.get(0);
    }
}