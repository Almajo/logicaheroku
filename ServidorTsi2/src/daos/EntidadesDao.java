package daos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Hibernate;

import Entidades.AtributoContenido;
import Entidades.Categoria;
import Entidades.Cliente;
import Entidades.Contenido;
import Entidades.Empresa;
import Entidades.Suscripcion;
import Entidades.Suscripcion_Cliente;
import datatypes.DatosAtributoContenido;
import datatypes.DatosCliente;
import datatypes.DatosContenido;
import datatypes.DatosIdNombre;
import datatypes.DatosSuscripcion;
import datatypes.DatosTipoContenido;

@Stateless
@LocalBean
public class EntidadesDao {
	
	 @PersistenceContext
	    private EntityManager em;
	 
	 
	 
	 public Boolean suscripcionEstaVigente(String idFacebook, String empresa) {
	    	try {
	    		//Cliente cli = em.find(Cliente.class, idFacebook);
	    		Cliente cli = getCliente(idFacebook, empresa);
	    		Date hoy = new Date();
	    		if(cli.getFechaFinSuscripcion() == null) {
	    			return false;
	    		} else if(hoy.after(cli.getFechaFinSuscripcion())) {
	    			return false;
	    		} else {
	    			return true;
	    		}
	    	} catch (Exception e) {
	    		return null;
	    	}
	    }
	 
	 public Suscripcion getSuscripcion(String tipoSuscripcion, String empresa) {
		 List<Suscripcion> suscripciones = null;
		 Suscripcion retorno = null;
			try
		    {
				Query query;
				
		    		query = em.createQuery("FROM Suscripcion s WHERE nombre_empresa=:empresa AND tipo=:tipoSuscripcion");
		    		query.setParameter("empresa", empresa);
		    		query.setParameter("tipoSuscripcion", tipoSuscripcion);
		    	
		    		suscripciones = (ArrayList<Suscripcion>) query.getResultList();
		    		
		    		if(suscripciones!=null && suscripciones.size()==1) {
		    			retorno = suscripciones.get(0);
		    		}
		    		
		    }
			catch(Exception e)
		    {
				System.out.println(e.getMessage());
				return null;
		    }
			
			return retorno;
	 }
	 
	 public Contenido getContenido(String titulo, String empresa) {
	    	
	    	List<Contenido> contenidos = null;
	    	Contenido retorno = null;
			try
		    {
				Query query;
				
		    		query = em.createQuery("FROM Contenido c WHERE nombre_empresa=:empresa AND titulo=:titulo");
		    		query.setParameter("empresa", empresa);
		    		query.setParameter("titulo", titulo);
		    	
		    		contenidos = (ArrayList<Contenido>) query.getResultList();
		    		
		    		if(contenidos!=null && contenidos.size()==1) {
		    			retorno = contenidos.get(0);
		    		}
		    		
		    }
			catch(Exception e)
		    {
				System.out.println(e.getMessage());
				return null;
		    }
			
			return retorno;
	    	
	    }
	 
	 public Cliente getCliente(String idfacebook, String empresa) {
	    	
	    	List<Cliente> clientes = null;
	    	Cliente retorno = null;
			try
		    {
				Query query;
				
		    		query = em.createQuery("FROM Cliente c WHERE c.empresa.nombre=:empresa AND c.idfacebook=:idfacebook");
		    		query.setParameter("empresa", empresa);
		    		query.setParameter("idfacebook", idfacebook);
		    	
		    		clientes = (ArrayList<Cliente>) query.getResultList();
		    		
		    		if(clientes!=null && clientes.size()==1) {
		    			retorno = clientes.get(0);
		    		}
		    		
		    }
			catch(Exception e)
		    {
				System.out.println(e.getMessage());
				return null;
		    }
			
			return retorno;
	    	
	    }

}
