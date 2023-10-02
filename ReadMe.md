
<h2>TOPIC's</h2>
<ul>
    <li><a href="#spring_basics">Spring Basics</a></li>
    <li><a href="https://github.com/ashishugi/java-react-fullstack/blob/main/ReadMe_JDBC.md" >JDBC</a></li>
    <li><a href="https://github.com/ashishugi/java-react-fullstack/blob/main/ReadMe_Testing.md">Testing (Unit + Integration)</a></li>
    <li><a href="https://github.com/ashishugi/java-react-fullstack/blob/main/ReadMe_building_app_Jar.md">Building Jar using maven plugin</a></li>
    <li><a href="https://github.com/ashishugi/java-react-fullstack/blob/main/ReadMe_devops.md">Devops (Docker, CI, CD)</a></li>
</ul>

<h4>Spring Basic's</h4>

1. **@SpringBootApplication**: Indicates that this is the spring boot application and its starts from this file. This annotation is equivalent to @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
2. **@Configuration**: Used to create the beans, conventionally called AppConfig. Ex: if you want to bind your database username and password to your bean, then we can use this annotation.
3. **@EnableAutoConfiguration**: It enable the spring to get the configuration based on JAR files available on classpath. It can pre-configure the library you use without your intervention. Ex: we have tomcat server, if we use @EnableAutoConfiguration then that tomcat get autoconfiguration and can be used directly without configuring us the tomcat. 
4. **@ComponentScan**: By default spring searches within the package that the main class is located, along with it child packages. 
5. SpringApplication.run : launches the spring boot application
6. Tomcat: It is java web application server. Apache Tomcat is the free and open source implementation of Jakarta servlet, Jakarta Expression language and Websoket technologies.
    Other webservers example: Jetty. <br/>
    request(chrome) --> TOMCAT (/8080, spring boot application) --> response. <br/>
    If we want to use any other web-server then we can do this by adding excluding tomcat dependency and adding the dependency for other webserver (google it !)
7. Running / building with out web-server: In application.yml use below property, by default web-application-type: servlet
   spring:
    main:
     web-application-type: none 
8. **@RestController**: for making the class for handle the rest api(request, response)
9. **@GetMapping** : type of api-end point/request that we will receive.
10. Servlet: A process in which we handle the HTTP request and give the response back.
11. SpringWebMVC: abstract of servlet, helps to handle the http request and make response to the request. Annotation: @Controllers(mark the class as web controller), @RestController = @Controller + @ResponseBody (this says that this class is a controller and all the method in the class will return the JSON response), 
12. **@ResponseBody** : It tell the  spring to automatically serialize the return value of this class http response.It converts the object into JSON for easier consumption. It sends the response in JSON.
13. Record : Class that are immutable. 
14. JACKSON : Jackson performs the conversion of POJO to JSON and JSON to POJO . "The Java JSON library" or "the best JSON parser for JAVA". It is used in JSON serialisation and JSON deserialization. There are 2 types of data binding :

    a. Simple Data Binding : This term is used when we are converting primitive objects like : Boolean, Number, Strings, maps in java to JSON. And vice-versa.
    b. Full Data Binding : When ANY TYPE of object in java can be converted to JSON and vice-versa, then we say that this is FULL Data Binding.
    Example: simple pojo class (@Data is an Annotation of the Lombok library which reduces the need to write getters and Setters for us.)
    ```
    import lombok.Data;
    
    @Data
    public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    
        public Student(int i, String firstName, String lastName, String email) {
            this.id = i;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
    }   
    ```
    RestController:
    ```
    package com.geeksforgeeks.jacksondemo.jackson.RESTControllers;

    @RestController
    @RequestMapping("/api")
    public class JacksonDemoController {
    
        @GetMapping("/students")
        public List<Student> getAllStudents() {
            List<Student> studentList = new ArrayList<>();
      
            studentList.add(new Student(1,"Adwitiya", "Mourya", "adwitiya@example.com"));
            studentList.add(new Student(2,"Paramjeet", "singh", "param@example.com"));
            studentList.add(new Student(3,"Anish", "Jagadevan", "Anish56@example.com"));
            studentList.add(new Student(4,"Juan", "Philips", "juan123@example.com"));
      
            return studentList;
        }
    
    }
    ```
    The important thing to highlight here is that when we return a POJO, the Jackson project behind the scene will call the getter methods that we’ve defined in the entity class to convert Java POJO to JSON.
    so when we will hit the postman then instead of getting the object and response we will get output as a json
    ![Screenshot 2023-08-21 at 10.58.32 PM.png](..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fzz%2Ftxdp3zpn54l9qbhczqwj7vdh0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_j6PGkm%2FScreenshot%202023-08-21%20at%2010.58.32%20PM.png)
