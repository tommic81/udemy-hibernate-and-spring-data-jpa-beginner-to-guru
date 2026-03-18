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
### Relational Database Principles 
#### Keys
- A Primary Key is an optional special database column or columns used to identify a database record.
 - Unique - There can be only one!
- A Surrogate Key is a type of Primary Key which used a unique generated value.
  - Should have no business value, and should never change.
  - Typically a system generated self incrementing number. Can be a unique string (UUID).
  - Considered a best practice in Relational Database Design.
  - This will typically map to the @Id value of your entity
- A Natural Key is a type of Primary Key which uses one or more data columns
  - Is not a best practice because tied to business data, which can change
  - Few reasons to use natural keys, one is a ‘codes’ table for configuration values
  
#### Database Table Relations
- Defined through Foreign Key Constraints in conjunction with Primary Keys.
- Types of Relations:
  - One to One - Record in Table A matches exactly one record in Table B
  - One to Many - Record in Table A matches many in Table B, but Table B matches only one record
in Table A. (Think - An Order with multiple items)
  - Many to Many - Record in Table A matches many in Table B, and Table B matches many records in Table A.
#### Database Constraints
- Not Null - Column is not allowed to have null values
- Unique - Column value must be unique
- Primary Key - Identifier of row, combines Not Null and Unique
- Foreign Key - Value must exist in referenced (foreign) table aka, referential integrity
- Check Constraint - Sets condition on data - like min max, or list of values (ENUM)
#### Database Transactions
- A database transaction is a series of one or more DML statements
- A transaction has a begin and an end
- Committing will make the data changes permanent
- A rollback will revert the changed data to the original state
#### ACID
- Atomicity - all or nothing. All statements must be able to complete.
- Consistency - transactions are valid to rules of the DB. Changes do not violate constraints.
- Isolation - Results of transactions are as if they are done end to end.Reads inside the transaction ‘see’ changed data. Reads outside transaction ‘see’ original data (until commit).
- Durability - Once a transaction is committed, it remains so. Once committed, changes are permanent.
#### Preventing Lost Updates
- Locking is one technique which can be used to prevent lost updates
- Pessimistic Locking uses a database lock to prevent inflight transactions and will allow transactions to complete sequentially. ie - Select for update, will wait for an exclusive lock
- Optimistic Locking - Uses a version property which is checked in the update
- Examples of both will be explored later in the course!

### Relational Database Management System
- DBMS’s have 4 important characteristics:
  - Data Definition - define the data being tracked
  - Data Manipulation - add, update or remove data
  - Data Retrieval - extract and report on the data in the database
  - Administration - defining users on the system, security, monitoring, system administration
  
#### SQL & Langauge Support
- RDBMS - All support ANSI SQL
- Also support their own version of SQL
- May support their own language for stored procedures and triggers
  -  This will vary by platform
  
####  Data Definition
- DDL - Data Definition Language (ie CREATE TABLE…) is used to define the relational model
- Under the covers, the RDBMS will store data about your tables in catalog tables

#### Data Manipulation
- DML - Data Manipulation Language
- Allows you to add (INSERT), change (UPDATE), or remove (DELETE) data.
- The RDBMS enforces data manipulation adheres to the rules of the Data Definition.
- The RDBMS allows set up ‘rules’ for multi-user systems.
- These rules manage what happens in competing conditions. (what happens when two users
want to update the same data, at the same time)
- Enforces Data Integrity - What happens when things go wrong

#### Data Retrieval
- Data Retrieval is the act of pulling data out of the database
- The RDBMS determines the optimal way to retrieve data out of the database.
- Multi-table joins can become very complex.
- The RDBMS also considers what happens when updates occur while your report is running.

#### Administration
- Users - RDBMS define user accounts
- User accounts have security roles which control what individual users can see, update, delete, etc
- RDBMS have tools to see performance metrics which can be used to identify costly operations
- System Administration Includes:
  - Defining users
  - Where the database stores its data on the computer system
  - Backups and Auditing
  
#### How Data is Stored
- RDBMS - Control how data is stored to disk
- Can be a few files
- Can be very VERY complex spread across multiple drives or network devices
- This becomes more important on larger database systems

#### Administration Best Practices
- Use a schema for your application
- Do not use a ‘super user’ account for your application user
- Apply the principle of minimal authorities
- Account used by application often called a ‘service account’
- Service account should only have CRUD access
- Service account should not be able to perform DDL operations
  - Prevents malicious acts via SQL Injection attacks
  
