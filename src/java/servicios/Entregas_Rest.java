/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.Conector;
import entidades.Entrega;
import entidades.EntregaDetalle;
import entidades.MiEntrega;
import general.BeanBase;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
 * @author Propietario
 */
@Stateless
@Path("/entregasRest")
public class Entregas_Rest extends BeanBase{

    //Lista de entregas realizadas por un usuario
    //Si el idUsuario=0 muestra todas las entregas
    @GET
    @Path("/getListaEntregas")
    public Response ListaEntregas(@QueryParam("idUsuario") int idUsuario, @QueryParam("tipoRespuesta") String tipoRespuesta) {
        System.out.println("Invoca servicio...");
        List<MiEntrega> items=null;
        try {
            items = getListaEntregas(idUsuario);
        } catch (Exception ex) {
            Logger.getLogger(Entregas_Rest.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        switch (tipoRespuesta){
              case "JSON":
                  Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                  String sb=gson.toJson(items);
                  return Response.ok(sb,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(items,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }     
       
    }

    @GET
    @Path("/getDatosEntrega") 
    public Response getDatosEntrega(@QueryParam("idEntrega") int idEntrega,@QueryParam("tipoRespuesta") String tipoRespuesta){
        
    Entrega entrega=null;
    try {
        entrega = getEntrega(idEntrega);
    } catch (Exception ex) {
        Logger.getLogger(Empresa_Rest.class.getName()).log(Level.SEVERE, null, ex);
    }
        
    switch (tipoRespuesta){
              case "JSON":
                  Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                  String sb=gson.toJson(entrega);
                  return Response.ok(sb,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(entrega,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
        
    }            
    
    
    public List<MiEntrega> getListaEntregas(int idUsuario) throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id,li_idLocal,li_idCliente,li_idUsuario;
        String ls_nombreLocal="",ls_nombreCliente="",ls_nombreUsuario;
        Date lda_fec_ent;
        double ld_totalKilos,ld_valorFlete;
        MiEntrega item;
        List resultados = new ArrayList();
        Connection conexion=null;
        
        try {
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_entregas ( ? )}");
             s.setInt(1,idUsuario);
             r=s.executeQuery();
            
             while (r.next()){
                   li_id=r.getInt("id");
                   li_idLocal=r.getInt("idLocal");
                   ls_nombreLocal=r.getString("nombreLocal");
                   li_idCliente=r.getInt("idClienteEstancia");
                   ls_nombreCliente=r.getString("nombreCliente");
                   li_idUsuario=r.getInt("idUsuarioEntrega");
                   ls_nombreUsuario=r.getString("nombreUsuario");
                   lda_fec_ent=r.getDate("fec_ent");
                   ld_totalKilos=r.getDouble("totalKilos");
                   ld_valorFlete=r.getDouble("valorFlete");
                   
                   item=new MiEntrega();
                   item.setId(li_id);
                   item.setIdLocal(li_idLocal);
                   item.setNombreLocal(ls_nombreLocal);
                   item.setIdCliente(li_idCliente);
                   item.setNombreCliente(ls_nombreCliente);
                   item.setIdUsuarioEntrega(li_idUsuario);
                   item.setNombreUsuarioEntrega(ls_nombreUsuario);
                   item.setFec_ent(lda_fec_ent);
                   item.setTotalKilos(ld_totalKilos);
                   item.setValorFlete(ld_valorFlete);
                   resultados.add(item);
                  
                } 
             s.close();
           } catch (SQLException e){
                    System.out.println("Error SQL: " + e.getMessage());
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
    
    //Obtiene una entrega por su id
    public Entrega getEntrega(int idEntrega) throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id,li_idLocal,li_idCliente,li_idUsuarioEntrega,li_idUsuarioRecibe;
        String ls_nombreLocal="",ls_nombreCliente="",ls_nombreUsuarioEntrega,ls_nombreUsuarioRecibe;
        Date lda_fec_carga,lda_fec_ent,lda_fec_rec;
        String ls_estado,ls_observaciones;
        double ld_totalKilos,ld_valorFlete;
        double ld_cantidadGarrones,ld_cantidadPiernas,ld_cantidadDelanteros;
        Entrega entrega = null;
        List resultados = new ArrayList();
        Connection conexion=null;
        
        try {
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_entrega ( ? )}");
             s.setInt(1,idEntrega);
             r=s.executeQuery();
            
             if (r.next()){
                   li_id=r.getInt("id");
                   li_idLocal=r.getInt("idLocal");
                   ls_nombreLocal=r.getString("nombreLocal");
                   li_idCliente=r.getInt("idCliente");
                   ls_nombreCliente=r.getString("nombreCliente");
                   lda_fec_carga=r.getDate("fec_carga");
                   li_idUsuarioEntrega=r.getInt("idUsuarioEntrega");
                   ls_nombreUsuarioEntrega=r.getString("nombreUsuarioEntrega");
                   lda_fec_ent=r.getDate("fec_ent");
                   li_idUsuarioRecibe=r.getInt("idUsuarioRecibe");
                   ls_nombreUsuarioRecibe=r.getString("nombreUsuarioRecibe");
                   lda_fec_rec=r.getDate("fec_rec");
                   ls_estado=r.getString("estado");
                   ld_cantidadGarrones=r.getDouble("cantidad_garrones");
                   ld_cantidadPiernas=r.getDouble("cantidad_piernas");
                   ld_cantidadDelanteros=r.getDouble("cantidad_delanteros");
                   ld_totalKilos=r.getDouble("totalKilos");
                   ld_valorFlete=r.getDouble("valorFlete");
                   ls_observaciones=r.getString("observaciones");
                   
                   entrega=new Entrega();
                   entrega.setId(li_id);
                   entrega.setFec_carga(lda_fec_carga);
                   entrega.setIdLocal(li_idLocal);
                   entrega.setNombreLocal(ls_nombreLocal);
                   entrega.setIdCliente(li_idCliente);
                   entrega.setNombreCliente(ls_nombreCliente);
                   entrega.setIdUsuarioEntrega(li_idUsuarioEntrega);
                   entrega.setNombreUsuarioEntrega(ls_nombreUsuarioEntrega);
                   entrega.setFec_ent(lda_fec_ent);
                   entrega.setFec_rec(lda_fec_rec);
                   entrega.setIdUsuarioRecibe(li_idUsuarioRecibe);
                   entrega.setNombreUsuarioRecibe(ls_nombreUsuarioRecibe);
                   entrega.setCantidadGarrones(ld_cantidadGarrones);
                   entrega.setCantidadPiernas(ld_cantidadPiernas);
                   entrega.setCantidadDelanteros(ld_cantidadDelanteros);
                   entrega.setTotalKilos(ld_totalKilos);
                   entrega.setValorFlete(ld_valorFlete);
                   entrega.setObservaciones(ls_observaciones);
                   entrega.setListaDetalle(getDetalleEntrega(idEntrega,conexion));

                  
                } 
             s.close();
           } catch (SQLException e){
                    System.out.println("Error SQL: " + e.getMessage());
                    e.getMessage();
                    return null;
           }
           finally{
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
           }
           return entrega;
          
    }    
    
    //Obtiene el detalle de una entrega
    public List<EntregaDetalle> getDetalleEntrega(int idEntrega,Connection conexion) throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id,li_idEntrega,li_idGarronInv,li_idGarron,li_numGarron;
        String ls_tipo="";
        double ld_kilos;
        EntregaDetalle item;
        List<EntregaDetalle> resultados=new ArrayList<EntregaDetalle>();
        
        try {
             s=conexion.prepareCall("{call sp_get_entrega_detalle ( ? )}");
             s.setInt(1,idEntrega);
             r=s.executeQuery();
            
             while (r.next()){
                   li_id=r.getInt("id");
                   li_idEntrega=r.getInt("idEntrega");
                   li_idGarronInv=r.getInt("idGarronInv");
                   li_idGarron=r.getInt("idGarron");
                   li_numGarron=r.getInt("numGarron");
                   ls_tipo=r.getString("tipo");
                   ld_kilos=r.getDouble("kilos");
                   
                   item=new EntregaDetalle();
                   item.setId(li_id);
                   item.setIdGarronInv(li_idGarronInv);
                   item.setIdGarron(li_idGarron);
                   item.setNumGarron(li_numGarron);
                   item.setTipo(ls_tipo);
                   item.setIdEntrega(idEntrega);
                   item.setKilos(ld_kilos);
                   item.setEstado("E");
                   resultados.add(item);
                  
                } 
             s.close();
           } catch (SQLException e){
                    System.out.println("Error SQL: " + e.getMessage());
                    e.getMessage();
                    return null;
           }
           finally{
            if (r != null) {r.close();}
            if (s != null) {s.close();}
           }
           return resultados;
          
    }    
    
    
    
}
