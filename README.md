# Loan API

Loan API is a Spring Boot Java application that simulates a backend application for a bank so that its employees can create, list, and pay loans for their customers.


## Prerequisites

To run this project, the following software must be installed on your system:

- **Java**
- **Maven**

## Installation

LoanApi can be installed using the commands below.

   ```bash
   git clone https://github.com/gkoays/loanApi.git
   ```

   ```bash
   cd loanApi
   ```

   ```bash
   mvn install
   ```

## Running the Application

To run the project, you can use the following command:

```bash
mvn spring-boot:run
```

Alternatively, you can create a `.jar` file and run it with the following commands:

```bash
mvn clean package -DskipTests
java -jar target/loanapplication-0.0.1-SNAPSHOT.jar
```

## Usage

Once the application is running, you can access the API via the following endpoints.
Endpoints are authorized with user and password which can be set from application.properties.

 The credit limit for a customer, interest rate, and discount rate can be set from application.properties.


- **POST /loan/api/create/loan**: You can create a loan for a customer with this post request. The body should be like this:
```bash
{
    "id": 11111111,
    "name": "jack",
    "surname": "shephard",

    "loanInstances" : [
        {
            "loanAmount": 600,
            "numberOfInstallment": 9
        }
    ]
}
```

- **GET /loan/API/list/loans?customerId=11111111**: You can list loans belonging to a customer with an ID.

- **GET /loan/API/list/loan/installments?loanId=1**: You can list loan installments belonging to a loan with id. (Each loan has a unique id.)

- **POST /loan/API/pay/loan?loanId=1&sentAmount=2000**: You can pay loan installments belonging to a loan by setting id and amount.

- **GET /loan/API/admin/all/customers**: You can list all customers who got loans with just the admin user. The customer role cannot be permitted for this endpoint.

The parameters are set for example purposes.
