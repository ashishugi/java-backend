1. Hibernate: It is an ORM(Object Relation Mapping) and allows us to map our objects to relational database tables.
2. JPA: Java Persistence API: Jpa used hibernate internally to map the entities. And it gives us all the sql queries to us.
3. JDBC(Java Database connectivity): It is an api(application programming interface) for java, which define how a client may access a database.
   There is 5 step process while making JDBC connectivity: refer here to read more https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html
   1. Establishing a connection
   2. Create a statement
   3. Execute the query
   4. Process the result object.
   5. Close the connection.
   ex: sample example- 
   ```
    public static void viewTable(Connection con) throws SQLException {
    String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";
    try (Statement stmt = con.createStatement()) {
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String coffeeName = rs.getString("COF_NAME");
        int supplierID = rs.getInt("SUP_ID");
        float price = rs.getFloat("PRICE");
        int sales = rs.getInt("SALES");
        int total = rs.getInt("TOTAL");
        System.out.println(coffeeName + ", " + supplierID + ", " + price +
                           ", " + sales + ", " + total);
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
    }

   ```
4. JDBC Template: It simplifies the use of JDBC and helps to avoid the common error. It executes core JDBC workflow, leaving application code to provide SQL and extract result.
   Ex: select * from Employee;
   <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jdbc</artifactId>
   </dependency>
5. Data Source: Datasource object provides a new way for JDBC clients to obtain a DBMS connection. Basically is a way of obtaining a connection to your database.
   This dataSource entry then points to a connection pool. Reasons for making a connect pool is just to reduce the cost as opening and closing a connection to a database is expensive.
   Inside the connection pools there is already connections which are ready to be used. So if we have a client that wants to connect to database(client here refers to a thread that may call database to retrieve some data). 
   So client go to connection pool and looks for already available connections(hence client do not need to open and close the connection). There may be connection that may be ready to use in connection pool.
   Client do not hold the connection inside the connection pool for all the time, it may the query and get the data, after getting the data client release the connection from the connection pool.

   for connecting the data source to connection pool we need to give few properties:
   1. datasource.url=postgresql://localhost:5432/YOUR_URL
   2. datasource.username
   3. datasource.password
   4. datasource.driver-class-name=org.postgresql.driver

   Two popular connection pools - HikariCP, TomcatCP
6. ddl-auto: create-drop (application.yml) : By using create-drop property, JPA + Hibernate automatically creates the database for us.
   if we do ddl-auto: none - then database will not be created automatically, we must create the database first to insert the data in it.
   For create database without using JPA + Hibernate we have tools database migration tools  like:  Flyway, Liquibase etc.

   Flyway: 
   resource/db/migration/V1__Initial_Setup.sql
   NOTE: here v1 represents the version1, In you run the application after making V1__...sql file, then you cannnot change anything in the content of that file as this history and content property will be stored by flyway_schema_history DB.
   Hence to add then changes we need to make a new file V2__NEW_NAME.sql
7. Replacing JPA enitity with flyway DB:
   below is the replacement of CustomerEntity of JPA with sql 

   ```
        @Entity
   public class Customer {
   @Id
   @SequenceGenerator(
   name = "customer_id_sequence",
   sequenceName = "customer_id_sequence"
   )
   @GeneratedValue(
   strategy = GenerationType.SEQUENCE,
   generator = "customer_id_sequence"
   )
   private Integer id;
   @Column(
   nullable = false
   )
   private String name;
   @Column( ........................................... etc
   
   SQL :
   CREATE TABLE customer(
         id BIGSERIAL DEFAULT PRIMARY KEY ,
         name TEXT NOT NULL ,
         email TEXT UNIQUE NOT NULL ,
         age INT NOT NULL
         );
      ```
   also add below configuration in application.yml:
   spring: 
    flyway:
     baselineOnMigrate: true
8. Making Entity using JDBC and flyway: 
   ```
   v1__intial_setup.sql:
   
   CREATE SEQUENCE customer_id_seq;

   CREATE TABLE customer(
   id BIGINT DEFAULT nextval('customer_id_seq') PRIMARY KEY , //==> not the name here: customer_id_seq,  
   name TEXT NOT NULL ,
   email TEXT UNIQUE NOT NULL ,
   age INT NOT NULL
   );
   
   OR
   CREATE TABLE customer(
    id BIGSERIAL PRIMARY KEY , // check customer db, that which name is create for sequence generation.
    name TEXT NOT NULL ,
    email TEXT NOT NULL ,
    age INT NOT NULL
   );
   v2__adding_unique_constraint_to_customer: 
   
   ALTER TABLE customer ADD CONSTRAINT customer_unique_email UNIQUE (email); /// here a separate indexing get created for this given constraint name as :customer_unique_email
   
   
   customer.java(Entity):
   
   @Entity
   @Table(
   name = "customer",
    uniqueConstraints = {
     @UniqueConstraint(
      name = "customer_unique_email",
      columnNames = "email"
     )
    }
   )
   public class Customer {
   @Id
   @SequenceGenerator(
    name = "customer_id_seq",
    sequenceName = "customer_id_seq",
    initialValue = 1,
    allocationSize = 1
   )
   @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "customer_id_seq"
   )
   private Integer id;
   @Column(
    nullable = false
   )
   private String name;
   @Column(
    nullable = false
   )
   private String email;
   
   ```
9. Maven faker dependency: https://github.com/DiUS/java-faker
   ```
   <!-- https://mvnrepository.com/artifact/com.github.javafaker/javafaker -->
   <dependency>
       <groupId>com.github.javafaker</groupId>
       <artifactId>javafaker</artifactId>
       <version>1.0.2</version>
   </dependency>

   ```
   j
10. Spring classpath: The Spring classpath refers to the set of directories and JAR files that Spring scans for resources, configurations, and classes.
    It is essentially a collection of locations where Spring looks for files and classes needed to configure and run your Spring application.
    The Spring classpath can include directories containing configuration files (e.g., XML or Java-based configuration), compiled Java classes, property files, and other resources used by your application.

11. Spring Application Context: The Spring application context, on the other hand, is a runtime container for managing beans, configurations, and other application components.
    It is responsible for creating and managing beans (objects) defined in your Spring configuration, and it provides various services such as dependency injection and lifecycle management.
    The Spring application context is typically created based on the configuration files and classes found on the Spring classpath.

12. JDBCTemplate (JDBC): We will be using JDBCTemplate for implementing JDBC as it helps in doing less errors:

    <img width="1648" alt="Screenshot 2023-09-09 at 5 39 28 PM" src="https://github.com/ashishugi/java-react-fullstack/assets/46626591/b135f88b-e5df-431a-8a58-d8f41d910d4d">
    
   Here we do not need to make Bean as JDBCTemplate will be instantiated for us (As we have spring data JDBC on classpath, it will instantiate it in spring application context and we just need to inject this instantiated object)
   ```
   @Repository("jdbc")
   public class CustomerJDBCDataAccessService implements CustomerDao{
    private final JdbcTemplate jdbcTemplate;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
   ```
13. 


LAST VIDEO : 107