15. HTTP ? : It is protcol for fetching the HTML documents, files, images etc. 
16. HTTP status codes :
    100 - 199 : informational response
    200 - 299:  successful response 
    300 - 399: redirection response
    400 - 499: client error
    500 - 599 : server error
17. URL : Unified resource locator
18. API : Application Programming Interface
19. Fake api's that can be used: https://github.com/public-apis/public-apis
20. @RequestMapping(path = "api/v1/customers", method = RequestMethod.GET) ===> same as ==> @GetMapping("api/v1/customers");
21. frontend  --> HTTP request --> API Layer --> Bussiness Layer --> DAO --> Data base
22. CustomerController --> CustomerService --> CustomerDao(Interface).
    CustomerDataAccessService(implements CustomerDao) --> DataBase
23. Dependency Injection/Beans: Spring is a dependency injection framework, hence this part is managed by spring.
    Ex : CustomerService cs = new CustomerService(new CustomerDataAccessService());
    We do not need to do or manage it like above. Spring does this for us.  
    How Spring initialise this for us ? Spring has a concept of beans, these beans are then can be accessed anywhere throughout the application.
    Ex: @Component, @Service, @Repository etc. these annotation will create beans, which are further taken reference and injected whenever required. 
    <strong>using annotation / beans over the class: it means spring does the instantiation/initialisation of that class/object for us. And this can be used at other places just by constructor injection. In this way other service do not need to create any other instance of the same within the application context. </strong>
    
    <code>
    @RestController
    public class CustomerController {
        private final CustomerService customerService;
    
        @Autowired
        public CustomerController(CustomerService customerService) { this parameter comes from application context, where all the beans are present.
            this.customerService = customerService;
        }
    }
    </code>
   
    the parameter customerService in constructor comes from applicationContext,
    Adding @Autowired means that: we are saying that go and find the bean of CustomerService and inject it here.
    Above code can also be written as :
    
    //without using @Autowired
    public CustomerController(CustomerService customerService) { // this parameter comes from application context, where all the beans are present.
        this.customerService = customerService;
    }
    
    
    OR
    
   
    @Inject
    public CustomerController(CustomerService customerService) { // this parameter comes from application context, where all the beans are present.
        this.customerService = customerService;
    }
  
    It depends on different - different frameworks, but the meaning remains the same. 
1. Application Context: It provides basic functionalities for managing beans. The default bean scope - singleton beans - are created once and then re-used forever.
![Screenshot 2023-08-23 at 11.07.08 PM.png](..%2F..%2F..%2F..%2F..%2Fvar%2Ffolders%2Fzz%2Ftxdp3zpn54l9qbhczqwj7vdh0000gn%2FT%2FTemporaryItems%2FNSIRD_screencaptureui_pnxlVj%2FScreenshot%202023-08-23%20at%2011.07.08%20PM.png)
2. How to see all the beans of the application and application context ? : 
   ```
    
   ConfigurableApplicationContext applicationContext = SpringApplication.run(Main.class);
    
   String[] beanDefinationNames = applicationContext.getBeanDefinitionNames();
   for(String beanDefinationName: beanDefinationNames) {
       System.out.println(beanDefinationName);
   }
   ```
   We can see customerController, customerDataAccessService, customerService beans along with lot other beans.
   We can also see bean using spring actuators.
3. Inversion of Control (IOC) : Dependency injection is the way we achieve inversion of control for both bean and application context.
4. Bean Scope:
   Singleton : single object instance per spring IOC container
   Prototype: Instead of reusing single bean everytime we get new instance every time.
   Request: Till the life cycle of single http request
   Session: till lifecycle of single http session
   Global session: till life of global http session
    
   We can change the scope of the bean as well : 
   ```
   @Bean("foo")
   @Scope()
   public Foo foo(value = ConfigurableBeanFactor.SCOPE_SINGLETON) {
       return new Foo("foo");
   }
   ```
