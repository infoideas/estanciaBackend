package entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "pedido")
public class Pedido implements Serializable {
    private int id;
    private int idEmpresa;
    private java.util.Date fecha;
    private int idCliente;
    private int idDireccion;
    private String modoEntrega;
    private String estado;
    private double subTotalPedido;
    private double costoEnvio;
    private double totalPedido;
    private String fueCobrado;
    private long numOperacion;
    private String formaPago;
    private int idTransporte;
    private String observaciones;
    private ArrayList<PedidoDetalle> listaProductos= new ArrayList<PedidoDetalle>();

    public Pedido() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }

    public String getModoEntrega() {
        return modoEntrega;
    }

    public void setModoEntrega(String modoEntrega) {
        this.modoEntrega = modoEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getSubTotalPedido() {
        return subTotalPedido;
    }

    public void setSubTotalPedido(double subTotalPedido) {
        this.subTotalPedido = subTotalPedido;
    }

    public double getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(double costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public double getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(double totalPedido) {
        this.totalPedido = totalPedido;
    }

    public String getFueCobrado() {
        return fueCobrado;
    }

    public void setFueCobrado(String fueCobrado) {
        this.fueCobrado = fueCobrado;
    }

    public long getNumOperacion() {
        return numOperacion;
    }

    public void setNumOperacion(long numOperacion) {
        this.numOperacion = numOperacion;
    }
    
    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public int getIdTransporte() {
        return idTransporte;
    }

    public void setIdTransporte(int idTransporte) {
        this.idTransporte = idTransporte;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public ArrayList<PedidoDetalle> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<PedidoDetalle> listaProductos) {
        this.listaProductos = listaProductos;
    }
}
