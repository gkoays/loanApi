package gokay.loanapplication.dto;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class CustomerDto {
	private Integer id;
	private String name;
	private String surname;
	private List<LoanDto> loanInstances;

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

	public List<LoanDto> getLoanInstances() {
		return loanInstances;
	}

	public void setLoanInstances(List<LoanDto> loanInstances) {
		this.loanInstances = loanInstances;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
