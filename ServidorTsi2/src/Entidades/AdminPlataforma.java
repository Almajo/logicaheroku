package Entidades;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="usuario_administrador", referencedColumnName="usuario")
public class AdminPlataforma extends Administrador {

	public AdminPlataforma() {
		super();
	}

	public AdminPlataforma(String usuario, String password) {
		super(usuario, password);
	}

}
