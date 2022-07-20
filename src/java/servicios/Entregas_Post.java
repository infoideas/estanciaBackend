/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import database.Conector;
import entidades.Entrega;
import entidades.EntregaDetalle;
import entidades.EntregaRealizada;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Propietario
 */

@Stateless
@Path("/entregas")
public class Entregas_Post extends BeanBase {
    Connection conexion=null;
    
    @POST    
    @Path("/realizarEntrega")
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response realizarEntrega(Entrega entrega) throws SQLException {
        EntregaRealizada entregaRealizada= new EntregaRealizada();
        int li_idEntrega,li_idRemito,li_idStock;
        boolean lb_resultado=false;
        
        try {
            Conector conector = new Conector();  
            conexion = conector.connect("estancia");
            conexion.setAutoCommit(false);
            
            //Grabo la entrega
            li_idEntrega=grabaEntrega(entrega.getIdLocal(),entrega.getIdUsuarioEntrega(),entrega.getTotalKilos(),
                                      entrega.getValorFlete(),entrega.getObservaciones());
            List<EntregaDetalle> lista= new ArrayList<EntregaDetalle>();
            if (li_idEntrega == 0 ){
                conexion.rollback();
                conexion.setAutoCommit(true);
                entregaRealizada.setIdEntrega(0);
                entregaRealizada.setResultado("Error al grabar la entrega");
                return Response.ok(entregaRealizada,MediaType.APPLICATION_JSON).build();
            }
            
            //Grabo el detalle de la entrega
            lista=entrega.getListaDetalle();
            for ( EntregaDetalle detalle : lista) {
                    //Actualizo estdo de garrón
                    int li_resultado;
                    //Actualizo estado del garrón
                    //Devuelve 1 si se trata de un garrón ya existente
                    //o el id del registro insertado en caso de ser una fracción del garrón
                    //que se insertó en el inventario
                    li_resultado=actualizaEstadoGarron(detalle.getIdGarronInv(),detalle.getEstado(),
                            detalle.getIdTropa(),detalle.getIdGarron(),detalle.getTipo(),detalle.getKilos());
                    if(li_resultado==0){
                        conexion.rollback();
                        conexion.setAutoCommit(true);
                        entregaRealizada.setIdEntrega(0);   
                        entregaRealizada.setResultado("Error al actualizar estado");
                        System.out.println("Error al actualizar estado");
                        return Response.ok(entregaRealizada,MediaType.APPLICATION_JSON).build();
                    }
                    
                    if (li_resultado > 1)
                        detalle.setIdGarronInv(li_resultado);

                    //Grabo el registro en el detalle de la entrega
                    if (detalle.getEstado().equals("E")){
                        lb_resultado=grabaEntregaDetalle(li_idEntrega,detalle.getIdGarronInv(),detalle.getTipo(),detalle.getKilos());
                        if(lb_resultado==false){
                            conexion.rollback();
                            conexion.setAutoCommit(true);
                            entregaRealizada.setIdEntrega(0);   
                            entregaRealizada.setResultado("Error al grabar detalle de la entrega");
                            return Response.ok(entregaRealizada,MediaType.APPLICATION_JSON).build();
                        }
                    }
                    
            }
            
            //Grabo remito de venta
            li_idRemito=grabaRemito(entrega.getIdCliente(),entrega.getIdLocal(),entrega.getIdUsuarioEntrega(),entrega.getObservaciones());
            if (li_idRemito == 0 ){
                conexion.rollback();
                conexion.setAutoCommit(true);
                entregaRealizada.setIdEntrega(0);
                entregaRealizada.setResultado("Error al grabar cabecera de remito de venta");
                return Response.ok(entregaRealizada,MediaType.APPLICATION_JSON).build();
            }
                
            //Grabo detalle de remito de venta
            for ( EntregaDetalle detalle : lista) {
                if (detalle.getEstado().equals("E")){
                    //Si es una fracción de la media res entonces obtengo el producto relacionado
                    int li_idProductoRel=0;
                    if (detalle.getTipo().equals("PI") || detalle.getTipo().equals("DE") ){
                        li_idProductoRel=getProductoRelacionado(detalle.getIdCategoria(),detalle.getTipo());
                        if (li_idProductoRel==0){
                            conexion.rollback();
                            conexion.setAutoCommit(true);
                            entregaRealizada.setIdEntrega(0);
                            entregaRealizada.setResultado("Error al obtener producto relacionado");
                            return Response.ok(entregaRealizada,MediaType.APPLICATION_JSON).build();
                        }
                    }
                    
                    else
                        li_idProductoRel=detalle.getIdProductoRel();
                    
                    lb_resultado=grabaRemitoDetalle(li_idRemito,detalle.getIdGarronInv(),li_idProductoRel,detalle.getIdUnidad(),detalle.getKilos());
                    if(lb_resultado==false){
                        conexion.rollback();
                        conexion.setAutoCommit(true);
                        entregaRealizada.setIdEntrega(0);
                        entregaRealizada.setResultado("Error al grabar cabecera de remito de venta");
                        return Response.ok(entregaRealizada,MediaType.APPLICATION_JSON).build();
                    }
                }
            }
            
            //Grabo mov. de stock
            final String TIPO_COMPROBANTE_REL="R";
            li_idStock=grabaMovStock(entrega.getIdUsuarioEntrega(),li_idRemito,TIPO_COMPROBANTE_REL,entrega.getObservaciones());
            if (li_idStock == 0 ){
                conexion.rollback();
                conexion.setAutoCommit(true);
                entregaRealizada.setIdEntrega(0);
                entregaRealizada.setResultado("Error al grabar cabecera de movimiento de stock");
                return Response.ok(entregaRealizada,MediaType.APPLICATION_JSON).build();
            }
            
            //Grabo detalle de movimiento de stock
            for ( EntregaDetalle detalle : lista) {
                if (detalle.getEstado().equals("E")){
                    lb_resultado=grabaMovStockDetalle(li_idStock,detalle.getIdProductoRel(),detalle.getIdUnidad(),detalle.getKilos());
                    if(lb_resultado==false){
                        conexion.rollback();
                        conexion.setAutoCommit(true);
                        entregaRealizada.setIdEntrega(0);
                        entregaRealizada.setResultado("Error al grabar cabecera de movimiento de stock");
                        return Response.ok(entregaRealizada,MediaType.APPLICATION_JSON).build();
                    }
                }
            }
            
            conexion.commit();
            conexion.setAutoCommit(true);
            entregaRealizada.setIdEntrega(li_idEntrega);
            entregaRealizada.setResultado("Ok");
            return Response.ok(entregaRealizada,MediaType.APPLICATION_JSON).build();
                
        } catch (SQLException ex) {
            Logger.getLogger(Entregas_Post.class.getName()).log(Level.SEVERE, null, ex);
            entregaRealizada.setIdEntrega(0);
            entregaRealizada.setResultado("Error al realizar la entrega");
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        } catch (Exception e) {
            Logger.getLogger(Entregas_Post.class.getName()).log(Level.SEVERE, null, e);
            entregaRealizada.setIdEntrega(0);
            entregaRealizada.setResultado("Error al realizar la entrega");
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();    
        }
        finally{
            if (conexion != null) conexion.close();   
        }

    }
    
