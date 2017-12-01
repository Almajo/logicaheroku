
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
import daos.EmpresaDao;
import datatypes.DatosContenido;
import datatypes.DatosIngresoMensual;
import datatypes.DatosJson;
import datatypes.DatosPuntuacionContenido;
import datatypes.DatosSuscripcion;
import datatypes.DatosTipoContenido;

@Path("/empresa")
@Stateless
@LocalBean
public class ControladorEmpresa {

    @EJB
    private EmpresaDao empresaDao;
    
    @GET
    @Path("/{empresa}/obtenerSuscripciones")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatosSuscripcion> obtenerSuscripciones(@PathParam("empresa")String empresa) {
        return this.empresaDao.obtenerTiposSuscripcionEmpresa(empresa);
    }
    
    @GET
    @Path("/{empresa}/obtenerTopFiveContenidos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatosPuntuacionContenido> filtrar(@PathParam("empresa")String empresa) {
        return this.empresaDao.obtenerTopFiveContenidos(empresa);
    }
    
    @GET
    @Path("/{empresa}/obtenerIngresosSuscripcionesMensuales")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatosIngresoMensual> obtenerIngresosSuscripcionesMensuales(@PathParam("empresa")String empresa) {
        return this.empresaDao.obtenerIngresosSuscripcionesMensuales(empresa);
    }

    @GET
    @Path("/obtenerEmpresas")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> obtenerEmpresas() {
        return this.empresaDao.obtenerEmpresas();
    }
    
    @POST
    @Path("/agregarEmpresa")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean agregarEmpresa(DatosJson in) {
    	String empresa = in.getParameter("empresa");
        return this.empresaDao.agregarEmpresa(empresa);
    }
    
    @POST
    @Path("/agregarSuscripcion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean agregarSuscripcion(DatosJson in) {
    	String empresa = in.getParameter("empresa");
    	String tipo = in.getParameter("tipo");
    	String precio = in.getParameter("precio");
    	
    	double precioAux = Double.parseDouble(precio);
    	
        return this.empresaDao.agregarSuscripcion(empresa, tipo, precioAux);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}
