/**
 * Implementação da política de preempção nula.
 * Usada por algoritmos não-preemptivos.
 */
public class PreempcaoNenhuma implements PreempcaoStrategy {

    @Override
    public boolean devePreemptar(Escalonador escalonador) {
        // Algoritmos não-preemptivos nunca interrompem o processo em execução.
        return false;
    }
}