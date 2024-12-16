package gokay.loanapplication.dto;

import java.lang.reflect.Field;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class LoanDto {
	
	private Float loanAmount;
	private Integer numberOfInstallment;
	
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
	

	public ResponseEntity<String> validateFields() {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(this) == null) {
                    return new ResponseEntity<>(field.getName() + " cannot be null.", HttpStatus.BAD_REQUEST);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
