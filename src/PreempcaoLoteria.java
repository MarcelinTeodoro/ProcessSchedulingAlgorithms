import java.util.ArrayList;
import java.util.List;

/**
 * Implementação da política de preempção para o algoritmo de Loteria Preemptivo.
 * A cada ciclo de clock, uma nova loteria é realizada entre o processo em execução
 * e todos os processos na fila de prontos. Se o processo em execução não for
 * o vencedor, ele sofre preempção.
 */
public class PreempcaoLoteria implements PreempcaoStrategy {


    private final AlgoritmoLoteria logicaDeSorteio;

    public PreempcaoLoteria() {
        this.logicaDeSorteio = new AlgoritmoLoteria();
    }

    @Override
    public boolean devePreemptar(Escalonador escalonador) {
        Processo processoEmExecucao = escalonador.getProcessoEmExecucao();
        List<Processo> filaDeProntos = escalonador.getFilaDeProntos();

        // Se não há ninguém na fila de prontos, não há competição.
        // O processo atual continua executando.
        if (filaDeProntos.isEmpty()) {
            return false;
        }

        // --- Realiza uma nova loteria ---
        // 1. Cria uma lista de competidores: o processo em execução + todos da fila de prontos.
        List<Processo> competidores = new ArrayList<>(filaDeProntos);
        competidores.add(processoEmExecucao);

        // 2. Sorteia um vencedor entre os competidores.
        Processo vencedor = logicaDeSorteio.selecionarProximoProcesso(competidores);

        // 3. A preempção deve ocorrer se o vencedor da nova loteria
        //    NÃO for o mesmo processo que já estava em execução.
        return vencedor != processoEmExecucao;
    }
}