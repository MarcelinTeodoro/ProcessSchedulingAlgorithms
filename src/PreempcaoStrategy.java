/**
 * Interface (Contrato) para todas as estratégias de preempção.
 * Define que toda política de preempção deve ter um método para verificar
 * se o processo em execução deve ser interrompido.
 */
public interface PreempcaoStrategy {

    /**
     * Verifica se o processo atualmente em execução deve sofrer preempção.
     * @param escalonador O contexto atual do escalonador, que dá acesso
     * ao processo em execução, à fila de prontos, ao quantum, etc.
     * @return true se a preempção deve ocorrer, false caso contrário.
     */
    boolean devePreemptar(Escalonador escalonador);
}