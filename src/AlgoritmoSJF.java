import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementação da estratégia de escalonamento Shortest Job First (SJF) - Não Preemptivo.
 */
public class AlgoritmoSJF implements AlgoritmoEscalonador {

    @Override
    public Processo selecionarProximoProcesso(List<Processo> filaDeProntos) {
        // Se a fila de prontos estiver vazia, não há processo para selecionar.
        if (filaDeProntos.isEmpty()) {
            return null;
        }

        // Utiliza a API de Streams do Java para encontrar o processo com o menor 'tempoExecucao'.
        // 1. .stream() -> Cria um fluxo de dados a partir da lista.
        // 2. .min(...) -> Encontra o elemento mínimo no fluxo.
        // 3. Comparator.comparingInt(Processo::getTempoExecucao) -> Define que o critério
        //    de comparação para encontrar o "mínimo" é o valor retornado por getTempoExecucao().
        Optional<Processo> processoEscolhido = filaDeProntos.stream().min(Comparator.comparingInt(Processo::getTempoExecucao));

        // O método .min() retorna um Optional, que pode estar vazio.
        // .orElse(null) retorna o processo se ele foi encontrado, ou null caso contrário.
        return processoEscolhido.orElse(null);
    }
}