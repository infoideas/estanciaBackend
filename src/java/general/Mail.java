/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import database.Conector;
import entidades.Empresa;
import general.BeanBase;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TimeZone;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * @author rafaelg
 */
public class Mail extends BeanBase {

    private String EMAIL;
    private String PASSWORD;
    private String HOST_SMTP; //smtp.gmail.com
    private String STARTTLS;
    private String PUERTO;
    private String AUTH;
    private String PROTOCOLO;

    private Session sesion;
    private String emaildestino = "";

    public Session getSesion() {
        return sesion;
    }

    public void setSesion(Session sesion) {
        this.sesion = sesion;
    }

    public String getEmaildestino() {
        return emaildestino;
    }

    public void setEmaildestino(String emaildestino) {
        this.emaildestino = emaildestino;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getHOST_SMTP() {
        return HOST_SMTP;
    }

    public void setHOST_SMTP(String HOST_SMTP) {
        this.HOST_SMTP = HOST_SMTP;
    }

    public String getSTARTTLS() {
        return STARTTLS;
    }

    public void setSTARTTLS(String STARTTLS) {
        this.STARTTLS = STARTTLS;
    }

    public String getPUERTO() {
        return PUERTO;
    }

    public void setPUERTO(String PUERTO) {
        this.PUERTO = PUERTO;
    }

    public String getAUTH() {
        return AUTH;
    }

    public void setAUTH(String AUTH) {
        this.AUTH = AUTH;
    }

    public String getPROTOCOLO() {
        return PROTOCOLO;
    }

    public void setPROTOCOLO(String PROTOCOLO) {
        this.PROTOCOLO = PROTOCOLO;
    }

    public Mail() throws Exception {  
        //Obtengo los datos de la cuenta
        obtieneDatosCorreo();
        System.out.println("usuario:" + EMAIL);
        System.out.println("clave:" + PASSWORD);
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.user", EMAIL);
        prop.setProperty("mail.smtp.host", HOST_SMTP);
        prop.setProperty("mail.smtp.port", PUERTO);
        prop.setProperty("mail.smtp.starttls.enable", STARTTLS);
        prop.setProperty("mail.smtp.auth", AUTH);

        sesion = Session.getInstance(prop);
    }

    public String enviarMail(String titulo, String texto_mensaje) {
        System.out.println("Entra por enviarMail");
        try {
            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(EMAIL, "Estancia El Duende"));
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(emaildestino));
            mensaje.setSubject(titulo);
            mensaje.setText(texto_mensaje, "ISO-8859-1", "html");

            System.out.println("Usuario: " + EMAIL);
            
            Transport t = sesion.getTransport("smtp");
            t.connect(EMAIL, PASSWORD);
            System.out.println("Ok2");
            t.sendMessage(mensaje, mensaje.getAllRecipients());
            System.out.println("Ok3");
            t.close();

            //Transport.send(mensaje);
            return "emailok";
        } catch (AuthenticationFailedException e) {
            System.out.println("Error autenticación: " + e.getMessage());  
            return e.getLocalizedMessage();
        } catch (java.io.UnsupportedEncodingException e) {
            System.out.println("Error: " + e.getMessage());
            return e.getLocalizedMessage();
        } catch (MessagingException e) {
            System.out.println("Error: " + e.getMessage());
            return e.getLocalizedMessage();
        }catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return "error";
        }
    }

    public String enviarMailInputStream(String titulo, String texto_mensaje, InputStream contenidoStream, String tipo,
            String nombre_archivo, String emaildestino) //fpaz: funcion para enviar un mail con solo un archivo adjunto
    {
        try {
            //Contenido de texto del mensaje
            MimeBodyPart contenidoMensaje = new MimeBodyPart();
            contenidoMensaje.setText(texto_mensaje, "utf-8", "html"); //fpaz: atributos para que cualquier cliente de correo pueda formatear el mensaje en html

            //Parte del adjunto
            DataSource datasource = new ByteArrayDataSource(contenidoStream, tipo);
            MimeBodyPart adjuntos = new MimeBodyPart();
            adjuntos.setDataHandler(new DataHandler(datasource));
            adjuntos.setFileName(nombre_archivo);

            //Mime multipart
            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(contenidoMensaje);
            mimeMultipart.addBodyPart(adjuntos);

            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setSender(new InternetAddress(EMAIL));  //Remitente
            mensaje.setSubject(titulo);
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(emaildestino));
            mensaje.setContent(mimeMultipart);
            java.util.Calendar fecha = java.util.Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            mensaje.setSentDate(fecha.getTime());

            Transport t = sesion.getTransport("smtp");
            t.connect(EMAIL, PASSWORD);
            t.sendMessage(mensaje, mensaje.getAllRecipients());
            t.close();
            return "emailok";
        } catch (java.io.UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
            return "error";
        } catch (IOException ex) {
            ex.printStackTrace();
            return "error";
        }

    }

    public ResultadoEnvioEmail enviarMailInputStream(String titulo, String texto_mensaje, ArrayList<AdjuntoMail> archivosAdjuntos, String tipo,
            String nombre_archivo, String emaildestino) //fpaz: funcion para mandar por mail un array de archivos adjuntos
    {
        ResultadoEnvioEmail result = new ResultadoEnvioEmail();
        try {
            //Contenido de texto del mensaje
            MimeBodyPart contenidoMensaje = new MimeBodyPart();
            contenidoMensaje.setText(texto_mensaje, "utf-8", "html"); //fpaz: atributos para que cualquier cliente de correo pueda formatear el mensaje en html

            MimeMultipart mimeMultipart = new MimeMultipart("mixed"); //fpaz: para enviar multiples archivos adjuntos
            
            //Parte del adjunto     
            if (archivosAdjuntos != null) {
                for (AdjuntoMail adj : archivosAdjuntos) {
                MimeBodyPart adjuntos = new MimeBodyPart();
                ByteArrayDataSource datasource = new ByteArrayDataSource(adj.getStreamAdjunto(), tipo);
                adjuntos.setDataHandler(new DataHandler(datasource));
                adjuntos.setFileName(adj.getNombreAdjunto());
                mimeMultipart.addBodyPart(adjuntos);
            }
            }
            

            //Mime multipart            
            mimeMultipart.addBodyPart(contenidoMensaje);            

            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setSender(new InternetAddress(EMAIL));  //Remitente
            mensaje.setSubject(titulo);
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(emaildestino));
            mensaje.addRecipient(Message.RecipientType.CC, new InternetAddress("gerenciacomercial@hamburgoseguros.com.ar")); //llega con copia a la gerencia comercial
            mensaje.setContent(mimeMultipart, "text/html; charset=utf-8");
            java.util.Calendar fecha = java.util.Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            mensaje.setSentDate(fecha.getTime());

            Transport t = sesion.getTransport("smtp");
            t.connect(EMAIL, PASSWORD);
            t.sendMessage(mensaje, mensaje.getAllRecipients());
            t.close();
            
            result.setIsEnvioExitoso(true);
            result.setMensajeGeneral("Envio de Email Exitoso");
            return result;
        } catch (java.io.UnsupportedEncodingException | MessagingException e) {
            result.setIsEnvioExitoso(false);
            result.setMensajeError(e.getMessage());
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            result.setIsEnvioExitoso(false);
            result.setMensajeError(e.getMessage());
            e.printStackTrace();
            return result;
        } catch (Exception e){
            result.setIsEnvioExitoso(false);
            result.setMensajeError(e.getMessage());
            e.printStackTrace();
            return result;
        }

    }
    
    public ResultadoEnvioEmail enviarMailInputStreamSinCc(String titulo, String texto_mensaje, ArrayList<AdjuntoMail> archivosAdjuntos, String tipo,
            String nombre_archivo, String emaildestino) //fpaz: funcion para mandar por mail un array de archivos adjuntos SIN COPIA A GERENCIA COMERCIAL
    {
        ResultadoEnvioEmail result = new ResultadoEnvioEmail();
        try {
            //Contenido de texto del mensaje
            MimeBodyPart contenidoMensaje = new MimeBodyPart();
            contenidoMensaje.setText(texto_mensaje, "utf-8", "html"); //fpaz: atributos para que cualquier cliente de correo pueda formatear el mensaje en html

            MimeMultipart mimeMultipart = new MimeMultipart("mixed"); //fpaz: para enviar multiples archivos adjuntos
            
            //Parte del adjunto     
            if (archivosAdjuntos != null) {
                for (AdjuntoMail adj : archivosAdjuntos) {
                MimeBodyPart adjuntos = new MimeBodyPart();
                ByteArrayDataSource datasource = new ByteArrayDataSource(adj.getStreamAdjunto(), tipo);
                adjuntos.setDataHandler(new DataHandler(datasource));
                adjuntos.setFileName(adj.getNombreAdjunto());
                mimeMultipart.addBodyPart(adjuntos);
            }
            }
            

            //Mime multipart            
            mimeMultipart.addBodyPart(contenidoMensaje);            

            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setSender(new InternetAddress(EMAIL));  //Remitente
            mensaje.setSubject(titulo);
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(emaildestino));            
            mensaje.setContent(mimeMultipart, "text/html; charset=utf-8");
            java.util.Calendar fecha = java.util.Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            mensaje.setSentDate(fecha.getTime());
            
            System.out.println("Usuario: " + EMAIL);
            
            Transport t = sesion.getTransport("smtp");
            t.connect(EMAIL, PASSWORD);
            t.sendMessage(mensaje, mensaje.getAllRecipients());
            t.close();
            
            result.setIsEnvioExitoso(true);
            result.setMensajeGeneral("Envio de Email Exitoso");
            return result;
        } catch (java.io.UnsupportedEncodingException | MessagingException e) {
            result.setIsEnvioExitoso(false);
            result.setMensajeError(e.getMessage());
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            result.setIsEnvioExitoso(false);
            result.setMensajeError(e.getMessage());
            e.printStackTrace();
            return result;
        } catch (Exception e){
            result.setIsEnvioExitoso(false);
            result.setMensajeError(e.getMessage());
            e.printStackTrace();
            return result;
        }

    }
    
    public boolean esMailValido(String email){
        System.out.println("Email: " + email);
        boolean result = true;
        try {
                InternetAddress emailAddr = new InternetAddress(email);
                emailAddr.validate();                
        } catch (AddressException e) {
            result = false;
        }
        return result;
    }
    
    //Obtiene los datos de la cuenta de correo usada por el sistema
    public boolean obtieneDatosCorreo( ) throws Exception {
        CallableStatement s=null;
        ResultSet r=null;
        Connection conexion=null;
        try {        
             Conector conector = new Conector();  
             conexion = conector.connect("estancia");
             s=conexion.prepareCall("{call sp_get_datos_correo ( )}");  
             r=s.executeQuery();
             if (r.next()){
                EMAIL=r.getString("emailSistemas"); 
                PASSWORD=r.getString("claveEmail"); 
                HOST_SMTP=r.getString("hostSMTP");
                PUERTO=String.valueOf(r.getInt("puertoEmail"));
                STARTTLS=r.getString("startTLS");
                AUTH=r.getString("auth");
            }
            return true; 

        }catch (SQLException e){
            System.out.print("Error: " + e.getMessage());
            return false;
        }finally {
            if (r != null) {r.close();}
            if (s != null) {s.close();}
            if (conexion != null) conexion.close(); 
        }
        
    }
    
    
}
