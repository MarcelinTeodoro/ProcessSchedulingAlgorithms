import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da estratégia de escalonamento Múltiplas Filas (Não-Preemptivo).
 * Esta estratégia simula múltiplas filas de prioridade.
 */
public class AlgoritmoMultiplasFilas implements AlgoritmoEscalonador {

    @Override
    public Processo selecionarProximoProcesso(List<Processo> filaDeProntos) {
        if (filaDeProntos.isEmpty()) {
            return null;
        }

        // 1. Cria as "filas virtuais" para cada nível de prioridade.
        List<Processo> filaPrioridade1 = new ArrayList<>(); // Alta
        List<Processo> filaPrioridade2 = new ArrayList<>(); // Média
        List<Processo> filaPrioridade3 = new ArrayList<>(); // Baixa

        // 2. Distribui os processos da fila principal nas filas virtuais.
        // Como a filaDeProntos já está na ordem de chegada, o critério FCFS
        // dentro de cada prioridade é mantido naturalmente.
        for (Processo p : filaDeProntos) {
            switch (p.getPrioridade()) {
                case 1:
                    filaPrioridade1.add(p);
                    break;
                case 2:
                    filaPrioridade2.add(p);
                    break;
                default:
                    filaPrioridade3.add(p);
                    break;
            }
        }

        // 3. Lógica de seleção: busca da fila de maior prioridade para a menor.
        if (!filaPrioridade1.isEmpty()) {
            // Se há processos na fila de alta prioridade, retorna o primeiro.
            return filaPrioridade1.get(0);
        } else if (!filaPrioridade2.isEmpty()) {
            // Se não, se há processos na de média prioridade, retorna o primeiro.
            return filaPrioridade2.get(0);
        } else if (!filaPrioridade3.isEmpty()) {
            // Se não, se há processos na de baixa prioridade, retorna o primeiro.
            return filaPrioridade3.get(0);
        }

        // Caso algo inesperado aconteça (não deveria)
        return null;
    }
}