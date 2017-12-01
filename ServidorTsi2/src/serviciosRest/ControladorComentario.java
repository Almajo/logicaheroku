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

import Entidades.Cliente;
import Entidades.Comentario;
import daos.ClienteDao;
import daos.ComentarioDao;
import daos.ReporteDao;
import datatypes.DatosComentario;
import datatypes.DatosJson;

@Path("/comentario")
@Stateless
@LocalBean
public class ControladorComentario {
	
	@EJB
    private ComentarioDao comentarioDao;

	@GET
    @Path("/{empresa}/obtenerComentarios/{titulo}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DatosComentario> obtenerComentarios(@PathParam("titulo") String titulo, @PathParam("empresa") String empresa) {
        return this.comentarioDao.obtenerComentarios(titulo, empresa);
    }
	
	@POST
    @Path("/denunciarSpoiler")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean denunciarComentario(DatosJson in) {
		int idComentario = Integer.parseInt(in.getParameter("idComentario"));
		String idFacebook = in.getParameter("idFacebook");
		String empresa = in.getParameter("empresa");
    	return this.comentarioDao.denunciarComentario(idFacebook, idComentario, empresa);
    }
	
	@POST
    @Path("/agregarComentario")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean agregarComentario(DatosJson in) {
		String texto = in.getParameter("texto");
		String titulo = in.getParameter("titulo");
		String idFacebook = in.getParameter("idFacebook");
		String empresa = in.getParameter("empresa");
    	return this.comentarioDao.agregarComentario(idFacebook, titulo, texto, empresa);
    }
		
	
	
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