5. Bean : A bean is an object that spring container instantiate, assembles and manages the entire life cycle for us.
   Let say a bean is created outside the applicationContext(outside main)
   ```
       public static void main(String[] args) {
   SpringApplication.run(Main.class, args);
   }

   @Bean("rename")
   public Foo getFoo() {
       return new Foo("bar");
   }
    
   FooService.java 
   // here it can be autowired/injected: 
    
    
   private final Main.Foo foo;

   public FooService(Main.Foo foo) {
       this.foo = foo;
       System.out.println();
   }
   ```
   In the case when bean could not be found then we see this kind of error: Parameter 0 of constructor in com.abc.customer.FooService required a bean of type 'com.abc.Main$Foo' that could not be found.
6. Error handling in spring: 
   ```
   ResourceNotFound.java
    
   @ResponseStatus(code = HttpStatus.NOT_FOUND)
   public class ResourceNotFound extends RuntimeException{
    public ResourceNotFound(String message) {
     super(message);
    }
   }
    
   CustomerService.java: 
    
   customerDao.selectCustomerById(id)
               .orElseThrow(() -> new ResourceNotFound("customer with id [%s] not found".formatted(id)));
    
   application.yml
    
   spring:
     error: 
       include-message: always
   ```
    
   To include message give in the response add the application.yml settings as well.
7. Docker(a bit about it):
   docker --version
   docker run hello-world

   ```
   services:
   db:
    container_name: postgres
    image: postgres
    environment:
      POSTGRES_USER: amigoscode
      POSTGRES_PASSWORD: password
      PGDATA: /data/postgres
   volumes:
   - db:/data/postgres
   ports:
     - "5433:5432"
     networks:
     - db
     restart: unless-stopped
    
   networks:
    db:
    driver: bridge
    
   volumes:
    db:
   ```
   1. docker compose up -d (pull the postgre)
   2. docker compose ps (shows the running files in docker)
   to changes the port changes "5433:5432" --> left one --> 5433 to some other port
   3. docker exec -it postgres bash (docker execute interactive mode name_of_container, it will give container bash/terminal)
   4. psql -U amigoscode
   5. \l
   6. \q (exist)
   7. control + d (to exist finally)
   We can connect to database using intellij as well, rightside naviagation --> databases --> ddl source --> postgres
8. Spring data JPA (Jakarta Persistence API) : It describes the management of relation data in Java application. Previously known as Javax Persistence api. Spring data JPA depends on JDBC driver. JPA uses hibernate internally for map the entities.
9. JDBC (Java Database connectivity): JDBC is an API(Application programming interface) used in java programming to interact with databases. The classes and interfaces of JDBC allow the application to send requests made by users to the specified database.
   1. JDBC driver manager :  It loads a database-specific driver in an application to establish a connection with a database. It is used to make a database-specific call to the database to process the user request.
   2. JDBC drivers: To communicate with a data source through JDBC, you need a JDBC driver that intelligently communicates with the respective data source.
   <strong> Application --> JDBC API --> JDBC driver manager  --> JDBC driver --> Database </strong>
   Adding postgresql JDBC driver:
      <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
      </dependency>
   adding spring data JPA:
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
      </dependency>
        
10. @Entity: By spring JPA, 
    command + P -> to get method parameters options in intellij editor 
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
    ```
11. @Qualifier: From below example we can see that there are two implementations of CustomerServiceDao(CustomerListDataAccessService and CustomerJPADataAccessService),
    Due to this CustomerService get confuse which bean to inject in, hence we have named our bean with "list" and "jpa",
    and in CustomerService we can give either of the name that we wanted to use inside @Qualifier.
    ```
    CustomerDao.java ************************
    
    public interface CustomerDao {
     List<Customer> selectAllCustomer();
     Optional<Customer> selectCustomerById(Integer id);
    }
    
    CustomerListDataAccessService.java ***********************
    
    @Repository("list")
    public class CustomerListDataAccessService implements CustomerDao {

    private static List<Customer> customers;
    @Override
    public List<Customer> selectAllCustomer() {
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
    }
    
    CustomerJPADataAccessService.java ***************************
    
    @Repository("jpa")
    public class CustomerJPADataAccessService implements CustomerDao {

    private final CustomerRepository customerRepository;

    public CustomerJPADataAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> selectAllCustomer() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return Optional.empty();
    }
    }
    
    CustomerService.java ***********************
    @Service
    public class CustomerService {

      private final CustomerDao customerDao;

      public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
      }
    
    
    ```
12. @PostMapping:
    ```
    @PostMapping
     public void registerCustomer(@RequestBody CustomerRegistrationRequest request) {
     customerService.addCustomer(request);
    }
    ```

<h3>Next part on ReadMe_JDBC.md</h3>

