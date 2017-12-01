package Entidades;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="usuario_administrador", referencedColumnName="usuario")
public class AdminEmpresa extends Administrador {
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "nombre_empresa", referencedColumnName = "nombre")
	private Empresa empresa;

	public AdminEmpresa() {
		super();
	}

	public AdminEmpresa(String usuario, String password, Empresa empresa) {
		super(usuario, password);
		this.empresa = empresa;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
}
