/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.Conector;
import entidades.GarronDet;
import entidades.Provincia;
import entidades.TropaCategoria;
import entidades.TropaDet;
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
@Path("/tropas")
public class Tropas_Rest extends BeanBase{
    
    //Obtiene la lista de tropas 
    //tipoConsulta: T= Todas las tropas, D= Solo las con inventario disponible
    //tipoRespuesta: formato de salida= JSON,XML
    @GET
    @Path("/getListaTropas")
    public Response ListaTropas(@QueryParam("tipoConsulta") String tipoConsulta, @QueryParam("tipoRespuesta") String tipoRespuesta) {
        System.out.println("Invoca servicio...");
        List<TropaDet> items=null;
        try {
            items = getListaTropas(tipoConsulta);
        } catch (Exception ex) {
            Logger.getLogger(Lista_Rest.class.getName()).log(Level.SEVERE, null, ex);
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
    
    //Obtiene el detalle de garrones disponible de una tropa
    //idTropa: Identificador de la tropa
    //tipoRespuesta: formato de salida= JSON,XML    
    @GET
    @Path("/getInventarioDisponibleTropa")
    public Response InventarioDisponible(@QueryParam("idTropa") int idTropa, @QueryParam("tipoRespuesta") String tipoRespuesta) {
        System.out.println("Invoca servicio...");
        List<GarronDet> items=null;
        try {
            items = getListaGarronesTropa(idTropa);
        } catch (Exception ex) {
            Logger.getLogger(Lista_Rest.class.getName()).log(Level.SEVERE, null, ex);
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
    
    //Obtiene el inventario disponible agrupado por tropa y categoria
    @GET
    @Path("/getInventarioDisponibleTropaCategoria")
    public Response InventarioDisponibleTropaCategoria(@QueryParam("tipoRespuesta") String tipoRespuesta) {
        System.out.println("Invoca servicio...");
        List<TropaCategoria> items=null;
        try {
            items = getListaTropaCategoria();
        } catch (Exception ex) {
            Logger.getLogger(Lista_Rest.class.getName()).log(Level.SEVERE, null, ex);
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
    
    //Obtiene el inventario disponible agrupado por tropa y categoria
    //Usado cuando se buscan los garrones para una entrega
    @GET
    @Path("/getInventarioDisponibleTropaCategoriaDetalle")
    public Response InventarioDisponibleTropaCategoriaDetalle(@QueryParam("idCategoria") int idCategoria,
            @QueryParam("idTropa") int idTropa,
            @QueryParam("tipoRespuesta") String tipoRespuesta) {
        System.out.println("Invoca servicio...");
        List<GarronDet> items=null;
        try {
            items = getListaTropaCategoriaDetalle(idCategoria,idTropa);
        } catch (Exception ex) {
            Logger.getLogger(Lista_Rest.class.getName()).log(Level.SEVERE, null, ex);
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
    //Lista de tropas 
    public List<TropaDet> getListaTropas(String tipoConsulta) throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id,li_num_tropa;
        String ls_tipo="",ls_estado="",ls_productor="",ls_deposito="",ls_comisionista="";
        Date lda_fec_ing;
        double ld_disponible,ld_entregado;
        List resultados = new ArrayList();
        TropaDet item;
        Connection conexion=null;
        
        try {
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_lista_tropas ( ? )}");
             s.setString(1,tipoConsulta);
             r=s.executeQuery();
            
             while (r.next()){
                   li_id=r.getInt("id");
                   li_num_tropa=r.getInt("numeroTropa");
                   ls_tipo=r.getString("tipo");
                   lda_fec_ing=r.getDate("fec_ing");
                   ls_productor=r.getString("productor");
                   ls_comisionista=r.getString("comisionista");
                   ls_deposito=r.getString("deposito");
                   ld_disponible=r.getDouble("disponible");
                   ld_entregado=r.getDouble("entregado");
                   
                   item=new TropaDet();
                   item.setId(li_id);
                   item.setNumeroTropa(li_num_tropa);
                   item.setTipo(ls_tipo);
                   item.setFec_ing(lda_fec_ing);
                   item.setProductor(ls_productor);
                   item.setComisionista(ls_comisionista);
                   item.setDeposito(ls_deposito);
                   item.setDisponible(ld_disponible);
                   item.setEntregado(ld_entregado);
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
    
    //Lista de garrones disponible de una tropa
    public List<GarronDet> getListaGarronesTropa(int idTropa) throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id,li_idGarron,li_numGarron;
        String ls_tipo="",ls_estado="";
        double ld_kilos;
        List resultados = new ArrayList();
        GarronDet item;
        Connection conexion=null;
        
        try {
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_inventario_tropa_disponible ( ? )}");
             s.setInt(1,idTropa);
             r=s.executeQuery();
            
             while (r.next()){
                   li_id=r.getInt("id");
                   li_idGarron=r.getInt("idGarron");
                   li_numGarron=r.getInt("numGarron");
                   ls_tipo=r.getString("tipo");
                   ld_kilos=r.getDouble("kilos");
                   ls_estado=r.getString("estado");
                   
                   item=new GarronDet();
                   item.setId(li_id);
                   item.setIdGarron(li_idGarron);
                   item.setNumGarron(li_numGarron);
                   item.setTipo(ls_tipo);
                   item.setKilos(ld_kilos);
                   item.setEstado(ls_estado);
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
    
    //Lista de tropas y categorias disponibles
    public List<TropaCategoria> getListaTropaCategoria() throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id,li_idTropa;
        String ls_nombre_categoria="",ls_productor="",ls_numero_tropa;
        int li_cantidad;
        double ld_inventario_actual;
        Date lda_fecha_faena = null;
        List resultados = new ArrayList();
        TropaCategoria item;
        Connection conexion=null;
        
        try {
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_inventario_tropas_categoria ( )}");
             r=s.executeQuery();
            
             while (r.next()){
                   li_id=r.getInt("idCategoria");
                   ls_nombre_categoria=r.getString("nombreCategoria");
                   li_idTropa=r.getInt("idTropa");
                   ls_numero_tropa=r.getString("numeroTropa");
                   ls_productor=r.getString("productor");
                   lda_fecha_faena=r.getDate("fecha_faena");
                   li_cantidad=r.getInt("cantidad");
                   ld_inventario_actual=r.getDouble("inventarioActual");
                   
                   item=new TropaCategoria();
                   item.setIdCategoria(li_id);
                   item.setNombreCategoria(ls_nombre_categoria);
                   item.setIdTropa(li_idTropa);
                   item.setNumeroTropa(ls_numero_tropa);
                   item.setProductor(ls_productor);
                   item.setFecha_faena(lda_fecha_faena);
                   item.setCantidad(li_cantidad);
                   item.setInventarioActual(ld_inventario_actual);
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
    
    
    //Lista de garrones de una tropa y categoría específica
    public List<GarronDet> getListaTropaCategoriaDetalle(int idCategoria,int idTropa) throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id,li_idGarron,li_numGarron,li_idTropa,li_idCategoria;
        String ls_tipo="",ls_estado="",ls_categoria;
        double ld_kilos;
        int li_idProducto,li_idUnidad;
        List resultados = new ArrayList();
        GarronDet item;
        Connection conexion=null;
        
        try {
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_inventario_tropa_categoria_detalle ( ? , ? )}");
             s.setInt(1,idCategoria);
             s.setInt(2,idTropa);
             r=s.executeQuery();
            
             while (r.next()){
                   li_id=r.getInt("id");
                   li_idTropa=r.getInt("idTropa");
                   li_idGarron=r.getInt("idGarron");
                   li_numGarron=r.getInt("numGarron");
                   ls_tipo=r.getString("tipo");
                   ld_kilos=r.getDouble("kilos");
                   li_idProducto=r.getInt("idProducto");
                   li_idUnidad=r.getInt("idUnidad");
                   li_idCategoria=r.getInt("idCategoria");
                   ls_categoria=r.getString("categoria");
                   ls_estado=r.getString("estado");
                   
                   item=new GarronDet();
                   item.setId(li_id);
                   item.setIdTropa(idTropa);
                   item.setIdGarron(li_idGarron);
                   item.setNumGarron(li_numGarron);
                   item.setTipo(ls_tipo);
                   item.setIdCategoria(li_idCategoria);
                   item.setKilos(ld_kilos);
                   item.setIdProducto(li_idProducto);
                   item.setIdUnidad(li_idUnidad);
                   item.setCategoria(ls_categoria);
                   item.setEstado(ls_estado);
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


