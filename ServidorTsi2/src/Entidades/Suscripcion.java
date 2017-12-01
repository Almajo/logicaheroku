package Entidades;

import java.util.LinkedList;
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
import javax.persistence.OneToMany;

@Entity
public class Suscripcion {

	@Id
	@GeneratedValue
	private int id;
	
	private String tipo;
	
	private double precio;
	
	@OneToMany(mappedBy="suscripcion", fetch=FetchType.LAZY)
	private List<Suscripcion_Cliente> historicoSuscripciones;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "nombre_empresa", referencedColumnName = "nombre")
	private Empresa empresa;

	public Suscripcion(String tipo, double precio, List<Suscripcion_Cliente> historicoSuscripciones, Empresa empresa) {
		super();
		this.tipo = tipo;
		this.precio = precio;
		this.historicoSuscripciones = historicoSuscripciones;
		this.empresa = empresa;
	}

	public Suscripcion() {
		super();
		this.historicoSuscripciones = new LinkedList<Suscripcion_Cliente>();
		// TODO Auto-generated constructor stub
	}

	public boolean addSuscripcion_Cliente(Suscripcion_Cliente suscripcion) {
        boolean bRetorno = true;
        if (!this.historicoSuscripciones.contains(suscripcion)) {
            bRetorno = this.historicoSuscripciones.add(suscripcion);
        }
        return bRetorno;
    }
	
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public List<Suscripcion_Cliente> getHistoricoSuscripciones() {
		return historicoSuscripciones;
	}

	public void setHistoricoSuscripciones(List<Suscripcion_Cliente> historicoSuscripciones) {
		this.historicoSuscripciones = historicoSuscripciones;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	
}
