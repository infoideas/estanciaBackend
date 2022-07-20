/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Propietario
 */

@XmlRootElement (name = "mientrega")
public class MiEntrega implements Serializable {
    private int id;
    private int idLocal;
    private String nombreLocal;
    private int idCliente;
    private String nombreCliente;
    private Date fec_carga;
    private int idUsuarioEntrega;
    private String nombreUsuarioEntrega;    
    private Date fec_ent;
    private int idUsuarioRecibe;
    private String nombreUsuarioRecibe;
    private Date fec_rec;
    private double totalKilos;
    private double valorFlete;
    private String observaciones;

    public MiEntrega() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }

    public String getNombreLocal() {
        return nombreLocal;
    }

    public void setNombreLocal(String nombreLocal) {
        this.nombreLocal = nombreLocal;
    }
    
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Date getFec_carga() {
        return fec_carga;
    }

    public void setFec_carga(Date fec_carga) {
        this.fec_carga = fec_carga;
    }

    public int getIdUsuarioEntrega() {
        return idUsuarioEntrega;
    }

    public void setIdUsuarioEntrega(int idUsuarioEntrega) {
        this.idUsuarioEntrega = idUsuarioEntrega;
    }

    public Date getFec_ent() {
        return fec_ent;
    }

    public void setFec_ent(Date fec_ent) {
        this.fec_ent = fec_ent;
    }

    public int getIdUsuarioRecibe() {
        return idUsuarioRecibe;
    }

    public void setIdUsuarioRecibe(int idUsuarioRecibe) {
        this.idUsuarioRecibe = idUsuarioRecibe;
    }

    public Date getFec_rec() {
        return fec_rec;
    }

    public void setFec_rec(Date fec_rec) {
        this.fec_rec = fec_rec;
    }

    public double getTotalKilos() {
        return totalKilos;
    }

    public void setTotalKilos(double totalKilos) {
        this.totalKilos = totalKilos;
    }

    public double getValorFlete() {
        return valorFlete;
    }

    public void setValorFlete(double valorFlete) {
        this.valorFlete = valorFlete;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getNombreUsuarioEntrega() {
        return nombreUsuarioEntrega;
    }

    public void setNombreUsuarioEntrega(String nombreUsuarioEntrega) {
        this.nombreUsuarioEntrega = nombreUsuarioEntrega;
    }

    public String getNombreUsuarioRecibe() {
        return nombreUsuarioRecibe;
    }

    public void setNombreUsuarioRecibe(String nombreUsuarioRecibe) {
        this.nombreUsuarioRecibe = nombreUsuarioRecibe;
    }

    
    
}
