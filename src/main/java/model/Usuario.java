package model;

import org.bson.types.ObjectId;

public class Usuario {
    private ObjectId id;
    private String email;
    private String password;
    private double saldo;
    private double limite;

    public Usuario(ObjectId id, String email, String password, double saldo, double limite) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.saldo = saldo;
        this.limite = limite;
    }

    public Usuario() {
    }


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", saldo=" + saldo +
                ", limite=" + limite +
                '}';
    }
}
