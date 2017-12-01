
package serviciosRest;

import java.text.SimpleDateFormat;
import java.util.Date;
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

import Entidades.Categoria;
import Entidades.Cliente;
import Entidades.Contenido;
import Entidades.TipoContenido;
import daos.ClienteDao;
import daos.ContenidoDao;
import datatypes.DatosCompartirContenido;
import datatypes.DatosContenido;
import datatypes.DatosJson;
import datatypes.DatosTipoContenido;

@Path("/contenido")
@Stateless
@LocalBean
public class ControladorContenido {

    @EJB
    private ContenidoDao contenidoDao;
    
    /*
     * agregarContenido
     * agregarCategoriaTipoContenido
     * agregarAtributoTipoContenido
     * crearTipoContenido
     * compartirContenido
     * setearURL
     * puntuar
     * filtrar
     * eliminarTipoContenido
     * eliminarAtributoTipoContenido
     * eliminarCategoriaTipoContenido
     * obtenerContenidos
     * obtenerTiempoReproduccion
     * guardarTiempoReproduccion
     * tipoContenido
     * destacar
     * quitarDestacado
     * bloquear
     * desbloquear
     * */
    
    @GET
    @Path("/obtenerURL/{empresa}/{titulo}")
    @Produces(MediaType.APPLICATION_JSON)
    public DatosContenido obtenerURL(@PathParam("titulo")String titulo, @PathParam("empresa")String empresa) {
        return this.contenidoDao.obtenerURL(titulo, empresa);
    }
    
    @POST
    @Path("/contenidoEsPPV")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean contenidoEsPPV(DatosJson in){
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
    	
    	return this.contenidoDao.contenidoEsPPV(titulo, empresa);
    }
    
    @POST
    @Path("/obtenerPuntuacionClienteContenido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Double obtenerPuntuacionClienteContenido(DatosJson in) {
    	String idFacebook = in.getParameter("idFacebook");
    	String empresa = in.getParameter("empresa");
    	String titulo = in.getParameter("titulo");
        return this.contenidoDao.obtenerPuntuacionClienteContenido(idFacebook, empresa, titulo);
    }
    
    @POST
    @Path("/setearURL")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean setearURL(DatosJson in){
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
    	String url = in.getParameter("url");
    	
    	return this.contenidoDao.setearURL(titulo, empresa, url);
    }
    
    @POST
    @Path("/puntuar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DatosContenido puntuarContenido(DatosJson in){
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
    	String idFacebook = in.getParameter("idFacebook");
    	double puntAux = Double.parseDouble(in.getParameter("puntuacion"));
    	int puntuacion = (int)puntAux;
    	
    	return this.contenidoDao.puntuarContenido(titulo, empresa, idFacebook, puntuacion);
    }

    @GET
    @Path("/{empresa}/filtrar/{tipoContenido}/{categoria}")
    @Produces(MediaType.APPLICATION_JSON)
    public List <DatosContenido> filtrar(@PathParam("tipoContenido")String tipocont, @PathParam("categoria")Integer idcategoria, @PathParam("empresa")String empresa) {
        return this.contenidoDao.filtrar(tipocont,idcategoria, empresa);
    }
    
    @GET
    @Path("/{empresa}/filtrar/{titulo}")
    @Produces(MediaType.APPLICATION_JSON)
    public DatosContenido filtrarContenidoByTitulo(@PathParam("titulo")String titulo, @PathParam("empresa")String empresa) {
        return this.contenidoDao.filtrarPorTitulo(titulo, empresa);
    }

    @GET
    @Path("/cargarDatos")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean cargarDatos() {
        return this.contenidoDao.cargarDatos(true);
    }
    
    @GET
    @Path("/cargarDatosSinEmpresas")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean cargarDatosSinEmpresas() {
        return this.contenidoDao.cargarDatos(false);
    }
        
