/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Propietario
 */
@XmlRootElement (name = "tropadet")
public class TropaDet {
    private int id;
    private int numeroTropa;
    private String tipo;
    private java.util.Date fec_ing;
    private String productor;
    private String comisionista;
    private String deposito;
    private double disponible;
    private double entregado;

    public TropaDet() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumeroTropa() {
        return numeroTropa;
    }

    public void setNumeroTropa(int numeroTropa) {
        this.numeroTropa = numeroTropa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFec_ing() {
        return fec_ing;
    }

    public void setFec_ing(Date fec_ing) {
        this.fec_ing = fec_ing;
    }

    public String getProductor() {
        return productor;
    }

    public void setProductor(String productor) {
        this.productor = productor;
    }

    public String getComisionista() {
        return comisionista;
    }

    public void setComisionista(String comisionista) {
        this.comisionista = comisionista;
    }
    
    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = deposito;
    }

    public double getDisponible() {
        return disponible;
    }

    public void setDisponible(double disponible) {
        this.disponible = disponible;
    }

    public double getEntregado() {
        return entregado;
    }

    public void setEntregado(double entregado) {
        this.entregado = entregado;
    }

}
