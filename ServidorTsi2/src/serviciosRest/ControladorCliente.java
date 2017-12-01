
package serviciosRest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import Entidades.Cliente;
import daos.ClienteDao;
import datatypes.DatosCliente;
import datatypes.DatosContenido;
import datatypes.DatosJson;
import datatypes.DatosNotificacion;

@Path("/cliente")
@Stateless
@LocalBean
public class ControladorCliente {

    @EJB
    private ClienteDao cliente;
    
    /* String id
     * foto: url
     * nombre: String
     * 
     * */
    
//    @POST
//    @Path("/bajaCliente")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Boolean bajaCliente(DatosJson in) {
//    	String idFacebook = in.getParameter("idFacebook");
//    	String empresa = in.getParameter("empresa");
//        return this.cliente.bajaCliente(idFacebook, empresa);
//    }
    
    @POST
    @Path("/obtenerContenidosSugeridos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatosContenido> obtenerContenidosSugeridos(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String empresa = in.getParameter("empresa");
        return this.cliente.obtenerContenidosSugeridos(idFacebook, empresa);
    }
    
    @POST
    @Path("/verificarSuscripcionVigente")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean verificarSuscripcionVigente(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String empresa = in.getParameter("empresa");
        return this.cliente.suscripcionEstaVigente(idFacebook, empresa);
    }
    
    @POST
    @Path("/suscribir")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean suscribir(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String tipoSuscripcion = in.getParameter("tipoSuscripcion");
    	String empresa = in.getParameter("empresa");
        return this.cliente.suscribir(idFacebook, tipoSuscripcion, empresa);
    }
    
    @POST
    @Path("/clienteEstaBloqueado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean clienteEstaBloqueado(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String empresa = in.getParameter("empresa");
        return this.cliente.clienteEstaBloqueado(idFacebook, empresa);
    }
    
    @POST
    @Path("/comprarPPV")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean comprarPPV(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
        return this.cliente.comprarPPV(idFacebook, titulo, empresa);
    }
    
    @POST
    @Path("/tieneCompradoPPV")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean tieneCompradoPPV(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
        return this.cliente.tieneCompradoPPV(idFacebook, titulo, empresa);
    }
    
    @POST
    @Path("/obtenerNotificaciones")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatosNotificacion> obtenerNotificaciones(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String empresa = in.getParameter("empresa");
        return this.cliente.obtenerNotificaciones(idFacebook, empresa);
    }
    
    @POST
    @Path("/eliminarNotificaciones")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean eliminarNotificaciones(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String empresa = in.getParameter("empresa");
        return this.cliente.eliminarNotificaciones(idFacebook, empresa);
    }
    
    @POST
    @Path("/eliminarNotificacion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean eliminarNotificacion(DatosJson in) {
    	String id = in.getParameter("id");
        return this.cliente.eliminarNotificacion(Integer.parseInt(id));
    }
    
    @POST
    @Path("/modificarPerfil")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean modificarPerfil(DatosCliente dc) {
        return this.cliente.modificarPerfil(dc);
    }
    
    @GET
    @Path("/obtenerPerfil")
    @Produces(MediaType.APPLICATION_JSON)
    public DatosCliente obtenerDatosPerfil(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	return this.cliente.obtenerDatosPerfil(idFacebook);
    }
    
    @POST
    @Path("/altaCliente")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean altaCliente(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String urlFoto = in.getParameter("urlFoto");
    	String nombre = in.getParameter("nombre");
    	String empresa = in.getParameter("empresa");
        return this.cliente.altaCliente(idFacebook, urlFoto, nombre, empresa);
    }
    
    /*TODO: test*/
    @POST
    @Path("/obtenerFavoritos")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List <DatosContenido> obtenerFavoritos(DatosJson in) {
    	String empresa = in.getParameter("empresa");
    	String idfacebook = in.getParameter("idFacebook");
        return this.cliente.obtenerFavoritos(idfacebook, empresa);
    }
    
    @POST
    @Path("/obtenerHistorico")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List <DatosContenido> obtenerHistorico(DatosJson in) {
    	String empresa = in.getParameter("empresa");
    	String idfacebook = in.getParameter("idFacebook");
        return this.cliente.obtenerHistorico(idfacebook, empresa);
    }
    
    @POST
    @Path("/bloquear")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean bloquearCliente(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String empresa = in.getParameter("empresa");
        return this.cliente.bloquearCliente(idFacebook, empresa);
    }
    
    @POST
    @Path("/desbloquear")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean desbloquearCliente(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String empresa = in.getParameter("empresa");
        return this.cliente.desbloquearCliente(idFacebook, empresa);
    }
    
    @GET
    @Path("{empresa}/obtenerClientes")
    @Produces(MediaType.APPLICATION_JSON)
    public List <DatosCliente> obtenerClientesEmpresa(@PathParam("empresa") String empresa) {
        return this.cliente.obtenerClientesEmpresa(empresa);
    }   
    
    @GET
    @Path("/obtenerClientes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatosCliente> obtenerClientes() {
        return this.cliente.obtenerClientes();
    }   

    @POST
    @Path("/agregarFavorito")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean agregarFavorito(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
    	return this.cliente.agregarFavorito(idFacebook, titulo, empresa);
    }
    
    @POST
    @Path("/agregarHistorico")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean agregarHistorico(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
    	return this.cliente.agregarHistorico(idFacebook, titulo, empresa);
    }
    
    @POST
    @Path("/quitarFavorito")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean quitarFavorito(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
    	return this.cliente.quitarFavorito(idFacebook, titulo, empresa);
    }
    
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Boolean agregarCliente(Cliente cliente) {
//        return this.cliente.addCliente(cliente);
//    }
    
//    @GET
//    @Path("/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Cliente obtenerCliente(@PathParam("id") String id) {
//        return this.cliente.getCliente(id);
//    }
   /* 
    @POST
    @Path("/agregarAdminEmpresa")
    @Produces(MediaType.APPLICATION_JSON)
    public void agregarAdministradorEmpresa(AdminEmpresa admin) {
    	usuariosAdministradorDao.agregarAdministrador(admin);
    }
    
    @POST
    @Path("/agregarAdminPlataforma")
    @Produces(MediaType.APPLICATION_JSON)
    public void agregarAdministradorPlataforma(AdminPlataforma admin) {
    	usuariosAdministradorDao.agregarAdministrador(admin);
    }
    
    @POST
    @Path("/eliminarAdminPlataforma/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void agregarAdministradorPlataforma(@PathParam("id") String id) {
        usuariosAdministradorDao.eliminarAdministradorPlataforma(id);
    }
    
    @POST
    @Path("/eliminarAdminEmpresa/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void agregarAdministradorEmpresa(@PathParam("id") String id) {
        usuariosAdministradorDao.eliminarAdministradorEmpresa(id);
    }*/
}