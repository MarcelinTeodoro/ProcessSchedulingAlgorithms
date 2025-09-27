import java.util.List;

/**
 * Interface (Contrato) para todas as estratégias de escalonamento.
 * Define que todo algoritmo deve ter um método para selecionar o próximo processo.
 */
public interface AlgoritmoEscalonador {

    /**
     * Seleciona o próximo processo a ser executado a partir da fila de prontos.
     * @param filaDeProntos A lista de processos que já chegaram e estão prontos para executar.
     * @return O processo escolhido, ou null se a fila estiver vazia.
     */
    Processo selecionarProximoProcesso(List<Processo> filaDeProntos);
}