    @POST
    @Path("/crearTipoContenido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean crearTipoContenido(DatosTipoContenido in) {
        return this.contenidoDao.crearTipoContenido(in);
    }
    
    @POST
    @Path("/compartirContenido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean compartirContenido(DatosCompartirContenido in) {
        return this.contenidoDao.compartirContenido(in);
    }
    
    @POST
    @Path("/agregarCategoriaTipoContenido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean agregarCategoriaTipoContenido(DatosJson in) {
    	String nombreTC = in.getParameter("nombreTC");
    	String nombreCategoria = in.getParameter("nombreCategoria");
        return this.contenidoDao.agregarCategoriaTipoContenido(nombreTC, nombreCategoria);
    }
    
    @POST
    @Path("/agregarAtributoTipoContenido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean agregarAtributoTipoContenido(DatosJson in) {
    	
    	String nombreTC = in.getParameter("nombreTC");
    	String nombreAtributo = in.getParameter("nombreAtributo");
        return this.contenidoDao.agregarAtributoTipoContenido(nombreTC, nombreAtributo);
    }
    
    @POST
    @Path("/eliminarAtributoTipoContenido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean eliminarAtributoTipoContenido(DatosJson in) {
    	String nombreTC = in.getParameter("nombreTC");
    	int idAtributo = Integer.parseInt(in.getParameter("idAtributo"));
        return this.contenidoDao.eliminarAtributoTipoContenido(nombreTC, idAtributo);
    }
    
    @POST
    @Path("/eliminarCategoriaTipoContenido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean eliminarCategoriaTipoContenido(DatosJson in) {
    	String nombreTC = in.getParameter("nombreTC");
    	int idCategoria = Integer.parseInt(in.getParameter("idCategoria"));
        return this.contenidoDao.eliminarCategoriaTipoContenido(nombreTC, idCategoria);
    }
    
    @POST
    @Path("/eliminarTipoContenido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean eliminarTipoContenido(DatosJson in) {
    	String nombreTC = in.getParameter("nombreTC");
        return this.contenidoDao.eliminarTipoContenido(nombreTC);
    }
    
    @GET
    @Path("{empresa}/obtenerContenidos")
    @Produces(MediaType.APPLICATION_JSON)
    public List <DatosContenido> obtenerContenidos(@PathParam("empresa")String empresa) {
        return this.contenidoDao.obtenerContenidos(empresa);
    }
    
    @GET
    @Path("{empresa}/obtenerContenidosNoBloqueados")
    @Produces(MediaType.APPLICATION_JSON)
    public List <DatosContenido> obtenerContenidosNoBloqueados(@PathParam("empresa")String empresa) {
        return this.contenidoDao.obtenerContenidosNoBloqueados(empresa);
    }
    
    @POST
    @Path("/agregarContenido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean agregarContenido(DatosContenido contenido) {
        return this.contenidoDao.agregarContenido(contenido);
    }
    
    @POST
    @Path("/obtenerTiempoReproduccion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public int obtenerTiempoReproduccion(DatosJson in) {
        
    	String tituloContenido = in.getParameter("titulo");
		String idfacebookCliente = in.getParameter("idFacebook");
		String empresa = in.getParameter("empresa");
    	
    	return this.contenidoDao.obtenerTiempoReproduccion(tituloContenido, idfacebookCliente, empresa);
    }
	
	@POST
    @Path("/guardarTiempoReproduccion")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean guardarTiempoReproduccion(DatosJson in) {
		
		String tituloContenido = in.getParameter("titulo");
		String idfacebookCliente = in.getParameter("idFacebook");
		String tiempo = in.getParameter("tiempo");
		String empresa = in.getParameter("empresa");
		
		double tiempoAux = Double.parseDouble(tiempo);
		int tiempoEnSegundos = (int)tiempoAux;
		
	    int hours = (int) tiempoEnSegundos / 3600;
	    int remainder = (int) tiempoEnSegundos - hours * 3600;
	    int mins = remainder / 60;
	    remainder = remainder - mins * 60;
	    int secs = remainder;

		String segundosADateString = hours + ":" + mins + ":" + secs;

	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	    Date date = null;
	    try {
	    date = sdf.parse(segundosADateString);
	    } catch(Exception e) {
	    	return false;
	    }
		
		return this.contenidoDao.guardarTiempoReproduccion(tituloContenido, idfacebookCliente, date, empresa);
    }
   
