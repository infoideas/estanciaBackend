/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seguridad;
import database.Conector;
import general.BeanBase;
import general.Mail;
import general.StringMD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import seguridad.UsuarioController;

/**
 *
 * @author rafael
 */

@Stateless
@Path("/seguridad")
public class loginRest extends BeanBase{
   
    //Valida el login del usuario cliente
    @GET
    @Path("/validaLoginUsuarioCliente") 
    public Response validaLoginUsuarioCliente(@QueryParam("nombreUsuario") String nombreUsuario,@QueryParam("clave") String clave,
            @QueryParam("tipoRespuesta") String tipoRespuesta){
        
        UsuarioController usuariocontroller=new UsuarioController();
        Usuario usuario=null;
        try {     
            usuario=usuariocontroller.validaLoginUsuarioCliente(nombreUsuario, clave);
        } catch (Exception ex) {
            Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        switch (tipoRespuesta){
              case "JSON":
                  return Response.ok(usuario,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(usuario,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
        
    }    
    
    //Valida el login del usuario cliente
    @GET
    @Path("/obtieneDatosUsuarioCliente") 
    public Response obtieneDatosUsuario(@QueryParam("nombreUsuario") String nombreUsuario,
            @QueryParam("tipoRespuesta") String tipoRespuesta){
        
        UsuarioController usuariocontroller=new UsuarioController();
        Usuario usuario=null;
        try {     
            usuario=usuariocontroller.getDatosUsuarioCliente(nombreUsuario);
        } catch (Exception ex) {
            Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        switch (tipoRespuesta){
              case "JSON":
                  return Response.ok(usuario,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(usuario,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
        
    }    
    
    @GET
    @Path("/activaUsuarioCliente")
    public Response activaUsuarioCliente(@QueryParam("idUsuario") int idUsuario) throws SQLException {
        Usuario usuario=new Usuario();
        CallableStatement s=null;
        ResultSet r=null;
        int li_resul=0;
        Connection conexion=null;
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("administracion");
             conexion.setAutoCommit(false);
             s=conexion.prepareCall("{call sp_activa_usuario_cliente ( ? , ? )}");  
             s.setInt(1,idUsuario);
             s.registerOutParameter(2,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el resultado
             li_resul=s.getInt(2);
             if (li_resul==1){
                conexion.commit();
                conexion.setAutoCommit(true);
                usuario.setEstado("ACTIVADO");
             }else{
                conexion.rollback();
                usuario.setEstado("ERROR");
                usuario.setObservaciones("Usuario no se pudo activar" );
             }
             

        }catch (SQLException e){
            try {    
                conexion.rollback();
                usuario.setEstado("ERROR");
                usuario.setObservaciones("Error: " + e.getMessage() );
            } catch (SQLException ex) {
                Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
        String ls_html_bienvenida="<html><head><title><BizApp</title><meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1'></head><body><p align='center'><table width='900' height='96' border='0' align='center' cellpadding='5' cellspacing='5' bordercolor='#000000' bgcolor='#FFFFFF'>"
                + "<tr><td><div align='center'><a href='http://www.infoideas.com.ar/infoideas'><img src='http://www.infoideas.com.ar/infoideas/imagenes/logo.jpg' width='528' height='234' border='0'></a></p>" 
                + "</div><p align='center'><font color='#009933' size='4' face='Tahoma'>Bienvenido " 
                + "a BizApp su app de negocios. Su usuario ha sido activado</font></p><p align='center'><a href='http://www.infoideas.com.ar/infoideas' target='_blank'><font color='#009933' size='3' face='Tahoma'>"
                + "Iniciar sesi&oacute;n</font></a></p><p align='center'>&nbsp;</p></td></tr></table></p><div align='center'></div><p align='center'></body></html>";

        return Response.ok(ls_html_bienvenida, MediaType.TEXT_HTML).build();
        
    }
    
    
    //Valida el login del usuario administrador
    @GET
    @Path("/validaLoginUsuarioAdmin") 
    public Response validaLoginUsuarioAdmin(@QueryParam("nombreUsuario") String nombreUsuario,@QueryParam("clave") String clave,
            @QueryParam("tipoUsuario") String tipoUsuario,        
            @QueryParam("tipoRespuesta") String tipoRespuesta){
        
        UsuarioController usuariocontroller=new UsuarioController();
        UsuarioAdmin usuario=null;
        try {     
            usuario=usuariocontroller.validaLoginUsuarioAdmin(nombreUsuario,clave,tipoUsuario);
            System.out.println("Usuario devuelto: " + usuario.getNombreUsuario());
        } catch (Exception ex) {
            Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        switch (tipoRespuesta){
              case "JSON":
                  return Response.ok(usuario,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(usuario,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
        
    }    
    
    //Manda un mail con el link para cambiar la clave del usuario administrador
    @GET
    @Path("/solicitaCambioClaveAdmin")
    public Response actualizaClave(@QueryParam("nombreUsuario") String nombreUsuario,@QueryParam("tipoRespuesta") String tipoRespuesta) {
        String ls_resul;
        String ls_palabra_secreta="DUENDEJLM";
        String ls_usuario_enc="";

        //Concateno el nombre de usuario con la palabra secreta y la encripto en MD5
        StringMD md5=new StringMD();       
        ls_usuario_enc=nombreUsuario.concat(ls_palabra_secreta);
        ls_usuario_enc=md5.getStringMessageDigest(ls_usuario_enc,"MD5");
        
        String linkCambioClave=URI_RESETEAR_CLAVE + "?nombreUsuarioEnc=" + ls_usuario_enc;
        
        String ls_html_cambio_clave="<html><head><title>Estancia El Duende</title><meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1'></head><body><p align='center'><table width='900' height='96' border='0' align='center' cellpadding='5' cellspacing='5' bordercolor='#000000' bgcolor='#FFFFFF'>"
        + "<tr><td><div align='center'><a href='http://www.estanciaelduende.com.ar'><img src='http://www.estanciaelduende.com.ar/img/logo.jpg' width='528' height='234' border='0'></a></p>" 
        + "</div><p align='center'><font color='#009933' size='4' face='Tahoma'>Bienvenido " 
        + "a Estancia El Duende. Hemos tenido una solicitud de cambio de clave de su cuenta</font></p><p align='center'><a href='" + linkCambioClave +
        "' target='_blank'><font color='#009933' size='3' face='Tahoma'>"
        + "Por favor haga click en este enlace para cambiar su clave</font></a></p><p align='center'>&nbsp;</p></td></tr></table></p><div align='center'></div><p align='center'></body></html>";
        
        //Valido que exista el usuario
        UsuarioController usuariocontroller=new UsuarioController();    
        UsuarioAdmin usuario=new UsuarioAdmin();
        try {
            int li_cod_usuario=usuariocontroller.existeUsuarioAdmin(nombreUsuario);
            if (li_cod_usuario == 0){
              //No existe usuario con ese mail
              usuario.setEstado("NO EXISTE");
              usuario.setObservaciones("No existe un usuario con ese email");  
            }
            else{
                //Usuario existe entonces mando el mail
                String mensaje = ls_html_cambio_clave;
                Mail mail=null;
                try {
                    mail = new Mail();
                } catch (Exception ex) {
                    Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
                }
                mail.setEmaildestino(nombreUsuario);
                ls_resul=mail.enviarMail("Cambio de clave", mensaje);
 
                usuario.setNombreUsuario(nombreUsuario);
                usuario.setEstado("Ok");
                usuario.setObservaciones("Solicitud enviada");
                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        switch (tipoRespuesta){
              case "JSON":
                  return Response.ok(usuario,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(usuario,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } 
        
    }
    
    //Modifica la clave del usuario administrativo
    @GET
    @Path("/cambiaClaveAdmin")
    public Response cambiaClaveAdmin(@QueryParam("nombreUsuario") String nombreUsuario,
            @QueryParam("claveUsuarioNueva") String claveUsuarioNueva,
            @QueryParam("tipoRespuesta") String tipoRespuesta) throws SQLException {
    
            UsuarioAdmin usuario=new UsuarioAdmin();
            Connection conexion=null;
            CallableStatement s=null;
            ResultSet r=null;
            int li_resultado=0;
            String ls_resul;
            boolean lb_error=false;
            
            //Palabra secreta
            String ls_palabra_secreta="DUENDEJLM";
            //Encripto la clave en MD5    
            StringMD md5=new StringMD();                
            String ls_claveUsuarioNueva_MD5= md5.getStringMessageDigest(claveUsuarioNueva,"MD5");
                
            try {        
                 Conector conector = new Conector();  
                 conexion = conector.connect("administracion");
                 conexion.setAutoCommit(false);
                 s=conexion.prepareCall("{call sp_cambia_clave_usuario_admin ( ? , ? , ? , ? )}"); 
                 s.setString(1,nombreUsuario);     
                 s.setString(2,ls_claveUsuarioNueva_MD5); //Clave nueva encriptada   
                 s.setString(3,ls_palabra_secreta);     
                 s.registerOutParameter(4,java.sql.Types.INTEGER);
                 s.executeUpdate();
             
                 //Obtengo el resultado
                 li_resultado=s.getInt(4);

                 if (li_resultado==1){
                    usuario.setNombreUsuario(nombreUsuario);
                    usuario.setEstado("MODIFICADO");
                    conexion.commit();
                    conexion.setAutoCommit(true);
                }
                else {
                    //No se pudo actualizar 
                    conexion.rollback(); 
                    conexion.setAutoCommit(true);
                    usuario.setNombreUsuario(nombreUsuario);
                    usuario.setEstado("ERROR");
                    usuario.setObservaciones("No se pudo cambiar la clave del usuario");  
                }
             

            }catch (SQLException e){
                try {
                    System.out.println("Hace Rollback2..."); 
                    conexion.rollback();
                    conexion.setAutoCommit(true);
                    usuario.setEstado("ERROR");
                    usuario.setObservaciones("Error: " + e.getMessage() );
                } catch (SQLException ex) {
                    Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            
            }finally {
                if (r != null) {r.close();}
                if (s != null) {s.close();}
                if (conexion != null) conexion.close(); 
            }
            
        switch (tipoRespuesta){
              case "JSON":
                  return Response.ok(usuario,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(usuario,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
    
    }      
    
    //Valida permiso de transacción
    @GET
    @Path("/validaPermiso")
    public Response validaPermiso(@QueryParam("usuarioTrans") String usuarioTrans,@QueryParam("codigoPermiso") String codigoPermiso,
                                   @QueryParam("tipoRespuesta") String tipoRespuesta) throws SQLException {
        Connection conexion=null;
        CallableStatement s=null;
        ResultSet r=null;
        java.sql.Date lda_fec_ing=null;
        boolean lb_resultado;
        
        Auditoria auditoria= new Auditoria();
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("administracion");
             s=conexion.prepareCall("{call sp_valida_permiso ( ? , ? , ? )}"); 
             s.setString(1, usuarioTrans);             
             s.setString(2, codigoPermiso);
             s.registerOutParameter(3,java.sql.Types.BOOLEAN);
             s.execute();
             
             //Obtengo el resultado
             lb_resultado=s.getBoolean(3);
             
             if (lb_resultado){
                 auditoria.setCodigo(codigoPermiso);
                 auditoria.setObservaciones("OK");
                 auditoria.setResultado(1);
                 
             }
             else{
                 auditoria.setCodigo(codigoPermiso);
                 auditoria.setObservaciones("Transacción no autorizada");
                 auditoria.setResultado(0);
             }

        }catch (SQLException e){
            auditoria.setCodigo(codigoPermiso);
            auditoria.setObservaciones("ERROR");
            auditoria.setResultado(0);
            
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }

        switch (tipoRespuesta){
              case "JSON":
                  return Response.ok(auditoria,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(auditoria,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
    
    }
    
    
    //Graba auditoria de transacción
    @GET
    @Path("/grabaAuditoria")
    public Response grabaAuditoria(@QueryParam("idUsuarioTrans") int idUsuarioTrans,@QueryParam("codigoPermiso") String codigoPermiso,
                                   @QueryParam("observacionesTrans") String observacionesTrans,
                                   @QueryParam("aplicacionTrans") String aplicacionTrans,
                                   @QueryParam("nombreEquipoTrans") String nombreEquipoTrans,
                                   @QueryParam("tipoRespuesta") String tipoRespuesta) throws SQLException {
        Connection conexion=null;
        CallableStatement s=null;
        ResultSet r=null;
        java.sql.Date lda_fec_ing=null;
        int li_resultado;
        
        Auditoria auditoria= new Auditoria();
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("administracion");
             conexion.setAutoCommit(false);
             s=conexion.prepareCall("{call sp_graba_auditoria ( ? , ? , ? , ? , ? , ? , ? )}"); 
             //Fecha de hoy
             java.util.Calendar fecha_mov = java.util.Calendar.getInstance();
             fecha_mov.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
             fecha_mov.setTime(new Date());
             lda_fec_ing=new java.sql.Date(fecha_mov.getTimeInMillis());
             s.setTimestamp(1,new java.sql.Timestamp(lda_fec_ing.getTime()));             
             s.setInt(2, idUsuarioTrans);
             s.setString(3, codigoPermiso);
             s.setString(4, observacionesTrans);
             s.setString(5, aplicacionTrans);
             s.setString(6, nombreEquipoTrans);
             s.registerOutParameter(7,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el resultado
             li_resultado=s.getInt(7);
             
             if (li_resultado == 1 ){
                 conexion.commit();
                 conexion.setAutoCommit(true);
                 auditoria.setCodigo(codigoPermiso);
                 auditoria.setObservaciones("OK");
                 auditoria.setResultado(1);
                 
             }
             else{
                 conexion.rollback(); 
                 conexion.setAutoCommit(true);
                 auditoria.setCodigo(codigoPermiso);
                 auditoria.setObservaciones("ERROR");
                 auditoria.setResultado(0);
             }

        }catch (SQLException e){
            try {
                System.out.println("Hace Rollback2..."); 
                conexion.rollback();
                conexion.setAutoCommit(true);
                auditoria.setCodigo(codigoPermiso);
                auditoria.setObservaciones("ERROR");
                auditoria.setResultado(0);
            } catch (SQLException ex) {
                Logger.getLogger(loginRest.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }

        switch (tipoRespuesta){
              case "JSON":
                  return Response.ok(auditoria,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(auditoria,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
    
    }
    
    
    
}
