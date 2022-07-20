/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package contabilidad;

import database.Conector;
import general.BeanBase;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

/**
 *
 * @author rafaelg
 */
public class InterfazContable extends BeanBase{
    private Conector conector;  
    private Connection conexion;
    private String mensaje;
    
    public InterfazContable() {
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    
    //Graba el asiento en base a los datos y la plantilla asociada
    public int grabaAsiento(Asiento asiento) throws SQLException, UnsupportedEncodingException{
        //Fecha del sistema
        Date lda_fec_carga = new Date();
        double ld_total_debe=0,ld_total_haber=0;
        
        TimeZone gmtZone = TimeZone.getTimeZone("America/Buenos_Aires");
        DateFormat destDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        destDateFormat.setTimeZone(gmtZone);

        Date lda_fec_mov=null;
        try {
            lda_fec_mov=destDateFormat.parse(asiento.getFecMov());
        } catch (ParseException ex) {
            Logger.getLogger(InterfazContable.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Fecha de mov: " + lda_fec_mov);
        
        //Reviso si el período de la fecha del asiento está abierto
        //y permite asientos
        java.util.Calendar fecha_mov = java.util.Calendar.getInstance();
        fecha_mov.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
        fecha_mov.setTime(lda_fec_mov);
        fecha_mov.set(Calendar.HOUR_OF_DAY, 0);
        fecha_mov.set(Calendar.MINUTE, 0);
        fecha_mov.set(Calendar.SECOND, 0);
        fecha_mov.set(Calendar.MILLISECOND, 0);
        
        int li_anio = fecha_mov.get(Calendar.YEAR);
        int li_mes = fecha_mov.get(Calendar.MONTH);
        li_mes++;
        
        //Obtengo el ejercicio correspondiente de acuerdo a la fecha de movimiento
        int li_idEjercicio=obtieneEjercicioContable(asiento.getIdEmpresa(),lda_fec_mov);
        if (li_idEjercicio==0){
            mensaje="No existe ejercicio para la fecha del asiento";
            return 0;
        }
        
        //Valido si el período permite asientos
        String ls_estado=buscaPeriodo(li_idEjercicio,li_anio, li_mes);
        if(ls_estado.compareTo("A")!=0)
        {
            mensaje="Período se encuentra cerrado";
            return 0;
        }
        
        //Obtengo el id del número de plantilla 
        int li_idPlantilla;
        if (asiento.getNumPlantilla() > 0){
          li_idPlantilla=buscaIdPlantilla(li_idEjercicio,asiento.getNumPlantilla());
          if (li_idPlantilla==0){
            mensaje="Número de plantilla relacionada no existe";
            return 0;
          }  
        }
        else
            li_idPlantilla=0;
        
        ArrayList<AsientoDet> listaCuentasAsiento= new ArrayList<AsientoDet>();
        //Si trae una plantilla relacionada seteo los valores
        if (li_idPlantilla > 0){
            //Obtengo las cuentas de la plantilla
            ArrayList<PlantillaDet> listaCuentasPlantilla=buscaCuentasPlantilla(li_idPlantilla);
        
            //Recorro las cuentas de la plantilla
            //y armo la lista de cuentas para el asiento
            for (PlantillaDet r : listaCuentasPlantilla) {
                String ls_formula;
                ls_formula=r.getFormula();
                if (ls_formula==null)
                    ls_formula="";

                ls_formula=ls_formula.trim();
                if (!ls_formula.isEmpty()){
                //Cuenta tiene asignada una fórmula
                    double ld_valor=evaluarFormula(ls_formula,asiento.getListaPar());
                    if (ld_valor > 0){
                        AsientoDet d= new AsientoDet();
                        d.setIdCuenta(r.getIdCuenta());
                        d.setDc(r.getDc());
                        if (r.getDc().compareTo("D")==0)
                            ld_total_debe=ld_total_debe + ld_valor;
                        else
                            ld_total_haber=ld_total_haber + ld_valor;
                        //Veo si tiene centro de costo
                        if (r.getTieneCC().compareTo("1")==0)
                            d.setIdCentroCosto(asiento.getIdCentroCosto());
                        
                        d.setValor(ld_valor);
                        listaCuentasAsiento.add(d);
                    }
//                else{
//                    mensaje="Valor asignado a Cuenta: " + r.getCuentaNumero() + "-" + r.getCuentaNombre() + " incorrecto";
//                    return 0;
//                }
                    
                }
                else{
                    mensaje="Cuenta: " + r.getCuentaNumero() + "-" + r.getCuentaNombre() + " no tiene parámetros asignados";                
                    return 0;
                }
                
            }
        }
        
        
        //Agrego las cuentas adicionales
        for (AsientoCuentaAdicional p : asiento.getListaCuentasAdic()) {
            
            //Obtengo el id de la cuenta relacionada
            CuentaContable cuentaContable=buscaCuenta(li_idEjercicio,p.getNumeroCuenta());
            if (cuentaContable == null){
                mensaje="Número de cuenta relacionada no existe";
                return 0;
            }

            AsientoDet d= new AsientoDet();
            d.setIdCuenta(cuentaContable.getId());
            d.setDc(p.getDc());
            
            double ld_valor;
            ld_valor=Math.round(p.getValor()*100d)/100d;
            System.out.println("Cuenta adicional: " + p.getNumeroCuenta() + "-" + p.getDc() + "-" + ld_valor);
            
            if (p.getDc().compareTo("D")==0)
                ld_total_debe=ld_total_debe + ld_valor;
            else
                ld_total_haber=ld_total_haber + ld_valor;
            
            //Veo si tiene centro de costo
            if (cuentaContable.getTieneCC().compareTo("1")==0)
                d.setIdCentroCosto(asiento.getIdCentroCosto());
            
            d.setValor(ld_valor);
            listaCuentasAsiento.add(d);
            
        }        

        ld_total_debe=Math.round(ld_total_debe*100d)/100d;
        ld_total_haber=Math.round(ld_total_haber*100d)/100d;
        
        System.out.println("Total debe: " + ld_total_debe);
        System.out.println("Total haber: " + ld_total_haber);
        
        if (ld_total_debe == 0){
            mensaje="Valor del debe tiene que ser mayor a cero";
            return 0;
        }
        
        if (ld_total_haber == 0){
            mensaje="Valor del haber tiene que ser mayor a cero";
            return 0;
        }
        
        if (ld_total_debe != ld_total_haber){
            mensaje="Asiento descuadrado";
            return 0;
        }
        
        //Conectamos a la base
        conector = new Conector();  
        conexion = conector.connect("contabilidad");
        conexion.setAutoCommit(false);
        
        CallableStatement s=null;
        ResultSet r=null;
        int li_asiento=0;
        int li_resultado=0;
        boolean lb_resultado;
        java.sql.Date lda_fecha;
        
        try {      
            
             li_asiento=grabaCabAsiento(li_idEjercicio,lda_fec_mov,asiento.getIdUsuario(),
                     asiento.getDescripcion(),li_idPlantilla);
            
             if (li_asiento > 0 ){
                 //Se grabó ok la cabecera del asiento
                 //Procedo a grabar el detalle
                 for (AsientoDet q : listaCuentasAsiento) {
                    int li_idCC=0;
                    li_idCC=q.getIdCentroCosto();
                    lb_resultado=grabaCuentaAsiento(li_asiento,q.getIdCuenta(),String.valueOf(q.getDc()),q.getValor(),li_idCC);
                    if(lb_resultado==false){
                        conexion.rollback();
                        conexion.setAutoCommit(true);
                        return 0;
                    }
                 }
                 
                 conexion.commit();
                 conexion.setAutoCommit(true);
                 return li_asiento;
                 
             }
             
        }catch (SQLException e){
            conexion.rollback();
            conexion.setAutoCommit(true);
            System.out.println("Error al grabar asiento" + e.getMessage() );
            return 0;
        }catch (Exception e){
            conexion.rollback();
            conexion.setAutoCommit(true);
            System.out.println("Error al grabar asiento" + e.getMessage() );
            return 0;
        }finally {
            if (conexion != null) conexion.close();   
            if (s != null) s.close();  
            if (r != null) r.close();  
        }

        return 0;
        
    }
       
    //Graba cabecera del asiento
    public int grabaCabAsiento(int idEjercicio,Date fecha_mov,int idUsuario,String descripcion,int numPlantilla) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        int li_asiento=0;
        int li_resultado=0;
        java.sql.Date lda_fecha;
        Date lda_fec_carga = new Date();        
        
        try {      
             s=conexion.prepareCall("{call sp_graba_cab_asiento ( ? , ? , ? , ? , ? , ? , ? )}"); 
             s.setInt(1,idEjercicio); 
             lda_fecha=new java.sql.Date(fecha_mov.getTime());
             //s.setDate(2,lda_fecha);
             s.setTimestamp(2,new java.sql.Timestamp(fecha_mov.getTime()));
             s.setTimestamp(3,new java.sql.Timestamp(lda_fec_carga.getTime()));
             s.setInt(4,idUsuario);
             s.setString(5,descripcion);
             s.setInt(6,numPlantilla);
             s.registerOutParameter(7,java.sql.Types.INTEGER);
             s.executeUpdate();
             
             //Obtengo el id del registro insertado
             li_asiento=s.getInt(7);
        }catch (SQLException e){
            conexion.setAutoCommit(true);
            System.out.println("Error al grabar asiento" + e.getMessage() );
        }catch (Exception e){
            conexion.setAutoCommit(true);
            System.out.println("Error al grabar asiento" + e.getMessage() );
        }finally {
        }
        return li_asiento;
    }
    
    //Graba cada cuenta del asiento
    public boolean grabaCuentaAsiento(int idAsiento,int idCuenta,String dc,double valor,int idCC){
        CallableStatement s=null;
        ResultSet r=null;
        int li_resultado=0;
        boolean lb_resultado;
        try {    
            s=conexion.prepareCall("{call sp_graba_det_asiento ( ? , ? , ? , ? , ? , ? , ? )}"); 
            s.setInt(1,idAsiento); 
            s.setInt(2,idCuenta); 
            s.setString(3,null);
            s.setString(4,dc);
            s.setDouble(5,valor);
            s.setInt(6,idCC);
            s.registerOutParameter(7,java.sql.Types.INTEGER);
            s.executeUpdate();
            //Obtengo el resultado
            li_resultado=s.getInt(7);  
            lb_resultado = li_resultado > 0;
        
        }catch (SQLException e){
            System.out.println("Error graba pedido detalle SQL: " + e.getMessage() );
            lb_resultado=false;
        }catch (Exception e){
            System.out.println("Error graba pedido detalle: " + e.getMessage() );
            lb_resultado=false;
        }finally {
        }
        return lb_resultado;
        
    }
    
    //Elimina asiento
    public int eliminaAsiento(int idAsiento) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        int li_resultado=0;
        
        try {      
             conector = new Conector();  
             conexion = conector.connect("contabilidad");
             conexion.setAutoCommit(false);
             s=conexion.prepareCall("{call sp_elimina_asiento ( ? , ? )}"); 
             s.setInt(1,idAsiento); 
             s.registerOutParameter(2,java.sql.Types.INTEGER);
             s.executeUpdate();
             conexion.commit();  
             
             //Obtengo el id del registro insertado
             li_resultado=s.getInt(2);
             
        }catch (SQLException e){
            conexion.rollback();
            conexion.setAutoCommit(true);
            System.out.println("Error al eliminar asiento" + e.getMessage() );
        }catch (Exception e){
            conexion.rollback();
            conexion.setAutoCommit(true);
            System.out.println("Error al eliminar asiento" + e.getMessage() );
        }finally {
            if (conexion != null) conexion.close();   
            if (s != null) s.close();  
            if (r != null) r.close();  
        }

        return li_resultado;
    }
    
    //Obiene valor de la cuenta en base a fórmula
    public double evaluarFormula(String formula,List<ParametroAsiento> listaPar){
        double ld_valor=0;
        String ls_formula="",ls_var="",ls_valor="";
        boolean lb_var=false;
        int i=0,li_len=0;
        int li_pos=0;
        
        li_len=formula.length();
        while ( i < li_len){
            char l=formula.charAt(i);
            if (l=='{'){
                //Buscamos el fin de la variable
                li_pos=formula.indexOf("}",i);
                ls_var=formula.substring(i,li_pos + 1);
                
                i=li_pos + 2;
                //Recorro los parámetros para encontrar el valor de la variable
                for (ParametroAsiento q : listaPar) {
                    ls_valor="";
                    if ( ("{" + q.getNombre().trim() + "}").equals(ls_var)){
                        ld_valor=q.getValor();
                        ls_valor=String.valueOf(ld_valor);
                        ls_formula=ls_formula + ls_valor;	
                    }
                }
            }
            else{
                i++;
                ls_formula=ls_formula + String.valueOf(l);
            }
        }
        
        
        //Evalúo la fórmula
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try { 
            ld_valor=((Double) engine.eval(ls_formula)).doubleValue();
        } catch (NullPointerException ex) {            
            ld_valor=0;
            Logger.getLogger(InterfazContable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScriptException ex) {
            ld_valor=0;
            Logger.getLogger(InterfazContable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ld_valor;
        
    }

    //Obtiene las cuentas de la plantilla
    public ArrayList<PlantillaDet> buscaCuentasPlantilla(int idPlantilla) throws SQLException{
        CallableStatement s=null;
        ResultSet r=null;
        Connection conexion=null;
        String ls_num_cuenta,ls_nombre_cuenta,ls_dc,ls_cc,ls_formula;
        int li_cuenta;
        ArrayList<PlantillaDet> listaCuentas= new ArrayList<PlantillaDet>();
        try {     
             Conector conector = new Conector();  
             conexion = conector.connect("contabilidad");
             s=conexion.prepareCall("{call sp_get_cuentas_plantilla ( ? )}");  
             s.setInt(1, idPlantilla);
             
             r=s.executeQuery();
             while (r.next()){
                li_cuenta=r.getInt("idCuenta");
                ls_num_cuenta=r.getString("cuentaNumero");
                ls_nombre_cuenta=r.getString("cuentaNombre");
                ls_dc=r.getString("dc");
                ls_cc=r.getString("cc");
                ls_formula=r.getString("formula");
                
                PlantillaDet cuenta= new PlantillaDet();
                cuenta.setIdCuenta(li_cuenta);
                cuenta.setCuentaNumero(ls_num_cuenta);
                cuenta.setCuentaNombre(ls_nombre_cuenta);
                cuenta.setDc(ls_dc);
                cuenta.setTieneCC(ls_cc);
                cuenta.setFormula(ls_formula);
                listaCuentas.add(cuenta);
             }

        }catch (SQLException e){
            System.out.print("Error: " + e.getMessage());
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        return listaCuentas;
    }    
    
    //Obtiene el estado del período
    private String buscaPeriodo(int idEjercicio,int anio,int mes) throws SQLException {
        PreparedStatement s=null;
        ResultSet r=null;
        Connection conexion=null;
        String ls_estado="";
        
        try {
            Conector conector = new Conector();  
            conexion = conector.connect("contabilidad");
            s=conexion.prepareStatement("select estado from periodo where idEjercicio = ? and anio = ? and mes = ? ");
            s.setInt(1,idEjercicio);      
            s.setInt(2,anio);
            s.setInt(3,mes);
            r = s.executeQuery();
            if (r.next()) {                
                 //Ya existe el mail 
                 ls_estado=r.getString("estado");
            }
        }catch (SQLException e){
            throw e;
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        return ls_estado;
    }
    
    //Obtiene id del ejercicio contable correspondiente de acuerdo a la fecha
    public int obtieneEjercicioContable(int idEmpresa,Date fec_mov) {
        CallableStatement s=null;
        ResultSet r=null;
        int li_id_ejer;
        //Conectamos a la base
        Conector conector = new Conector();  
        Connection conexion = conector.connect("contabilidad");
        
        try {

               s=conexion.prepareCall("{call sp_get_ejercicio_fecha ( ? , ? , ? )}",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
               s.setInt(1,idEmpresa);
               s.setDate(2,new java.sql.Date(fec_mov.getTime()));
               s.registerOutParameter(3,java.sql.Types.INTEGER);
               r=s.executeQuery();
               
               li_id_ejer=s.getInt(3);
               
        } catch (SQLException e){
            e.getMessage();
            li_id_ejer=0;
        } finally {
             try {
                 if (conexion != null)  conexion.close();
                 if (r != null) r.close();   
                 if (s != null) s.close(); 
             } catch (SQLException ex) {
                 Logger.getLogger(BeanBase.class.getName()).log(Level.SEVERE, null, ex);
                 li_id_ejer=0;
             }   
        }
        return li_id_ejer;
    }
    
    //Obtiene el id de la plantilla en base al número
    private int buscaIdPlantilla(int idEjercicio,int numPlantilla) throws SQLException {
        PreparedStatement s=null;
        ResultSet r=null;
        Connection conexion=null;
        int li_idPlantilla=0;
        
        try {
            Conector conector = new Conector();  
            conexion = conector.connect("contabilidad");
            s=conexion.prepareStatement("select id from plantilla where idEjercicio = ? and numero = ? ");
            s.setInt(1,idEjercicio);     
            s.setInt(2, numPlantilla);
            r = s.executeQuery();
            if (r.next()) {                
                 //Ya existe el mail 
                 li_idPlantilla=r.getInt("id");
            }
        }catch (SQLException e){
            throw e;
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        return li_idPlantilla;
    }
    
    //Obtiene el id de la cuenta en base al número
    private CuentaContable buscaCuenta(int idEjercicio,String numCuenta) throws SQLException {
        PreparedStatement s=null;
        ResultSet r=null;
        Connection conexion=null;
        int li_idCuenta=0;
        String ls_nombreCuenta;
        String ls_tiene_cc;
        CuentaContable cuenta = null;
        try {
            Conector conector = new Conector();  
            conexion = conector.connect("contabilidad");
            s=conexion.prepareStatement("select id,cuentaNombre,cc from cuenta where idEjercicio = ? and cuentaNumero = ? ");
            s.setInt(1,idEjercicio);     
            s.setString(2, numCuenta);
            r = s.executeQuery();
            if (r.next()) {                
                 //Existe la cuenta
                 li_idCuenta=r.getInt("id");
                 ls_nombreCuenta=r.getString("cuentaNombre");
                 ls_tiene_cc=r.getString("cc");
                 cuenta= new CuentaContable();
                 cuenta.setId(li_idCuenta);
                 cuenta.setNombre(ls_nombreCuenta);
                 cuenta.setTieneCC(ls_tiene_cc);
            }
        }catch (SQLException e){
            throw e;
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        return cuenta;
    }
    
}
