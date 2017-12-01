package daos;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.PathParam;

import org.hibernate.Hibernate;

import Entidades.Cliente;
import Entidades.Comentario;
import Entidades.Contenido;
import Entidades.EstadoVisualizacion;
import Entidades.TipoContenido;
import datatypes.DatosComentario;

@Stateless
@LocalBean
public class ComentarioDao {

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private EntidadesDao entidades;
    
    
    
    public List<DatosComentario> obtenerComentarios(String titulo, String empresa){
    	Contenido cont = entidades.getContenido(titulo, empresa);
    	Hibernate.initialize(cont.getComentarios());
    	
    	List<DatosComentario> retorno = new LinkedList<DatosComentario>();
    	for(Comentario com : cont.getComentarios()) {
    		retorno.add(new DatosComentario(com.getId(), com.getMensaje(), com.getFecha(), com.getSpoilerCount(), com.getCliente().getIdfacebook(), com.getContenido().getTitulo(), com.getCliente().getNombre()));
    	}
    	
    	return retorno;
    }

    public boolean denunciarComentario(String idfacebookCliente, int idComentario, String empresa) {
    	try {
    	//Cliente cli = em.find(Cliente.class, idfacebookCliente);
    	Cliente cli = entidades.getCliente(idfacebookCliente, empresa);
    	if(cli.isBloqueado() || !entidades.suscripcionEstaVigente(idfacebookCliente, empresa)) {
    		return false;
    	}
    	Comentario com = em.find(Comentario.class, idComentario);
    	com.setSpoilerCount(com.getSpoilerCount() + 1);
    	cli.addComentarioDenunciado(com);
    	em.persist(cli);
    	} catch(Exception e) {
    		System.out.println("ERROR en comentarioDao->denunciarComentario: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
    public boolean agregarComentario(String idfacebookCliente, String tituloContenido, String texto, String empresa) {
    	try {
        	//Cliente cli = em.find(Cliente.class, idfacebookCliente);
        	Cliente cli = entidades.getCliente(idfacebookCliente, empresa);
        	if(cli.isBloqueado() || !entidades.suscripcionEstaVigente(idfacebookCliente, empresa)) {
        		return false;
        	}
        	//Contenido cont = em.find(Contenido.class, tituloContenido);
        	Contenido cont = entidades.getContenido(tituloContenido, empresa);
        	Comentario coment = new Comentario(texto, new Date(), 0, cli, cont);
        	em.persist(coment);
        	} catch(Exception e) {
        		System.out.println("ERROR en comentarioDao->agregarComentario: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
        		return false;
        	}
        	return true;
    }
    
    
    
    
    
    
    
}
