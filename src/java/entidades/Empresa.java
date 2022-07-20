/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entidades;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rafael
 */

@XmlRootElement (name = "empresa")
public class Empresa implements Serializable {
    private int id;
    private String nombre_fantasia;
    private String razon_social;
    private String sitioWeb;
    private String sitioNoticias;
    private String email;
    private String direccion;
    private String telefonoFijo;
    private String telefonoMovil;
    private String mp_public_key;
    private String mp_access_token;
    private double porcViajesSimples;
    private double porcViajesDobles;
    private double pagoCuentaIva;
    private double costoFleteEntrega;
    private String emailSistemas;
    private String claveEmail;
    private String hostSmtp;
    private Short puertoEmail;
    private String startTls;
    private String auth;

    public Empresa() {
    }

    public Empresa(int id, String nombre_fantasia, String razon_social, String sitioWeb, String sitioNoticias) {
        this.id = id;
        this.nombre_fantasia = nombre_fantasia;
        this.razon_social = razon_social;
        this.sitioWeb = sitioWeb;
        this.sitioNoticias = sitioNoticias;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre_fantasia() {
        return nombre_fantasia;
    }

    public void setNombre_fantasia(String nombre_fantasia) {
        this.nombre_fantasia = nombre_fantasia;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public String getSitioNoticias() {
        return sitioNoticias;
    }

    public void setSitioNoticias(String sitioNoticias) {
        this.sitioNoticias = sitioNoticias;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefonoFijo() {
        return telefonoFijo;
    }

    public void setTelefonoFijo(String telefonoFijo) {
        this.telefonoFijo = telefonoFijo;
    }

    public String getTelefonoMovil() {
        return telefonoMovil;
    }

    public void setTelefonoMovil(String telefonoMovil) {
        this.telefonoMovil = telefonoMovil;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMp_public_key() {
        return mp_public_key;
    }

    public void setMp_public_key(String mp_public_key) {
        this.mp_public_key = mp_public_key;
    }

    public String getMp_access_token() {
        return mp_access_token;
    }

    public void setMp_access_token(String mp_access_token) {
        this.mp_access_token = mp_access_token;
    }

    public double getPorcViajesSimples() {
        return porcViajesSimples;
    }

    public void setPorcViajesSimples(double porcViajesSimples) {
        this.porcViajesSimples = porcViajesSimples;
    }

    public double getPorcViajesDobles() {
        return porcViajesDobles;
    }

    public void setPorcViajesDobles(double porcViajesDobles) {
        this.porcViajesDobles = porcViajesDobles;
    }

    public double getPagoCuentaIva() {
        return pagoCuentaIva;
    }

    public void setPagoCuentaIva(double pagoCuentaIva) {
        this.pagoCuentaIva = pagoCuentaIva;
    }

    public double getCostoFleteEntrega() {
        return costoFleteEntrega;
    }

    public void setCostoFleteEntrega(double costoFleteEntrega) {
        this.costoFleteEntrega = costoFleteEntrega;
    }
    
    public String getEmailSistemas() {
        return emailSistemas;
    }

    public void setEmailSistemas(String emailSistemas) {
        this.emailSistemas = emailSistemas;
    }

    public String getClaveEmail() {
        return claveEmail;
    }

    public void setClaveEmail(String claveEmail) {
        this.claveEmail = claveEmail;
    }

    public String getHostSmtp() {
        return hostSmtp;
    }

    public void setHostSmtp(String hostSmtp) {
        this.hostSmtp = hostSmtp;
    }

    public Short getPuertoEmail() {
        return puertoEmail;
    }

    public void setPuertoEmail(Short puertoEmail) {
        this.puertoEmail = puertoEmail;
    }

    public String getStartTls() {
        return startTls;
    }

    public void setStartTls(String startTls) {
        this.startTls = startTls;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
    
}
