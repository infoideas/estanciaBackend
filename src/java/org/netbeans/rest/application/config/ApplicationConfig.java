/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.rest.application.config;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author rafael
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(seguridad.loginRest.class);
        resources.add(servicios.Asientos_Post.class);
        resources.add(servicios.Empresa_Rest.class);
        resources.add(servicios.Entregas_Post.class);
        resources.add(servicios.Entregas_Rest.class);
        resources.add(servicios.Lista_Rest.class);
        resources.add(servicios.PerfilUsuario_Rest.class);
        resources.add(servicios.Procesos_Rest.class);
        resources.add(servicios.Tropas_Rest.class);
    }
    
}
