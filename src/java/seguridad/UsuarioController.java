/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seguridad;
import database.Conector;
import general.BeanBase;
import general.StringMD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author rafael
 */
public class UsuarioController extends BeanBase {

    public UsuarioController() {
    }
    
    //Valida el login del usuario cliente
    public Usuario validaLoginUsuarioCliente(String nombreUsuario,String clave) throws Exception {
        Usuario usuario=new Usuario();
        CallableStatement s=null;
        ResultSet r=null;
        StringMD md5=new StringMD();
        String ls_clave_MD5,ls_clave,ls_estado;
        String ls_apellido,ls_nombre;
        int li_idUsuario;
        
        System.out.println("Usuario:" + nombreUsuario);
        System.out.println("Clave:" + clave);
        
        //Encripto la clave en MD5    
        ls_clave_MD5= md5.getStringMessageDigest(clave,"MD5");
        Connection conexion=null;
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("administracion");
             s=conexion.prepareCall("{call sp_get_def_usuario_cliente ( ? )}");  
             s.setString(1,nombreUsuario);
             
             r=s.executeQuery();
             if (r.next())
             {
                 usuario.setNombreUsuario(nombreUsuario);
                 li_idUsuario=r.getInt("id");
                 ls_clave=r.getString("clave");
                 ls_estado=r.getString("estado");
                 ls_apellido=r.getString("apellido");
                 ls_nombre=r.getString("nombre");
                 
                 if (ls_clave.equals(ls_clave_MD5))
                        {   //Claves coinciden
                            usuario.setEstado(r.getString("estado"));
                            if (usuario.getEstado().equals("1"))
                                 {
                                usuario.setIdUsuario(li_idUsuario); 
                                usuario.setNombreUsuario(nombreUsuario);
                                usuario.setApellido(ls_apellido);
                                usuario.setNombre(ls_nombre);
                                usuario.setEstado("HABILITADO");
                                usuario.setObservaciones("CONEXION OK");
                                //Habilitado entonces cargo las mesas habilitadas
                                System.out.print("Usuario conectado: ".concat(usuario.getNombreUsuario()));
                                 }
                             else
                             {
                                usuario.setIdUsuario(li_idUsuario);
                                usuario.setNombreUsuario(nombreUsuario);
                                usuario.setEstado("NO HABILITADO");
                                usuario.setObservaciones("USUARIO NO ACTIVADO");
                             }
                        }
                        else
                        {
                            usuario.setIdUsuario(li_idUsuario);
                            usuario.setNombreUsuario(nombreUsuario);
                            usuario.setEstado("NO HABILITADO");
                            usuario.setObservaciones("CLAVE INCORRECTA");
                        }
                 
            }
            else
            {
                 usuario.setNombreUsuario(nombreUsuario);
                 usuario.setEstado("NO HABILITADO");
                 usuario.setObservaciones("USUARIO NO EXISTE");
            }    

        }catch (SQLException e){
                usuario.setEstado("ERROR");
                usuario.setObservaciones("Error: " + e.getMessage() );
            
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
            System.out.print("Conexión LogIn Usuario: ".concat(usuario.getNombreUsuario().concat(" cerrada")));
        }
        
