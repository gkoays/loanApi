package gokay.loanapplication.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Loan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
    @JoinColumn(name = "customerId")
	@JsonBackReference
    private Customer customer;
	
	private Float loanAmount;
	private Integer numberOfInstallment;
	private LocalDateTime createDate;
	private boolean isPaid;
	
	@OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
    private List<LoanInstallment> loanInstallmentInstances;
	
	
	public Float getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(Float loanAmount) {
		this.loanAmount = loanAmount;
	}
	public Integer getNumberOfInstallment() {
		return numberOfInstallment;
	}
	public void setNumberOfInstallment(Integer numberOfInstallment) {
		this.numberOfInstallment = numberOfInstallment;
	}
	public LocalDateTime getCreateDate() {
		return createDate;
	}
	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}
	public boolean isPaid() {
		return isPaid;
	}
	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public List<LoanInstallment> getLoanInstallmentInstances() {
		return loanInstallmentInstances;
	}
	public void setLoanInstallmentInstances(List<LoanInstallment> loanInstallmentInstances) {
		this.loanInstallmentInstances = loanInstallmentInstances;
	}

}
