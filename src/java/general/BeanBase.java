/*
 * BeanBase.java
 *
 * Created on 10 de noviembre de 2005, 10:00
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package general;
import database.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
//import javax.faces.context.*;
import javax.faces.model.SelectItem;


/**
 *
 * @author rafaelg
 */

//Bean b�sico con m�todos para la conexi�n a las bases de datos

public class BeanBase {
    private String tipo_exportacion;
    public static final String  URI_BACKEND=System.getProperty("URI_BACKEND"); 
    public static final String  URI_SERVICIO_ACTIVACION=System.getProperty("URI_SERVICIO_ACTIVACION"); //para activar usuario
    public static final String  URI_RESETEAR_CLAVE=System.getProperty("URI_ESTANCIA_RESETEAR_CLAVE"); //para resetear clave
    
    //public static final String  CARPETA_JASPERS=System.getProperty("CARPETA_JASPERS"); //para desarrollo

   

    public String getURI_BACKEND() {
        return URI_BACKEND;
    }

    private static final SelectItem[] TIPOS_ARCHIVOS = {new SelectItem("xls", "XLS"),
                                                        new SelectItem("pdf","PDF"),
                                                        new SelectItem("csv","CSV")};

    /** Creates a new instance of BeanBase */
     public BeanBase() {

    }

    public SelectItem[] getTipos() {
        return TIPOS_ARCHIVOS;
    }

    public String getTipo_exportacion() {
        return tipo_exportacion;
    }

    public void setTipo_exportacion(String tipo_exportacion) {
        this.tipo_exportacion = tipo_exportacion;
    }

    
    public static SelectItem[] getTIPOS_ARCHIVOS() {
        return TIPOS_ARCHIVOS;
    }
    
   
    
}