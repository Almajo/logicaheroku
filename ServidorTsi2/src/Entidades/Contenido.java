package Entidades;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Contenido {
	
	@Id
	@GeneratedValue
	private int id;
	
	private String titulo;
	
	private String descripcion;
	private int cantPuntuaciones;
	private Double puntuacion;
	private boolean destacado;
	private boolean bloqueado;
	private String portada;
	private String url;
	private String urlLive;
	
	@ElementCollection
	private List<String> elenco;
	@ElementCollection
	private List<String> directores;
	
	@ManyToMany(mappedBy="contenidosCategoria", cascade = CascadeType.ALL)
	private List<Categoria> categorias;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "nombre_tipocontenido", referencedColumnName = "nombre")
	private TipoContenido tipoContenido;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "nombre_empresa", referencedColumnName = "nombre")
	private Empresa empresa;
	
	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_contenido", referencedColumnName = "id")
	private List<AtributoContenido> atributos;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_contenido", referencedColumnName = "id")
	private PayPerView payPerView;
	
	@OneToMany(mappedBy="contenido")
	private List<Comentario> comentarios;

	public Contenido() {
		super();
	}
	
	public PayPerView getPayPerView() {
		return payPerView;
	}

	public void setPayPerView(PayPerView payPerView) {
		this.payPerView = payPerView;
	}

	/**Contructor para contenidos que no sean eventos*/
	public Contenido(String titulo, String descripcion, List<String> elenco, List<String> directores,
			List<Categoria> categorias, String portada, String url) {
		super();
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.cantPuntuaciones = 0;
		this.puntuacion = null;
		this.destacado = false;
		this.bloqueado = false;
		this.elenco = elenco;
		this.directores = directores;
		this.categorias = categorias;
		this.portada=portada;
		this.url = url;
	}
	
	/**Constructor para eventos*/
	public Contenido(String titulo, String descripcion, Date comienzo, Date fin, List<Categoria> categorias, String url) {
		super();
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.cantPuntuaciones = 0;
		this.puntuacion = null;
		this.destacado = false;
		this.bloqueado = false;
		this.elenco = null;
		this.directores = null;
		this.categorias = categorias;
		this.url = url;
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
	
	
	public boolean addAtributo(AtributoContenido atr) {
        boolean bRetorno = true;
        if(this.atributos != null) {
        	if (!this.atributos.contains(atr)) {
                bRetorno = this.atributos.add(atr);
            }
        } else {
        	this.atributos = new LinkedList<AtributoContenido>();
        	this.atributos.add(atr);
        }
        return bRetorno;
    }
	
	public String getPortada() {
		return portada;
	}

	public TipoContenido getTipoContenido() {
		return tipoContenido;
	}

	public void setTipoContenido(TipoContenido tipoContenido) {
		this.tipoContenido = tipoContenido;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<AtributoContenido> getAtributos() {
		return atributos;
	}

	public void setAtributos(List<AtributoContenido> atributos) {
		this.atributos = atributos;
	}

	public void setPortada(String portada) {
		this.portada = portada;
	}

	public void setPuntuacion(Double puntuacion) {
		this.puntuacion = puntuacion;
	}
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getCantPuntuaciones() {
		return cantPuntuaciones;
	}

	public void setCantPuntuaciones(int cantPuntuaciones) {
		this.cantPuntuaciones = cantPuntuaciones;
	}

	public Double getPuntuacion() {
		return puntuacion;
	}

	public boolean isDestacado() {
		return destacado;
	}

	public void setDestacado(boolean destacado) {
		this.destacado = destacado;
	}

	public boolean isBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

	public List<String> getElenco() {
		return elenco;
	}

	public void setElenco(List<String> elenco) {
		this.elenco = elenco;
	}

	public List<String> getDirectores() {
		return directores;
	}

	public void setDirectores(List<String> directores) {
		this.directores = directores;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}
	
	public List<Comentario> getComentarios() {
		return comentarios;
	}

	public void setComentarios(List<Comentario> comentarios) {
		this.comentarios = comentarios;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlLive() {
		return urlLive;
	}

	public void setUrlLive(String urlLive) {
		this.urlLive = urlLive;
	}
	
}
