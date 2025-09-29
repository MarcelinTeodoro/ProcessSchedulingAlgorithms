import java.util.Objects;

public class Processo {
    // Atributos de Entrada
    private final int id;
    private final int tempoChegada;
    private final int tempoExecucao;
    private final int prioridade;

    // Atributos de Controle
    private StatusProcesso status;

    // Atributos para Métricas
    private int bilhetes;
    private int tempoRestante;
    private int tempoConclusao;
    private int tempoRetorno;
    private int tempoEspera;

    public Processo(int id, int tempoChegada, int tempoExecucao, int prioridade) {
        this.id = id;
        this.tempoChegada = tempoChegada;
        this.tempoExecucao = tempoExecucao;
        this.prioridade = prioridade;
        this.tempoRestante = this.tempoExecucao;
        this.status = StatusProcesso.NAO_CHEGOU;
    }

    // --- Getters e Setters ---

    public int getId() { return id; }
    public int getTempoChegada() { return tempoChegada; }
    public int getTempoExecucao() { return tempoExecucao; }
    public int getTempoRestante() { return tempoRestante; }
    public void setTempoRestante(int tempoRestante) { this.tempoRestante = tempoRestante; }
    public int getTempoConclusao() { return tempoConclusao; }
    public void setTempoConclusao(int tempoConclusao) { this.tempoConclusao = tempoConclusao; }
    public int getTempoRetorno() { return tempoRetorno; }
    public void setTempoRetorno(int tempoRetorno) { this.tempoRetorno = tempoRetorno; }
    public int getTempoEspera() { return tempoEspera; }
    public void setTempoEspera(int tempoEspera) { this.tempoEspera = tempoEspera; }
    public int getPrioridade(){return prioridade; }
    public int getBilhetes() { return bilhetes; }
    public void setBilhetes(int bilhetes) { this.bilhetes = bilhetes; }

    public StatusProcesso getStatus() { // ALTERADO: O tipo de retorno do getter
        return status;
    }

    public void setStatus(StatusProcesso status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Processo{" + "id=" + id + ", status=" + status + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Processo processo = (Processo) o;
        return id == processo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}