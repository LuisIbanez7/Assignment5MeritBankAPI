package com.assignments.assignment5.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity(name = "CDAccount")
@Table(name = "CDAccount")
public class CDAccount extends BankAccount {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "offering_id", nullable = false) 
	private CDOffering cDOffering;
	 
	public CDAccount() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@JsonBackReference(value="cdAccount")
	public CDOffering getcDOffering() {
		return cDOffering;
	}

	public void setcDOffering(CDOffering cDOffering) {
		this.cDOffering = cDOffering;
	}
}