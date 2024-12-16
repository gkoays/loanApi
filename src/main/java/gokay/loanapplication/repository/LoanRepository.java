package gokay.loanapplication.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import gokay.loanapplication.model.Customer;
import gokay.loanapplication.model.Loan;
import gokay.loanapplication.model.LoanInstallment;


@Repository
public interface LoanRepository extends CrudRepository<Customer, Long> {

	Customer findById(int id);
	
	@Query("SELECT l FROM Loan l WHERE l.customer.id = :customerId")
    List<Loan> findLoansByCustomerId(int customerId);
	
	@Query("SELECT l FROM Loan l WHERE l.id = :loanId")
    Loan findLoanByLoanId(int loanId);
	
	@Query("SELECT l FROM LoanInstallment l WHERE l.loan.id = :loanId")
    List<LoanInstallment> findLoanInstallmentsByLoanId(int loanId);
	
	@Query("SELECT l FROM LoanInstallment l WHERE l.loan.id = :loanId AND l.isPaid = FALSE")
    List<LoanInstallment> findUnpaidLoanInstallmentsByLoanId(int loanId);


}
