package contabilidad;
// Generated 23/04/2021 14:52:05 by Hibernate Tools 4.3.1


import entidades.PedidoDetalle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Plantilla generated by hbm2java
 */

@XmlRootElement (name = "plantilla")
public class Plantilla  implements java.io.Serializable {
    private Integer id;
    private String nombre;
    private ArrayList<PlantillaDet> listaCuentas= new ArrayList<PlantillaDet>();

    public Plantilla() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<PlantillaDet> getListaCuentas() {
        return listaCuentas;
    }

    public void setListaCuentas(ArrayList<PlantillaDet> listaCuentas) {
        this.listaCuentas = listaCuentas;
    }

	

}

