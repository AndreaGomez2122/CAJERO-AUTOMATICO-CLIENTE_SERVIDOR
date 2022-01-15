package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Movimiento {
    private LocalDate fecha;
    private tipo tipoMovimiento;
    private String user_email;
    private double valor;

    public Movimiento(LocalDate fecha, tipo tipoMovimiento, String user_email, double valor) {
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.user_email = user_email;
        this.valor = valor;
    }

    public Movimiento(LocalDate fecha, tipo tipoMovimiento, String user_email) {
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.user_email = user_email;

    }

    public Movimiento() {
    }


    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public tipo getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(tipo tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email){this.user_email = user_email;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Movimiento{" +
                "fecha=" + fecha +
                ", tipoMovimiento=" + tipoMovimiento +
                ", user_id=" + user_email +
                ", valor=" + valor +
                '}';
    }
}
