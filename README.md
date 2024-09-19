# Hibernate and Spring Data JPA: Beginner to Guru

## Intro

### Prerequisites

- Java 17
- JPA 3.0 - package name change
  - javax.persistence -> jakarta.presistence
  - javax.validation -> jakarta.validation
- Flyway module change from “flyway-core” to “flyway-mysql”
- Maven version 3.8.0 or higher

## Introduction to Spring Data JPA

### Spring Data JPA
#### Spring Data JPA

- Is the JPA based version of data repository access of the Spring Data family of projects
  - “Spring Data” has many implementations for various data stores including:
    - MongoDB, Redis, Cassandra, GemFile, Couchbase, Neo4J
- Spring Data JPA focuses on supporting the JPA API standard
- An abstraction layer built on top of JPA
  - JPA - Java Persistence API
  - JPA is a common API for working with data Relational Databases
- Hibernate is an implementation of JPA
- JPA is just the interface for the API; JPA is not an implementation
- Other implementations include EclipseLink, Apache OpenJPA, and TopLink

#### Hibernate
- Hibernate - Is a Object Relational Mapping Tool which also implements the JPA API specification
- Leakage - term for one paradigm ‘leaking’ into the other model
- The ID value is considered ‘leakage’. The ID is the Primary Key for the relational model, and needs to
be carried over to the Object Model
- Hibernate performs database operations using SQL

#### JDBC
- JDBC - Java DataBase Connectivity
  - JDBC is the Java API for connecting to databases
  - Just the API - NOT the implementation
  - The JDBC Implementation is typically referred to as a JDBC Driver
  - Each Database will have it’s own JDBC Driver implementation
  - Each Driver will implement the JDBC API, and have platform specific extensions
  
#### When to Use JPA?
- Spring Data JPA - is very good for single object CRUD operations
- When you have multiple operations against a small set of objects
  - Like a checkout operation in a web store application
  - Hibernate will cache and batch DB operations for efficiency
- When you have control of the database schema

#### When NOT to Use JPA?
- Spring Data JPA - is not very good for batch operations
- There is a cost to fetching a single record from the database and mapping it to a Java object
  - Fine for simple operations, costly for 10’s of thousands of operations
- Relational Databases are very efficient at what they do
- SQL is a very powerful language
- If you are performing batch operations on 10’s of thousands of records, you should consider using SQL/JDBC

### Repository
```
//JpaRepository<ENTITY_CLASS, ID_TYPE>
public interface BookRepository extends JpaRepository<Book, Long> {
}
```

### SQL Logging

```properties
spring.jpa.show-sql=true

#Show SQL
spring.jpa.properties.hibernate.show_sql=true

#Format SQL
spring.jpa.properties.hibernate.format_sql=true

#Show bind values
logging.level.org.hibernate.type.descriptor.sql=trace


``` 

### Loading data on start
```java
@Component
public class DataInitializer implements CommandLineRunner {
    public final BookRepository bookRepository;

    public DataInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    @Override
    public void run(String... args) throws Exception {
        Book bookDDD = new Book("domain Driven Design", "123", "RandomHouse");

        System.out.println("Id: " + bookDDD.getId());

        Book savedDDD = bookRepository.save(bookDDD);

        System.out.println("Id: " + bookDDD.getId());

        Book bookSIA = new Book("Spring In Action", "234234", "Oriely");
        Book savedSIA = bookRepository.save(bookSIA);

        bookRepository.findAll().forEach(book -> {
            System.out.println("Book Id: " + book.getId());
            System.out.println("Book Title: " + book.getTitle());
        });
    }
}

```



### H2 Database Console
- Enabling
```
spring.h2.console.enabled=true
```
- Copy connection path from the console 

```
2024-09-19T18:00:50.529+02:00  INFO 3816 --- [           main] o.s.b.a.h2.H2ConsoleAutoConfiguration    : H2 console available at '/h2-console'. Database available at 'jdbc:h2:mem:f2ce4a26-c046-4b92-b998-5009634bdc9a'
```
- Open a browser: **http://localhost:8080/h2-console/login.jsp** and use in **JDBC URL** field
```
jdbc:h2:mem:f2ce4a26-c046-4b92-b998-5009634bdc9a

```
## Intro to MySQL