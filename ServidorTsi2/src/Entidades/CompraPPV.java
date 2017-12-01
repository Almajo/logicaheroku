package Entidades;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class CompraPPV {

	@Id
	@GeneratedValue
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaVencimiento;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_ppv", referencedColumnName = "id")
	private PayPerView payPerView;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public PayPerView getPayPerView() {
		return payPerView;
	}

	public void setPayPerView(PayPerView payPerView) {
		this.payPerView = payPerView;
	}

	public CompraPPV(Date fechaVencimiento, PayPerView payPerView) {
		super();
		this.fechaVencimiento = fechaVencimiento;
		this.payPerView = payPerView;
	}

	public CompraPPV() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