    //Graba la cabecera de la entrega
    public int grabaEntrega(int idLocal,int idUsuarioEntrega,double totalKilos,double valorFlete,
            String observaciones) throws SQLException{
        
        CallableStatement s=null;
        ResultSet r=null;
        int li_idEntrega=0;
        Date lda_fec_carga = new Date(); 
        
        try {      
             System.out.println("Entra a graba entrega");
             s=conexion.prepareCall("{call sp_graba_entrega ( ? , ? , ? , ? , ? , ? , ? , ? )}"); 
             s.setInt(1,idLocal); 
             s.setTimestamp(2,new java.sql.Timestamp(lda_fec_carga.getTime()));
             s.setInt(3,idUsuarioEntrega);
             s.setDate(4,new java.sql.Date(lda_fec_carga.getTime()));             
             s.setDouble(5,totalKilos);
             s.setDouble(6,valorFlete);
             s.setString(7,observaciones);
             s.registerOutParameter(8,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el id del registro insertado
             li_idEntrega=s.getInt(8);

        }catch (SQLException e){
            System.out.println("Error graba entrega SQL: " + e.getMessage() );
        }catch (Exception e){
            System.out.println("Error graba entrega: " + e.getMessage() );
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
        }
        return li_idEntrega;
        
    }
    
    //Graba detalle de la entrega
    public boolean grabaEntregaDetalle(int idEntrega,int idGarron,String tipo,double kilos) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        int li_idDet=0;
        boolean lb_resultado=false;
        System.out.println("Graba detalle de entrega : " + idGarron );

        try {        
             s=conexion.prepareCall("{call sp_graba_entrega_detalle ( ? , ? , ? , ? , ? )}"); 
             s.setInt(1,idEntrega); 
             s.setInt(2,idGarron);
             s.setString(3,tipo);
             s.setDouble(4,kilos);
             s.registerOutParameter(5,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el id del registro insertado
             li_idDet=s.getInt(5);

             if (li_idDet > 0){
                lb_resultado=true;
            }
            else {
                //No se pudo actualizar 
                lb_resultado=false;
            }
        }catch (SQLException e){
            System.out.println("Error graba pedido detalle SQL: " + e.getMessage() );
            lb_resultado=false;
        }catch (Exception e){
            System.out.println("Error graba pedido detalle: " + e.getMessage() );
            lb_resultado=false;
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
        }
        return lb_resultado;
    }
    
    //Actualiza estado del garrón
    public int actualizaEstadoGarron(int idInventario,String estado,int idTropa,int idGarron,
                String tipo,double kilos) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        int li_Resultado=0;
        System.out.println("Actualiza estado del garrón");

        try {        
             s=conexion.prepareCall("{call sp_actualiza_garron_inventario ( ? , ? , ? , ? , ? , ? , ? )}"); 
             s.setInt(1,idInventario); 
             s.setString(2,estado);
             s.setInt(3,idTropa);
             s.setInt(4,idGarron);
             s.setString(5,tipo);
             s.setDouble(6,kilos);
             s.registerOutParameter(7,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el resultado
             li_Resultado=s.getInt(7);
             System.out.println("Resultado:" + li_Resultado);

        }catch (SQLException e){
            System.out.println("Error al actualizar estado SQL: " + e.getMessage() );
        }catch (Exception e){
            System.out.println("Error al actualizar estado SQL: " + e.getMessage() );
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
        }
        System.out.println("Retorna: " + li_Resultado);
        return li_Resultado;
    }
    
    //Graba la cabecera del remito
    public int grabaRemito(int idCliente,int idLocal,int idUsuario,
            String observaciones) throws SQLException{
        
        CallableStatement s=null;
        ResultSet r=null;
        int li_idRemito=0;
        Date lda_fec_carga = new Date(); 
        final String REMITO_PENDIENTE="0";
        final String TIPO_MOV="RE";
                
        try {
             s=conexion.prepareCall("{call sp_graba_remito ( ? , ? , ? , ? , ? , ? , ? , ? , ? )}"); 
             s.setString(1,TIPO_MOV); 
             s.setInt(2,idCliente);
             s.setInt(3,idLocal);
             s.setInt(4,idUsuario);
             s.setTimestamp(5,new java.sql.Timestamp(lda_fec_carga.getTime()));
             s.setDate(6,new java.sql.Date(lda_fec_carga.getTime()));
             s.setString(7,observaciones);
             s.setString(8,REMITO_PENDIENTE);
             s.registerOutParameter(9,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el id del registro insertado
             li_idRemito=s.getInt(8);

        }catch (SQLException e){
            System.out.println("Error graba entrega SQL: " + e.getMessage() );
        }catch (Exception e){
            System.out.println("Error graba remito: " + e.getMessage() );
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
        }
        return li_idRemito;
        
    }
    
    //Graba detalle del remito
    public boolean grabaRemitoDetalle(int idRemito,int idGarronInventario,int idProducto,int idUnidad,double cantidad) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        int li_idRem=0;
        boolean lb_resultado=false;

        try {        
             s=conexion.prepareCall("{call sp_graba_remito_detalle ( ? , ? , ? , ? , ? , ? )}"); 
             s.setInt(1,idRemito); 
             s.setInt(2,idGarronInventario);
             s.setInt(3,idProducto);
             s.setInt(4,idUnidad);
             s.setDouble(5,cantidad);
             s.registerOutParameter(6,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el id del registro insertado
             li_idRem=s.getInt(6);

             if (li_idRem > 0){
                lb_resultado=true;
            }
            else {
                //No se pudo actualizar 
                lb_resultado=false;
            }
        }catch (SQLException e){
            System.out.println("Error graba remito detalle SQL: " + e.getMessage() );
            lb_resultado=false;
        }catch (Exception e){
            System.out.println("Error graba remito detalle: " + e.getMessage() );
            lb_resultado=false;
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
        }
        return lb_resultado;
    }
    
    
    //Graba la cabecera del movimiento de stock
    public int grabaMovStock(int idUsuario,int idComprobanteRel,
            String tipoComprobanteRel,String observaciones) throws SQLException{
        
        CallableStatement s=null;
        ResultSet r=null;
        int li_idStock=0;
        Date lda_fec_carga = new Date(); 
        final String TIPO_MOV="I";
                
        try {
             s=conexion.prepareCall("{call sp_graba_mov_stock ( ? , ? , ? , ? , ? , ? , ? )}"); 
             s.setString(1,TIPO_MOV); 
             s.setTimestamp(2,new java.sql.Timestamp(lda_fec_carga.getTime()));
             s.setInt(3,idUsuario);
             s.setInt(4,idComprobanteRel);
             s.setString(5,tipoComprobanteRel);
             s.setString(6,observaciones);
             s.registerOutParameter(7,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el id del registro insertado
             li_idStock=s.getInt(7);

        }catch (SQLException e){
            System.out.println("Error graba entrega SQL: " + e.getMessage() );
        }catch (Exception e){
            System.out.println("Error graba remito: " + e.getMessage() );
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
        }
        return li_idStock;
        
    }
    
    //Graba detalle del stock
    public boolean grabaMovStockDetalle(int idStockRealizado,int idProducto,int idUnidad,double cantidad) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        int li_idStock=0;
        boolean lb_resultado=false;

        try {        
             s=conexion.prepareCall("{call sp_graba_mov_stock_detalle ( ? , ? , ? , ? , ? )}"); 
             s.setInt(1,idStockRealizado); 
             s.setInt(2,idProducto);
             s.setInt(3,idUnidad);
             s.setDouble(4,cantidad);
             s.registerOutParameter(5,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el id del registro insertado
             li_idStock=s.getInt(5);

             if (li_idStock > 0){
                lb_resultado=true;
            }
            else {
                //No se pudo actualizar 
                lb_resultado=false;
            }
        }catch (SQLException e){
            System.out.println("Error graba stock detalle SQL: " + e.getMessage() );
            lb_resultado=false;
        }catch (Exception e){
            System.out.println("Error graba stock detalle: " + e.getMessage() );
            lb_resultado=false;
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
        }
        return lb_resultado;
    }
    
    public int getProductoRelacionado(int idCategoria,String tipo) throws SQLException{
        int li_idProducto = 0;
        CallableStatement s=null;
        ResultSet r=null;

        Connection conexion=null;
        try {     
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_producto_relacionado ( ? , ? , ? )}");  
             s.setInt(1,idCategoria);
             s.setString(2,tipo);
             s.registerOutParameter(3,java.sql.Types.INTEGER);
             r=s.executeQuery();
             
             //Obtengo el id del producto relacionado
             li_idProducto=s.getInt(3);

        }catch (SQLException e){
            System.out.print("Error: " + e.getMessage());
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        return li_idProducto;
    }
    
}
