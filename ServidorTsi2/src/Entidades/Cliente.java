package Entidades;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Cliente {
	
	@Id
	@GeneratedValue
	private int id;
	
	private String idfacebook;
	
//	@EmbeddedId
//	private idCliente idCliente;
	
	private String urlFoto;
	private String nombre;
	private int edad;
	private char sexo;
	private String pais;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFinSuscripcion;
	private boolean bloqueado;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "nombre_empresa", referencedColumnName = "nombre")
	private Empresa empresa;
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_cliente", referencedColumnName = "id")
	private List<Suscripcion_Cliente> historicoSuscripciones;
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_cliente", referencedColumnName = "id")
	private List<CompraPPV> comprasPayPerView;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "cliente_contenido_favoritos", joinColumns = @JoinColumn(name = "id_cliente", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_contenido", referencedColumnName = "id"))
	private List<Contenido> contenidosFavoritos;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "cliente_contenido_omitidos", joinColumns = @JoinColumn(name = "id_cliente", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_contenido", referencedColumnName = "id"))
	private List<Contenido> contenidosOmitidos;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "cliente_contenido_sugeridos", joinColumns = @JoinColumn(name = "id_cliente", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_contenido", referencedColumnName = "id"))
	private List<Contenido> contenidosSugeridos;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "cliente_contenido_historico", joinColumns = @JoinColumn(name = "id_cliente", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_contenido", referencedColumnName = "id"))
	private List<Contenido> contenidosHistorico;
	
	@OneToMany(mappedBy="cliente", fetch=FetchType.LAZY)
	private List<Comentario> comentarios;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "cliente_comentariosdenunciados", joinColumns = @JoinColumn(name = "id_cliente", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_comentario", referencedColumnName = "id"))
	private List<Comentario> comentariosDenunciados;
	
	//@JoinTable(name = "cliente_ranking", joinColumns = @JoinColumn(name = "id_cliente", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_contenido", referencedColumnName = "titulo"))
	@OneToMany
	@JoinColumn(name = "id_cliente", referencedColumnName = "id")
	private List<Ranking> rankeados;
	
	
	public List<Comentario> getComentarios() {
		return comentarios;
	}

	public void setComentarios(List<Comentario> comentarios) {
		this.comentarios = comentarios;
	}

	public List<Comentario> getComentariosDenunciados() {
		return comentariosDenunciados;
	}

	public void setComentariosDenunciados(List<Comentario> comentariosDenunciados) {
		this.comentariosDenunciados = comentariosDenunciados;
	}
	
	public List<CompraPPV> getComprasPayPerView() {
		return comprasPayPerView;
	}

	public void setComprasPayPerView(List<CompraPPV> comprasPayPerView) {
		this.comprasPayPerView = comprasPayPerView;
	}


	public Cliente() {
		this.historicoSuscripciones = new LinkedList<Suscripcion_Cliente>();
		this.bloqueado = false;
	}

	public Cliente(String idfacebook, String nombre, int edad, char sexo, String pais, String urlFoto/*, Date fechaFinSuscripcion*/) {
		super();
		this.idfacebook = idfacebook;
		this.nombre = nombre;
		this.edad = edad;
		this.sexo = sexo;
		this.pais = pais;
		this.urlFoto = urlFoto;
		this.bloqueado = false;
		//this.fechaFinSuscripcion = fechaFinSuscripcion;
	}
	
	public Cliente(String idfacebook, String nombre, int edad, char sexo, String pais, Date fechaFinSuscripcion,
			boolean bloqueado, String urlFoto) {
		super();
		this.idfacebook = idfacebook;
		this.nombre = nombre;
		this.edad = edad;
		this.sexo = sexo;
		this.pais = pais;
		this.fechaFinSuscripcion = fechaFinSuscripcion;
		this.bloqueado = bloqueado;
		this.urlFoto = urlFoto;
	}

	public boolean addSuscripcion_Cliente(Suscripcion_Cliente suscripcion) {
        boolean bRetorno = true;
        if(this.historicoSuscripciones != null) {
        	if (!this.historicoSuscripciones.contains(suscripcion)) {
                bRetorno = this.historicoSuscripciones.add(suscripcion);
            }
        } else {
        	this.historicoSuscripciones = new LinkedList<Suscripcion_Cliente>();
        	this.historicoSuscripciones.add(suscripcion);
        }
        return bRetorno;
    }
	
	public boolean addContenidoSugerido(Contenido sugerido) {
        boolean bRetorno = true;
        if(this.contenidosSugeridos != null) {
        	if (!this.contenidosSugeridos.contains(sugerido)) {
                bRetorno = this.contenidosSugeridos.add(sugerido);
            }
        } else {
        	this.contenidosSugeridos = new LinkedList<Contenido>();
        	this.contenidosSugeridos.add(sugerido);
        }
        return bRetorno;
    }
	
	public boolean addContenidoHistorico(Contenido historico) {
        boolean bRetorno = true;
        if(this.contenidosHistorico != null) {
        	if (!this.contenidosHistorico.contains(historico)) {
                bRetorno = this.contenidosHistorico.add(historico);
            }
        } else {
        	this.contenidosHistorico = new LinkedList<Contenido>();
        	this.contenidosHistorico.add(historico);
        }
        return bRetorno;
    }
	
	public boolean addContenidoFavorito(Contenido favorito) {
        boolean bRetorno = true;
        if(this.contenidosFavoritos != null) {
        	if (!this.contenidosFavoritos.contains(favorito)) {
                bRetorno = this.contenidosFavoritos.add(favorito);
            }
        } else {
        	this.contenidosFavoritos = new LinkedList<Contenido>();
        	this.contenidosFavoritos.add(favorito);
        }
        return bRetorno;
    }
	
	public boolean addComentarioDenunciado(Comentario comentarioADenunciar) {
        boolean bRetorno = true;
        if(this.comentariosDenunciados != null) {
        	if (!this.comentariosDenunciados.contains(comentarioADenunciar)) {
                bRetorno = this.comentariosDenunciados.add(comentarioADenunciar);
            }
        } else {
        	this.comentariosDenunciados = new LinkedList<Comentario>();
        	this.comentariosDenunciados.add(comentarioADenunciar);
        }
        return bRetorno;
    }
	
	public boolean addComprasPayPerView(CompraPPV compra) {
        boolean bRetorno = true;
        if(this.comprasPayPerView != null) {
        	if (!this.comprasPayPerView.contains(compra)) {
                bRetorno = this.comprasPayPerView.add(compra);
            }
        } else {
        	this.comprasPayPerView = new LinkedList<CompraPPV>();
        	this.comprasPayPerView.add(compra);
        }
        return bRetorno;
    }
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdfacebook() {
		return idfacebook;
	}

	public void setIdfacebook(String idfacebook) {
		this.idfacebook = idfacebook;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public List<Ranking> getRankeados() {
		return rankeados;
	}

	public void setRankeados(List<Ranking> rankeados) {
		this.rankeados = rankeados;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public char getSexo() {
		return sexo;
	}

	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public Date getFechaFinSuscripcion() {
		return fechaFinSuscripcion;
	}

	public void setFechaFinSuscripcion(Date fechaFinSuscripcion) {
		this.fechaFinSuscripcion = fechaFinSuscripcion;
	}

	public List<Suscripcion_Cliente> getHistoricoSuscripciones() {
		return historicoSuscripciones;
	}

	public List<Contenido> getContenidosFavoritos() {
		return contenidosFavoritos;
	}

	public void setContenidosFavoritos(List<Contenido> contenidosFavoritos) {
		this.contenidosFavoritos = contenidosFavoritos;
	}

	public List<Contenido> getContenidosOmitidos() {
		return contenidosOmitidos;
	}

	public void setContenidosOmitidos(List<Contenido> contenidosOmitidos) {
		this.contenidosOmitidos = contenidosOmitidos;
	}

	public List<Contenido> getContenidosSugeridos() {
		return contenidosSugeridos;
	}

	public void setContenidosSugeridos(List<Contenido> contenidosSugeridos) {
		this.contenidosSugeridos = contenidosSugeridos;
	}

	public List<Contenido> getContenidosHistorico() {
		return contenidosHistorico;
	}

	public void setContenidosHistorico(List<Contenido> contenidosHistorico) {
		this.contenidosHistorico = contenidosHistorico;
	}

	public void setHistoricoSuscripciones(List<Suscripcion_Cliente> historicoSuscripciones) {
		this.historicoSuscripciones = historicoSuscripciones;
	}

	public boolean isBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

	public String getUrlFoto() {
		return urlFoto;
	}

	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}

}
