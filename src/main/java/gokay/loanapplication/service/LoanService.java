package gokay.loanapplication.service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import gokay.loanapplication.dto.CustomerDto;
import gokay.loanapplication.dto.LoanDto;
import gokay.loanapplication.model.Customer;
import gokay.loanapplication.model.Loan;
import gokay.loanapplication.model.LoanInstallment;
import gokay.loanapplication.repository.LoanRepository;
import jakarta.transaction.Transactional;

@Service
public class LoanService {
	
	private static final Logger logger     = LogManager.getLogger(LoanService.class);
	
//	private final float interestRate = 0.2f;
//	private final float discountRate = 0.001f;
	
	@Value("${loan.api.credit.limit}")
    private Float creditLimit;
	
	@Value("${loan.api.discount.rate}")
    private Float discountRate;
	
	@Value("${loan.api.interest.rate}")
    private Float interestRate;
	
	@Autowired
	LoanRepository loanRepository;
	
	public ResponseEntity<String> createLoanForACustomer(CustomerDto customerDto) {
		// check null parameters from incoming request
		ResponseEntity<String> checkNullParamsResponse = customerDto.validateFields();
		if(checkNullParamsResponse != null)
			return checkNullParamsResponse;
		
		LoanDto loanDto = customerDto.getLoanInstances().get(0);
		checkNullParamsResponse = loanDto.validateFields();
		if(checkNullParamsResponse != null)
			return checkNullParamsResponse;
		
		Customer customer = loanRepository.findById(customerDto.getId());
		
		// loan number check
		if(customerDto.getLoanInstances().size() > 1) {
			return ResponseEntity.badRequest().body("Incoming loan number can only be one! Please create one loan for a customer.");
		}
		
		
		if(!checkInstallmentsNumber(loanDto.getNumberOfInstallment()))
			return new ResponseEntity<>("Number of installments can only be 6, 9, 12, 24 for customer: " + customerDto.getName(), HttpStatus.BAD_REQUEST);
			
		// create new customer
		if (customer == null) {
			logger.debug("Generating a new customer with id: " + customerDto.getId());
			
			customer = new Customer();
			customer.setId(customerDto.getId());
			customer.setName(customerDto.getName());
			customer.setSurname(customerDto.getSurname());
			customer.setCreditLimit(creditLimit);
		}
		
		if(!checkCreditLimit(customer.getCreditLimit(), loanDto.getLoanAmount()))
			return new ResponseEntity<>("Remaning credit limit of the customer: " + customerDto.getName() + " is " + customer.getCreditLimit() 
			+ " It cannot be lower than the loan amount *(1 + interest rate): " + (loanDto.getLoanAmount() * (1 + interestRate)), HttpStatus.BAD_REQUEST);
		
		// okay to get loan
		calculateAndSaveLoanInfo(customer, loanDto);
		
		return new ResponseEntity<>("Requested loan is successfully given to " + customer.getName() + customer.getSurname(), HttpStatus.OK);
	}
	
