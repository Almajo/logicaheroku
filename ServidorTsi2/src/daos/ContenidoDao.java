package daos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Hibernate;

import Entidades.AdminEmpresa;
import Entidades.AtributoContenido;
import Entidades.AtributoTipoContenido;
import Entidades.Categoria;
import Entidades.Cliente;
import Entidades.Comentario;
import Entidades.Contenido;
import Entidades.Empresa;
import Entidades.EstadoVisualizacion;
import Entidades.Notificacion;
import Entidades.PayPerView;
import Entidades.Ranking;
import Entidades.Suscripcion;
import Entidades.Suscripcion_Cliente;
import Entidades.TipoContenido;
import datatypes.DatosIdNombre;
import datatypes.DatosSuscripcion;
import datatypes.DatosAtributoContenido;
import datatypes.DatosCompartirContenido;
import datatypes.DatosContenido;
import datatypes.DatosTipoContenido;

@Stateless
@LocalBean
public class ContenidoDao {

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private EntidadesDao entidades;
    @EJB
    private ClienteDao clienteDao;
    
    public Boolean contenidoEsPPV(String titulo, String empresa) {
    	Contenido cont = entidades.getContenido(titulo, empresa);
		Hibernate.initialize(cont.getPayPerView());
		PayPerView ppv = cont.getPayPerView();
		
		return ppv != null;
    }
    
    public Double obtenerPuntuacionClienteContenido(String idFacebook, String empresa, String titulo){
    	Double retorno = null;
    	
    	Query query = em.createQuery("SELECT r FROM Ranking r WHERE r.contenido.empresa.nombre=:empresa AND r.contenido.titulo=:titulo AND r.cliente.idfacebook=:idFacebook");
    	query.setParameter("titulo", titulo);
    	query.setParameter("idFacebook", idFacebook);
    	query.setParameter("empresa", empresa);
    	
    	List<Ranking> rankings = (ArrayList<Ranking>)query.getResultList();
    	
    	if(rankings!=null && rankings.size()==1) {
    		Ranking ran = rankings.get(0);
    		retorno = ran.getPuntuacion()*1.0;
    	} 
    	
    	return retorno;    	
    }
    
