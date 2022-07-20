/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servicios;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.Conector;
import entidades.CostoEnvioProd;
import entidades.CuponDescargado;
import entidades.DireccionUsuario;
import entidades.UsuarioCliente;
import general.BeanBase;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import seguridad.Usuario;
import seguridad.loginRest;

/**
 *
 * @author rafael
 */
@Stateless
@Path("/procesos")
public class Procesos_Rest extends BeanBase{

    @GET
    @Path("/descargaCuponPromocion")
    public Response descargaCuponPromocion(@QueryParam("idEmpresa") int idEmpresa, @QueryParam("idUsuario") int idUsuario,
            @QueryParam("idPromocion") int idPromocion,@QueryParam("tipoRespuesta") String tipoRespuesta) throws SQLException {
        Usuario usuario=new Usuario();
        CallableStatement s=null;
        ResultSet r=null;
        int li_resul=0;
        String ls_codigo,ls_texto_cupon;
        java.sql.Date lda_fecha=null;
        Connection conexion=null;
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             conexion.setAutoCommit(false);
             s=conexion.prepareCall("{call sp_graba_promocion_descargada ( ? , ? , ? , ? , ? , ? , ?)}");  
             s.setInt(1,idEmpresa);
             s.setInt(2,idUsuario);
             s.setInt(3,idPromocion);
             lda_fecha=new java.sql.Date(new java.util.Date().getTime());
             s.setDate(4,lda_fecha);
             s.registerOutParameter(5,java.sql.Types.INTEGER);
             s.registerOutParameter(6,java.sql.Types.VARCHAR);
             s.registerOutParameter(7,java.sql.Types.VARCHAR);
             s.executeUpdate();
             conexion.setAutoCommit(true);
             
             //Obtengo el resultado
             li_resul=s.getInt(5);
             if (li_resul==1){
                ls_codigo=s.getString(6); 
                ls_texto_cupon=s.getString(7); 
                CuponDescargado cupon = new CuponDescargado();
                cupon.setCodigo(ls_codigo);
                cupon.setTextoGeneralCupon(ls_texto_cupon);
                
                switch (tipoRespuesta){
                case "JSON":
                  Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
                  String sb=gson.toJson(cupon);
                  return Response.ok(sb,MediaType.APPLICATION_JSON).build();
                case "XML":
                  return Response.ok(cupon,MediaType.APPLICATION_XML).build();
                default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
             }else{
                conexion.rollback();
                return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
             }

        }catch (SQLException e){
            try {    
                conexion.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
            
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
    }
    
    
    
    
    
    
    
}