    @GET
    @Path("/tipoContenido")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatosTipoContenido> obtenerTiposContenido() {
        return this.contenidoDao.obtenerTiposContenido();
    }
    
    @GET
    @Path("{empresa}/contenidosParaTransmitir")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatosContenido> obtenerContenidosParaTransmitir(@PathParam("empresa") String empresa) {
        return this.contenidoDao.obtenerContenidosParaTransmitir(empresa);
    }
    
    @POST
    @Path("/destacar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean destacarContenido(DatosJson in) {
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
        return this.contenidoDao.destacarContenido(titulo, empresa);
    }
    
    @POST
    @Path("/quitarDestacado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean quitarDestacadoContenido(DatosJson in) {
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
        return this.contenidoDao.quitarDestacado(titulo, empresa);
    }
    
    @POST
    @Path("/bloquear")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean bloquearContenido(DatosJson in) {
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
        return this.contenidoDao.bloquearContenido(titulo, empresa);
    }
    
    @POST
    @Path("/desbloquear")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean desbloquearContenido(DatosJson in) {
    	String titulo = in.getParameter("titulo");
    	String empresa = in.getParameter("empresa");
        return this.contenidoDao.desbloquearContenido(titulo, empresa);
    }
    
    
    
//  @POST
//  @Path("/obtenerSugeridos")
//  @Consumes(MediaType.TEXT_PLAIN)
//  @Produces(MediaType.APPLICATION_JSON)
//  public List<Contenido> obtenerSugeridos(String idfacebookCliente) {
//      return this.contenidoDao.obtenerSugeridos(idfacebookCliente);
//  }
    
//    @GET
//    @Path("/getTiposContenido")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<DatosTipoContenido> getTiposContenido(){
//    	return this.contenidoDao.obtenerTiposContenido();
//    }
    
//    @GET
//    @Path("/getCategorias")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<Categoria> getCategorias(){
//    	return this.contenidoDao.getCategorias();
//    }

//    @GET
//    @Path("/{titulo}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Contenido obtenerContenido(@PathParam("titulo") String titulo) {
//        return this.contenidoDao.getContenido(titulo);
//    }
 
//    @GET
//    @Path("/contenidosDestacados")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List <Contenido> obtenerContenidosDestacados() {
//        return this.contenidoDao.getContenidosDestacados();
//    }
    
//  /**TODO: implementar bien, igual no se necesita porque obtenerTipoContenido ya las retorna */
//  @POST
//  @Path("/getCategoriasTipoContenido")
//  @Consumes(MediaType.TEXT_PLAIN)
//  @Produces(MediaType.APPLICATION_JSON)
//  public List<Categoria> getCategoriasTipoContenido(int idCont){
//  	return this.contenidoDao.getCategoriasTipoContenido(idCont);
//  }
    
//    @GET
//    @Path("/contenidosNoBloqueados")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List <Contenido> obtenerContenidosNoNloqueados() {
//        return this.contenidoDao.getContenidosNoBloqueados();
//    }
    
//  @GET
//  @Path("/obtenerUrlContenido/{empresa}/{titulo}/{ipCLiente}")
//  @Produces(MediaType.TEXT_PLAIN)
//  public String obtenerUrlContenido(@PathParam("empresa")String empresa, @PathParam("titulo")String titulo, @PathParam("ipCLiente")String ipCliente) {
//      return this.contenidoDao.obtenerUrlContenido(empresa, titulo, ipCliente);
//  }
    
}