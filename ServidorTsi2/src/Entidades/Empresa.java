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
public class Empresa {
	
	@Id
	private String nombre;
	
	@OneToMany(mappedBy="empresa")
	private List<Contenido> contenidos;
	
	@OneToMany(mappedBy="empresa")
	private List<Suscripcion> suscripciones;
	
	@OneToMany(mappedBy="empresa")
	private List<Cliente> clientes;
	
	@OneToMany(mappedBy="empresa")
	private List<AdminEmpresa> administradores;

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<AdminEmpresa> getAdministradores() {
		return administradores;
	}

	public void setAdministradores(List<AdminEmpresa> administradores) {
		this.administradores = administradores;
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

	public Empresa(String nombre, List<Contenido> contenidos, List<Suscripcion> suscripciones, List<AdminEmpresa> administradores, List<Cliente> clientes) {
		super();
		this.nombre = nombre;
		this.contenidos = contenidos;
		this.suscripciones = suscripciones;
		this.administradores = administradores;
		this.clientes = clientes;
	}
	
	public Empresa(String nombre) {
		super();
		this.nombre = nombre;
		// TODO Auto-generated constructor stub
	}

	public Empresa() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public boolean addContenido(Contenido con) {
        boolean bRetorno = true;
        if(this.contenidos != null) {
        	if (!this.contenidos.contains(con)) {
                bRetorno = this.contenidos.add(con);
            }
        } else {
        	this.contenidos = new LinkedList<Contenido>();
        	this.contenidos.add(con);
        }
        return bRetorno;
    }
	
	public boolean addCliente(Cliente cli) {
        boolean bRetorno = true;
        if(this.clientes != null) {
        	if (!this.clientes.contains(cli)) {
                bRetorno = this.clientes.add(cli);
            }
        } else {
        	this.clientes = new LinkedList<Cliente>();
        	this.clientes.add(cli);
        }
        return bRetorno;
    }
	
	public boolean addSuscripcion(Suscripcion sus) {
        boolean bRetorno = true;
        if(this.suscripciones != null) {
        	if (!this.suscripciones.contains(sus)) {
                bRetorno = this.suscripciones.add(sus);
            }
        } else {
        	this.suscripciones = new LinkedList<Suscripcion>();
        	this.suscripciones.add(sus);
        }
        return bRetorno;
    }
	
	public boolean addAdministrador(AdminEmpresa admin) {
        boolean bRetorno = true;
        if(this.administradores != null) {
        	if (!this.administradores.contains(admin)) {
                bRetorno = this.administradores.add(admin);
            }
        } else {
        	this.administradores = new LinkedList<AdminEmpresa>();
        	this.administradores.add(admin);
        }
        return bRetorno;
    }

	public List<Suscripcion> getSuscripciones() {
		return suscripciones;
	}

	public void setSuscripciones(List<Suscripcion> suscripciones) {
		this.suscripciones = suscripciones;
	}

}
