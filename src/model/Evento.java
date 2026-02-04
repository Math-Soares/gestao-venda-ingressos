package model;

public class Evento {
    private int id;
    private String nome;
    private String data;
    private double preco;
    private int capacidade;
    
    public Evento() {}

    public Evento(int id, String nome, String data, double preco, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.data = data;
        this.preco = preco;
        this.capacidade = capacidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }
}