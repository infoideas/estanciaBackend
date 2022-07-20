/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;

/**
 *
 * @author Propietario
 */
public class EntregaDetalle implements Serializable {
    private int id;
    private int idEntrega;
    private int idTropa;
    private int idGarronInv;
    private int idGarron;
    private int numGarron;
    private String tipo;
    private int idCategoria;
    private double kilos;
    private int idProductoRel;
    private int idUnidad;
    private String estado;

    public EntregaDetalle() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(int idEntrega) {
        this.idEntrega = idEntrega;
    }

    public int getIdTropa() {
        return idTropa;
    }

    public void setIdTropa(int idTropa) {
        this.idTropa = idTropa;
    }

    public int getIdGarronInv() {
        return idGarronInv;
    }

    public void setIdGarronInv(int idGarronInv) {
        this.idGarronInv = idGarronInv;
    }

    public int getIdGarron() {
        return idGarron;
    }

    public void setIdGarron(int idGarron) {
        this.idGarron = idGarron;
    }

    public int getNumGarron() {
        return numGarron;
    }

    public void setNumGarron(int numGarron) {
        this.numGarron = numGarron;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public double getKilos() {
        return kilos;
    }

    public void setKilos(double kilos) {
        this.kilos = kilos;
    }

    public int getIdProductoRel() {
        return idProductoRel;
    }

    public void setIdProductoRel(int idProductoRel) {
        this.idProductoRel = idProductoRel;
    }

    public int getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(int idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
