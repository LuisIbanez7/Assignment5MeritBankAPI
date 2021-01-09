package com.assignments.assignment5.models;

import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "AccountHolder")
public class AccountHolder {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "accountHolder_id")
	Integer id;

	@NotNull(message = "First Name can not be null")
	@NotBlank(message = "First Name can not be blank")
	String firstName;

	String middleName;
	
	@NotNull(message = "Last Name can not be null")
	@NotBlank(message = "Last Name can not be blank")
	String lastName;
	
	@NotNull(message = "SSN can not be null")
	@NotBlank(message = "SSN can not be blank")
	String SSN;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "accountHolder", fetch = FetchType.LAZY)
	private List<CheckingAccount> checkingAccounts;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "accountHolder", fetch = FetchType.LAZY)
	private List<SavingsAccount> savingsAccounts;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "accountHolder", fetch = FetchType.LAZY)
	private List<CDAccount> cDAccounts;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "account_holders_contact_details_id", referencedColumnName = "account_holders_contact_details_id") 
	AccountHoldersContactDetails accountHoldersContactDetails;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "users_id") 
	private User user;
	
	public AccountHolder() {
	}

	public AccountHoldersContactDetails getAccountHoldersContactDetails() {
		return accountHoldersContactDetails;
	}

	public void setAccountHoldersContactDetails(AccountHoldersContactDetails accountHoldersContactDetails) {
		this.accountHoldersContactDetails = accountHoldersContactDetails;
	}

	@JsonManagedReference(value="users")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@JsonManagedReference
	public List<CheckingAccount> getCheckingAccounts() {
		return checkingAccounts;
	}
	public void setCheckingAccounts(List<CheckingAccount> checkingAccounts) {
		this.checkingAccounts = new ArrayList<CheckingAccount>(checkingAccounts);
	}
	@JsonManagedReference
	public List<SavingsAccount> getSavingsAccounts() {
		return savingsAccounts;
	}
	public void setSavingsAccounts(List<SavingsAccount> savingsAccounts) {
		this.savingsAccounts = new ArrayList<SavingsAccount>(savingsAccounts);
	}
	@JsonManagedReference
	public List<CDAccount> getcDAccounts() {
		return cDAccounts;
	}
	public void setcDAccounts(List<CDAccount> cDAccounts) {
		this.cDAccounts = new ArrayList<CDAccount>(cDAccounts);
	}
	public Integer getId() {
		return id;
	}
	public AccountHolder setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getFirstName() {
		return firstName;
	}
	public AccountHolder setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	public String getMiddleName() {
		return middleName;
	}
	public AccountHolder setMiddleName(String middleName) {
		this.middleName = middleName;
		return this;
	}
	public String getLastName() {
		return lastName;
	}
	public AccountHolder setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	public String getSSN() {
		return SSN;
	}
	public AccountHolder setSSN(String sSN) {
		SSN = sSN;
		return this;
	}
	public int getNumberOfCheckingAccounts() {
		if (checkingAccounts != null) {
			return checkingAccounts.size();
		}
		return 0;
	}
	public double getCheckingBalance() {
		double totalBalance = 0;
		if (checkingAccounts != null) {
			for (BankAccount ca : checkingAccounts) {
				totalBalance = totalBalance + ca.getBalance();
			}
			return totalBalance;
		}
		return 0;
	}
	public int getNumberOfSavingsAccounts() {
		if (savingsAccounts != null) {
			return savingsAccounts.size();
		}
		return 0;
	}
	public double getSavingsBalance() {
		double totalBalance = 0;
		if (savingsAccounts != null) {
			for (BankAccount sa : savingsAccounts) {
				totalBalance = totalBalance + sa.getBalance();
			}
		}
		return totalBalance;
	}
	public int getNumberOfCDAccounts() {
		if (cDAccounts != null) {
			return cDAccounts.size();
		}
		return 0;
	}
	public double getCdbalance() {
		double totalBalance = 0;
		if (cDAccounts != null) {
			for (BankAccount cda : cDAccounts) {
				totalBalance = totalBalance + cda.getBalance();
			}
		}
		return totalBalance;
	}
	public double getCombinedBalance() {
		return getCheckingBalance() + getSavingsBalance() + getCdbalance();
	}
}