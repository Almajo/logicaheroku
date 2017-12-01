package daos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.spi.CalendarNameProvider;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.postgresql.jdbc.PSQLSavepoint;

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
import datatypes.DatosIngresoMensual;
import datatypes.DatosPuntuacionContenido;
import datatypes.DatosSuscripcion;
import datatypes.DatosTipoContenido;

@Stateless
@LocalBean
public class EmpresaDao {

    @PersistenceContext
    private EntityManager em;
    
    
    //Ver como sabe diego en la presentacion en qué orden mostrar los meses... debería poder indicar el año además del mes..
    public List<DatosIngresoMensual> obtenerIngresosSuscripcionesMensuales(String empresa){
    	List<DatosIngresoMensual> retorno = new LinkedList<DatosIngresoMensual>();
    	List<Suscripcion_Cliente> suscripcionesEmpresa = new LinkedList<Suscripcion_Cliente>();
    	
    	Query query = em.createQuery("FROM Suscripcion_Cliente sc WHERE sc.suscripcion.empresa.nombre=:empresa");
		query.setParameter("empresa", empresa);
		suscripcionesEmpresa = (ArrayList<Suscripcion_Cliente>) query.getResultList();
		
		DatosIngresoMensual Enero = new DatosIngresoMensual("Enero", 0, 1);
		DatosIngresoMensual Febrero = new DatosIngresoMensual("Febrero", 0, 2);
		DatosIngresoMensual Marzo = new DatosIngresoMensual("Marzo", 0, 3);
		DatosIngresoMensual Abril = new DatosIngresoMensual("Abril", 0, 4);
		DatosIngresoMensual Mayo = new DatosIngresoMensual("Mayo", 0, 5);
		DatosIngresoMensual Junio = new DatosIngresoMensual("Junio", 0, 6);
		DatosIngresoMensual Julio = new DatosIngresoMensual("Julio", 0, 7);
		DatosIngresoMensual Agosto = new DatosIngresoMensual("Agosto", 0, 8);
		DatosIngresoMensual Setiembre = new DatosIngresoMensual("Setiembre", 0, 9);
		DatosIngresoMensual Octubre = new DatosIngresoMensual("Octubre", 0, 10);
		DatosIngresoMensual Noviembre = new DatosIngresoMensual("Noviembre", 0, 11);
		DatosIngresoMensual Diciembre = new DatosIngresoMensual("Diciembre", 0, 12);
		
		Date hoy = new Date();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(hoy);
		int mesActual = cal.get(Calendar.MONTH);
		int anioActual = cal.get(Calendar.YEAR);
		cal.add(Calendar.YEAR, -1);
		Date haceUnAno = cal.getTime();
		
		for(Suscripcion_Cliente sc : suscripcionesEmpresa) {
			
			Date fechaSuscripcion = sc.getFechaInicio();
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(fechaSuscripcion);
			int mes = cal2.get(Calendar.MONTH) + 1;
			
			//obtengo las suscripciones del ultimo anio y descarto las del mes actual porque hasta que no cierre el mes no se sabe
			if(fechaSuscripcion.after(haceUnAno) && cal2.get(Calendar.MONTH)!=mesActual) {
								
				if(mes == 1) {
					Enero.setIngreso(Enero.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 2) {
					Febrero.setIngreso(Febrero.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 3) {
					Marzo.setIngreso(Marzo.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 4) {
					Abril.setIngreso(Abril.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 5) {
					Mayo.setIngreso(Mayo.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 6) {
					Junio.setIngreso(Junio.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 7) {
					Julio.setIngreso(Julio.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 8) {
					Agosto.setIngreso(Agosto.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 9) {
					Setiembre.setIngreso(Setiembre.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 10) {
					Octubre.setIngreso(Octubre.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 11) {
					Noviembre.setIngreso(Noviembre.getIngreso() + sc.getPrecioPagado());
				} else if(mes == 12) {
					Diciembre.setIngreso(Diciembre.getIngreso() + sc.getPrecioPagado());
				}
			}
		}
    	
		retorno.add(Enero);
		retorno.add(Febrero);
		retorno.add(Marzo);
		retorno.add(Abril);
		retorno.add(Mayo);
		retorno.add(Junio);
		retorno.add(Julio);
		retorno.add(Agosto);
		retorno.add(Setiembre);
		retorno.add(Octubre);
		retorno.add(Noviembre);
		retorno.add(Diciembre);
		
		//reordeno segun mes actual las posiciones, ademas elimino el mes actual
		mesActual = mesActual +1;
		int contador = 0;
		int posicionBorrar = -1;
		for(DatosIngresoMensual mesRetorno : retorno) {
			if(mesRetorno.getPosicion() == mesActual) {
				posicionBorrar = contador;
			} else if(mesRetorno.getPosicion()>mesActual) {
				mesRetorno.setPosicion(mesRetorno.getPosicion()-mesActual);
				mesRetorno.setAnio(anioActual - 1);
			} else {
				mesRetorno.setPosicion(mesRetorno.getPosicion() + (12-mesActual));
				mesRetorno.setAnio(anioActual);
			}
			contador++;
		}
		
		retorno.remove(posicionBorrar);
		
    	return retorno;
    }
    
    
    
    public List<DatosPuntuacionContenido> obtenerTopFiveContenidos(String empresa){
    	
    	List<DatosPuntuacionContenido> retorno = new LinkedList<DatosPuntuacionContenido>();
    	
    	try {
	    	List<Contenido> contenidos = null;
	    	
	    	Query query = em.createQuery("SELECT c FROM Contenido c WHERE c.empresa.nombre=:empresa ORDER BY c.puntuacion DESC");
	    	query.setParameter("empresa", empresa);
	       	contenidos = (ArrayList<Contenido>) query.getResultList();
	       	
	       	int contador = 0;
	       	
	       	for(Contenido con : contenidos) {
	       		DatosPuntuacionContenido dpc = new DatosPuntuacionContenido(con.getTitulo(), con.getPuntuacion());
	       		retorno.add(dpc);
	       		contador++;
	       		if(contador==5) {
	       			break;
	       		}
	       	}
       	}catch(Exception e) {
       		System.out.println("ERROR en empresaDao->obtenerTopTenContenidos: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
       		return null;
       	}
       	
    	return retorno;
    }
    
    public Boolean agregarEmpresa(String empresa) {
    	try{
	    	Empresa emp = new Empresa(empresa);
	    	em.persist(emp);
    	} catch(Exception e) {
			System.out.println("ERROR en empresaDao->agregarEmpresa: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	
    	return true;
    	
    }
    
    public Boolean agregarSuscripcion(String empresa, String tipo, double precioAux) {
    	try {
	    	Empresa emp = em.find(Empresa.class, empresa);
	    	Suscripcion sus = new Suscripcion(tipo, precioAux, null, emp);
	    	
	    	em.persist(sus);
    	} catch(Exception e) {
    		System.out.println("ERROR en empresaDao->agregarSuscripcion: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return false;
    	}
    	return true;
    }
    
    public List<DatosSuscripcion> obtenerTiposSuscripcionEmpresa(String empresa) {
    	List<DatosSuscripcion> suscripcionesEmpresa = new LinkedList<DatosSuscripcion>();
    	
    	try {
    		
    		Query query = em.createQuery("FROM Suscripcion s WHERE s.empresa.nombre=:empresa");
    		query.setParameter("empresa", empresa);

	       	List<Suscripcion> suscripciones = (ArrayList<Suscripcion>) query.getResultList();
    		
	       	for(Suscripcion sus : suscripciones) {
	       		DatosSuscripcion ds = new DatosSuscripcion(sus.getTipo(), sus.getPrecio());
	       		suscripcionesEmpresa.add(ds);
	       	}
	       	

    	} catch (Exception e) {
    		System.out.println("ERROR en empresaDao->obtenerTiposSuscripcionEmpresa: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return null;
    	}
    	
    	return suscripcionesEmpresa;
    }
    
    public List<String> obtenerEmpresas(){
    	
    	List<String> empresas = new LinkedList<String>();
    	
    	try {
    		
    		Query query = em.createQuery("FROM Empresa e");
	       	List<Empresa> empresasAux = (ArrayList<Empresa>) query.getResultList();
    		
	       	for(Empresa aux : empresasAux) {
	       		empresas.add(aux.getNombre());
	       	}
	       	

    	} catch (Exception e) {
    		System.out.println("ERROR en empresaDao->obtenerEmpresas: " + e.getMessage() + "CAUSA: " + e.getCause().getMessage());
    		return null;
    	}
    	
    	return empresas;
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
