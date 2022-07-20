/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servicios;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.Conector;
import entidades.ImagenProducto;
import entidades.LocalCarniceria;
import entidades.Localidad;
import entidades.MiCupon;
import entidades.Producto;
import entidades.Promocion;
import entidades.Provincia;
import entidades.Sucursal;
import general.BeanBase;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
@Path("/lista")
public class Lista_Rest extends BeanBase{
    //Lista de Provincias
    @GET
    @Path("/getProvincias")
    public Response ListaProvincias(@QueryParam("tipoRespuesta") String tipoRespuesta) {
        System.out.println("Invoca servicio...");
        List items=null;
        try {
            items = getListaProvincias();
        } catch (Exception ex) {
            Logger.getLogger(Lista_Rest.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        switch (tipoRespuesta){
              case "JSON":
                  Gson gson = new Gson();
                  String sb=gson.toJson(items);
                  return Response.ok(sb,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(items,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }     
       
    }    

    //Lista de Localidades por provincia
    @GET
    @Path("/getLocalidades")
    public Response ListaLocalidades(@QueryParam("idProvincia") int idProvincia, @QueryParam("tipoRespuesta") String tipoRespuesta) {
        System.out.println("Invoca servicio...");
        List items=null;
        try {
            items = getListaLocalidades(idProvincia);
        } catch (Exception ex) {
            Logger.getLogger(Lista_Rest.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        switch (tipoRespuesta){
              case "JSON":
                  Gson gson = new Gson();
                  String sb=gson.toJson(items);
                  return Response.ok(sb,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(items,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }     
       
    }    
    
    //Lista de locales de carnicería
    @GET
    @Path("/getLocalesCarniceria")
    public Response ListaLocalesCarniceria(@QueryParam("tipoRespuesta") String tipoRespuesta) {
        System.out.println("Invoca servicio...");
        List items=null;
        try {
            items = getListaLocalesCarniceria();
        } catch (Exception ex) {
            Logger.getLogger(Lista_Rest.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        switch (tipoRespuesta){
              case "JSON":
                  Gson gson = new Gson();
                  String sb=gson.toJson(items);
                  return Response.ok(sb,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(items,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }     
       
    }          
    
    //Lista de Provincias
    public List getListaProvincias() throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id;
        String ls_nombre="";
        List resultados = new ArrayList();
        Provincia item;
        Connection conexion=null;
        
        try {
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_provincias ( )}");
             r=s.executeQuery();
            
             while (r.next()){
                   li_id=r.getInt("id");
                   ls_nombre=r.getString("nombre");

                   item=new Provincia();
                   item.setId(li_id);
                   item.setNombre(ls_nombre);
                   resultados.add(item);
                  
                } 
             s.close();
           } catch (SQLException e){
                    e.getMessage();
                    return null;
           }
        
           finally{
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
           }
        
           return resultados;
          
    }    
    
    //Lista de Localidades por provincia
    public List getListaLocalidades(int idProvincia) throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id;
        String ls_nombre="",ls_cp,ls_esCapital;
        List resultados = new ArrayList();
        Localidad item;
        Connection conexion=null;        
       
        try {
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_localidades ( ? )}");
             s.setInt(1,idProvincia);
             r=s.executeQuery();
            
             while (r.next()){
                   li_id=r.getInt("id");
                   ls_nombre=r.getString("nombre");

                   item=new Localidad();
                   item.setId(li_id);
                   item.setNombre(ls_nombre);
                   resultados.add(item);
                  
                } 
             s.close();
           } catch (SQLException e){
                    e.getMessage();
                    return null;
           }
           finally{
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
           }
           return resultados;
          
    }    
    
    //Lista de locales de carnicería
    public List getListaLocalesCarniceria() throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        int li_idLocal,li_idClienteEstancia;
        String ls_nombreCliente,ls_nombreLocal,ls_responsable;
        String ls_direccion,ls_provincia,ls_localidad;
        List resultados = new ArrayList();
        LocalCarniceria item;
        Connection conexion=null;
        
        try {
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_lista_locales ( )}");
             r=s.executeQuery();
            
             while (r.next()){
                   li_idLocal=r.getInt("idLocal");
                   li_idClienteEstancia=r.getInt("idClienteEstancia");
                   ls_nombreCliente=r.getString("nombreCliente");
                   ls_nombreLocal=r.getString("nombreLocal");
                   ls_responsable=r.getString("responsable");
                   ls_direccion=r.getString("direccion");
                   ls_provincia=r.getString("provincia");
                   ls_localidad=r.getString("localidad");

                   item=new LocalCarniceria();
                   item.setIdLocal(li_idLocal);
                   item.setIdClienteEstancia(li_idClienteEstancia);
                   item.setNombreCliente(ls_nombreCliente);
                   item.setNombreComercial(ls_nombreLocal);
                   item.setResponsable(ls_responsable);
                   item.setDireccion(ls_direccion);
                   item.setProvincia(ls_provincia);
                   item.setLocalidad(ls_localidad);
                   resultados.add(item);
                  
                } 
             s.close();
           } catch (SQLException e){
                    e.getMessage();
                    return null;
           }
           finally{
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
           }
           return resultados;
          
    }    
}
