/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servicios;

import database.Conector;
import entidades.Empresa;
import general.BeanBase;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author rafael
 */
@Stateless
@Path("/empresa")
public class Empresa_Rest extends BeanBase{    
    
    @GET
    @Path("/getDatosEmpresa") 
    public Response getDatosEmpresa(@QueryParam("idEmpresa") int idEmpresa,@QueryParam("tipoRespuesta") String tipoRespuesta){
        
    Empresa empresa=null;
    try {
        empresa = getDatosEmpresa(idEmpresa);
    } catch (Exception ex) {
        Logger.getLogger(Empresa_Rest.class.getName()).log(Level.SEVERE, null, ex);
    }
        
    switch (tipoRespuesta){
              case "JSON":
                  return Response.ok(empresa,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(empresa,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
        
    }        
    
    public Empresa getDatosEmpresa(int idEmpresa) throws Exception {
        Empresa empresa=new Empresa();
        CallableStatement s=null;
        ResultSet r=null;
        String ls_nombreFantasia,ls_RazonSocial,ls_sitioWeb,ls_sitioNoticias;
        String ls_direccion,ls_email,ls_telefonoFijo,ls_telefonoMovil,ls_tipo_pedido;
        int li_cantidadMaxCupones,li_cantidadMaxImaProd;
        String ls_mp_public_key,ls_mp_access_token;
        double ld_costoFleteEntrega;
        Connection conexion=null;
        
        try {     
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_def_empresa ( ? )}");  
             s.setInt(1, idEmpresa);
             
             r=s.executeQuery();
             if (r.next()){
                ls_nombreFantasia=r.getString("nombre_fantasia");
                ls_RazonSocial=r.getString("razon_social");
                ls_sitioWeb=r.getString("sitioWeb");
                ls_sitioNoticias=r.getString("sitioNoticias"); 
                ls_email=r.getString("email"); 
                ls_direccion=r.getString("direccion"); 
                ls_telefonoFijo=r.getString("telefono_fijo");  
                ls_telefonoMovil=r.getString("telefono_movil");  
                
                //Identificadores Mercado Pago
                ls_mp_public_key=r.getString("mp_public_key");   
                ls_mp_access_token=r.getString("mp_access_token");  
                
                //Costo del flete de entrega por 1/2 res
                ld_costoFleteEntrega=r.getDouble("costoFleteEntrega");
                
                empresa.setId(idEmpresa);
                empresa.setNombre_fantasia(ls_nombreFantasia);
                empresa.setRazon_social(ls_RazonSocial);
                empresa.setSitioWeb(ls_sitioWeb);
                empresa.setSitioNoticias(ls_sitioNoticias);
                empresa.setEmail(ls_email);
                empresa.setDireccion(ls_direccion);
                empresa.setTelefonoFijo(ls_telefonoFijo);
                empresa.setTelefonoMovil(ls_telefonoMovil);
                empresa.setMp_public_key(ls_mp_public_key);
                empresa.setMp_access_token(ls_mp_access_token);
                empresa.setCostoFleteEntrega(ld_costoFleteEntrega);
               
            }

        }catch (SQLException e){
            System.out.print("Error: " + e.getMessage());
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
        return empresa;
        
    }
    
}
