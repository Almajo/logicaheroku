package daos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
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
import Entidades.CompraPPV;
import Entidades.Contenido;
import Entidades.Empresa;
import Entidades.Notificacion;
import Entidades.PayPerView;
import Entidades.Ranking;
import Entidades.Suscripcion;
import Entidades.Suscripcion_Cliente;
import datatypes.DatosAtributoContenido;
import datatypes.DatosCliente;
import datatypes.DatosContenido;
import datatypes.DatosIdNombre;
import datatypes.DatosNotificacion;
import datatypes.DatosTipoContenido;

@Stateless
@LocalBean
public class ClienteDao {

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private EntidadesDao entidades;
    
        
    public List<DatosContenido> obtenerContenidosSugeridos(String idFacebook, String empresa)
	{
    	
    	//obtengo todos los contenidos
    	List<Contenido> contenidos = null;
		List<DatosContenido> todosLosContenidos = new LinkedList<DatosContenido>();
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
				
				todosLosContenidos.add(conAux);
			}
			
			List<DatosContenido> contenidosHistorico = obtenerHistorico(idFacebook, empresa);
			
			List<DatosContenido> contenidosNoVistos = new LinkedList<DatosContenido>();
			
			for(DatosContenido cont : todosLosContenidos) {
				//si no está en el historico lo agrego a los no vistos
				if(!estaEnHistorico(cont.getTitulo(), contenidosHistorico)) {
					contenidosNoVistos.add(cont);
				}
			}
			
			List<DatosContenido> contenidosFavoritos = obtenerFavoritos(idFacebook, empresa);
			