### Data mapping SQL to Java
- [SQL Data Types](https://www.digitalocean.com/community/tutorials/sql-data-types)
- [MySQL Data Types](https://dev.mysql.com/doc/refman/8.0/en/data-types.html)
- [Hibernate Basic Types](https://docs.jboss.org/hibernate/orm/5.0/mappingGuide/en-US/html/ch03.html)
### MySQL Installation
- [MySQL Installation Guide](https://dev.mysql.com/doc/mysql-installation-excerpt/8.0/en/)
- [MySQL Workbench](https://dev.mysql.com/doc/workbench/en/wb-installing.html)
- [DBeaver](https://dbeaver.io/)

## Introduction to testing with Spring Boot
- `@DataJpaTest` - brings a minimal context for testing JPA
- Ordering test execution
```
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
public class SpringBootJpaTestSlice {

  @Order(1)
  @Test
  void testJpaTestSplice(){}
    
  @Order(2)
  @Test
  void testJpaTestSpliceSecond(){}

}
```
- The tests methods normally rollback the changes. We can  turn it off with the annotations: `@Rollback(value = false)` or `@Commit`

## Hibernate with MySQL
### Hibernate Update modes
#### Hibernate DDL Schema Generation Tool
- Hibernate Schema Generation Tool - aka - hbm2ddl.auto configuration property
- Hibernate can:
  - Create DDL statements to file
  - Execute DDL statements to create or update database tables
#### HBM DDL Auto Properties
- none - Disables schema generation tool
- create-only - Create database schema from JPA Entities
- drop - drops database tables related to JPA Entities
- create - drops database schema and re-creates from JPA Entities
- create-drop - drops database schema and re-creates from JPA Entities, then will drop when shutting down
- validate - Validates schema, fatal error if wrong
- update - updates schema from JPA Entities
#### How it Works
- Hibernate Schema Generation Tool uses reflection on JPA Entities to determine database structure
- Table names and column names inferred from type and property names
  - Default is camel case to snake case
  - productDescription -> PRODUCT_DESCRIPTION
  - Datatypes are also defaulted
  - If JPA mappings are present, they will be used (You can set table names, column names, types, etc in JPA)
#### Which Mode to Use
- Hibernate Schema Generation Tool is great to use for rapid development
- NOT recommended for production databases
- For production databases use validate or none
  - Validate is a good option since startup will fail if database schema is wrong
  - Without errors could occur at run time
  
### MySQL Spring Boot Configuration
```xml
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
```
- application-local.properties
```properties
spring.datasource.username=bookadmin
spring.datasource.password=password
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/bookdb?userUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.jpa.database=mysql
spring.jpa.hibernate.ddl-auto=update
```

### Integration test for MySQL
```java
@ActiveProfiles("local")
@DataJpaTest
@ComponentScan(basePackages = {"guru.springframework.sdjpaintro.bootstrap"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//turn off default h2
public class MySQLIntegrationTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    void testMySQL() {
        long countBefore = bookRepository.count();
        assertThat(countBefore).isEqualTo(2);
    }
}
```
### Schema initialization with Hibernate
- schema.sql
```sql
drop table if exists book;
drop table if exists book_seq

create table book (
        id bigint not null,
        isbn varchar(255),
        publisher varchar(255),
        title varchar(255),
        primary key (id)
) engine=InnoDB;

create table book_seq (
        next_val bigint
) engine=InnoDB;

insert into book_seq values ( 1 );
```
- Changes in application.properties
```
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.defer-datasource-initialization=false
```
### Schema initialization with MySQL
- Changes in application-local.properties
```
spring.jpa.hibernate.ddl-auto=validate

spring.sql.init.mode=always
```
### Using H2 for Spring Boot application
- pom.xml
```
    <profiles>
        <profile>
            <id>h2</id>
            <dependencies>
                <dependency>
                    <groupId>com.h2database</groupId>
                    <artifactId>h2</artifactId>
                </dependency>
            </dependencies>
        </profile>
    </profiles>
```
## Using Liquibase
- Liquibase Terminology
  - ChangeSet - A set of changes to be applied to the database
  - Change - A single change to be applied to the database
  - Changelog - A file which has a list of changeSet’s to be applied
  - Preconditions - Conditions which control the execution
  - Context - An expression to help control if the script should or should not run
  - ChangeLog Parameters - Placeholders which can be replaced at run time
  
- Liquibase Best Practices
  - Organizing Change Logs - Create a master change log to organize Change Sets
  - One Change Per Change Set - Allows for easier rollback if there is a failure
  - Never Modify a Change Set - Changes should be additive
  - Use Meaningful Change Set Ids - some use a sequence number, others use a descriptive name
- Running Liquibase
  - Command Line (CLI) - CLI available for Windows, MacOS, and Linux
  - Maven / Gradle Plugins
  - Spring Boot - Will run Liquibase on startup to update configured database to latest changeset.
  
### Liquibase Maven Plugin
- [Liquibase Maven Docs](https://docs.liquibase.com/tools-integrations/maven/home.html)

### Generate Changeset from Database
```xml
            <plugin>
                <groupId>org.liquibase</groupId>
                <artifactId>liquibase-maven-plugin</artifactId>
                <version>4.2.2</version>
                <configuration>
                    <url>jdbc:mysql://127.0.0.1:3306/bookdb?useUnicode=true&amp;characterEncoding=UTF-8&amp;serverTimezone=UTC</url>
                    <username>bookadmin</username>
                    <password>password</password>
                    <outputChangeLogFile>changelog.xml</outputChangeLogFile>
                    <changeSetAuthor>JT</changeSetAuthor>
                    <changelogSchemaName>bookdb</changelogSchemaName>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>${mysql.version}</version>
                    </dependency>
                </dependencies>
            </plugin>           
```
### Organizing Change Logs
- [Best Practices](https://docs.liquibase.com/concepts/bestpractices.html)

### Spring Boot Configuration
- [Using Liquibase with Spring Boot](https://contribute.liquibase.com/extensions-integrations/directory/integration-docs/springboot/)

### Initializing Data with Spring
- init-hibernate.xml
```
<?xml version="1.1" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog" xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext" xmlns:pro="http://www.liquibase.org/xml/ns/pro" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd http://www.liquibase.org/xml/ns/pro http://www.liquibase.org/xml/ns/pro/liquibase-pro-4.1.xsd http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.1.xsd">
    <changeSet author="JT" id="3">
        <sql>insert into book_seq values (0)</sql>
    </changeSet>
</databaseChangeLog>
```
### Alter table with Liquibaee
- add-author-id-to-book.xml
```
<?xml version="1.1" encoding="UTF-8" standalone="no"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.1.xsd">
    <changeSet author="JT" id="5">
        <addColumn tableName="book">
            <column name="author_id" type="BIGINT"/>
        </addColumn>
    </changeSet>
</databaseChangeLog>
```
## Using Flyway
### Liquibase vs Flyway
- Liquibase and Flyway are very similar in terms of functionality
- Share same concepts, slightly different terminology
- Liquibase supports change scrips in SQL, XML, YAML, and JSON
  - XML, YAML and JSON abstract SQL, which may be beneficial for different DB technologies
- Flyway supports SQL and Java only
- Liquibase is a larger and more robust product
- Flyway seems to have more popularity
- Both are mature and widely used

### Which to Use?
- Liquibase is probably a better solution for large enterprises with complex environments
- Flyway is good for 90% of applications which don’t need the additional capabilities
- Recommendation:
  - If one or the other is being used in the organization, use it
  - If in doubt, do your own research on each option
  - John’s preference is Flyway - simple and easy to use
### Flyway Commands
- Migrate - Migrate to latest version
- Clean - Drops all database objects - NOT FOR PRODUCTION USE
- Info - Prints info about migrations
- Validate - Validates applied migrations against available
- Undo - Reverts most recently applied migration
- Baseline - Baselines an existing database
- Repair - Used to fix problems with schema history table

### Running Flyway
- Command Line (CLI) - CLI available for Windows, MacOS, and Linux
- Maven / Gradle Plugins
- Spring Boot - Will run Flyway on startup to update configured database to latest changeset.

### Spring Boot Configuration
- pom.xml
```xml
<dependency>
	<groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```
- V1__init_database.sql
```sql
drop table if exists book;
drop table if exists book_seq;

create table book (
                      id bigint not null,
                      isbn varchar(255),
                      publisher varchar(255),
                      title varchar(255),
                      primary key (id)
) engine=InnoDB;

create table book_seq (
                          next_val bigint
) engine=InnoDB;

insert into book_seq values ( 1 );
```
- application-local.properties
```properties
spring.flyway.user=bookadmin
spring.flyway.password=password
```
### Alter Table with Flyway
- V3__add_author_id_to_book.sql
```sql
alter table book ADD author_id BIGINT;
```
### Clean and Rebuild with Flyway (for Dev env)
- Bean
```
/**Cleaning the Database only on DEV!!!
 * /
@Profile("clean")
@Configuration
public class DbClean {

    @Bean
    public FlywayMigrationStrategy clean(){
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}

```
- application-clean.properties
```
spring.flyway.clean-disabled=true
```
## Hibernate Primary Keys
### Overview
- Numeric Primary Keys
  - GenerationType.AUTO - Let Hibernate Pick - best practice is to specify
  - GenerationType.SEQUENCE - Use database sequence (Not a feature of MySQL)
  - GenerationType.IDENTITY - Use auto-incremented database columns
  - GenerationType.TABLE - Use database table to simulate sequence
  
- UUID Primary Keys
UUID (you-id) - Universally Unique Identifier, a unique 128 bit value
  - Common to use as primary key, can help index performance
  - Downside is it uses more disk space
  - IETF RFC 4122 - an international standard for UUID generation
  - Hibernate by default implements a custom generator
  - Hibernate can be configured to generate a IETF RFC 4122 compliant UUID
  
- Natural Primary Keys
  - A unique value with business meaning outside of the database
  - A UPC or ISBN could be considered a natural key, since both are expected to be unique
  - Common in old legacy databases
  - NOT considered best practice
  
- Composite Primary Keys
  - Two values with business meaning combined to make a unique value
  - Not considered best practice
  - Also common in legacy databases
  
- Which to Use
  - Small Table - ie, few million rows - favor number (Integer or Long)
  - Large Table - ie, 10’s of millions or billions - favor UUID (if disk space allows)
  - Generally avoid using natural or composite keys
  - Fine in edge cases, like a small code lookup table
  
### Auto Incremented Primary Key
- V4__autoincrement_pk.sql
```
alter table book MODIFY id  BIGINT AUTO_INCREMENT;
alter table author MODIFY id BIGINT AUTO_INCREMENT;
```
### Vendor Specific Flyway Migrations
- Create a subdirectory for vendor specific scripts. Standard scripts go to **common** directory.
- application.properties:
```
spring.flyway.locations=classpath:db/migration/common,classpath:db/migration/{vendor}
```
### UUID Primary Key
- UUID stored as varchar
```
@Entity
public class AuthorUuid {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(value = Types.VARCHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;
    
}
```
### UUID RFC 4122 Primary Key
- UUID stored as Binary
```
@Entity
public class BookUuid {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;
}
```
### Natural Primary Key
```
@Entity
public class BookNatural {
    @Id
    private String title;
    private String isbn;
    private String publisher;
}
```
### Composite Primary Key
```
@Entity
@IdClass(NameId.class)
public class AuthorComposite {
    @Id
    private String firstName;

    @Id
    private String lastName;
    private String country;
}


public class NameId implements Serializable {
    private String firstName;
    private String lastName;
    
}    
```
### Embedded Composite Primary Key
```
@Embeddable
public class NameId implements Serializable {
    private String firstName;
    private String lastName;

    public NameId() {
    }
}

@Entity
@Table(name = "author_composite")
public class AuthorEmbedded {
    @EmbeddedId
    private NameId nameId;

    private String country;

    public AuthorEmbedded() {
    }
}
```
## DAO Pattern with JDBC
### Introduction
#### Java DAO Pattern
- DAO - Data Access Object
- Pattern was a precursor to JPA, before ORMs become popular
- Older and uses JDBC for data access
- Common to see in legacy J2EE applications
- While not common in use anymore, it is a good way to utilize JDBC
- Very similar to the Repository Pattern used by Spring Data
- DAO Pattern - Purpose is to isolate persistence operations from the application layer
- For example, when the application needs to persist an object, it should not need to understand the underlying persistence technology
- Domain Class - Simple POJOs, same as JPA entities
- NOTE: DAO Pattern will not utilize JPA annotations
- DAO API - Provide interface for CRUD operations (similar to Repository)
- DAO Implementation - Implement persistence functionality
#### Spring Boot JDBC
- Database Connection - Typically with JDBC you need to create and manage the database connection
- Spring Boot will auto configure the database connection for us
- Database connection components are available as Spring Beans in the Spring Context

### Create Author DAO
```
@Component
public class AuthorDaoImpl implements AuthorDao{
    @Override
    public Author getById(Long id) {
        return null;
    }
}
```
### Implement Get Author By Id
```java
    @Override
    public Author getById(Long id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = source.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM author where id = " + id);

            if (resultSet.next()){
                Author author = new Author();
                author.setId(id);
                author.setFirstName(resultSet.getString("first_name"));
                author.setLastName(resultSet.getString("last_name"));

                return author;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
```
### Release Database Resources
``` 
{} } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (statement != null){
                    statement.close();
                }

                if (connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
```
### Using Prepared Statements
```
ps = connection.prepareStatement("SELECT * FROM author where id = ?");
            ps.setLong(1, id);
            resultSet = ps.executeQuery();
```
### Save New Author
```java
    @Override
    public Author saveNewAuthor(Author author) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = source.getConnection();
            ps = connection.prepareStatement("INSERT INTO author (first_name, last_name) values (?, ?)");
            ps.setString(1, author.getFirstName());
            ps.setString(2, author.getLastName());
            ps.execute();

            Statement statement = connection.createStatement();
            //specific to mysql
            resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");

            if (resultSet.next()) {
                Long savedId = resultSet.getLong(1);
                return this.getById(savedId);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeAll(resultSet, ps, connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
```
### Update Author
```java
	connection = source.getConnection();
	ps = connection.prepareStatement("UPDATE author set first_name = ?, last_name = ? where author.id = ?");
	ps.setString(1, author.getFirstName());
	ps.setString(2, author.getLastName());
	ps.setLong(3, author.getId());
	ps.execute();
```
### Delete Author
```
	connection = source.getConnection();
    ps = connection.prepareStatement("DELETE from author where id = ?");
    ps.setLong(1, id);
    ps.execute();
```
## Spring JDBC Template
### Row Mapper
```java
public class AuthorMapper implements RowMapper<Author> {
    @Override
    public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
        Author author = new Author();
        author.setId(rs.getLong("id"));
        author.setFirstName(rs.getString("first_name"));
        author.setLastName(rs.getString("last_name"));
        return author;
    }
}
```

### Get Author By Id
```java
@Override
public Author getById(Long id) {
  return jdbcTemplate.queryForObject("SELECT * FROM author where id = ?", getRowMapper(), id);
}

```
### Save new Author
```java
@Override
public Author saveNewAuthor(Author author) {
	jdbcTemplate.update("INSERT INTO author (first_name, last_name) VALUES (?, ?)",
    author.getFirstName(), author.getLastName());

    Long createdId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

    return this.getById(createdId);
}
```
### Update Author
```java
@Override
public Author updateAuthor(Author author) {
  jdbcTemplate.update("UPDATE author SET first_name = ?, last_name = ? WHERE id = ?",
    author.getFirstName(), author.getLastName(), author.getId());

  return this.getById(author.getId());
}
```
### Delete Author
```
 @Override
 public void deleteAuthorById(Long id) {
    jdbcTemplate.update("DELETE FROM author WHERE id = ?", id);
 }
```
## Hibernate DAO
### Hibernate Introduction
- ORM - Object Relational Mapping
- Hibernate implements the Java JPA specification
- Coding to the Java JPA specification will keep your code independent of Hibernate
- Hibernate also has a native API
  - Coding to this API will make your code dependent on Hibernate
  
#### Hibernate Terms
- Session Factory - Expensive to create, your application should only have one instance
- Entity Manager Factory - JPA Equivalent of Session Factory
- Session - single threaded, short lived object. Cheap to create
  - Session wraps a JDBC connection
- Entity Manager - JPA Equivalent of Session
- Transaction - single threaded, short lived object to define transaction boundaries
- Entity Transaction - JPA equivalent of Transaction

#### Persistence Context
- Session / Entity Manager - create a context for dealing with persistent data
- Transient - the entity has just been instantiated and is not associated with a
  persistence context. It has no persistent representation in the
  database and typically no identifier value has been assigned (unless the assigned  generator was used).
- Managed or Persistent - the entity has an associated identifier and is associated with a persistence context. It may or may not
physically exist in the database yet.
- Detached - the entity has an associated identifier but is no longer associated with a persistence context (usually because the persistence
context was closed or the instance was evicted from the context)
- Removed - the entity has an associated identifier and is associated with a persistence context, however, it is scheduled for removal from
the database.

#### Detached Entities
- Detached Entities - Very common error to see
- Common root cause is working outside of the session scope, or a closed session
- Spring Data JPA by default will do an implicit transaction
  - Meaning you can see this error when accessing entity properties outside of a transaction
#### Caching
- Persistence Context / First Level Cache - By default Hibernate will cache entities in the persistence context
  - Changes outside the context might not be seen
  - Very efficient for doing work within the context of a session
- Second Level Cache - Disabled by default. JVM or cluster level cache
  - Recommend to enable on a per entity basis
  - Broad support for popular options such as jCache, Ehcache, and Infinispan
- Problems:
  - Hibernate would add 100,000 objects to session level cache, possible out of memory
  - Long running transaction could deplete transaction pool
  - JDBC Batching not enabled by default, each insert is a round trip to the DB.
  - flush() and clear() methods can be used to clear session cache
  
### Get Author By ID
```
//AuthorDaoImpl
@Override
public Author getById(Long id) {
  return getEntityManager().find(Author.class, id);
}
    
private EntityManager getEntityManager() {
        return emf.createEntityManager();
}    
```
### Find Author By Name
```java
//AuthorDaoImpl
@Override
public Author findAuthorByName(String firstName, String lastName) {
  TypedQuery<Author> query = getEntityManager().createQuery("SELECT a FROM Author a " +
                "WHERE a.firstName = :first_name and a.lastName = :last_name", Author.class);

  query.setParameter("first_name", firstName);
  query.setParameter("last_name", lastName);

  return query.getSingleResult();
}
```
### Save a new Author
```
 @Override
    public Author saveNewAuthor(Author author) {
        EntityManager em = getEntityManager();
        //added due to lazy transaction
        em.getTransaction().begin();
        em.persist(author);
        em.flush();
        em.getTransaction().commit();

        return author;
    }
```
### Update an Author
```
    @Override
    public Author updateAuthor(Author author) {
        EntityManager em = getEntityManager();
        em.joinTransaction();
        em.merge(author);
        //forcing hibernate to send changes to a DB
        em.flush(); 
        //clearing a cache
        em.clear();
        return em.find(Author.class, author.getId());
    }
```
### Delete an Author
```
   @Override
    public void deleteAuthorById(Long id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Author author = em.find(Author.class, id);
        em.remove(author);
        em.flush();
        em.getTransaction().commit();
    }
```
## Spring Data JPA Queries
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/#repositories.query-methods.query-creation)
### Author CRUD Operations
```java
    private final AuthorRepository authorRepository;

    public AuthorDaoImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author getById(Long id) {
        return authorRepository.getById(id);
    }

    @Override
    public Author saveNewAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    @Override
    public Author updateAuthor(Author author) {
        Author foundAuthor = authorRepository.getById(author.getId());
        foundAuthor.setFirstName(author.getFirstName());
        foundAuthor.setLastName(author.getLastName());
        return authorRepository.save(foundAuthor);
    }

    @Override
    public void deleteAuthorById(Long id) {
        authorRepository.deleteById(id);
    }
```

### Query Methods
```java
 @Override
    public Author getById(Long id) {
        return authorRepository.getById(id);
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        return authorRepository.findAuthorByFirstNameAndLastName(firstName, lastName);
    }
```

### Optional Return Type
```
 Optional<Author> findAuthorByFirstNameAndLastName(String firstName, String lastName);
```
### Null handling
- [Nullability](https://docs.spring.io/spring-data/jpa/reference/#repositories.nullability)

```java
    Book readByTitle(String title);
    
    @Nullable
    Book getByTitle(@Nullable String title);
```
- Add file package-info.java to keep other methods (without `@Nullable` working)
```
@org.springframework.lang.NonNullApi
package guru.springframework.jdbc.repositories;
```
### Stream Query Results
```
Stream<Book> findAllByTitleNotNull();
```
## Asynchronous Query Results
```
  @Async
  Future<Book> queryByTitle(String title);
```
## Declaring queries using @Query
```
  @Query("SELECT b FROM Book b where b.title =?1")
  Book findBookByTitleWithQuery(String title);
```
## Named Parameters with @Query
```
  @Query("SELECT b FROM Book b where b.title = :title")
  Book findBookByTitleWithQueryNamed(@Param("title")  String title);
```
## Native SQL Queries
```
  @Query(value = "SELECT * FROM book where title = :title", nativeQuery = true)
  Book findBookByTitleNativeQuery(@Param("title") String title);
```