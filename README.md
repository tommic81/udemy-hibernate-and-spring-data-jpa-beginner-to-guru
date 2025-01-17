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