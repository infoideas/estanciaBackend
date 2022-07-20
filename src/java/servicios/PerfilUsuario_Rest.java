/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servicios;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.Conector;
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
import seguridad.loginRest;

/**
 *
 * @author rafael
 */
@Stateless
@Path("/perfilUsuario")
public class PerfilUsuario_Rest extends BeanBase{    

    @GET
    @Path("/getDatos") 
    public Response getDatosPerfil(@QueryParam("idUsuario") int idUsuario,@QueryParam("tipoRespuesta") String tipoRespuesta){
        
    UsuarioCliente perfilCliente=null;
    try {
        perfilCliente = getPerfilUsuario(idUsuario);
    } catch (Exception ex) {
        Logger.getLogger(Empresa_Rest.class.getName()).log(Level.SEVERE, null, ex);
    }
        
    switch (tipoRespuesta){
              case "JSON":
                  Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
                  String sb=gson.toJson(perfilCliente);
                  return Response.ok(sb,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(perfilCliente,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
        
    }        

    @GET
    @Path("/grabaDireccion") 
    public Response grabaDireccion(@QueryParam("idDireccion") int idDireccion,@QueryParam("idUsuario") int idUsuario,
            @QueryParam("idProvincia") int idProvincia,@QueryParam("idLocalidad") int idLocalidad,
            @QueryParam("ciudad") String ciudad,@QueryParam("cp") String cp,
            @QueryParam("direccion") String direccion,@QueryParam("telefono") String telefono,
            @QueryParam("tipoRespuesta") String tipoRespuesta){
        
            DireccionUsuario direccionUsuario = new DireccionUsuario();
        
            try {
                int li_idDireccion;    
                li_idDireccion=grabaDireccionUsuario(idDireccion,idUsuario,idProvincia,idLocalidad,ciudad,
                    cp,direccion,telefono);
                direccionUsuario.setId(li_idDireccion);
                direccionUsuario.setIdProvincia(idProvincia);
                direccionUsuario.setIdLocalidad(idLocalidad);
                direccionUsuario.setCiudad(ciudad);
                direccionUsuario.setCp(cp);
                direccionUsuario.setDireccion(direccion);
                direccionUsuario.setTelefono(telefono);

            } catch (Exception ex) {
                Logger.getLogger(Empresa_Rest.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    switch (tipoRespuesta){
              case "JSON":
                  Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
                  String sb=gson.toJson(direccionUsuario);
                  return Response.ok(sb,MediaType.APPLICATION_JSON).build();
              case "XML":
                  return Response.ok(direccionUsuario,MediaType.APPLICATION_XML).build();
              default:
                  return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }  
        
    }    
    
    @GET
    @Path("/eliminaDireccion") 
    public Response eliminaDireccion(@QueryParam("idDireccion") int idDireccion,
            @QueryParam("tipoRespuesta") String tipoRespuesta){
        
            try {
                int li_resultado;    
                li_resultado=eliminaDireccionUsuario(idDireccion);

            } catch (Exception ex) {
                Logger.getLogger(Empresa_Rest.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build(); 
            }
        
            return Response.ok().build();
    }      
    
    
    //Obtiene los datos del perfil del usuario
    public UsuarioCliente getPerfilUsuario(int idUsuario) throws Exception {
        UsuarioCliente perfilUsuario=new UsuarioCliente();
        CallableStatement s=null;
        ResultSet r=null;
        int li_id,li_idEmpresa;
        String ls_nombreUsuario,ls_clave,ls_estado;
        java.util.Date lda_fecha;
        String ls_apellido,ls_nombre;
        Connection conexion=null;
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_perfil_usuario ( ? )}");  
             s.setInt(1, idUsuario);
             
             r=s.executeQuery();
             if (r.next()){
                li_id=r.getInt("id");
                li_idEmpresa=r.getInt("idEmpresa");
                ls_nombreUsuario=r.getString("nombreUsuario");
                ls_clave=r.getString("clave");
                ls_estado=r.getString("estado");
                lda_fecha=r.getDate("fecha");
                ls_apellido=r.getString("apellido");
                ls_nombre=r.getString("nombre");
                
                perfilUsuario.setId(li_id);
                perfilUsuario.setIdEmpresa(li_idEmpresa);
                perfilUsuario.setNombreUsuario(ls_nombreUsuario);
                perfilUsuario.setClave(ls_clave);
                perfilUsuario.setEstado(ls_estado);
                perfilUsuario.setFecha(lda_fecha);
                perfilUsuario.setApellido(ls_apellido);
                perfilUsuario.setNombre(ls_nombre);
               
                //Obtenemos la lista de direcciones
                perfilUsuario.setDirecciones((ArrayList<DireccionUsuario>) getDireccionesUsuario(idUsuario));
            }

        }catch (SQLException e){
            System.out.print("Error: " + e.getMessage());
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
        return perfilUsuario;
        
    }
    
    //Retorna lista de direcciones de un cliente
    public List<DireccionUsuario> getDireccionesUsuario(int idUsuario) throws Exception {
        List<DireccionUsuario> listaDirecciones=new ArrayList<DireccionUsuario>();
        CallableStatement s=null;
        ResultSet r=null;
        int li_id,li_idProvincia,li_idLoc;
        String ls_provincia,ls_ciudad,ls_cp,ls_direccion,ls_telefono,ls_localidad;
        Connection conexion=null;
       
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_direcciones_usuario ( ? )}");  
             s.setInt(1, idUsuario);
             
             r=s.executeQuery();
             while (r.next()){
                li_id=r.getInt("id");
                li_idProvincia=r.getInt("idProvincia");
                ls_provincia=r.getString("provincia");
                li_idLoc=r.getInt("idLocalidad");
                ls_localidad=r.getString("localidad");
                ls_ciudad=r.getString("ciudad");
                ls_cp=r.getString("cp");
                ls_direccion=r.getString("direccion");                
                ls_telefono=r.getString("telefono");

                DireccionUsuario direccion= new DireccionUsuario();
                direccion.setId(li_id);
                direccion.setIdProvincia(li_idProvincia);
                direccion.setProvincia(ls_provincia);
                direccion.setIdLocalidad(li_idLoc);
                direccion.setLocalidad(ls_localidad);
                direccion.setCiudad(ls_ciudad);
                direccion.setCp(ls_cp);
                direccion.setDireccion(ls_direccion);
                direccion.setTelefono(ls_telefono);
  
                listaDirecciones.add(direccion);
                
            }

        }catch (SQLException e){
            System.out.print("Error: " + e.getMessage());
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
        return listaDirecciones;
        
    }

    //Graba una dirección nueva o modifica una ya existente
    public int grabaDireccionUsuario(int idDireccion,int idUsuario,int idProvincia,
                    int idLocalidad,String ciudad,
                    String cp,String direccion,String telefono) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        int li_idDireccion=0;
        Connection conexion=null;
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             conexion.setAutoCommit(false);
             s=conexion.prepareCall("{call sp_graba_direccion ( ? , ? , ? , ? , ? , ? , ? , ? , ? )}");  
             s.setInt(1,idDireccion);
             s.setInt(2,idUsuario);
             s.setInt(3,idProvincia);
             s.setInt(4,idLocalidad);
             s.setString(5,ciudad);
             s.setString(6,cp);
             s.setString(7,direccion);
             s.setString(8,telefono);
             s.registerOutParameter(9,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el resultado
             li_idDireccion=s.getInt(9);
             if (li_idDireccion > 0){
                conexion.commit();
                conexion.setAutoCommit(true);

             }else{
                conexion.rollback();
             }
             

        }catch (SQLException e){
            System.out.println("Error SQL graba dirección: " + e.getMessage() );
            try {    
                conexion.rollback();
            } catch (SQLException ex) {
                System.out.println("Error SQL graba dirección: " + e.getMessage() );
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
        
        return li_idDireccion;

        
    }
    
    //Elimina una dirección del usuario
    public int eliminaDireccionUsuario(int idDireccion) throws SQLException{
        
        CallableStatement s=null;
        ResultSet r=null;
        int li_resultado=0;
        Connection conexion=null;
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             conexion.setAutoCommit(false);
             s=conexion.prepareCall("{call sp_elimina_direccion_usuario ( ? , ? )}");  
             s.setInt(1,idDireccion);
             s.registerOutParameter(2,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el resultado
             li_resultado=s.getInt(2);
             if (li_resultado > 0){
                conexion.commit();
                conexion.setAutoCommit(true);

             }else{
                conexion.rollback();
             }
             
        }catch (SQLException e){
            System.out.println("Error SQL eliminar dirección: " + e.getMessage() );
            try {    
                conexion.rollback();
            } catch (SQLException ex) {
                System.out.println("Error SQL eliminar dirección: " + e.getMessage() );
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
        
        return li_resultado;

        
    }
}

