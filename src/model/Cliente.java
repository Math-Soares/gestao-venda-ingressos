package model;

public class Cliente {
    private int id;
    private String usuario;
    private String email;
    private String cargo;

    public Cliente() {}
    
    public Cliente(int id_cliente, String usuario, String email, String cargo) {
        this.id = id_cliente;
        this.usuario = usuario;
        this.email = email;
        this.cargo = cargo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id_cliente) {
        this.id = id_cliente;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}