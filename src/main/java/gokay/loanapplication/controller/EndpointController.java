package gokay.loanapplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gokay.loanapplication.dto.CustomerDto;
import gokay.loanapplication.model.Customer;
import gokay.loanapplication.model.Loan;
import gokay.loanapplication.model.LoanInstallment;
import gokay.loanapplication.service.LoanService;

@RestController
@RequestMapping("/loan/api")
public class EndpointController {
	
	@Autowired
	LoanService loanService;

	@PostMapping("/create/loan")
    public ResponseEntity<String> createLoan(@RequestBody CustomerDto customerParams){
        return loanService.createLoanForACustomer(customerParams);
    }
	
	@GetMapping("/list/loans")
    public ResponseEntity<List<Loan>> listLoans(@RequestParam Integer customerId){
        return loanService.listLoans(customerId);
    }
	
	@GetMapping("/list/loan/installments")
    public ResponseEntity<List<LoanInstallment>> listLoansInstallments(@RequestParam Integer loanId){
        return loanService.listLoanInstallments(loanId);
    }
	
	@PostMapping("/pay/loan")
    public ResponseEntity<String> payLoan(@RequestParam Integer loanId, @RequestParam Float sentAmount){
        return loanService.payLoan(loanId, sentAmount);
    }
	
	@GetMapping("/admin/all/customers")
    public ResponseEntity<List<Customer>> listAllCustomers(){
        return loanService.listAllCustomers();
    }


}
