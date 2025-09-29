/**
 * Implementação da política de preempção baseada em um quantum de tempo.
 * Usada pelo algoritmo Round Robin.
 */
public class PreempcaoPorQuantum implements PreempcaoStrategy {

    @Override
    public boolean devePreemptar(Escalonador escalonador) {
        // A preempção só ocorre se o algoritmo for configurado com um quantum.
        if (escalonador.getQuantum() <= 0) {
            return false;
        }
        // A preempção acontece se o número de fatias de tempo executadas
        // atingir ou ultrapassar o valor do quantum.
        return escalonador.getFatiasDeTempoExecutadas() >= escalonador.getQuantum();
    }
}