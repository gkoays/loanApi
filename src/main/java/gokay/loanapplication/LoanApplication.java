package gokay.loanapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


//@EntityScan(basePackages = "gokay.loanapplication.model")
//@EnableJpaRepositories(basePackages = "tr.com.argela.supersetapi.repository")  
@SpringBootApplication
public class LoanApplication {
	public static void main(String[] args) {
		SpringApplication.run(LoanApplication.class, args);

		System.out.println("Loan API has started.");
	}
}
