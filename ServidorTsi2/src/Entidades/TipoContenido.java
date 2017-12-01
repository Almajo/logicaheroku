package Entidades;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
public class TipoContenido {
	
	@Id
	private String nombre;
	
	private boolean vivo;
	
	@ManyToMany(cascade = CascadeType.ALL)/*(fetch=FetchType.EAGER)*/
	@JoinTable(name = "tipocontenido_categoria", joinColumns = @JoinColumn(name = "nombre_tipocontenido", referencedColumnName = "nombre"), inverseJoinColumns = @JoinColumn(name = "id_categoria", referencedColumnName = "id"))
	private List<Categoria> categorias;
	
	@OneToMany(mappedBy="tipoContenido")
	private List<Contenido> contenidos;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "nombre_tipocontenido", referencedColumnName = "nombre")
	private List<AtributoTipoContenido> atributos;

	public TipoContenido(List<Categoria> categorias, String nombre, List<AtributoTipoContenido> atributos, boolean vivo) {
		super();
		this.categorias = categorias;
		this.nombre = nombre;
		this.atributos = atributos;
		this.vivo = vivo;
	}

	public TipoContenido() {
		super();
	}

	
	public boolean addCategoria(Categoria cat) {
        boolean bRetorno = true;
        if(this.categorias != null) {
        	if (!this.categorias.contains(cat)) {
                bRetorno = this.categorias.add(cat);
            }
        } else {
        	this.categorias = new LinkedList<Categoria>();
        	this.categorias.add(cat);
        }
        return bRetorno;
    }
	
	public boolean addAtributo(AtributoTipoContenido atr) {
        boolean bRetorno = true;
        if(this.atributos != null) {
        	if (!this.atributos.contains(atr)) {
                bRetorno = this.atributos.add(atr);
            }
        } else {
        	this.atributos = new LinkedList<AtributoTipoContenido>();
        	this.atributos.add(atr);
        }
        return bRetorno;
    }

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Contenido> getContenidos() {
		return contenidos;
	}

	public void setContenidos(List<Contenido> contenidos) {
		this.contenidos = contenidos;
	}

	public List<AtributoTipoContenido> getAtributos() {
		return atributos;
	}

	public void setAtributos(List<AtributoTipoContenido> atributos) {
		this.atributos = atributos;
	}

	public boolean isVivo() {
		return vivo;
	}

	public void setVivo(boolean vivo) {
		this.vivo = vivo;
	}

}