        return usuario;
        
    }
    
    //Obtiene datos del usuario en base al email
    public Usuario getDatosUsuarioCliente(String nombreUsuario) throws Exception {
        Usuario usuario=new Usuario();
        CallableStatement s=null;
        ResultSet r=null;
        StringMD md5=new StringMD();
        String ls_clave_MD5,ls_clave,ls_estado;
        String ls_apellido,ls_nombre;
        int li_idUsuario;
        Connection conexion=null;
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("administracion");
             s=conexion.prepareCall("{call sp_get_def_usuario_cliente ( ? , ? )}");  
             s.setString(1,nombreUsuario);
             
             r=s.executeQuery();
             if (r.next())
             {
                 //Existe un usuario
                 usuario.setNombreUsuario(nombreUsuario);
                 li_idUsuario=r.getInt("id");
                 ls_clave=r.getString("clave");
                 ls_estado=r.getString("estado");
                 ls_apellido=r.getString("apellido");
                 ls_nombre=r.getString("nombre");
                 
                 usuario.setIdUsuario(li_idUsuario); 
                 usuario.setNombreUsuario(nombreUsuario);
                 usuario.setApellido(ls_apellido);
                 usuario.setNombre(ls_nombre);
                 usuario.setEstado("HABILITADO");
                 usuario.setObservaciones("CONEXION OK");
             }
            else
            {
                 usuario.setNombreUsuario(nombreUsuario);
                 usuario.setEstado("NO HABILITADO");
                 usuario.setObservaciones("USUARIO NO EXISTE");
            }    

        }catch (SQLException e){
                usuario.setEstado("ERROR");
                usuario.setObservaciones("Error: " + e.getMessage() );
            
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
        return usuario;
        
    }
    
    
    //Valida el login del usuario administrativo
    public UsuarioAdmin validaLoginUsuarioAdmin(String nombreUsuario,String clave,String tipoUsuario) throws Exception {
        UsuarioAdmin usuario=new UsuarioAdmin();
        CallableStatement s=null;
        ResultSet r=null;
        StringMD md5=new StringMD();
        int li_idUsuario;
        String ls_clave_MD5,ls_clave,ls_apellido,ls_nombre,ls_tipo;
        Connection conexion=null;

        System.out.println("Usuario:" + nombreUsuario);
        System.out.println("Clave:" + clave);
        
        //Encripto la clave en MD5    
        ls_clave_MD5= md5.getStringMessageDigest(clave,"MD5");

        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("administracion");
             s=conexion.prepareCall("{call sp_get_def_usuario_admin ( ? )}");  
             s.setString(1,nombreUsuario);
             
             r=s.executeQuery();
             if (r.next()){
                 li_idUsuario=r.getInt("id");
                 ls_apellido=r.getString("apellido");
                 if(ls_apellido==null) ls_apellido="";
                 ls_nombre=r.getString("nombre");
                 if(ls_nombre==null) ls_nombre="";                 
                 ls_clave=r.getString("clave");
                 ls_tipo=r.getString("tipo");
                 
                 System.out.print("Usuario:" + ls_nombre +  " " + ls_tipo );
                 
                 usuario.setId(li_idUsuario);
                 usuario.setNombreUsuario(nombreUsuario);
                 usuario.setNombreCompletoUsuario(ls_apellido + " " + ls_nombre);
                 if (ls_clave.equals(ls_clave_MD5))
                 {   //Claves coinciden

                     usuario.setEstado(r.getString("estado"));
                     if (usuario.getEstado().equals("1"))
                         {
                           if (tipoUsuario.equals("S")){
                             //Validación de un super usuario  
                             if (ls_tipo.equals("S")){
                                usuario.setEstado("HABILITADO");
                                usuario.setObservaciones("CONEXION OK");
                                usuario.setTipo(ls_tipo);
                                System.out.print("Usuario conectado: ".concat(usuario.getNombreUsuario()));
                             }   
                             else
                             {
                                usuario.setEstado("NO HABILITADO");
                                usuario.setObservaciones("No es un super usuario");
                                System.out.print("No es super Usuario:" + ls_nombre +  " " + ls_tipo );
                             }  
                           }  
                           else{
                            usuario.setEstado("HABILITADO");
                            usuario.setObservaciones("CONEXION OK");
                            usuario.setTipo(ls_tipo);
                            System.out.print("Usuario conectado: ".concat(usuario.getNombreUsuario()));
                           }  
                         }
                     else
                     {
                        usuario.setEstado("NO HABILITADO");
                        usuario.setObservaciones("USUARIO NO ACTIVADO");
                     }
                 }
                 else
                 {
                      usuario.setEstado("NO HABILITADO");
                      usuario.setObservaciones("CLAVE INCORRECTA");
                 }
            }
            else
             {
                 usuario.setNombreUsuario(nombreUsuario);
                 usuario.setEstado("NO HABILITADO");
                 usuario.setObservaciones("USUARIO NO EXISTE");
             }    

        }catch (SQLException e){
                usuario.setEstado("ERROR");
                usuario.setObservaciones("Error: " + e.getMessage() );
            
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
        return usuario;
        
    }
    
    //Valida si existe el usuario administrador y devuelve su código
    public int existeUsuarioAdmin(String email) throws SQLException{
        PreparedStatement s=null;
        ResultSet r=null;
        int li_cod_usuario;
        Connection conexion=null;
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("administracion");
             s=conexion.prepareStatement("select id from usuario_admin where nombreUsuario = ?");
             s.setString(1,email);      
             r = s.executeQuery();
             if (r.next()) {                
                 //Ya existe el mail 
                 li_cod_usuario=r.getInt("id");
                 return li_cod_usuario;
             }
             else
                 return 0;
        }catch (SQLException e){
            throw e;
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
    }
    
    //Valida si existe el usuario y devuelve su código
    public int existeUsuarioCliente(String email) throws SQLException{
        PreparedStatement s=null;
        ResultSet r=null;
        int li_cod_usuario;
        Connection conexion=null;
        
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("administracion");
             s=conexion.prepareStatement("select id from usuario_cliente where nombreUsuario = ?");
             s.setString(1,email);      
             r = s.executeQuery();
             if (r.next()) {                
                 //Ya existe el mail 
                 li_cod_usuario=r.getInt("id");
                 return li_cod_usuario;
             }
             else
                 return 0;
        }catch (SQLException e){
            throw e;
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
    }  
    
    
    
}
