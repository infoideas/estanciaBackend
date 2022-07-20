/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contabilidad;

/**
 *
 * @author Propietario
 */
public class CuentaContable {
    int id;
    String numeroCuenta;
    String nombre;
    String tieneCC;

    public CuentaContable() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTieneCC() {
        return tieneCC;
    }

    public void setTieneCC(String tieneCC) {
        this.tieneCC = tieneCC;
    }

   
}
