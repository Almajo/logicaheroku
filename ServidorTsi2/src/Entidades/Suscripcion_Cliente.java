package Entidades;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Suscripcion_Cliente {

	@Id
	@GeneratedValue
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicio;
	
	private double precioPagado;

	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_suscripcion", referencedColumnName = "id")
	private Suscripcion suscripcion;
	
	public Suscripcion_Cliente() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Suscripcion_Cliente(Date fechaInicio, double precioPagado, Suscripcion suscripcion) {
		super();
		this.fechaInicio = fechaInicio;
		this.precioPagado = precioPagado;
		this.suscripcion = suscripcion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public double getPrecioPagado() {
		return precioPagado;
	}

	public void setPrecioPagado(double precioPagado) {
		this.precioPagado = precioPagado;
	}

	public Suscripcion getSuscripcion() {
		return suscripcion;
	}

	public void setSuscripcion(Suscripcion suscripcion) {
		this.suscripcion = suscripcion;
	}
	
}
