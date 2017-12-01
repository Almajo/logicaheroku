package Entidades;

import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class EstadoVisualizacion {

	@Id
	@GeneratedValue
	private int id;
	
	@Temporal(TemporalType.TIME)
	private Date tiempo;
	
	@ManyToOne
	@JoinColumn(name = "id_contenido", referencedColumnName = "id")
	private Contenido contenido;
	
	@ManyToOne
	@JoinColumn(name = "id_cliente", referencedColumnName = "id")
	private Cliente cliente;

	public EstadoVisualizacion(Contenido contenido, Cliente cliente, Date tiempo) {
		super();
		this.contenido = contenido;
		this.cliente = cliente;
		this.tiempo = tiempo;
	}

	public EstadoVisualizacion() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Contenido getContenido() {
		return contenido;
	}

	public void setContenido(Contenido contenido) {
		this.contenido = contenido;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Date getTiempo() {
		return tiempo;
	}

	public void setTiempo(Date tiempo) {
		this.tiempo = tiempo;
	}
	
	
	
}
