package Entidades;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Notificacion {
	
	@Id
	@GeneratedValue
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_notificante", referencedColumnName = "id")
	private Cliente notificante;
	
	@ManyToOne
	@JoinColumn(name = "id_receptor", referencedColumnName = "id")
	private Cliente receptor;
	
	@ManyToOne
	@JoinColumn(name = "id_contenido", referencedColumnName = "id")
	private Contenido contenido;

	public Cliente getNotificante() {
		return notificante;
	}

	public void setNotificante(Cliente notificante) {
		this.notificante = notificante;
	}

	public Cliente getReceptor() {
		return receptor;
	}

	public void setReceptor(Cliente receptor) {
		this.receptor = receptor;
	}

	public Contenido getContenido() {
		return contenido;
	}

	public void setContenido(Contenido contenido) {
		this.contenido = contenido;
	}

	public Notificacion() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Notificacion(Cliente notificante, Cliente receptor, Contenido contenido) {
		super();
		this.notificante = notificante;
		this.receptor = receptor;
		this.contenido = contenido;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	
	
}
