package gokay.loanapplication.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Customer {
	
	@Id
	private Integer id;
	private String name;
	private String surname;
	private Float creditLimit;
	private Float usedCreditLimit;
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
    private List<Loan> loanInstances;
	
	public Customer() {
        this.creditLimit = 10000f;
        this.usedCreditLimit = 0f;
    }
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public Float getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(Float creditLimit) {
		this.creditLimit = creditLimit;
	}
	public Float getUsedCreditLimit() {
		return usedCreditLimit;
	}
	public void setUsedCreditLimit(Float usedCreditLimit) {
		this.usedCreditLimit = usedCreditLimit;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<Loan> getLoanInstances() {
		return loanInstances;
	}
	public void setLoanInstances(List<Loan> loanInstances) {
		this.loanInstances = loanInstances;
	}
	

}
