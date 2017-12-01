package Entidades;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
public class Categoria {

	@Id
	@GeneratedValue
	private int id;
	
	private String nombre;
	
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name = "categoria_contenido", joinColumns = @JoinColumn(name = "id_categoria", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_contenido", referencedColumnName = "id"))
	private List<Contenido> contenidosCategoria;
	

	public Categoria() { 
		
	}
	
	public Categoria(String nombre) {
		super();
		this.nombre = nombre;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public boolean addContenido(Contenido con) {
        boolean bRetorno = true;
        if(this.contenidosCategoria != null) {
        	if (!this.contenidosCategoria.contains(con)) {
                bRetorno = this.contenidosCategoria.add(con);
            }
        } else {
        	this.contenidosCategoria = new LinkedList<Contenido>();
        	this.contenidosCategoria.add(con);
        }
        return bRetorno;
    }
	
}