			//si no tiene favoritos le sugiero todos los que no vio
			if(contenidosFavoritos == null || contenidosFavoritos.size() == 0) {
				return contenidosNoVistos;
			} else {
				//si tiene favoritos obtengo el tipo de contenido mas mirado dentro de los favoritos
				//y le recomiendo contenidosNoVistos de dicho tipoContenido
				String tipoContenidoMasVisto = obtenerTipoContenidoMasVisto(contenidosFavoritos);
				if(tipoContenidoMasVisto == null) {
					return contenidosNoVistos;
				} else {
					List<DatosContenido> contenidosRecomendados = new LinkedList<DatosContenido>();
					for(DatosContenido conte : contenidosNoVistos) {
						if(conte.getTipoContenido().getNombre().equals(tipoContenidoMasVisto)) {
							contenidosRecomendados.add(conte);
						}
					}
					return contenidosRecomendados;
				}
			}
		
	    } catch(Exception e) {
	    	System.out.println("ERROR en contenidoDAO->obtenerContenidosSugeridos: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
	    }
		
		return todosLosContenidos;
	}
    
    public String obtenerTipoContenidoMasVisto(List<DatosContenido> contenidosFavoritos) {
    	
    	Map<String, Integer> contadorTC = new HashMap<String, Integer>();
    	
    	for(DatosContenido cont : contenidosFavoritos) {
    		if(contadorTC.get(cont.getTipoContenido().getNombre()) == null) {
    			contadorTC.put(cont.getTipoContenido().getNombre(), 1);
    		} else {
    			contadorTC.put(cont.getTipoContenido().getNombre(), contadorTC.get(cont.getTipoContenido().getNombre()) + 1);
    		}
    	}
    	
    	int maximo = 0;
    	String tipoActual = null;
    	int cantidadActual = 0;
    	
    	Iterator it = contadorTC.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        if((int)pair.getValue() > cantidadActual) {
	        	cantidadActual = (int)pair.getValue();
	        	tipoActual = (String)pair.getKey();
	        }
	        //it.remove();
	    }
    	
    	return tipoActual;
    }
    
    public boolean estaEnHistorico(String titulo, List<DatosContenido> historico) {
    	for(DatosContenido cont : historico) {
    		if(cont.getTitulo().equals(titulo)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public Boolean clienteEstaBloqueado(String  idFacebook, String empresa) {
    	Cliente cli = entidades.getCliente(idFacebook, empresa);
    	return cli.isBloqueado();
    }
    
    public Boolean bajaCliente(String idFacebook, String empresa) {
    	
    	
    	
    	return true;
    }
    
    
    public List<DatosNotificacion> obtenerNotificaciones(String idFacebook, String empresa){
    	
    	List<DatosNotificacion> retorno = new LinkedList<DatosNotificacion>();
    	
    	try {
    		
    		List<Notificacion> notificaciones = new LinkedList<Notificacion>();
    		
    		Query query = em.createQuery("SELECT n FROM Notificacion n WHERE n.receptor.idfacebook=:idFacebook AND n.receptor.empresa.nombre=:empresa");
	       	query.setParameter("idFacebook", idFacebook);
	       	query.setParameter("empresa", empresa);
	       	notificaciones = (ArrayList<Notificacion>) query.getResultList();
	       	
	       	for(Notificacion notif : notificaciones) {
	       		DatosNotificacion dn = new DatosNotificacion(notif.getNotificante().getNombre(), notif.getContenido().getTitulo(), notif.getContenido().getUrl(), notif.getContenido().getPortada());
	       		dn.setId(notif.getId());
	       		retorno.add(dn);
	       	}
    		
    	} catch(Exception e) {
    		return null;
    	}
    	
    	return retorno;
    	
    }
    
    public Boolean eliminarNotificaciones(String idFacebook, String empresa){
    	    	
    	try {
    		
    		List<Notificacion> notificaciones = new LinkedList<Notificacion>();
    		
    		Query query = em.createQuery("SELECT n FROM Notificacion n WHERE n.receptor.idfacebook=:idFacebook AND n.receptor.empresa.nombre=:empresa");
	       	query.setParameter("idFacebook", idFacebook);
	       	query.setParameter("empresa", empresa);
	       	notificaciones = (ArrayList<Notificacion>) query.getResultList();
	       	
	       	for(Notificacion notif : notificaciones) {
	       	    em.remove(notif);
	       	}
    		
    	} catch(Exception e) {
    		return false;
    	}
    	
    	return true;
    	
    }
    
    public Boolean eliminarNotificacion(int id){
    	
    	try {
    		
    		Notificacion notif = em.find(Notificacion.class, id);
    		em.remove(notif);
    		
    	} catch(Exception e) {
    		return false;
    	}
    	
    	return true;
    	
    }
    
    public Boolean comprarPPV(String idFacebook, String titulo, String empresa) {
    	
    	try {
    		//Cliente cli = em.find(Cliente.class, idFacebook);
    		Cliente cli = entidades.getCliente(idFacebook, empresa);
    		Contenido cont = entidades.getContenido(titulo, empresa);
    		Hibernate.initialize(cont.getPayPerView());
    		PayPerView ppv = cont.getPayPerView();
    		
    		Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 1);
			Date fechaVencimiento = cal.getTime();
    		
    		CompraPPV compra = new CompraPPV(fechaVencimiento, ppv);
    		cli.addComprasPayPerView(compra);
    	
    		em.persist(cli);
    	} catch (Exception e) {
    		return false;
    	}
    	return true;    	
    	
    }
    
    public Boolean tieneCompradoPPV(String idFacebook, String titulo, String empresa) {
    	
    	try {
    		Cliente cli = entidades.getCliente(idFacebook, empresa);
    		Hibernate.initialize(cli.getComprasPayPerView());
    		
    		Contenido cont = entidades.getContenido(titulo, empresa);
    		Hibernate.initialize(cont.getPayPerView());
    		PayPerView ppv = cont.getPayPerView();
    		
    		for(CompraPPV compra : cli.getComprasPayPerView()) {
    			if (compra.getPayPerView().getId() == ppv.getId())
    			{
    				if(compra.getFechaVencimiento().after(new Date())) {
    					return true;
    				}
    			}
    		}
    		
    	} catch (Exception e) {
    		return false;
    	}
    	
    	return false;    	
    	
    }
    
    public Boolean suscripcionEstaVigente(String idFacebook, String empresa) {
    	return entidades.suscripcionEstaVigente(idFacebook, empresa);
    }
    
    public boolean suscribir(String idFacebook, String tipoSuscripcion, String empresa) {
    	try {
    		//Cliente cli = em.find(Cliente.class, idFacebook);
    		Cliente cli = entidades.getCliente(idFacebook, empresa);
    		Suscripcion sus = entidades.getSuscripcion(tipoSuscripcion, empresa);
    		Suscripcion_Cliente susClient = new Suscripcion_Cliente(new Date(), sus.getPrecio(), sus);
    		cli.addSuscripcion_Cliente(susClient);
    		Date fechaFinSusc;
    		if(tipoSuscripcion.equals("Anual")) {
    			Calendar cal = Calendar.getInstance();
    			cal.add(Calendar.YEAR, 1);
    			fechaFinSusc = cal.getTime();
    		} else if(tipoSuscripcion.equals("Mensual")) {
    			Calendar cal = Calendar.getInstance();
    			cal.add(Calendar.MONTH, 1);
    			fechaFinSusc = cal.getTime();
    		} else {
    			Calendar cal = Calendar.getInstance();
    			cal.add(Calendar.WEEK_OF_YEAR, 1);
    			fechaFinSusc = cal.getTime();
    		}
    		
    		cli.setFechaFinSuscripcion(fechaFinSusc);
    		em.persist(cli);
    	} catch (Exception e) {
    		return false;
    	}
    	return true;
    }
    
    public boolean modificarPerfil(DatosCliente datosNuevos) {
    	try {
    		//Cliente cli = em.find(Cliente.class, datosNuevos.getidfacebook());
    		Cliente cli = entidades.getCliente(datosNuevos.getidfacebook(), datosNuevos.getNombreEmpresa());
    		cli.setNombre(datosNuevos.getNombre());
    		cli.setUrlFoto(datosNuevos.getUrlFoto());
    		em.persist(cli);
    	} catch (Exception e) {
    		return false;
    	}
    	return true;
    }
    
    public DatosCliente obtenerDatosPerfil(String idFacebook){
    	List<Cliente> clientes = null;
    	DatosCliente cliRetorno = null;
		try
	    {
	       	Query query = em.createQuery("SELECT c FROM Cliente c WHERE c.idfacebook=:idFacebook");
	       	query.setParameter("idFacebook", idFacebook);
	       	clientes = (ArrayList<Cliente>) query.getResultList();
	       	
	       	if(clientes!=null && clientes.size()==1) {
	       		Cliente cli = clientes.get(0);
				cliRetorno = new DatosCliente(cli.getIdfacebook(), cli.getNombre(), cli.getEdad(), cli.getSexo(), cli.getPais(), cli.getFechaFinSuscripcion(),cli.isBloqueado(), cli.getUrlFoto());
	       	}

	    }
		catch(Exception e)
	    {
			System.out.println(e.getMessage());
	    }
       	return cliRetorno;
    }
    
    public boolean altaCliente(String idFacebook, String urlFoto, String nombre, String empresa) {
    	
    	try {
    		/**TODO: Quitar harcodeo*/
    		//empresa = "fox";
    		Cliente existe = entidades.getCliente(idFacebook, empresa);
    		
    		//si no existe lo creo
    		if(existe==null) {
	    		Empresa emp = em.find(Empresa.class, empresa);
	    		Cliente cli = new Cliente(idFacebook, "aa", 23, 'M', "aa", urlFoto);
	    		cli.setNombre(nombre);
	    		cli.setEmpresa(emp);
	    		em.persist(cli);
    		}
    	} catch (Exception e) {
    		return false;
    	}
    	return true;
    }
    
    /*Testear*/
    public List<DatosContenido> obtenerFavoritos(String idfacebook, String empresa){
    	//Cliente cli = em.find(Cliente.class, idfacebook);
    	Cliente cli = entidades.getCliente(idfacebook, empresa);
    	Hibernate.initialize(cli.getContenidosFavoritos());
    	
    	List<DatosContenido> retorno = new LinkedList<DatosContenido>();
		
		for(Contenido con : cli.getContenidosFavoritos()) {
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
		
	   	return retorno;
    }
    
    public List<DatosContenido> obtenerHistorico(String idfacebook, String empresa){
    	//Cliente cli = em.find(Cliente.class, idfacebook);
    	Cliente cli = entidades.getCliente(idfacebook, empresa);
    	Hibernate.initialize(cli.getContenidosHistorico());
    	
    	List<DatosContenido> retorno = new LinkedList<DatosContenido>();
		
		for(Contenido con : cli.getContenidosHistorico()) {
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
			Hibernate.initialize(con.getEmpresa());
			conAux.setEmpresa(con.getEmpresa().getNombre());
			Hibernate.initialize(con.getPayPerView());
			if(con.getPayPerView()!=null) {
				conAux.setPrecioPayPerView(con.getPayPerView().getPrecio());
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
		
	   	return retorno;
    }
    
    public boolean bloquearCliente(String idfacebook, String empresa) {

    	try {
    		//Cliente cli = em.find(Cliente.class, idfacebook);
    		Cliente cli = entidades.getCliente(idfacebook, empresa);
    		cli.setBloqueado(true);
    		em.persist(cli);
    	} catch (Exception e) {
    		return false;
    	}
    	return true;
    }
    
    public boolean desbloquearCliente(String idfacebook, String empresa) {

    	try {
    		//Cliente cli = em.find(Cliente.class, idfacebook);
    		Cliente cli = entidades.getCliente(idfacebook, empresa);
    		cli.setBloqueado(false);
    		em.persist(cli);
    	} catch (Exception e) {
    		return false;
    	}
    	return true;
    }
    
    public List<DatosCliente> obtenerClientesEmpresa(String empresa){
    	List<Cliente> clientes = null;
		try
	    {
//			Empresa emp = em.find(Empresa.class, empresa);
	       	Query query = em.createQuery("FROM Cliente c WHERE c.empresa.nombre=:empresa");
	       	query.setParameter("empresa", empresa);
	       	clientes = (ArrayList<Cliente>) query.getResultList();
	    }
		catch(Exception e)
	    {
			System.out.println(e.getMessage());
	    }
		
		List<DatosCliente> retorno = new LinkedList<DatosCliente>();
		
		for(Cliente cli : clientes) {
			DatosCliente cliAux = new DatosCliente(cli.getIdfacebook(), cli.getNombre(), cli.getEdad(), cli.getSexo(), cli.getPais(), cli.getFechaFinSuscripcion(),cli.isBloqueado(), cli.getUrlFoto());
			retorno.add(cliAux);
		}
		
	   	return retorno;
		
    }
    
    public List<DatosCliente> obtenerClientes(){
    	List<Cliente> clientes = null;
		try
	    {
	       	Query query = em.createQuery("FROM Cliente c");
	       	clientes = (ArrayList<Cliente>) query.getResultList();
	    }
		catch(Exception e)
	    {
			System.out.println(e.getMessage());
	    }
		
		List<DatosCliente> retorno = new LinkedList<DatosCliente>();
		
		for(Cliente cli : clientes) {
			DatosCliente cliAux = new DatosCliente(cli.getIdfacebook(), cli.getNombre(), cli.getEdad(), cli.getSexo(), cli.getPais(), cli.getFechaFinSuscripcion(),cli.isBloqueado(), cli.getUrlFoto());
			retorno.add(cliAux);
		}
		
	   	return retorno;
		
    }
    
    public boolean agregarFavorito(String idfacebook, String titulo, String empresa) {
    	try {
	    	//Cliente cli = em.find(Cliente.class, idfacebook);
	    	Cliente cli = entidades.getCliente(idfacebook, empresa);
	    	if(cli.isBloqueado() || !suscripcionEstaVigente(idfacebook, empresa)) {
	    		return false;
	    	}
	    	//Contenido cont = em.find(Contenido.class, titulo);
	    	Contenido cont = entidades.getContenido(titulo, empresa);
	    	
	    	cli.addContenidoFavorito(cont);
	    	em.persist(cli);
    	} catch(Exception e) {
    		System.out.println("ERROR en contenidoDAO->agregarAFavoritos: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
    public boolean agregarHistorico(String idfacebook, String titulo, String empresa) {
    	try {
	    	//Cliente cli = em.find(Cliente.class, idfacebook);
	    	Cliente cli = entidades.getCliente(idfacebook, empresa);
	    	//Contenido cont = em.find(Contenido.class, titulo);
	    	Contenido cont = entidades.getContenido(titulo, empresa);
	    	
	    	cli.addContenidoHistorico(cont);
	    	em.persist(cli);
    	} catch(Exception e) {
    		System.out.println("ERROR en contenidoDAO->agregarHistorico: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
    public boolean quitarFavorito(String idfacebook, String titulo, String empresa) {
    	
    	boolean retorno = false;
    	
    	try {
	    	//Cliente cli = em.find(Cliente.class, idfacebook);
	    	Cliente cli = entidades.getCliente(idfacebook, empresa);
	    	for(Contenido fav : cli.getContenidosFavoritos()) {
	    		if(fav.getTitulo().equals(titulo)) {
	    			cli.getContenidosFavoritos().remove(fav);
	    			em.persist(cli);
	    			retorno = true;
	    			break;
	    		}
	    	}
    	} catch(Exception e) {
    		System.out.println("ERROR en contenidoDAO->agregarAFavoritos: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    	}
    	return retorno;
    }
    
}

