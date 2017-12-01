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
public class AtributoContenido {

	@Id
	@GeneratedValue
	private int id;
	
	private String valor;
	
	@ManyToOne
	@JoinColumn(name = "id_atributotipocontenido", referencedColumnName = "id")
	private AtributoTipoContenido atributoTipoContenido;

	
	public AtributoContenido(String valor, AtributoTipoContenido atributoTipoContenido) {
		super();
		this.valor = valor;
		this.atributoTipoContenido = atributoTipoContenido;
	}

	public AtributoContenido() {
		super();
		// TODO Auto-generated constructor stub
	}	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public AtributoTipoContenido getAtributoTipoContenido() {
		return atributoTipoContenido;
	}

	public void setAtributoTipoContenido(AtributoTipoContenido atributoTipoContenido) {
		this.atributoTipoContenido = atributoTipoContenido;
	}

}
