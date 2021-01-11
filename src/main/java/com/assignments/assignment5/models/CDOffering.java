package com.assignments.assignment5.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "CDOffering")
public class CDOffering {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "offering_id")
	private Integer id;
	
	@Min(value = 1, message = "Term must be atleast one")
	Integer term;
	
	@DecimalMin(value = "0.0", inclusive = false, message = "interest rate must be greater than zero")
	@DecimalMax(value = "1.0", inclusive = false, message = "interest rate must be less than one")
	double interestRate; 

	@OneToMany(mappedBy = "cDOffering", fetch = FetchType.LAZY,
			cascade = CascadeType.ALL)
	private List<CDAccount> cDAccounts;
	
	public CDOffering() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@JsonManagedReference(value="cdAccount")
	public List<CDAccount> getcDAccounts() {
		return cDAccounts;
	}

	public void setcDAccounts(List<CDAccount> cDAccounts) {
		this.cDAccounts = new ArrayList<CDAccount>(cDAccounts);
	}

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

}