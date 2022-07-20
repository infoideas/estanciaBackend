/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import contabilidad.Asiento;
import contabilidad.AsientoRealizado;
import contabilidad.InterfazContable;
import database.Conector;
import general.BeanBase;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
@Path("/contabilidad")
public class Asientos_Post extends BeanBase{
    Connection conexion=null;
    
    @POST    
    @Path("/realizarAsiento")
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response realizarAsiento(Asiento asiento) {
        AsientoRealizado asientoRealizado= new AsientoRealizado();
        InterfazContable interfaz= new InterfazContable();
        try {
            int li_idAsiento;
            boolean lb_resultado=false;
            //Grabo la cabecera del pedido
            li_idAsiento=interfaz.grabaAsiento(asiento);
                    
            if (li_idAsiento > 0 ){
                System.out.println("Grabó asiento...");
                asientoRealizado.setIdAsiento(li_idAsiento);
                asientoRealizado.setResultado("Ok");
                return Response.ok(asientoRealizado,MediaType.APPLICATION_JSON).build();
            }
            else
            {
              asientoRealizado.setIdAsiento(0);
              asientoRealizado.setResultado("Error");
              asientoRealizado.setObservaciones(interfaz.getMensaje());
              return Response.ok(asientoRealizado,MediaType.APPLICATION_JSON).build();
            }
            
        } catch (NullPointerException ex) {
            Logger.getLogger(Asientos_Post.class.getName()).log(Level.SEVERE, null, ex);
            asientoRealizado.setIdAsiento(0);
            asientoRealizado.setResultado("Error");
            asientoRealizado.setObservaciones(interfaz.getMensaje());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
                
        } catch (SQLException ex) {
            Logger.getLogger(Asientos_Post.class.getName()).log(Level.SEVERE, null, ex);
            asientoRealizado.setIdAsiento(0);
            asientoRealizado.setResultado("Error");
            asientoRealizado.setObservaciones(interfaz.getMensaje());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        } catch (Exception e) {
            Logger.getLogger(Asientos_Post.class.getName()).log(Level.SEVERE, null, e);
            asientoRealizado.setIdAsiento(0);
            asientoRealizado.setResultado("Error");
            asientoRealizado.setObservaciones(interfaz.getMensaje());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();    
        }
    }
    
    @POST    
    @Path("/eliminarAsiento")
    @Consumes(MediaType.APPLICATION_JSON)  
    public Response eliminarAsiento(Asiento asiento) {
        AsientoRealizado asientoRealizado= new AsientoRealizado();
        InterfazContable interfaz= new InterfazContable();
        try {
            int li_resultado;
            boolean lb_resultado=false;
            //Grabo la cabecera del pedido
            li_resultado=interfaz.eliminaAsiento(asiento.getId());
                    
            if (li_resultado > 0 ){
                System.out.println("Eliminó asiento...");
                asientoRealizado.setIdAsiento(li_resultado);
                asientoRealizado.setResultado("Ok");
                return Response.ok(asientoRealizado,MediaType.APPLICATION_JSON).build();
            }
            else
            {
              asientoRealizado.setIdAsiento(0);
              asientoRealizado.setResultado("Error");
              asientoRealizado.setObservaciones(interfaz.getMensaje());
              return Response.ok(asientoRealizado,MediaType.APPLICATION_JSON).build();
            }
            
        } catch (NullPointerException ex) {
            Logger.getLogger(Asientos_Post.class.getName()).log(Level.SEVERE, null, ex);
            asientoRealizado.setIdAsiento(0);
            asientoRealizado.setResultado("Error");
            asientoRealizado.setObservaciones(interfaz.getMensaje());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
                
        } catch (SQLException ex) {
            Logger.getLogger(Asientos_Post.class.getName()).log(Level.SEVERE, null, ex);
            asientoRealizado.setIdAsiento(0);
            asientoRealizado.setResultado("Error");
            asientoRealizado.setObservaciones(interfaz.getMensaje());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        } catch (Exception e) {
            Logger.getLogger(Asientos_Post.class.getName()).log(Level.SEVERE, null, e);
            asientoRealizado.setIdAsiento(0);
            asientoRealizado.setResultado("Error");
            asientoRealizado.setObservaciones(interfaz.getMensaje());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();    
        }
    }
    
}
