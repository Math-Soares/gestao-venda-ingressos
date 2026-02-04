package model;

public class Ingresso {
    private int id;
    private Cliente cliente;
    private Evento evento;
    
    public Ingresso() {}

    public Ingresso(Cliente cliente, Evento evento) {
        this.cliente = cliente;
        this.evento = evento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}