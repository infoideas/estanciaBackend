/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Propietario
 */
@XmlRootElement (name = "tropacategoria")
public class TropaCategoria implements Serializable {
    int idCategoria;
    String nombreCategoria;
    int idTropa;
    String numeroTropa;
    String productor;
    Date fecha_faena;
    int cantidad;
    double inventarioActual;

    public TropaCategoria() {
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public int getIdTropa() {
        return idTropa;
    }

    public void setIdTropa(int idTropa) {
        this.idTropa = idTropa;
    }

    public String getNumeroTropa() {
        return numeroTropa;
    }

    public void setNumeroTropa(String numeroTropa) {
        this.numeroTropa = numeroTropa;
    }

    public String getProductor() {
        return productor;
    }

    public void setProductor(String productor) {
        this.productor = productor;
    }

    public Date getFecha_faena() {
        return fecha_faena;
    }

    public void setFecha_faena(Date fecha_faena) {
        this.fecha_faena = fecha_faena;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getInventarioActual() {
        return inventarioActual;
    }

    public void setInventarioActual(double inventarioActual) {
        this.inventarioActual = inventarioActual;
    }
    
    
    
}