    public List<DatosContenido> obtenerContenidosParaTransmitir(String empresa){
    	
    	List<Contenido> contenidos = null;
		List<DatosContenido> retorno = new LinkedList<DatosContenido>();
		try
	    {
			Query query = em.createQuery("FROM Contenido c WHERE nombre_empresa=:empresa AND (c.tipoContenido.vivo IS TRUE) AND (c.urlLive IS NULL)");
    		query.setParameter("empresa", empresa);
	       	contenidos = (ArrayList<Contenido>) query.getResultList();
	    
			for(Contenido con : contenidos) {
				DatosContenido conAux = new DatosContenido();
				conAux.setDescripcion(con.getDescripcion());
				conAux.setTitulo(con.getTitulo());
				conAux.setBloqueado(con.isBloqueado());
				conAux.setDestacado(con.isDestacado());
				conAux.setPortada(con.getPortada());
				Hibernate.initialize(con.getElenco());			
				conAux.setElenco(con.getElenco());
				Hibernate.initialize(con.getDirectores());	
				conAux.setDirectores(con.getDirectores());
				conAux.setCantPuntuaciones(con.getCantPuntuaciones());
				conAux.setPuntuacion(con.getPuntuacion());
				Hibernate.initialize(con.getTipoContenido());
				conAux.setTipoContenido(new DatosTipoContenido(con.getTipoContenido().getNombre(), null, null, con.getTipoContenido().isVivo()));
				conAux.setUrl(con.getUrl());
				conAux.setEmpresa(con.getEmpresa().getNombre());
				Hibernate.initialize(con.getPayPerView());
				conAux.setUrlLive(con.getUrlLive());
				
				if(con.getPayPerView() != null) {
					conAux.setPrecioPayPerView(con.getPayPerView().getPrecio());
				} else {
					conAux.setPrecioPayPerView(null);
				}
				
				
				Hibernate.initialize(con.getCategorias());
				List<DatosIdNombre> categoriasConAux = new LinkedList<DatosIdNombre>();
				for(Categoria cat : con.getCategorias()) {
					DatosIdNombre catAux = new DatosIdNombre(cat.getId(), cat.getNombre());
					categoriasConAux.add(catAux);
				}
				conAux.setCategorias(categoriasConAux);
				
				Hibernate.initialize(con.getAtributos());
				List<DatosAtributoContenido> atributosConAux = new LinkedList<DatosAtributoContenido>();
				for(AtributoContenido atr : con.getAtributos()) {
					DatosAtributoContenido atrAux = new DatosAtributoContenido(atr.getId(), atr.getValor());
					atributosConAux.add(atrAux);
				}
				conAux.setAtributos(atributosConAux);
				
				retorno.add(conAux);
			}
		
	    } catch(Exception e) {
			System.out.println("ERROR en contenidoDao->agregarAtributoTipoContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
	    }
		
	   	return retorno;
    	
    }
    
    public Boolean compartirContenido(DatosCompartirContenido datosCompartir) {
    	try {
    		Empresa emp = em.find(Empresa.class, datosCompartir.getEmpresa());
        	Cliente elQueComparte = entidades.getCliente(datosCompartir.getIdFacebookQueComparte(), datosCompartir.getEmpresa());
        	Hibernate.initialize(elQueComparte.getEmpresa());
        	if(elQueComparte.isBloqueado() || !entidades.suscripcionEstaVigente(elQueComparte.getIdfacebook(), elQueComparte.getEmpresa().getNombre())) {
        		return false;
        	}
        	Contenido compartido = entidades.getContenido(datosCompartir.getTitulo(), datosCompartir.getEmpresa());
        	
        	for(String idFacebookReceptor : datosCompartir.getIdFacebookReceptores()) {
        		Cliente receptor = entidades.getCliente(idFacebookReceptor, datosCompartir.getEmpresa());
        		Notificacion notificacion = new Notificacion(elQueComparte, receptor, compartido);
        		em.persist(notificacion);
        	}
    	} catch(Exception e) {
    		System.out.println("ERROR en contenidoDao->compartirContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	
    	
    	return true;
    }
    
    public DatosContenido obtenerURL(String titulo, String empresa){
    	
    	String retorno = "";
    	List<Contenido> contenidos = null;
    	DatosContenido ret = new DatosContenido();
		try
	    {
			Query query = em.createQuery("FROM Contenido c WHERE nombre_empresa=:empresa AND titulo=:titulo");
    		query.setParameter("empresa", empresa);
    		query.setParameter("titulo", titulo);
			
	       	contenidos = (ArrayList<Contenido>) query.getResultList();
	       	
	       	if(contenidos!=null && contenidos.size()==1) {
	       		Contenido con = contenidos.get(0);
	       		ret.setUrlLive(con.getUrlLive());
	       		//return con.getUrlLive();
	       	}
	    }
		catch(Exception e)
	    {
			System.out.println("ERROR en contenidoDao->obtenerURL: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
			return null;
	    }
		return ret;
    }
    
    public Boolean setearURL(String titulo, String empresa, String url) {
    	
    	List<Contenido> contenidos = null;
    	List<Contenido> contenidos2 = null;
		try
	    {
			Query query = em.createQuery("FROM Contenido c WHERE nombre_empresa=:empresa AND titulo=:titulo");
    		query.setParameter("empresa", empresa);
    		query.setParameter("titulo", titulo);
			
	       	contenidos = (ArrayList<Contenido>) query.getResultList();
	       	
	       	if(contenidos!=null && contenidos.size()==1) {
	       		Contenido con = contenidos.get(0);
	       		con.setUrlLive(url);
	       		
	       		Query query2 = em.createQuery("FROM Contenido c WHERE nombre_empresa=:empresa AND titulo=:titulo");
	    		query2.setParameter("empresa", "mantel");
	    		query2.setParameter("titulo", titulo);
				
		       	contenidos2 = (ArrayList<Contenido>) query2.getResultList();
		       	
		       	if(contenidos2!=null && contenidos2.size()==1) {
		       		Contenido con2 = contenidos2.get(0);
		       		con2.setUrlLive(url);
		       		em.persist(con);
		       		em.persist(con2);
		       	}
	       		
	       	}

       		return true;
	       	
	    }
		catch(Exception e)
	    {
			System.out.println("ERROR en contenidoDao->setearURL: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
	    }
		return false;
    }
    
    public DatosContenido puntuarContenido(String titulo, String empresa, String idFacebook, int puntuacion) {
    	
    	DatosContenido puntuacionRetorno = new DatosContenido();
    	puntuacionRetorno.setPuntuacion(0.0);
    	
    	try {
    		
    		Cliente cli = entidades.getCliente(idFacebook, empresa);
    		if(cli.isBloqueado() || !entidades.suscripcionEstaVigente(idFacebook, empresa)) {
	    		return puntuacionRetorno;
	    	}
    		
	    	Query query = em.createQuery("SELECT r FROM Ranking r WHERE r.contenido.empresa.nombre=:empresa AND r.contenido.titulo=:titulo AND r.cliente.idfacebook=:idFacebook");
	    	query.setParameter("titulo", titulo);
	    	query.setParameter("idFacebook", idFacebook);
	    	query.setParameter("empresa", empresa);
	
	    	List<Ranking> rankings = (ArrayList<Ranking>)query.getResultList();
	    	
	    	if(rankings!=null && rankings.size()==1) {
	    		Ranking ran = rankings.get(0);
	    		double puntuacionVieja = ran.getPuntuacion();
	    		ran.setPuntuacion(puntuacion);
	    		em.persist(ran);
	    		//tengo que recalcular el puntaje del contenido
	    		
	    		Contenido cont = entidades.getContenido(titulo, empresa);
	    		double puntAux = cont.getPuntuacion();
    			int cantidadVotos = cont.getCantPuntuaciones();
    			double sumaVotos = puntAux*cantidadVotos;
    			sumaVotos = sumaVotos - puntuacionVieja + puntuacion;
    			puntAux = sumaVotos/cantidadVotos;
    			
    			cont.setPuntuacion(puntAux);
    			puntuacionRetorno.setPuntuacion(puntAux);
    			puntuacionRetorno.setCantPuntuaciones(cantidadVotos);

	    	} else {
	    		Contenido cont = entidades.getContenido(titulo, empresa);
	    		
	    		Ranking ran = new Ranking(puntuacion, cont, cli);
	    		
	    		if(cont.getPuntuacion() == null) {
	    			puntuacionRetorno.setPuntuacion(puntuacion*1.0);
	    			puntuacionRetorno.setCantPuntuaciones(cont.getCantPuntuaciones()+1);
	    			cont.setPuntuacion((double)puntuacion);
	    			cont.setCantPuntuaciones(cont.getCantPuntuaciones()+1);
	    		} else {
	    			double puntAux = cont.getPuntuacion();
	    			int cantidadVotos = cont.getCantPuntuaciones();
	    			double sumaVotos = puntAux*cantidadVotos;
	    			sumaVotos = sumaVotos + puntuacion;
	    			cantidadVotos++;
	    			puntAux = sumaVotos/cantidadVotos;
	    			
	    			cont.setPuntuacion(puntAux);
	    			cont.setCantPuntuaciones(cantidadVotos);
	    			puntuacionRetorno.setPuntuacion(puntAux);
	    			puntuacionRetorno.setCantPuntuaciones(cantidadVotos);
	    		}
	    		
	    		em.persist(cont);
	    		em.persist(ran);
	    		
	    	}
    	}catch(Exception e) {
    		System.out.println("ERROR en contenidoDao->puntuarContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		DatosContenido dc = new DatosContenido();
    		dc.setPuntuacion(0.0);
    		return dc;
    	}
		return puntuacionRetorno;
    }
    	
    
    
    public DatosContenido filtrarPorTitulo (String titulo, String empresa){
    	List<Contenido> contenidos = null;
    	DatosContenido conAux = null;
    	try {
    		
    		Query query = em.createQuery("SELECT c FROM Contenido c WHERE c.titulo=:titulo AND c.empresa.nombre=:empresa");
    		query.setParameter("titulo", titulo);
    		query.setParameter("empresa", empresa);
        	
    		contenidos = (ArrayList<Contenido>) query.getResultList();
    		
    		if (!contenidos.isEmpty()){
    			Contenido con = contenidos.get(0);
    			
    			conAux = new DatosContenido();
    			
    			conAux.setDescripcion(con.getDescripcion());
				conAux.setTitulo(con.getTitulo());
				conAux.setBloqueado(con.isBloqueado());
				conAux.setDestacado(con.isDestacado());
				Hibernate.initialize(con.getElenco());			
				conAux.setElenco(con.getElenco());
				Hibernate.initialize(con.getDirectores());	
				conAux.setDirectores(con.getDirectores());
				conAux.setCantPuntuaciones(con.getCantPuntuaciones());
				conAux.setPuntuacion(con.getPuntuacion());
				Hibernate.initialize(con.getTipoContenido());
				conAux.setTipoContenido(new DatosTipoContenido(con.getTipoContenido().getNombre(), null, null, con.getTipoContenido().isVivo()));
				conAux.setUrl(con.getUrl());
				conAux.setPortada(con.getPortada());
				Hibernate.initialize(con.getPayPerView());
				if(con.getPayPerView() != null) {
					conAux.setPrecioPayPerView(con.getPayPerView().getPrecio());
				}
				Hibernate.initialize(con.getEmpresa());
				conAux.setEmpresa(con.getEmpresa().getNombre());
				
				Hibernate.initialize(con.getCategorias());
				List<DatosIdNombre> categoriasConAux = new LinkedList<DatosIdNombre>();
				for(Categoria cat : con.getCategorias()) {
					DatosIdNombre catAux = new DatosIdNombre(cat.getId(), cat.getNombre());
					categoriasConAux.add(catAux);
				}
				conAux.setCategorias(categoriasConAux);
				
				Hibernate.initialize(con.getAtributos());
				List<DatosAtributoContenido> atributosConAux = new LinkedList<DatosAtributoContenido>();
				for(AtributoContenido atr : con.getAtributos()) {
					DatosAtributoContenido atrAux = new DatosAtributoContenido(atr.getId(), atr.getValor());
					atributosConAux.add(atrAux);
				}
				conAux.setAtributos(atributosConAux);
    			
    		}
		} catch (Exception e) {
			System.out.println("ERROR en contenidoDao->filtrarPorTitulo: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
			return null;
		}
    	return conAux;
    }
    
    public List<DatosContenido> filtrar(String tipocont, Integer idcategoria, String empresa){
    	List<DatosContenido> retorno = new LinkedList<DatosContenido>();
    	
    	try {
    		Query query = em.createQuery("FROM Contenido c WHERE nombre_empresa=:empresa AND nombre_tipocontenido=:tipocont");
       		query.setParameter("tipocont", tipocont);
    		query.setParameter("empresa", empresa);

			List<Contenido> contenidos = null;
			contenidos = (ArrayList<Contenido>) query.getResultList();
			
			if(idcategoria == -1) {
				for(Contenido con : contenidos) {
					DatosContenido conAux = new DatosContenido();
					conAux.setDescripcion(con.getDescripcion());
					conAux.setTitulo(con.getTitulo());
					conAux.setBloqueado(con.isBloqueado());
					conAux.setDestacado(con.isDestacado());
					Hibernate.initialize(con.getElenco());			
					conAux.setElenco(con.getElenco());
					Hibernate.initialize(con.getDirectores());	
					conAux.setDirectores(con.getDirectores());
					conAux.setCantPuntuaciones(con.getCantPuntuaciones());
					conAux.setPuntuacion(con.getPuntuacion());
					Hibernate.initialize(con.getTipoContenido());
					conAux.setTipoContenido(new DatosTipoContenido(con.getTipoContenido().getNombre(), null, null, con.getTipoContenido().isVivo()));
					conAux.setUrl(con.getUrl());
					conAux.setPortada(con.getPortada());
					Hibernate.initialize(con.getPayPerView());
					if(con.getPayPerView() != null) {
						conAux.setPrecioPayPerView(con.getPayPerView().getPrecio());
					}
					Hibernate.initialize(con.getEmpresa());
					conAux.setEmpresa(con.getEmpresa().getNombre());
					conAux.setUrlLive(con.getUrlLive());
	
					Hibernate.initialize(con.getCategorias());
					List<DatosIdNombre> categoriasConAux = new LinkedList<DatosIdNombre>();
					for(Categoria cat : con.getCategorias()) {
						DatosIdNombre catAux = new DatosIdNombre(cat.getId(), cat.getNombre());
						categoriasConAux.add(catAux);
					}
					conAux.setCategorias(categoriasConAux);
					
					Hibernate.initialize(con.getAtributos());
					List<DatosAtributoContenido> atributosConAux = new LinkedList<DatosAtributoContenido>();
					for(AtributoContenido atr : con.getAtributos()) {
						DatosAtributoContenido atrAux = new DatosAtributoContenido(atr.getId(), atr.getValor());
						atributosConAux.add(atrAux);
					}
					conAux.setAtributos(atributosConAux);
					
					retorno.add(conAux);
				}
			} else {
				
				boolean descartar;
				
				for(Contenido con : contenidos) {
					descartar = true;
					Hibernate.initialize(con.getCategorias());
					for(Categoria cat : con.getCategorias()) {
						if(cat.getId()==idcategoria) {
							descartar = false;
						}
					}
					
					
					if(!descartar) {
					
						DatosContenido conAux = new DatosContenido();
						conAux.setDescripcion(con.getDescripcion());
						conAux.setTitulo(con.getTitulo());
						conAux.setBloqueado(con.isBloqueado());
						conAux.setDestacado(con.isDestacado());
						Hibernate.initialize(con.getElenco());			
						conAux.setElenco(con.getElenco());
						Hibernate.initialize(con.getDirectores());	
						conAux.setDirectores(con.getDirectores());
						conAux.setCantPuntuaciones(con.getCantPuntuaciones());
						conAux.setPuntuacion(con.getPuntuacion());
						Hibernate.initialize(con.getTipoContenido());
						conAux.setTipoContenido(new DatosTipoContenido(con.getTipoContenido().getNombre(), null, null, con.getTipoContenido().isVivo()));
						conAux.setUrl(con.getUrl());
						conAux.setPortada(con.getPortada());
						Hibernate.initialize(con.getPayPerView());
						if(con.getPayPerView() != null) {
							conAux.setPrecioPayPerView(con.getPayPerView().getPrecio());
						}
						Hibernate.initialize(con.getEmpresa());
						conAux.setEmpresa(con.getEmpresa().getNombre());
						conAux.setUrlLive(con.getUrlLive());
						
						Hibernate.initialize(con.getCategorias());
						List<DatosIdNombre> categoriasConAux = new LinkedList<DatosIdNombre>();
						for(Categoria cat : con.getCategorias()) {
							DatosIdNombre catAux = new DatosIdNombre(cat.getId(), cat.getNombre());
							categoriasConAux.add(catAux);
						}
						conAux.setCategorias(categoriasConAux);
						
						Hibernate.initialize(con.getAtributos());
						List<DatosAtributoContenido> atributosConAux = new LinkedList<DatosAtributoContenido>();
						for(AtributoContenido atr : con.getAtributos()) {
							DatosAtributoContenido atrAux = new DatosAtributoContenido(atr.getId(), atr.getValor());
							atributosConAux.add(atrAux);
						}
						conAux.setAtributos(atributosConAux);
						
						retorno.add(conAux);
					}
				}
			}
    	} catch(Exception e) {
    		System.out.println("ERROR en contenidoDao->filtrar: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    	}
		return retorno;
		
    }
    
    public boolean crearTipoContenido(DatosTipoContenido dtc) {
    	
    	try {
    		TipoContenido tc = new TipoContenido(null, dtc.getNombre(), null, dtc.isVivo());

    		for(DatosIdNombre cat : dtc.getCategorias()) {
    			Categoria cate = new Categoria(cat.getNombre());
    			tc.addCategoria(cate);
    		}
    		
    		for(DatosIdNombre atr : dtc.getAtributos()) {
    			AtributoTipoContenido atrib = new AtributoTipoContenido(atr.getNombre());
    			tc.addAtributo(atrib);
    		}
    		
        	em.persist(tc);
    	} catch (Exception e) {
			System.out.println("ERROR en contenidoDao->crearTipoContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    	
    }
        
    public boolean eliminarTipoContenido(String nombreTC){
    	try {

    		TipoContenido target = em.find(TipoContenido.class, nombreTC);
    		
    		if(target != null) {
    			//consulta nativa a ver si existe alguna tupla en la tabla atributocontenido que tenga id_atributotipocontenido con valor taget.getId
    			Query q = em.createNativeQuery("SELECT * FROM contenido WHERE nombre_tipocontenido=:nombreTC");
    			q.setParameter("nombreTC", nombreTC);
    			List<Object[]> results = q.getResultList();
    			
    			//si no existe entonces procedo a borrar
    			if(results == null || results.size()==0) {
    				  em.remove(target);
    			}
    			
    		} 
    		
    	} catch (Exception e) {
			System.out.println("ERROR en contenidoDao->eliminarTipoContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
    
    public boolean eliminarCategoriaTipoContenido(String nombreTC, int idCategoria){
    	try {
    		
    		Categoria target = em.find(Categoria.class, idCategoria);
    		
    		if(target != null) {
    			//consulta nativa a ver si existe alguna tupla en la tabla atributocontenido que tenga id_atributotipocontenido con valor taget.getId
    			Query q = em.createNativeQuery("SELECT * FROM categoria_contenido WHERE id_categoria=:id");
    			q.setParameter("id", idCategoria);
    			List<Object[]> results = q.getResultList();
    			
    			//si no existe entonces procedo a borrar
    			if(results == null || results.size()==0) {
    				  em.remove(target);
    			}
    			
    		} 
    		
    	} catch (Exception e) {
			System.out.println("ERROR en contenidoDao->eliminarCategoriaTipoContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
    
    public boolean eliminarAtributoTipoContenido(String nombreTC, int idAtributo){
    	try {
    		
    		AtributoTipoContenido target = em.find(AtributoTipoContenido.class, idAtributo);
    		
    		if(target != null) {
    			//consulta nativa a ver si existe alguna tupla en la tabla atributocontenido que tenga id_atributotipocontenido con valor taget.getId
    			Query q = em.createNativeQuery("SELECT * FROM atributocontenido WHERE id_atributotipocontenido=:id");
    			q.setParameter("id", idAtributo);
    			List<Object[]> results = q.getResultList();
    			
    			//si no existe entonces procedo a borrar
    			if(results == null || results.size()==0) {
    				  em.remove(target);
    			}
    			
    		} 
    		
    	} catch (Exception e) {
			System.out.println("ERROR en contenidoDao->eliminarAtributoTipoContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
    public boolean agregarCategoriaTipoContenido(String nombreTC, String nombreCategoria){
    	try {
    		TipoContenido tc = em.find(TipoContenido.class, nombreTC);
        	Categoria cat = new Categoria(nombreCategoria);
        	tc.addCategoria(cat);
        	em.persist(tc);
    	} catch (Exception e) {
			System.out.println("ERROR en contenidoDao->agregarCategoriaTipoContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
    public boolean agregarAtributoTipoContenido(String nombreTC, String nombreAtributo){
    	try {
    		TipoContenido tc = em.find(TipoContenido.class, nombreTC);
        	AtributoTipoContenido atr = new AtributoTipoContenido(nombreAtributo);
        	tc.addAtributo(atr);
        	em.persist(tc);
    	} catch (Exception e) {
			System.out.println("ERROR en contenidoDao->agregarAtributoTipoContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
	public List<DatosContenido> obtenerContenidos(String empresa)
	{
		List<Contenido> contenidos = null;
		List<DatosContenido> retorno = new LinkedList<DatosContenido>();
		try
	    {
			Query query = em.createQuery("FROM Contenido c WHERE nombre_empresa=:empresa");
    		query.setParameter("empresa", empresa);
	       	contenidos = (ArrayList<Contenido>) query.getResultList();
	    
			for(Contenido con : contenidos) {
				DatosContenido conAux = new DatosContenido();
				conAux.setDescripcion(con.getDescripcion());
				conAux.setTitulo(con.getTitulo());
				conAux.setBloqueado(con.isBloqueado());
				conAux.setDestacado(con.isDestacado());
				conAux.setPortada(con.getPortada());
				Hibernate.initialize(con.getElenco());			
				conAux.setElenco(con.getElenco());
				Hibernate.initialize(con.getDirectores());	
				conAux.setDirectores(con.getDirectores());
				conAux.setCantPuntuaciones(con.getCantPuntuaciones());
				conAux.setPuntuacion(con.getPuntuacion());
				Hibernate.initialize(con.getTipoContenido());
				conAux.setTipoContenido(new DatosTipoContenido(con.getTipoContenido().getNombre(), null, null, con.getTipoContenido().isVivo()));
				conAux.setUrl(con.getUrl());
				conAux.setEmpresa(con.getEmpresa().getNombre());
				Hibernate.initialize(con.getPayPerView());
				conAux.setUrlLive(con.getUrlLive());
				
				if(con.getPayPerView() != null) {
					conAux.setPrecioPayPerView(con.getPayPerView().getPrecio());
				} else {
					conAux.setPrecioPayPerView(null);
				}
				
				
				Hibernate.initialize(con.getCategorias());
				List<DatosIdNombre> categoriasConAux = new LinkedList<DatosIdNombre>();
				for(Categoria cat : con.getCategorias()) {
					DatosIdNombre catAux = new DatosIdNombre(cat.getId(), cat.getNombre());
					categoriasConAux.add(catAux);
				}
				conAux.setCategorias(categoriasConAux);
				
				Hibernate.initialize(con.getAtributos());
				List<DatosAtributoContenido> atributosConAux = new LinkedList<DatosAtributoContenido>();
				for(AtributoContenido atr : con.getAtributos()) {
					DatosAtributoContenido atrAux = new DatosAtributoContenido(atr.getId(), atr.getValor());
					atributosConAux.add(atrAux);
				}
				conAux.setAtributos(atributosConAux);
				
				retorno.add(conAux);
			}
		
	    } catch(Exception e) {
			System.out.println("ERROR en contenidoDao->agregarAtributoTipoContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
	    }
		
	   	return retorno;
	}
	
	public List<DatosContenido> obtenerContenidosNoBloqueados(String empresa)
	{
		List<Contenido> contenidos = null;
		List<DatosContenido> retorno = new LinkedList<DatosContenido>();
		try
	    {
			Query query = em.createQuery("FROM Contenido c WHERE nombre_empresa=:empresa AND bloqueado IS FALSE");
    		query.setParameter("empresa", empresa);
	       	contenidos = (ArrayList<Contenido>) query.getResultList();
	    
			for(Contenido con : contenidos) {
				DatosContenido conAux = new DatosContenido();
				conAux.setDescripcion(con.getDescripcion());
				conAux.setTitulo(con.getTitulo());
				conAux.setBloqueado(con.isBloqueado());
				conAux.setDestacado(con.isDestacado());
				conAux.setPortada(con.getPortada());
				Hibernate.initialize(con.getElenco());			
				conAux.setElenco(con.getElenco());
				Hibernate.initialize(con.getDirectores());	
				conAux.setDirectores(con.getDirectores());
				conAux.setCantPuntuaciones(con.getCantPuntuaciones());
				conAux.setPuntuacion(con.getPuntuacion());
				Hibernate.initialize(con.getTipoContenido());
				conAux.setTipoContenido(new DatosTipoContenido(con.getTipoContenido().getNombre(), null, null, con.getTipoContenido().isVivo()));
				conAux.setUrl(con.getUrl());
				conAux.setEmpresa(con.getEmpresa().getNombre());
				Hibernate.initialize(con.getPayPerView());
				conAux.setUrlLive(con.getUrlLive());
				
				if(con.getPayPerView() != null) {
					conAux.setPrecioPayPerView(con.getPayPerView().getPrecio());
				} else {
					conAux.setPrecioPayPerView(null);
				}
				
				
				Hibernate.initialize(con.getCategorias());
				List<DatosIdNombre> categoriasConAux = new LinkedList<DatosIdNombre>();
				for(Categoria cat : con.getCategorias()) {
					DatosIdNombre catAux = new DatosIdNombre(cat.getId(), cat.getNombre());
					categoriasConAux.add(catAux);
				}
				conAux.setCategorias(categoriasConAux);
				
				Hibernate.initialize(con.getAtributos());
				List<DatosAtributoContenido> atributosConAux = new LinkedList<DatosAtributoContenido>();
				for(AtributoContenido atr : con.getAtributos()) {
					DatosAtributoContenido atrAux = new DatosAtributoContenido(atr.getId(), atr.getValor());
					atributosConAux.add(atrAux);
				}
				conAux.setAtributos(atributosConAux);
				
				retorno.add(conAux);
			}
		
	    } catch(Exception e) {
			System.out.println("ERROR en contenidoDao->obtenerContenidosNoBloqueados: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
	    }
		
	   	return retorno;
	}	
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Boolean agregarContenido(DatosContenido contenido) {
	    try {
	    	
	    Empresa emp = em.find(Empresa.class, contenido.getEmpresa());
		Contenido cont = new Contenido();
		cont.setTitulo(contenido.getTitulo());
		cont.setDescripcion(contenido.getDescripcion());
		cont.setPortada(contenido.getPortada());
		cont.setElenco(contenido.getElenco());
		cont.setDirectores(contenido.getDirectores());
		cont.setEmpresa(emp);
		cont.setUrl(contenido.getUrl());
		cont.setCantPuntuaciones(0);
		cont.setPuntuacion(0.0);
		
		TipoContenido tc = em.find(TipoContenido.class, contenido.getTipoContenido().getNombre());
		cont.setTipoContenido(tc);
		if(contenido.getCategorias()!=null && contenido.getCategorias().size()>=1) {
			for(DatosIdNombre cat : contenido.getCategorias()) {
				Categoria c = em.find(Categoria.class, cat.getId());
				c.addContenido(cont);
				em.persist(c);
			}
		}
		
		if(contenido.getAtributos()!=null && contenido.getAtributos().size()>=1) {
			for(DatosAtributoContenido atr : contenido.getAtributos()) {
				AtributoTipoContenido atribReferenciado = em.find(AtributoTipoContenido.class, atr.getId());
				AtributoContenido atribCont = new AtributoContenido(atr.getValor(), atribReferenciado);
				cont.addAtributo(atribCont);
			}
		}
		
		if(contenido.getPrecioPayPerView()!=null) {
			//es contenido PPV
			PayPerView ppv = new PayPerView(contenido.getPrecioPayPerView());
			cont.setPayPerView(ppv);
		}
		
		em.persist(cont);
		
		//Se crea el contenido para mantel
		
		Empresa empmantel = em.find(Empresa.class, "mantel");
		Contenido contMantel = new Contenido();
		contMantel.setTitulo(contenido.getTitulo());
		contMantel.setDescripcion(contenido.getDescripcion());
		contMantel.setPortada(contenido.getPortada());
		contMantel.setElenco(contenido.getElenco());
		contMantel.setDirectores(contenido.getDirectores());
		contMantel.setEmpresa(empmantel);
		contMantel.setUrl(contenido.getUrl());
		contMantel.setCantPuntuaciones(0);
		contMantel.setPuntuacion(0.0);
		
		TipoContenido tcMantel = em.find(TipoContenido.class, contenido.getTipoContenido().getNombre());
		contMantel.setTipoContenido(tcMantel);
		if(contenido.getCategorias()!=null && contenido.getCategorias().size()>=1) {
			for(DatosIdNombre cat : contenido.getCategorias()) {
				Categoria cMantel = em.find(Categoria.class, cat.getId());
				cMantel.addContenido(contMantel);
				em.persist(cMantel);
			}
		}
		
		if(contenido.getAtributos()!=null && contenido.getAtributos().size()>=1) {
			for(DatosAtributoContenido atr : contenido.getAtributos()) {
				AtributoTipoContenido atribReferenciado = em.find(AtributoTipoContenido.class, atr.getId());
				AtributoContenido atribCont = new AtributoContenido(atr.getValor(), atribReferenciado);
				contMantel.addAtributo(atribCont);
			}
		}
		
		if(contenido.getPrecioPayPerView()!=null) {
			//es contenido PPV
			PayPerView ppv = new PayPerView(contenido.getPrecioPayPerView());
			contMantel.setPayPerView(ppv);
		}
		
		em.persist(contMantel);
		
		
	    } catch (Exception e) {
			System.out.println("ERROR en contenidoDao->agregarContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
			return false;
	    }
		
	    return true;
	}
    
    public List<DatosTipoContenido> obtenerTiposContenido(){
    	List<DatosTipoContenido> retorno = new LinkedList<DatosTipoContenido>();
    	List<TipoContenido> tiposContenido = null;
   		try
	    {
	       	Query query = em.createQuery("Select tc FROM TipoContenido tc");
	       	tiposContenido = (ArrayList<TipoContenido>) query.getResultList();
	       	
	       	for(TipoContenido tc : tiposContenido) {
	       		List<DatosIdNombre> categorias = new LinkedList<DatosIdNombre>();
	       		List<DatosIdNombre> atributos = new LinkedList<DatosIdNombre>();
	        	Hibernate.initialize(tc.getCategorias());
	        	for(Categoria cat : tc.getCategorias()) {
	        		categorias.add(new DatosIdNombre(cat.getId(), cat.getNombre()));
	        	}
	        	Hibernate.initialize(tc.getAtributos());
	        	for(AtributoTipoContenido atr : tc.getAtributos()) {
	        		atributos.add(new DatosIdNombre(atr.getId(), atr.getNombre()));
	        	}
	        	DatosTipoContenido dtc = new DatosTipoContenido(tc.getNombre(), categorias, atributos, tc.isVivo());
	        	retorno.add(dtc);
	       	}
	       	
	    }
		catch(Exception e)
	    {
			System.out.println("ERROR en contenidoDao->obtenerTiposContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());	    
		}
   		
	   	return retorno;
    }
    
    public boolean guardarTiempoReproduccion(String tituloContenido, String idfacebookCliente, Date tiempo, String empresa){
    	try {
    	Query query = em.createQuery("SELECT ev FROM EstadoVisualizacion ev WHERE ev.contenido.empresa.nombre=:empresa AND ev.contenido.titulo=:tituloContenido AND ev.cliente.idfacebook=:idfacebookCliente");
    	query.setParameter("tituloContenido", tituloContenido);
    	query.setParameter("idfacebookCliente", idfacebookCliente);
    	query.setParameter("empresa", empresa);

    	List<EstadoVisualizacion> ev = (ArrayList<EstadoVisualizacion>)query.getResultList();
    	
    	if(ev!=null && ev.size()==1) {
    		EstadoVisualizacion estado = ev.get(0);
    		estado.setTiempo(tiempo);
    		em.persist(estado);
    	} else {
    		//Contenido cont = em.find(Contenido.class, tituloContenido);
    		
    		Contenido cont = entidades.getContenido(tituloContenido, empresa);
    		Cliente cli = entidades.getCliente(idfacebookCliente, empresa);
    		EstadoVisualizacion estado = new EstadoVisualizacion(cont, cli, tiempo);
    		em.persist(estado);
    	}
    	}catch(Exception e) {
    		System.out.println("ERROR en contenidoDao->guardarTiempoReproduccion: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
		return true;
    }
    
    public int obtenerTiempoReproduccion(String tituloContenido, String idfacebookCliente, String empresa) {
    	
    	Query query = em.createQuery("SELECT ev FROM EstadoVisualizacion ev WHERE ev.contenido.empresa.nombre=:empresa AND ev.contenido.titulo=:tituloContenido AND ev.cliente.idfacebook=:idfacebookCliente");
    	query.setParameter("tituloContenido", tituloContenido);
    	query.setParameter("idfacebookCliente", idfacebookCliente);
    	query.setParameter("empresa", empresa);
    	List<EstadoVisualizacion> ev = (ArrayList<EstadoVisualizacion>)query.getResultList();
    	
    	int retorno = 0;
    	
    	if(ev!=null && ev.size()==1) {
    		retorno = ev.get(0).getTiempo().getSeconds() + ev.get(0).getTiempo().getMinutes()*60 + ev.get(0).getTiempo().getHours()*3600;
    	} 
    	
    	return retorno;	
    }
    
//    public List<Contenido> obtenerSugeridos(String idfacebookCliente, String empresa){
//    	
//    	List<Contenido> sugeridos = null;
//    	try {
//    		Cliente cli = entidades.getCliente(idfacebookCliente, empresa);
//    		sugeridos = cli.getContenidosSugeridos();
//    		return sugeridos;
//    	} catch (Exception e) {
//    		System.out.println("ERROR en contenidoDao->obtenerSugeridos: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
//    		return null;
//    	}
//    }
    
    public boolean destacarContenido(String titulo, String empresa) {

    	try {
    		
    		Query query = em.createQuery("FROM Contenido c WHERE c.empresa.nombre=:empresa AND c.titulo=:titulo");
    		query.setParameter("titulo", titulo);
    		query.setParameter("empresa", empresa);

	       	List<Contenido> contenidos = (ArrayList<Contenido>) query.getResultList();
    		
	       	if(contenidos!=null && contenidos.size()==1) {
	       		Contenido cont = contenidos.get(0);
	       		cont.setDestacado(true);
	    		em.persist(cont);
	       	} else {
	       		return false;
	       	}

    	} catch (Exception e) {
    		System.out.println("ERROR en contenidoDao->destacarContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
    public boolean quitarDestacado(String titulo, String empresa) {
    	try {
    		
    		Query query = em.createQuery("FROM Contenido c WHERE c.empresa.nombre=:empresa AND c.titulo=:titulo");
    		query.setParameter("titulo", titulo);
    		query.setParameter("empresa", empresa);

	       	List<Contenido> contenidos = (ArrayList<Contenido>) query.getResultList();
    		
	       	if(contenidos!=null && contenidos.size()==1) {
	       		Contenido cont = contenidos.get(0);
	       		cont.setDestacado(false);
	    		em.persist(cont);
	       	} else {
	       		return false;
	       	}

    	} catch (Exception e) {
    		System.out.println("ERROR en contenidoDao->quitarDestacado: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
  
    public boolean bloquearContenido(String titulo, String empresa) {
    	try {
    		
    		Query query = em.createQuery("FROM Contenido c WHERE c.empresa.nombre=:empresa AND c.titulo=:titulo");
    		query.setParameter("titulo", titulo);
    		query.setParameter("empresa", empresa);

	       	List<Contenido> contenidos = (ArrayList<Contenido>) query.getResultList();
    		
	       	if(contenidos!=null && contenidos.size()==1) {
	       		Contenido cont = contenidos.get(0);
	       		cont.setBloqueado(true);
	    		em.persist(cont);
	       	} else {
	       		return false;
	       	}

    	} catch (Exception e) {
    		System.out.println("ERROR en contenidoDao->bloquearContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
    public boolean cargarDatos(boolean crearEmpresas) {
    	    	
    	boolean retorno = true;
    	
    	try {
    		
    		Empresa fox;
//        	Empresa sony;
        	Empresa mantel;
    		
    		if(crearEmpresas) {
    			fox = new Empresa("fox");
//    			sony = new Empresa("Sony");
    			mantel = new Empresa("mantel");
    		} else {
    			fox = em.find(Empresa.class, "fox");
    			mantel = em.find(Empresa.class, "mantel");
    		}
    		
    	
    	//CREACION DE TIPOS DE CONTENIDO
    	
    	TipoContenido pelicula = new TipoContenido(null, "Peliculas", null, false);
    	TipoContenido serie = new TipoContenido(null, "Series", null, false);
    	TipoContenido eventoDeportivo = new TipoContenido(null, "Deportes", null, true);
    	TipoContenido eventoEspectaculo = new TipoContenido(null, "Espectaculos", null,true);
    	
    	//CREACION DE ATRIBUTOS PARA LOS TIPOS DE CONTENIDO
    	
    	AtributoTipoContenido fechaComienzo = new AtributoTipoContenido("Comienzo");
    	AtributoTipoContenido fechaFin = new AtributoTipoContenido("Fin");
    	
    	eventoDeportivo.addAtributo(fechaComienzo);
    	eventoDeportivo.addAtributo(fechaFin);
    	
    	AtributoTipoContenido duracion = new AtributoTipoContenido("Duracion");
    	pelicula.addAtributo(duracion);
    	
    	AtributoTipoContenido temporada = new AtributoTipoContenido("Temporada");
    	AtributoTipoContenido capitulo = new AtributoTipoContenido("Capitulo");
    	
    	serie.addAtributo(temporada);
    	serie.addAtributo(capitulo);
    	
    	//CREACION DE CATEGORIAS PARA LOS TIPOS DE CONTENIDO
    	
		Categoria catePelicula1 = new Categoria("Terror");
		Categoria catePelicula2 = new Categoria("Drama");
		Categoria catePelicula3 = new Categoria("Comedia");
		Categoria catePelicula4 = new Categoria("Thriller");
		Categoria catePelicula5 = new Categoria("Romance");
		Categoria catePelicula6 = new Categoria("Musical");
	
		pelicula.addCategoria(catePelicula1);
		pelicula.addCategoria(catePelicula2);
		pelicula.addCategoria(catePelicula3);
		pelicula.addCategoria(catePelicula4);
		pelicula.addCategoria(catePelicula5);
		pelicula.addCategoria(catePelicula6);
		
		Categoria cateSerie1 = new Categoria("Fantasia");
		Categoria cateSerie2 = new Categoria("Comedia");
		Categoria cateSerie3 = new Categoria("Superheroes");
		Categoria cateSerie4 = new Categoria("Drama");
		serie.addCategoria(cateSerie1);
		serie.addCategoria(cateSerie2);
		serie.addCategoria(cateSerie3);
		serie.addCategoria(cateSerie4);
		
		Categoria cateEventoDeportivo1 = new Categoria("Futbol");
		Categoria cateEventoDeportivo2 = new Categoria("Formula 1");
		Categoria cateEventoDeportivo3 = new Categoria("Boxeo");
		Categoria cateEventoDeportivo4 = new Categoria("Basketball");
		
		eventoDeportivo.addCategoria(cateEventoDeportivo1);
		eventoDeportivo.addCategoria(cateEventoDeportivo2);
		eventoDeportivo.addCategoria(cateEventoDeportivo3);
		eventoDeportivo.addCategoria(cateEventoDeportivo4);
		
		Categoria cateEventoEspectaculo1 = new Categoria("Comedia");
		Categoria cateEventoEspectaculo2 = new Categoria("Infantil");
		Categoria cateEventoEspectaculo3 = new Categoria("Boxeo");
		Categoria cateEventoEspectaculo4 = new Categoria("Basketball");
		
		eventoEspectaculo.addCategoria(cateEventoEspectaculo1);
		eventoEspectaculo.addCategoria(cateEventoEspectaculo2);
		eventoEspectaculo.addCategoria(cateEventoEspectaculo3);
		eventoEspectaculo.addCategoria(cateEventoEspectaculo4);
		
		//CREACION DE CONTENIDOS. SE LES ASOCIAN TIPOCONTENIDO, CATEGORIAS Y ATRIBUTOS
		Contenido peli1 = new Contenido();
		peli1.setTipoContenido(pelicula);
		
		catePelicula1.addContenido(peli1);
		catePelicula2.addContenido(peli1);
		
		peli1.addAtributo(new AtributoContenido("01:55:32", duracion));
		
		peli1.setTitulo("Inception");
		peli1.setDescripcion("Dom Cobb es un ladrón prófugo de la justicia estadounidense, que se especializa en infiltrarse en los sueños para robar ideas, claves de bancos, etc");
		peli1.setCantPuntuaciones(0);
		List<String> elenco = new LinkedList<String>();
		elenco.add("Leonardo DiCaprio");
		elenco.add("Scarlett Johanson");
		elenco.add("Natalia Oreiro");
		peli1.setElenco(elenco);
		List<String> directores = new LinkedList<String>();
		directores.add("Fede Alvarez");
		peli1.setDirectores(directores);
		peli1.setDestacado(false);
		peli1.setBloqueado(false);
		peli1.setPortada("https://www.warnerbros.com/sites/default/files/styles/key_art_270x400/public/inception_keyart.jpg?itok=7jXiglyb");
		peli1.setEmpresa(fox);
		peli1.setUrl("./videoEnArchivo/fox/Inception");
		peli1.setPuntuacion(3.5);
		
		em.persist(peli1);
		
		//
		
//		Contenido peli1Sony = new Contenido();
//		peli1Sony.setTipoContenido(pelicula);
//		
//		catePelicula1.addContenido(peli1Sony);
//		
//		peli1Sony.addAtributo(new AtributoContenido("09:22:1", duracion));
//		
//		peli1Sony.setTitulo("Inception");
//		peli1Sony.setDescripcion("assddsa dsasdadsa dsadsdsaassadsdasadsa");
//		peli1Sony.setCantPuntuaciones(0);
//		List<String> elenco14 = new LinkedList<String>();
//		elenco14.add("Morgan Frsasadeeman");
//		elenco14.add("Scarlett Josasdahanson");
//		elenco14.add("Natalia Oasdasasdreiro");
//		peli1Sony.setElenco(elenco14);
//		List<String> directores14 = new LinkedList<String>();
//		directores14.add("Fede Alassadsvarez");
//		peli1Sony.setDirectores(directores14);
//		peli1Sony.setDestacado(false);
//		peli1Sony.setBloqueado(false);
//		peli1Sony.setPortada("https://www.warnerbros.com/sites/default/files/styles/key_art_270x400/public/inception_keyart.jpg?itok=7jXiglyb");
//		peli1Sony.setEmpresa(sony);
//		peli1Sony.setUrl("./videoEnArchivo/fish3.mp4");
//		peli1Sony.setPuntuacion(4.5);
//		
//		em.persist(peli1Sony);
		
		//
		
		
		
		Contenido peli2 = new Contenido();
		peli2.setTipoContenido(pelicula);
		catePelicula3.addContenido(peli2);
		catePelicula6.addContenido(peli2);
		peli2.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli2.setTitulo("Spongebob");
		peli2.setDescripcion("vive en una pina debajo del mar ...");
		peli2.setCantPuntuaciones(0);
		List<String> elenco1 = new LinkedList<String>();
		elenco1.add("Bob esponja");
		elenco1.add("Scarlett Johanson");
		elenco1.add("Natalia Oreiro");
		peli2.setElenco(elenco1);
		List<String> directores1 = new LinkedList<String>();
		directores1.add("Fede Alvarez");
		peli2.setDirectores(directores1);
		peli2.setDestacado(false);
		peli2.setBloqueado(false);
		peli2.setPortada("http://poolpmx.com/wp-content/uploads/2015/08/Bob-Esponja-y-sus-amigos.jpg");
		peli2.setEmpresa(fox);
		peli2.setUrl("./videoEnArchivo/fox/Spongebob");
		peli2.setPuntuacion(1.2);
		
		em.persist(peli2);
		
		
		Contenido peli3 = new Contenido();
		peli3.setTipoContenido(pelicula);
		catePelicula4.addContenido(peli3);
		catePelicula2.addContenido(peli3);
		peli3.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli3.setTitulo("Spiderman");
		peli3.setDescripcion("Peter Parker es un adolescente huérfano brillante científicamente, pero inepto socialmente.");
		peli3.setCantPuntuaciones(0);
		List<String> elenco3 = new LinkedList<String>();
		elenco3.add("Ryan Phillippe");
		elenco3.add("James Franco");
		peli3.setElenco(elenco3);
		List<String> directores3 = new LinkedList<String>();
		directores3.add("Fede Alvarez");
		directores3.add("Tarantino");
		peli3.setDirectores(directores3);
		peli3.setDestacado(false);
		peli3.setBloqueado(false);
		peli3.setPortada("https://i.ytimg.com/vi/t7UZ1OQz4BA/maxresdefault.jpg");
		peli3.setEmpresa(fox);
		peli3.setUrl("./videoEnArchivo/fox/Spiderman");
		peli3.setPuntuacion(5.0);
		
		em.persist(peli3);
		
		
		Contenido peli5 = new Contenido();
		peli5.setTipoContenido(pelicula);
		catePelicula3.addContenido(peli5);
		peli5.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli5.setTitulo("AceVentura");
		peli5.setDescripcion("El detective Ace Ventura, especializado en rescatar animales secuestrados, recibe el encargo de localizar y rescatar el delfin Copo de Nieve");
		peli5.setCantPuntuaciones(0);
		List<String> elenco5 = new LinkedList<String>();
		elenco5.add("Jim Carrey");
		elenco5.add("Ryan Phillippe");
		elenco5.add("James Franco");
		peli5.setElenco(elenco5);
		List<String> directores5 = new LinkedList<String>();
		directores5.add("George Lucas");
		directores5.add("Tarantino");
		peli5.setDirectores(directores5);
		peli5.setDestacado(false);
		peli5.setBloqueado(false);
		peli5.setPortada("http://www.cuadrosylienzos.com/images/Ace%20Ventura%202.jpg");
		peli5.setEmpresa(fox);
		peli5.setUrl("./videoEnArchivo/fox/AceVentura");
		peli5.setPuntuacion(4.5);
		
		em.persist(peli5);
		
		Contenido peli6 = new Contenido();
		peli6.setTipoContenido(pelicula);
		catePelicula4.addContenido(peli6);
		catePelicula2.addContenido(peli6);
		peli6.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli6.setTitulo("StarWars");
		peli6.setDescripcion("Star Wars Episodio I: La Amenaza Fantasma es el primer episodio en la línea cronológica de la saga de Star Wars, y la cuarta película, siendo lanzada en 1999.");
		peli6.setCantPuntuaciones(0);
		List<String> elenco6 = new LinkedList<String>();
		elenco6.add("Jim Carrey");
		elenco6.add("Ryan Phillippe");
		elenco6.add("James Franco");
		peli6.setElenco(elenco6);
		List<String> directores6 = new LinkedList<String>();
		directores6.add("George Lucas");
		directores6.add("Tarantino");
		peli6.setDirectores(directores6);
		peli6.setDestacado(false);
		peli6.setBloqueado(false);
		peli6.setPortada("http://www.tododvdfull.com/wp-content/uploads/2016/01/star-wars-1-poster.jpg");
		peli6.setEmpresa(fox);
		peli6.setUrl("./videoEnArchivo/fox/StarWars");
		peli6.setPuntuacion(3.0);
		
		em.persist(peli6);
		
		Contenido peli7 = new Contenido();
		peli7.setTipoContenido(pelicula);
		catePelicula4.addContenido(peli7);
		catePelicula1.addContenido(peli7);
		catePelicula3.addContenido(peli7);
		peli7.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli7.setTitulo("FightClub");
		peli7.setDescripcion("Un joven hastiado de su gris y monótona vida lucha contra el insomnio. En un viaje en avión conoce a un carismático vendedor de jabón que sostiene una teoría muy particular");
		peli7.setCantPuntuaciones(0);
		List<String> elenco7 = new LinkedList<String>();
		elenco7.add("Brad Pitt");
		elenco7.add("Ryan Phillippe");
		elenco7.add("Paco Casal");
		peli7.setElenco(elenco7);
		List<String> directores7 = new LinkedList<String>();
		directores7.add("Tarantino");
		peli7.setDirectores(directores7);
		peli7.setDestacado(false);
		peli7.setBloqueado(false);
		peli7.setPortada("http://2.bp.blogspot.com/-IQX4LQOna8c/UEIUQlT7-AI/AAAAAAAAAqU/26tYnO0YTzk/s1600/el-club-de-la-pelea-pelicula-en-formato-dvd_MLM-O-66357439_7582.jpg");
		peli7.setEmpresa(fox);
		peli7.setUrl("./videoEnArchivo/fox/FightClub");
		peli7.setPuntuacion(4.0);
		
		em.persist(peli7);
		
		Contenido peli8 = new Contenido();
		peli8.setTipoContenido(pelicula);
		catePelicula4.addContenido(peli8);
		catePelicula2.addContenido(peli8);
		catePelicula3.addContenido(peli8);
		peli8.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli8.setTitulo("Matrix");
		peli8.setDescripcion("Thomas Anderson es un brillante programador de una respetable compañía de software. Pero fuera del trabajo es Neo, un hacker que un día recibe una misteriosa visita...");
		peli8.setCantPuntuaciones(0);
		List<String> elenco8 = new LinkedList<String>();
		elenco8.add("Keanu Reeves");
		elenco8.add("Ryan Phillippe");
		elenco8.add("Paco Casal");
		peli8.setElenco(elenco8);
		List<String> directores8 = new LinkedList<String>();
		directores8.add("Tarantino");
		directores8.add("Fede Alvarez");
		directores8.add("James Acosta");
		peli8.setDirectores(directores8);
		peli8.setDestacado(false);
		peli8.setBloqueado(false);
		peli8.setPortada("http://es.web.img3.acsta.net/medias/nmedia/18/72/16/76/20065616.jpg");
		peli8.setEmpresa(fox);
		peli8.setUrl("./videoEnArchivo/fox/Matrix");
		peli8.setPuntuacion(2.5);
		
		em.persist(peli8);
		
		Contenido peli9 = new Contenido();
		peli9.setTipoContenido(pelicula);
		catePelicula3.addContenido(peli9);
		peli9.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli9.setTitulo("Madagascar");
		peli9.setDescripcion("Alex el león es el rey de la selva urbana: es la atracción estelar del zoo neoyorquino de Central Park. Como sus mejores amigos, Marty la cebra, Melman la jirafa y Gloria la hipopótamo");
		peli9.setCantPuntuaciones(0);
		List<String> elenco9 = new LinkedList<String>();
		peli9.setElenco(elenco9);
		List<String> directores9 = new LinkedList<String>();
		directores9.add("Fede Alvarez");
		peli9.setDirectores(directores9);
		peli9.setDestacado(false);
		peli9.setBloqueado(false);
		peli9.setPortada("https://st-listas.20minutos.es/images/2017-01/417509/5099162_640px.jpg?1483235236");
		peli9.setEmpresa(fox);
		peli9.setUrl("./videoEnArchivo/fox/Madagascar");
		peli9.setPuntuacion(1.5);
		
		em.persist(peli9);
		
		Contenido serie1 = new Contenido();
		serie1.setTipoContenido(serie);
		cateSerie1.addContenido(serie1);
		cateSerie2.addContenido(serie1);
		serie1.addAtributo(new AtributoContenido("2", temporada));
		serie1.addAtributo(new AtributoContenido("7", capitulo));
		
		serie1.setTitulo("Friends");
		serie1.setDescripcion("un grupo de amigos");
		serie1.setCantPuntuaciones(0);
		List<String> elenco2 = new LinkedList<String>();
		elenco2.add("Morgan Freeman");
		elenco2.add("Scarlett Johanson");
		elenco2.add("Natalia Oreiro");
		serie1.setElenco(elenco2);
		List<String> directores2 = new LinkedList<String>();
		directores2.add("Fede Alvarez");
		serie1.setDirectores(directores2);
		serie1.setDestacado(false);
		serie1.setBloqueado(false);
		serie1.setPortada("https://okdiario.com/series/img/2017/04/13/friends-musical.jpg");
		serie1.setEmpresa(fox);
		serie1.setUrl("./videoEnArchivo/fox/Friends");
		serie1.setPuntuacion(3.5);
		
		em.persist(serie1);
		
		Contenido eventoDeportivo1 = new Contenido();
		eventoDeportivo1.setTipoContenido(eventoDeportivo);
		cateEventoDeportivo1.addContenido(eventoDeportivo1);
		eventoDeportivo1.addAtributo(new AtributoContenido("04/12/2017 - 13:45:00", fechaComienzo));
		eventoDeportivo1.addAtributo(new AtributoContenido("04/12/2017 - 15:45:00", fechaFin));
		
		eventoDeportivo1.setTitulo("Champions");
		eventoDeportivo1.setDescripcion("Barcelona(Esp) vs Rampla(Uy)");
		eventoDeportivo1.setCantPuntuaciones(0);
		eventoDeportivo1.setElenco(null);
		eventoDeportivo1.setDirectores(null);
		eventoDeportivo1.setDestacado(false);
		eventoDeportivo1.setBloqueado(false);
		eventoDeportivo1.setPortada("http://thenationonlineng.net/wp-content/uploads/2017/09/champions-league.jpg");
		eventoDeportivo1.setEmpresa(fox);
		eventoDeportivo1.setUrl(null);
		eventoDeportivo1.setPuntuacion(3.5);
		eventoDeportivo1.setUrl("./video/Champions");
		
		em.persist(eventoDeportivo1);
		
		Contenido eventoEspectaculo1 = new Contenido();
		eventoEspectaculo1.setTipoContenido(eventoEspectaculo);
		eventoEspectaculo1.addAtributo(new AtributoContenido("09/12/2017 - 18:45:00", fechaComienzo));
		eventoEspectaculo1.addAtributo(new AtributoContenido("09/12/2017 - 19:45:00", fechaFin));
		cateEventoEspectaculo1.addContenido(eventoEspectaculo1);
		cateEventoEspectaculo2.addContenido(eventoEspectaculo1);
		cateEventoEspectaculo3.addContenido(eventoEspectaculo1);
		
		eventoEspectaculo1.setTitulo("CirqueDuSoleil");
		eventoEspectaculo1.setDescripcion("Trucos copados del circo mas grande del mundo");
		eventoEspectaculo1.setCantPuntuaciones(0);
		eventoEspectaculo1.setElenco(null);
		eventoEspectaculo1.setDirectores(null);
		eventoEspectaculo1.setDestacado(false);
		eventoEspectaculo1.setBloqueado(false);
		eventoEspectaculo1.setPortada("https://www.cirquedusoleil.com/-/media/images/shows/kurios/highlights_carousel/kurios-show-contortion.jpg?db=web&h=1000&la=es&vs=1&w=1280&hash=E0AB40C0DB8556681511BE89E58BC90BD25E3F81");
		eventoEspectaculo1.setEmpresa(fox);
		eventoEspectaculo1.setPuntuacion(4.5);
		eventoEspectaculo1.setUrl("./video/CirqueDuSoleil");
		
		em.persist(eventoEspectaculo1);
		
		
		//Mismos contenidos para mantel
		
		Contenido peli11 = new Contenido();
		peli11.setTipoContenido(pelicula);
		
		catePelicula1.addContenido(peli11);
		catePelicula2.addContenido(peli11);
		
		peli11.addAtributo(new AtributoContenido("01:55:32", duracion));
		
		peli11.setTitulo("Inception");
		peli11.setDescripcion("Dom Cobb es un ladrón prófugo de la justicia estadounidense, que se especializa en infiltrarse en los sueños para robar ideas, claves de bancos, etc");
		peli11.setCantPuntuaciones(0);
		List<String> elenco99 = new LinkedList<String>();
		elenco99.add("Leonardo DiCaprio");
		elenco99.add("Scarlett Johanson");
		elenco99.add("Natalia Oreiro");
		peli11.setElenco(elenco99);
		List<String> directores99 = new LinkedList<String>();
		directores99.add("Fede Alvarez");
		peli11.setDirectores(directores99);
		peli11.setDestacado(false);
		peli11.setBloqueado(false);
		peli11.setPortada("https://www.warnerbros.com/sites/default/files/styles/key_art_270x400/public/inception_keyart.jpg?itok=7jXiglyb");
		peli11.setEmpresa(mantel);
		peli11.setUrl("./videoEnArchivo/fox/Inception");
		peli11.setPuntuacion(3.5);
		
		em.persist(peli11);
			
		Contenido peli22 = new Contenido();
		peli22.setTipoContenido(pelicula);
		catePelicula3.addContenido(peli22);
		catePelicula6.addContenido(peli22);
		peli22.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli22.setTitulo("Spongebob");
		peli22.setDescripcion("vive en una pina debajo del mar ...");
		peli22.setCantPuntuaciones(0);
		List<String> elenco11 = new LinkedList<String>();
		elenco11.add("Bob esponja");
		elenco11.add("Scarlett Johanson");
		elenco11.add("Natalia Oreiro");
		peli22.setElenco(elenco11);
		List<String> directores11 = new LinkedList<String>();
		directores11.add("Fede Alvarez");
		peli22.setDirectores(directores11);
		peli22.setDestacado(false);
		peli22.setBloqueado(false);
		peli22.setPortada("http://poolpmx.com/wp-content/uploads/2015/08/Bob-Esponja-y-sus-amigos.jpg");
		peli22.setEmpresa(mantel);
		peli22.setUrl("./videoEnArchivo/fox/Spongebob");
		peli22.setPuntuacion(1.2);
		
		em.persist(peli22);
		
		
		Contenido peli33 = new Contenido();
		peli33.setTipoContenido(pelicula);
		catePelicula4.addContenido(peli33);
		catePelicula2.addContenido(peli33);
		peli33.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli33.setTitulo("Spiderman");
		peli33.setDescripcion("Peter Parker es un adolescente huérfano brillante científicamente, pero inepto socialmente.");
		peli33.setCantPuntuaciones(0);
		List<String> elenco33 = new LinkedList<String>();
		elenco33.add("Ryan Phillippe");
		elenco33.add("James Franco");
		peli33.setElenco(elenco33);
		List<String> directores33 = new LinkedList<String>();
		directores33.add("Fede Alvarez");
		directores33.add("Tarantino");
		peli33.setDirectores(directores33);
		peli33.setDestacado(false);
		peli33.setBloqueado(false);
		peli33.setPortada("https://i.ytimg.com/vi/t7UZ1OQz4BA/maxresdefault.jpg");
		peli33.setEmpresa(mantel);
		peli33.setUrl("./videoEnArchivo/fox/Spiderman");
		peli33.setPuntuacion(5.0);
		
		em.persist(peli33);
		
		
		Contenido peli55 = new Contenido();
		peli55.setTipoContenido(pelicula);
		catePelicula3.addContenido(peli55);
		peli55.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli55.setTitulo("AceVentura");
		peli55.setDescripcion("El detective Ace Ventura, especializado en rescatar animales secuestrados, recibe el encargo de localizar y rescatar el delfin Copo de Nieve");
		peli55.setCantPuntuaciones(0);
		List<String> elenco55 = new LinkedList<String>();
		elenco55.add("Jim Carrey");
		elenco55.add("Ryan Phillippe");
		elenco55.add("James Franco");
		peli55.setElenco(elenco55);
		List<String> directores55 = new LinkedList<String>();
		directores55.add("George Lucas");
		directores55.add("Tarantino");
		peli55.setDirectores(directores55);
		peli55.setDestacado(false);
		peli55.setBloqueado(false);
		peli55.setPortada("http://www.cuadrosylienzos.com/images/Ace%20Ventura%202.jpg");
		peli55.setEmpresa(mantel);
		peli55.setUrl("./videoEnArchivo/fox/AceVentura");
		peli55.setPuntuacion(4.5);
		
		em.persist(peli55);
		
		Contenido peli66 = new Contenido();
		peli66.setTipoContenido(pelicula);
		catePelicula4.addContenido(peli66);
		catePelicula2.addContenido(peli66);
		peli66.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli66.setTitulo("StarWars");
		peli66.setDescripcion("Star Wars Episodio I: La Amenaza Fantasma es el primer episodio en la línea cronológica de la saga de Star Wars, y la cuarta película, siendo lanzada en 1999.");
		peli66.setCantPuntuaciones(0);
		List<String> elenco66 = new LinkedList<String>();
		elenco66.add("Jim Carrey");
		elenco66.add("Ryan Phillippe");
		elenco66.add("James Franco");
		peli66.setElenco(elenco66);
		List<String> directores66 = new LinkedList<String>();
		directores66.add("George Lucas");
		directores66.add("Tarantino");
		peli66.setDirectores(directores66);
		peli66.setDestacado(false);
		peli66.setBloqueado(false);
		peli66.setPortada("http://www.tododvdfull.com/wp-content/uploads/2016/01/star-wars-1-poster.jpg");
		peli66.setEmpresa(mantel);
		peli66.setUrl("./videoEnArchivo/fox/StarWars");
		peli66.setPuntuacion(3.0);
		
		em.persist(peli66);
		
		Contenido peli77 = new Contenido();
		peli77.setTipoContenido(pelicula);
		catePelicula4.addContenido(peli77);
		catePelicula1.addContenido(peli77);
		catePelicula3.addContenido(peli77);
		peli77.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli77.setTitulo("FightClub");
		peli77.setDescripcion("Un joven hastiado de su gris y monótona vida lucha contra el insomnio. En un viaje en avión conoce a un carismático vendedor de jabón que sostiene una teoría muy particular");
		peli77.setCantPuntuaciones(0);
		List<String> elenco77 = new LinkedList<String>();
		elenco77.add("Brad Pitt");
		elenco77.add("Ryan Phillippe");
		elenco77.add("Paco Casal");
		peli77.setElenco(elenco77);
		List<String> directores77 = new LinkedList<String>();
		directores77.add("Tarantino");
		peli77.setDirectores(directores77);
		peli77.setDestacado(false);
		peli77.setBloqueado(false);
		peli77.setPortada("http://2.bp.blogspot.com/-IQX4LQOna8c/UEIUQlT7-AI/AAAAAAAAAqU/26tYnO0YTzk/s1600/el-club-de-la-pelea-pelicula-en-formato-dvd_MLM-O-66357439_7582.jpg");
		peli77.setEmpresa(mantel);
		peli77.setUrl("./videoEnArchivo/fox/FightClub");
		peli77.setPuntuacion(4.0);
		
		em.persist(peli77);
		
		Contenido peli88 = new Contenido();
		peli88.setTipoContenido(pelicula);
		catePelicula4.addContenido(peli88);
		catePelicula2.addContenido(peli88);
		catePelicula3.addContenido(peli88);
		peli88.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli88.setTitulo("Matrix");
		peli88.setDescripcion("Thomas Anderson es un brillante programador de una respetable compañía de software. Pero fuera del trabajo es Neo, un hacker que un día recibe una misteriosa visita...");
		peli88.setCantPuntuaciones(0);
		List<String> elenco88 = new LinkedList<String>();
		elenco88.add("Keanu Reeves");
		elenco88.add("Ryan Phillippe");
		elenco88.add("Paco Casal");
		peli88.setElenco(elenco88);
		List<String> directores88 = new LinkedList<String>();
		directores88.add("Tarantino");
		directores88.add("Fede Alvarez");
		directores88.add("James Acosta");
		peli88.setDirectores(directores88);
		peli88.setDestacado(false);
		peli88.setBloqueado(false);
		peli88.setPortada("http://es.web.img3.acsta.net/medias/nmedia/18/72/16/76/20065616.jpg");
		peli88.setEmpresa(mantel);
		peli88.setUrl("./videoEnArchivo/fox/Matrix");
		peli88.setPuntuacion(2.5);
		
		em.persist(peli88);
		
		Contenido peli99 = new Contenido();
		peli99.setTipoContenido(pelicula);
		catePelicula3.addContenido(peli99);
		peli99.addAtributo(new AtributoContenido("01:25:11", duracion));
		
		peli99.setTitulo("Madagascar");
		peli99.setDescripcion("Alex el león es el rey de la selva urbana: es la atracción estelar del zoo neoyorquino de Central Park. Como sus mejores amigos, Marty la cebra, Melman la jirafa y Gloria la hipopótamo");
		peli99.setCantPuntuaciones(0);
		List<String> elenco199 = new LinkedList<String>();
		peli99.setElenco(elenco199);
		List<String> directores199 = new LinkedList<String>();
		directores99.add("Fede Alvarez");
		peli99.setDirectores(directores199);
		peli99.setDestacado(false);
		peli99.setBloqueado(false);
		peli99.setPortada("https://st-listas.20minutos.es/images/2017-01/417509/5099162_640px.jpg?1483235236");
		peli99.setEmpresa(mantel);
		peli99.setUrl("./videoEnArchivo/fox/Madagascar");
		peli99.setPuntuacion(1.5);
		
		em.persist(peli99);
		
		Contenido serie11 = new Contenido();
		serie11.setTipoContenido(serie);
		cateSerie1.addContenido(serie11);
		cateSerie2.addContenido(serie11);
		serie11.addAtributo(new AtributoContenido("2", temporada));
		serie11.addAtributo(new AtributoContenido("7", capitulo));
		
		serie11.setTitulo("Friends");
		serie11.setDescripcion("un grupo de amigos");
		serie11.setCantPuntuaciones(0);
		List<String> elenco223 = new LinkedList<String>();
		elenco223.add("Morgan Freeman");
		elenco223.add("Scarlett Johanson");
		elenco223.add("Natalia Oreiro");
		serie11.setElenco(elenco223);
		List<String> directores223 = new LinkedList<String>();
		directores223.add("Fede Alvarez");
		serie11.setDirectores(directores223);
		serie11.setDestacado(false);
		serie11.setBloqueado(false);
		serie11.setPortada("https://okdiario.com/series/img/2017/04/13/friends-musical.jpg");
		serie11.setEmpresa(mantel);
		serie11.setUrl("./videoEnArchivo/fox/Friends");
		serie11.setPuntuacion(3.5);
		
		em.persist(serie11);
		
		Contenido eventoDeportivo12 = new Contenido();
		eventoDeportivo12.setTipoContenido(eventoDeportivo);
		cateEventoDeportivo1.addContenido(eventoDeportivo12);
		eventoDeportivo12.addAtributo(new AtributoContenido("04/12/2017 - 13:45:00", fechaComienzo));
		eventoDeportivo12.addAtributo(new AtributoContenido("04/12/2017 - 15:45:00", fechaFin));
		
		eventoDeportivo12.setTitulo("Champions");
		eventoDeportivo12.setDescripcion("Barcelona(Esp) vs Rampla(Uy)");
		eventoDeportivo12.setCantPuntuaciones(0);
		eventoDeportivo12.setElenco(null);
		eventoDeportivo12.setDirectores(null);
		eventoDeportivo12.setDestacado(false);
		eventoDeportivo12.setBloqueado(false);
		eventoDeportivo12.setPortada("http://thenationonlineng.net/wp-content/uploads/2017/09/champions-league.jpg");
		eventoDeportivo12.setEmpresa(mantel);
		eventoDeportivo12.setUrl(null);
		eventoDeportivo12.setPuntuacion(3.5);
		eventoDeportivo12.setUrl("./video/Champions");
		
		em.persist(eventoDeportivo12);
		
		Contenido eventoEspectaculo12 = new Contenido();
		eventoEspectaculo12.setTipoContenido(eventoEspectaculo);
		eventoEspectaculo12.addAtributo(new AtributoContenido("09/12/2017 - 18:45:00", fechaComienzo));
		eventoEspectaculo12.addAtributo(new AtributoContenido("09/12/2017 - 19:45:00", fechaFin));
		cateEventoEspectaculo1.addContenido(eventoEspectaculo12);
		cateEventoEspectaculo2.addContenido(eventoEspectaculo12);
		cateEventoEspectaculo3.addContenido(eventoEspectaculo12);
		
		eventoEspectaculo12.setTitulo("CirqueDuSoleil");
		eventoEspectaculo12.setDescripcion("Trucos copados del circo mas grande del mundo");
		eventoEspectaculo12.setCantPuntuaciones(0);
		eventoEspectaculo12.setElenco(null);
		eventoEspectaculo12.setDirectores(null);
		eventoEspectaculo12.setDestacado(false);
		eventoEspectaculo12.setBloqueado(false);
		eventoEspectaculo12.setPortada("https://www.cirquedusoleil.com/-/media/images/shows/kurios/highlights_carousel/kurios-show-contortion.jpg?db=web&h=1000&la=es&vs=1&w=1280&hash=E0AB40C0DB8556681511BE89E58BC90BD25E3F81");
		eventoEspectaculo12.setEmpresa(mantel);
		eventoEspectaculo12.setPuntuacion(4.5);
		eventoEspectaculo12.setUrl("./video/CirqueDuSoleil");
		
		em.persist(eventoEspectaculo12);
		
		
				
		Suscripcion anual = new Suscripcion("Anual", 2200, null, fox);
		Suscripcion mensual = new Suscripcion("Mensual", 420, null, fox);
		Suscripcion semanal = new Suscripcion("Semanal", 180, null, fox);
		
		em.persist(anual);
		em.persist(mensual);
		em.persist(semanal);
		
//		Suscripcion anual2 = new Suscripcion("Anual", 2200, null, sony);
//		Suscripcion mensual2 = new Suscripcion("Mensual", 420, null, sony);
//		Suscripcion semanal2 = new Suscripcion("Semanal", 180, null, sony);
//		
//		em.persist(anual2);
//		em.persist(mensual2);
//		em.persist(semanal2);
		
		Suscripcion anual3 = new Suscripcion("Anual", 6788, null, mantel);
		Suscripcion mensual3 = new Suscripcion("Mensual", 3444, null, mantel);
		Suscripcion semanal3 = new Suscripcion("Semanal", 1990, null, mantel);
		
		
		
		Cliente cli = new Cliente("test@gmail.com", "Aristoteles", 25, 'M', "Noruega", "http://www.webconsultas.com/sites/default/files/styles/encabezado_articulo/public/articulos/perfil-resilencia.jpg?itok=iQzjOtzd");
		cli.addContenidoSugerido(peli1);
		cli.addContenidoSugerido(peli2);
		
		Suscripcion_Cliente susClient= new Suscripcion_Cliente(new Date(), anual.getPrecio(), anual);
		cli.addSuscripcion_Cliente(susClient);
		cli.setEmpresa(fox);
		
		//Datos para prueba de reporte de ingresos anuales
		
		
		Date hoy = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(hoy);
		
		cal.add(Calendar.MONTH, -1);
		Date reacomodada = cal.getTime();
		Suscripcion_Cliente susClient2= new Suscripcion_Cliente(reacomodada, 543.4, anual);
		cli.addSuscripcion_Cliente(susClient2);
		
		cal.add(Calendar.MONTH, -1);
		Date reacomodada2 = cal.getTime();
		Suscripcion_Cliente susClient22= new Suscripcion_Cliente(reacomodada2, 523.4, anual);
		cli.addSuscripcion_Cliente(susClient22);
		
		cal.add(Calendar.MONTH, -1);
		Date reacomodada3 = cal.getTime();
		Suscripcion_Cliente susClient23= new Suscripcion_Cliente(reacomodada3, 53.4, anual);
		cli.addSuscripcion_Cliente(susClient23);
		
		cal.add(Calendar.MONTH, -1);
		Date reacomodada4 = cal.getTime();
		Suscripcion_Cliente susClient24= new Suscripcion_Cliente(reacomodada4, 743.4, anual);
		cli.addSuscripcion_Cliente(susClient24);
		
		
		cal.add(Calendar.MONTH, -1);
		Date reacomodada9 = cal.getTime();
		Suscripcion_Cliente susClient29= new Suscripcion_Cliente(reacomodada9, 243.4, anual);
		cli.addSuscripcion_Cliente(susClient29);
		
		cal.add(Calendar.MONTH, -1);
		Date reacomodada5 = cal.getTime();
		Suscripcion_Cliente susClient25= new Suscripcion_Cliente(reacomodada5, 943.4, anual);
		cli.addSuscripcion_Cliente(susClient25);
		
		cal.add(Calendar.MONTH, -1);
		Date reacomodada6 = cal.getTime();
		Suscripcion_Cliente susClient26= new Suscripcion_Cliente(reacomodada6, 843.4, anual);
		cli.addSuscripcion_Cliente(susClient26);
		
		cal.add(Calendar.MONTH, -1);
		Date reacomodada7 = cal.getTime();
		Suscripcion_Cliente susClient27= new Suscripcion_Cliente(reacomodada7, 313.4, anual);
		cli.addSuscripcion_Cliente(susClient27);
		
		cal.add(Calendar.MONTH, -1);
		Date reacomodada8 = cal.getTime();
		Suscripcion_Cliente susClient28= new Suscripcion_Cliente(reacomodada8, 493.4, anual);
		cli.addSuscripcion_Cliente(susClient28);
		
		cal.add(Calendar.MONTH, -1);
		Date reacomodada11 = cal.getTime();
		Suscripcion_Cliente susClient211= new Suscripcion_Cliente(reacomodada11, 432.4, anual);
		cli.addSuscripcion_Cliente(susClient211);
		
		
		//Fin de datos para prueba de reporte de ingresos anuales
		
		em.persist(cli);
		
		Cliente cliMantel = new Cliente("test@gmail.com", "Aristoteles", 25, 'M', "Noruega", "http://www.webconsultas.com/sites/default/files/styles/encabezado_articulo/public/articulos/perfil-resilencia.jpg?itok=iQzjOtzd");
		cliMantel.addContenidoSugerido(peli1);
		cliMantel.addContenidoSugerido(peli2);
		
		Suscripcion_Cliente susClient7= new Suscripcion_Cliente(new Date(), anual3.getPrecio(), anual3);
		cliMantel.addSuscripcion_Cliente(susClient7);
		cliMantel.setEmpresa(mantel);
		
		em.persist(cliMantel);
		
		Cliente cli3 = new Cliente("alfonsito@gmail.com", "Socrates", 25, 'M', "Noruega", "http://www.webconsultas.com/sites/default/files/styles/encabezado_articulo/public/articulos/perfil-resilencia.jpg?itok=iQzjOtzd");
		cli3.addContenidoSugerido(peli1);
		cli3.addContenidoSugerido(peli2);
		
		Suscripcion_Cliente susClient3= new Suscripcion_Cliente(new Date(), anual.getPrecio(), anual);
		cli3.addSuscripcion_Cliente(susClient3);
		cli3.setEmpresa(fox);
		
		em.persist(cli3);
		
		Comentario com = new Comentario("Malisimo el contenido la verdad", reacomodada3, 0, cli,peli1);	
		em.persist(com);
		
		
		
		EstadoVisualizacion estado = new EstadoVisualizacion(peli1, cli, new Date());
		em.persist(estado);
		
		Cliente cli2 = new Cliente("cliente2@gmail.com", "Jason", 29, 'M', "Finlandia", "https://www.socialtools.me/blog/wp-content/uploads/2016/04/foto-de-perfil.jpg");
		cli2.addComentarioDenunciado(com);
		cli2.setEmpresa(fox);
		em.persist(cli2);
		
		Comentario com2 = new Comentario("A mi me parecio que estaba bueno... que amargo que sos", reacomodada5, 0, cli2,peli1);	
		em.persist(com2);
		
		Comentario com3 = new Comentario("Dejen de pelear o los marco como spam", reacomodada5, 0, cli3,peli1);	
		em.persist(com3);
		
		AdminEmpresa admin = new AdminEmpresa("admin", "admin", fox);
		
		fox.addCliente(cli);
		fox.addCliente(cli2);
		fox.addContenido(eventoEspectaculo1);
		fox.addContenido(eventoDeportivo1);
		fox.addContenido(serie1);
		fox.addContenido(peli2);
		fox.addContenido(peli1);
		fox.addContenido(peli3);
		fox.addContenido(peli5);
		fox.addContenido(peli6);
		fox.addContenido(peli7);
		fox.addContenido(peli8);
		fox.addContenido(peli9);
		fox.addSuscripcion(mensual);
		fox.addSuscripcion(semanal);
		fox.addSuscripcion(anual);
		
		mantel.addContenido(eventoEspectaculo12);
		mantel.addContenido(eventoDeportivo12);
		mantel.addContenido(serie11);
		mantel.addContenido(peli22);
		mantel.addContenido(peli11);
		mantel.addContenido(peli33);
		mantel.addContenido(peli55);
		mantel.addContenido(peli66);
		mantel.addContenido(peli77);
		mantel.addContenido(peli88);
		mantel.addContenido(peli99);
		
		em.persist(admin);
		em.persist(fox);
				
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    		retorno = false;
    	}
    	return retorno;
    	
    }
    
    public boolean desbloquearContenido(String titulo, String empresa) {

    	try {
    		
    		Query query = em.createQuery("FROM Contenido c WHERE c.empresa.nombre=:empresa AND c.titulo=:titulo");
    		query.setParameter("titulo", titulo);
    		query.setParameter("empresa", empresa);

	       	List<Contenido> contenidos = (ArrayList<Contenido>) query.getResultList();
    		
	       	if(contenidos!=null && contenidos.size()==1) {
	       		Contenido cont = contenidos.get(0);
	       		cont.setBloqueado(false);
	    		em.persist(cont);
	       	} else {
	       		return false;
	       	}

    	} catch (Exception e) {
    		System.out.println("ERROR en contenidoDao->desbloquearContenido: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
//    public List<Categoria> getCategorias(){
//    	Query query = em.createQuery("SELECT c FROM Categoria c");
//        return (ArrayList<Categoria>) query.getResultList();
//    }
    
    
//    public List<Categoria> getCategoriasTipoContenido(int idCont){
//    	Query query = em.createQuery("SELECT tc.categorias FROM TipoContenido tc WHERE tc.id =:idCont");
//    	query.setParameter("idCont", idCont);
//        return (ArrayList<Categoria>) query.getResultList();
//    }
   
	
//	@SuppressWarnings("unchecked")
//	public List<Contenido> getContenidosNoBloqueados()
//	{
//		List<Contenido> contenidos = null;
//		try
//	    {
//	       	Query query = em.createQuery("FROM Contenido c WHERE c.bloqueado == false");
//	       	contenidos = (ArrayList<Contenido>) query.getResultList();
//	    }
//		catch(Exception e)
//	    {
//			System.out.println(e.getMessage());
//	    }
//	   	return contenidos;
//	}
//
//	@SuppressWarnings("unchecked")
//	public List<Contenido> getContenidosDestacados()
//	{
//		List<Contenido> contenidos = null;
//		try
//	    {
//	       	Query query = em.createQuery("FROM Contenido c WHERE c.destacado == true");
//	       	contenidos = (ArrayList<Contenido>) query.getResultList();
//	    }
//		catch(Exception e)
//	    {
//			System.out.println(e.getMessage());
//	    }
//	   	return contenidos;
//	}
    
    
}


