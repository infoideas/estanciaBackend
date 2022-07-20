/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entidades;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rafael
 */
@XmlRootElement (name = "promocion")
public class CuponDescargado implements Serializable {
    private String codigo;
    private String textoGeneralCupon;

    public CuponDescargado() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTextoGeneralCupon() {
        return textoGeneralCupon;
    }

    public void setTextoGeneralCupon(String textoGeneralCupon) {
        this.textoGeneralCupon = textoGeneralCupon;
    }
    
    
}