	public ResponseEntity<List<Loan>> listLoans(Integer customerId) {
	    List<Loan> loans = loanRepository.findLoansByCustomerId(customerId);

	    if (loans != null) {
	        List<Loan> filteredLoans = loans.stream().map(loan -> {
	            Loan loanCopy = new Loan();
	            loanCopy.setId(loan.getId());
	            loanCopy.setLoanAmount(loan.getLoanAmount());
	            loanCopy.setNumberOfInstallment(loan.getNumberOfInstallment());
	            loanCopy.setCreateDate(loan.getCreateDate());
	            loanCopy.setPaid(loan.isPaid());
	            return loanCopy; 
	        }).collect(Collectors.toList());

	        return new ResponseEntity<>(filteredLoans, HttpStatus.OK);
	    }
	    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
	
	public ResponseEntity<List<LoanInstallment>> listLoanInstallments(Integer loanId) {
		List<LoanInstallment> loanInstallments = loanRepository.findLoanInstallmentsByLoanId(loanId);
		if(loanInstallments != null)
			return new ResponseEntity<>(loanInstallments, HttpStatus.OK);
		return new ResponseEntity<>(null , HttpStatus.BAD_REQUEST);
	}
	
	@Transactional
	public ResponseEntity<String> payLoan(Integer loanId, Float sentAmount) {
		List<LoanInstallment> loanInstallments = loanRepository.findUnpaidLoanInstallmentsByLoanId(loanId);
		if(loanInstallments != null && !loanInstallments.isEmpty()) {
			LocalDateTime now = LocalDateTime.now();

	        Collections.sort(loanInstallments, Comparator.comparing(LoanInstallment::getDueDate));

//	        int limitInstallmentNumber = 3;  // (can be changed to 3 months at a time to check the general work of application.)
	        Float tmpSentAmount = sentAmount;
	        int paidInstallmentsCount = 0;
	        for (LoanInstallment installment : loanInstallments) {
	        	Float amount = installment.getAmount();
	            if (tmpSentAmount <= installment.getAmount() || isMoreThanThreeMonthsAfter(now, installment.getDueDate())) {
//	        	if (tmpSentAmount <= amount || limitInstallmentNumber == 0) {
	                break; 
	            }
	            else {
	            	// bonus 2
	            	long days = Math.abs(ChronoUnit.DAYS.between(installment.getDueDate(), now));
	            	if(installment.getDueDate().isAfter(now)) {
	            		amount = amount - (amount * discountRate * days);
	            	}
	            	if(now.isAfter(installment.getDueDate())) {
	            		amount = amount + (amount * discountRate * days);
	            	}
	            	
	            	installment.setPaid(true);
	            	installment.setPaidAmount(amount);
	            	installment.setPaymentDate(now);
	            }
//	            limitInstallmentNumber--;
	            tmpSentAmount = tmpSentAmount - installment.getAmount();
	            paidInstallmentsCount++;
	        }
	         
	        boolean loanPaid = isLoanPaid(loanId);

			return new ResponseEntity<>("Number of paid installments: " + paidInstallmentsCount 
					+ "\n Total amount spent: " +  (sentAmount - tmpSentAmount)
					+ "\n Is load paid totally?: " + loanPaid, HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Loan is not found. id: " + loanId , HttpStatus.BAD_REQUEST);
		}
		
		
	}
	
	
	private boolean isLoanPaid(Integer loanId) {
		List<LoanInstallment> loanInstallments = loanRepository.findUnpaidLoanInstallmentsByLoanId(loanId);
		
		// loan is completed.
		if(loanInstallments.isEmpty()) {
			Loan loanByLoanId = loanRepository.findLoanByLoanId(loanId);
			loanByLoanId.setPaid(true);
			return true;
		}
		return false;
	}
	
	
	public static boolean isMoreThanThreeMonthsAfter(LocalDateTime date1, LocalDateTime date2) {
        LocalDateTime threeMonthsLater = date1.plusMonths(2);
        return date2.isAfter(threeMonthsLater);
    }
	

	private void calculateAndSaveLoanInfo(Customer customer, LoanDto loanDto) {
		Float totalLoanAmount = calculateTotalLoanAmount(loanDto.getLoanAmount());
		Float installmentAmount = calculateInstallmentAmount(totalLoanAmount, loanDto.getNumberOfInstallment());
		
		LocalDateTime now = LocalDateTime.now();
		
		
		Loan loan = new Loan();
		loan.setCustomer(customer);
		loan.setCreateDate(now);
		loan.setLoanAmount(totalLoanAmount);
		loan.setNumberOfInstallment(loanDto.getNumberOfInstallment());
		loan.setPaid(false);
		
		LocalDateTime firstDayOfNextMonth = now.plusMonths(1)  // minusMonths(1) plusMonths(1)
                .with(TemporalAdjusters.firstDayOfMonth());
		
		
		List<LoanInstallment> loanInstallmentList = new ArrayList<>();
		for(int i = 0 ; i < loanDto.getNumberOfInstallment(); i++) {
			LoanInstallment loanInst = new LoanInstallment();
			loanInst.setLoan(loan);
			loanInst.setAmount(installmentAmount);
			loanInst.setPaid(false);
			loanInst.setDueDate(firstDayOfNextMonth);
			
			loanInstallmentList.add(loanInst);
			
			firstDayOfNextMonth = firstDayOfNextMonth.plusMonths(1); // minusMonths(1) plusMonths(1)
		}
		
		loan.setLoanInstallmentInstances(loanInstallmentList);
		
		List<Loan> loanInstances = customer.getLoanInstances();
		List<Loan> loanList = new ArrayList<>();
		if (loanInstances == null || loanInstances.isEmpty()) {
			loanList.add(loan);
			customer.setLoanInstances(loanList);
		}
		else {
			loanInstances.add(loan);
		}

		customer.setCreditLimit(customer.getCreditLimit() - totalLoanAmount);
		customer.setUsedCreditLimit(customer.getUsedCreditLimit() + totalLoanAmount);
		
		loanRepository.save(customer);
	}
	
	private boolean checkInstallmentsNumber(Integer numberOfInstallment) {	
		Integer arr[] = { 6, 9, 12, 24 };
		return Arrays.asList(arr).contains(numberOfInstallment);
	}
	
	private boolean checkCreditLimit(Float creditLimit, Float loanAmount) {
		Float totalLoanAmount = calculateTotalLoanAmount(loanAmount);
		if(creditLimit < totalLoanAmount) {
			return false;
		}
		return true;
	}
	
	private Float calculateTotalLoanAmount(Float loanAmount) {
		return loanAmount * (1 + interestRate) ;
	}
	
	private Float calculateInstallmentAmount(Float totalLoanAmount, Integer numberOfInstallment) {
		return totalLoanAmount / numberOfInstallment ;
	}

	public ResponseEntity<List<Customer>> listAllCustomers() {
		List<Customer> all = loanRepository.findAll();
		if(all != null && !all.isEmpty())
			return new ResponseEntity<>(all, HttpStatus.OK);
		else
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}
