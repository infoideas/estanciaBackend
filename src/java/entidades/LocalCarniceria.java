/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Propietario
 */

@XmlRootElement (name = "localcarniceria")
public class LocalCarniceria {
    private int idLocal;
    private int idClienteEstancia;
    private String nombreCliente;
    private String nombreComercial;
    private String responsable;
    private String direccion;
    private String provincia;
    private String localidad;

    public LocalCarniceria() {
    }

    public int getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(int idLocal) {
        this.idLocal = idLocal;
    }

    public int getIdClienteEstancia() {
        return idClienteEstancia;
    }

    public void setIdClienteEstancia(int idClienteEstancia) {
        this.idClienteEstancia = idClienteEstancia;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    
    
}
