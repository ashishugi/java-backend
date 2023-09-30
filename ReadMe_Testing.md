
1. TestContainers: Usually for writing unit test for database, we need to mock the database. For mocking the database we might have to write 
    mock database and add some complicated configurations. Using testcontainers we just define test dependencies as code then simply run your tests and containers will be created and then deleted. 
    All we need is docker. https://testcontainers.com/ and https://java.testcontainers.org/quickstart/junit_5_quickstart/
    Before TestContainers we use to use in-memory databases like H2. Issue with H2 was that database which it provides was not the actual representation of database that we might use in our production.
    Ex: we may be using Postgresql as database in production but we have a test out unit tests on H2 database.
    
    But in case of TestContainer we can have actual database running on docker for our unit and integration tests.
    We need below dependency along JUnit 5, spring boot 3 already have Junit5 install in spring-boot-starter-parent  --> junit-platform-launcher
    ```
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <version>1.17.6</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>1.17.6</version>
        <scope>test</scope>
    </dependency>
   ```
2. Adding project specific dependency: as we need to get container for postgresql for testing. TestContainer will manage the lifeCycle for this postgresql container.
    ```
   <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>1.17.6</version>
        <scope>test</scope>
    </dependency>
   ```
3. TestContainer will get destroyed after test get finished running. While running test you should have you local docker running.
4. Command to format docker logs: after running docker
    ```
   export FORMAT="ID\t{{.ID}}\nNAME\t{{.Names}}\nIMAGE\t{{.Image}}\nPORTS\t{{.Ports}\nCOMMAND\t{{.Command}}\nCREATED\t{{.CreatedAt}}\nSTATUS\t{{.Status}}\n"
   ```
    after executing above cmd run: docker ps --format="$FORMAT"
    to go inside container: docker exec -it CONTAINER_NAME bash
5. Configuring Database Postgresql: 
    ```
   import static org.assertj.core.api.Assertions.assertThat;


    @Testcontainers
    public class TestcontainersTest {
    
        @Container
        private static final PostgreSQLContainer<?> postgreSQLContainer =
                new PostgreSQLContainer<>("postgres:latest")
                        .withDatabaseName("customer-dao-unit-test")
                        .withUsername("username")
                        .withPassword("password"); // 'postgres:latest' - takes the latest image of postgres from docker
    
        @Test
        void canStartPostgresDB() {
            assertThat(postgreSQLContainer.isRunning()).isTrue();
            assertThat(postgreSQLContainer.isCreated()).isTrue();
        }
    }
   ```
    `docker exec -it reverent_johnson bash` // reverent_johnson -> containerName
    `psql -U username -d customer-dao-unit-test` // U = username, d =database
    `\c customer-dao-unit-test` // connect with database
6. Setting Up Flyway for migrating DB to test DB - 
    ```
      @Test
    void canApplyDBMigrationsWithFlyWay() {
    //      https://documentation.red-gate.com/fd/api-java-184127629.html

        Flyway flyway = Flyway.configure().dataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        ).load();
        flyway.migrate();
        System.out.println("");
    }
   ```
    after you connect with DB (\c customer-dao-unit-test)
    `\dt` (shows two table customer and flyway_schema_history, this means db migration have taken correctly)
    `\d` (can also see customer_id_sequence index that we add during db migration in flyway)
    `SELECT * FROM customer; `(SQL)
7. Connecting testContainer postgresql Database to application, so that this DB can be accessed by our test. This is used to connect our test with the database, as it will map these properties with application.yml
    ```
       @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry registry) { // This is used to connect our test with the database, as it will map these properties with application.yml
        registry.add(
                "spring.datasource.url",
                postgreSQLContainer::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                postgreSQLContainer::getUsername
        );
        registry.add(
                "spring.datasource.password",
                postgreSQLContainer::getPassword
        );
    }
   ```
   class: 
    ```
   @Testcontainers
    public class TestcontainersTest {
    
        @Container
        private static final PostgreSQLContainer<?> postgreSQLContainer =
                new PostgreSQLContainer<>("postgres:latest")
                        .withDatabaseName("customer-dao-unit-test")
                        .withUsername("username")
                        .withPassword("password"); // 'postgres:latest' - takes the latest image of postgres from docker
    
        @DynamicPropertySource
        private static void registerDataSourceProperties(DynamicPropertyRegistry registry) { // This is used to connect our test with the database, as it will map these properties with application.yml
            registry.add(
                    "spring.datasource.url",
                    postgreSQLContainer::getJdbcUrl
            );
            registry.add(
                    "spring.datasource.username",
                    postgreSQLContainer::getUsername
            );
            registry.add(
                    "spring.datasource.password",
                    postgreSQLContainer::getPassword
            );
        }
    
        @Test
        void canStartPostgresDB() {
            assertThat(postgreSQLContainer.isRunning()).isTrue();
            assertThat(postgreSQLContainer.isCreated()).isTrue();
        }
    
        @Test
        void canApplyDBMigrationsWithFlyWay() {
    //      https://documentation.red-gate.com/fd/api-java-184127629.html
    
            Flyway flyway = Flyway.configure().dataSource(
                    postgreSQLContainer.getJdbcUrl(),
                    postgreSQLContainer.getUsername(),
                    postgreSQLContainer.getPassword()
            ).load();
            flyway.migrate();
        }
    }

   ```
8. @SpringBootTest: We should never use it for Unit Test because this will brake entire application if not correctly configured.
    and also load whole application Context. In this applicationContext there may be bean that are not required for out unit test.
    Hence our unit test may get slow.
    `@SpringBootTest` ====> wrong use
    public class CustomerUnitTest{}
9. Abstract class :  A class whos object cannot be created, but this abstract class can only we inherited into some other class.
    We may treat abstract class as a base class.
10. `@Autowired` vs Constructor Injection: both are same, both will take underTest from springContext and initialise the underTest
    constructor injection: 
    ```
    class CustomerRepositoryTest {

    private CustomerRepository underTest;

    CustomerRepositoryTest(CustomerRepository underTest) {
        this.underTest = underTest
    }
    ```
    using autowired:

    ```
    class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    ```
    <strong> @Autowired </strong>: This inject the dependency to out declared variable from the Bean present in application Context. If we
    do not use @Autowired then we must inject that using constructor injection. 
    
    Example: for below example @Autowired will not work as, there is no application context available while writing the application context. It will some error like: Autowired members must be defined in valid Spring bean (@Component|@Service|...)
    ```
    class CustomerRepositoryTest {
    
        @Autowired
        private CustomerRepository underTest;
    
        @Autowired
        private ApplicationContext applicationContext;
    
        @BeforeEach
        void setUp() {
            System.out.println(applicationContext.getBeanDefinitionCount());
        }
    
        @Test
        void existsCustomerByEmail() {
        }
    }
    ```
    To make above @Autowired run we must have applicationContext for injection. We know that using @SpringBootTest will load the all the bean of applicationContext.
    Hence below code will run successfully. 
    ```
    @SpringBootTest
    class CustomerRepositoryTest {
    
        @Autowired
        private CustomerRepository underTest;
    
        @Autowired
        private ApplicationContext applicationContext;
    
        @BeforeEach
        void setUp() {
            System.out.println(applicationContext.getBeanDefinitionCount());
        }
    
        @Test
        void existsCustomerByEmail() {
        }
    }
    ```
11. @DataJpaTest : It just load all the component that is needed to run JPA component. So instead of using @SpringBootTest which loads all the applicationContext. 
    We can use this @DataJpaTest(which just loads all the components needed for JPA).
    <strong> Hence after using @DataJpaTest annotation, @Autowired will inject all those dependency whos bean in created using @DataJpaTest </strong>
    It comes with its own embedded database source, but as we have our own database to connect for testing so we have to disable this database source.
    We do this using this annotation: ``` @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) ```. 
    To add out testContainerDb just extend the AbstractClass we made
12. Mockito : While doing the unit test we try to mock dependencies/classes/interfaces other than the current testing class. 
    This is because we just wanted to focus on current class to test, we do not want other class to invoke in real while running test. Hence we mock all other things.
    It allows us to create the mocks and we have full control over the mocks. 

    Example:   verify Interaction :
    ```
    Interaction with methods: 
    
    import static org.mockito.Mockito.*;

    // mock creation
    List mockedList = mock(List.class);
    // or even simpler with Mockito 4.10.0+
    // List mockedList = mock();
    
    // using mock object - it does not throw any "unexpected interaction" exception
    mockedList.add("one");
    mockedList.clear();
    
    // selective, explicit, highly readable verification
    verify(mockedList).add("one");
    verify(mockedList).clear();
    ```
    stub method calls :
    ```
    // you can mock concrete classes, not only interfaces
    LinkedList mockedList = mock(LinkedList.class);
    // or even simpler with Mockito 4.10.0+
    // LinkedList mockedList = mock();
    
    // stubbing appears before the actual execution
    when(mockedList.get(0)).thenReturn("first");
    
    // the following prints "first"
    System.out.println(mockedList.get(0));
    
    // the following prints "null" because get(999) was not stubbed
    System.out.println(mockedList.get(999));
    ```
13. Mockito.verify(MockedDependency).methodName() : inside this Mockito.verify() we placed mocked dependency only and verify it.
14. For initialising the object annotated with @Mock etc we need to use MockitoAnnotation.openMock(this). 
    And finally when the test gets over we need to close it as well.
    ```
    class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerService(customerDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
    ```
    Instead of writing same code for autoCloseable = MockitoAnnotations.openMocks(this); and autoCloseable.close() we can simply use @ExtendWith(MockitoExtension.class) - this will do this for us.
    
    ```
    @ExtendWith(MockitoExtension.class)
    class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }
    ```
15. Using when(), thenReturn(), verify() :
    1. when(dependency).thenReturn() : we put our dependency inside when(), and thenReturn() the output that we wanted to return.
    2. verify(dependency).method(): this verifies if particular method call was made or not. We place dependency to verify. We do not place underTest object to verify.
        to verify is no method call is made then: verify(dependency, never()).method(), to verify number of time method called: verify(dependency, time(1)).method()
16. ArgumentCaptor: ArgumentCaptor allows us to capture an argument passed to a method to inspect it. This is especially useful when we can't access the argument outside of the method we'd like to test.
    Example: 
    CustomerService.java:  here in customerDao.insertCustomer(customer); if we want to test is correct customer was passed to the method then how can we do it ? using ArgumentCaptor we can get that argument value.
    ```
    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        //check if email exist
        if(customerDao.existsPersonWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException("email already taken");
        }
        //add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age());

        customerDao.insertCustomer(customer);////////////
    }
    ```
    CustomerServiceTest.java: see below finally we are able to get that parameter and test it.
    ```
    @Test
    void addCustomer() {
        //Given
        String email = "aditya@yahoo.com";
        CustomerRegistrationRequest request =
                new CustomerRegistrationRequest("Aditya", email, 23);

        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        ); // maps the value from CustomerRegistrationRequest to Customer

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }
    ```
17. Mocking Exception:
    ```
    assertThatThrownBy(() -> underTest.updateCustomerById(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes are found");
    ```
18. Inline mocking: Usually we mock the dependecy using @Mock, but this can also be done inside test method - inline: using mockedValue = mock(dependency);
    ```
    @ExtendWith(MockitoExtension.class)
    class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper=  new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Aditya");
        when(resultSet.getString("email")).thenReturn("aditya@yahoo.com");
        when(resultSet.getInt("age")).thenReturn(19);

        //When

        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        //Then
        Customer expected = new Customer(1, "Aditya", "aditya@yahoo.com", 19);

        assertThat(actual).isEqualTo(expected);

        assertThat(actual.getAge()).isEqualTo(expected.getAge());
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
     }
    }
    ```
19. Sample Unit Test code for reference: 
    ```
    package com.panther.customer;

    import com.panther.exception.DuplicateResourceException;
    import com.panther.exception.RequestValidationException;
    import com.panther.exception.ResourceNotFound;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.ArgumentCaptor;
    import org.mockito.Mock;
    import org.mockito.Mockito;
    import org.mockito.junit.jupiter.MockitoExtension;
    
    import java.util.Optional;
    
    import static org.assertj.core.api.Assertions.assertThatThrownBy;
    import static org.assertj.core.api.Assertions.assertThat;
    import static org.mockito.Mockito.*;
    
    @ExtendWith(MockitoExtension.class)
    class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //Given - not required here

        //When
        underTest.getAllCustomers();

        //Then
        Mockito.verify(customerDao).selectAllCustomer();
    }

    @Test
    void getAllCustomerById() {
        //Given
        int id = 1;
        Customer customer = new Customer(id,"Aditya", "aditya@yahoo.com", 20);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        Customer actual = underTest.getAllCustomerById(id);

        //Then
        verify(customerDao).selectCustomerById(id);
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowExceptionWhenCustomerIdDoNotExist() {
        //Given
        int id = -1;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> underTest.getAllCustomerById(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        //Given
        String email = "aditya@yahoo.com";
        CustomerRegistrationRequest request =
                new CustomerRegistrationRequest("Aditya", email, 23);

        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        ); // maps the value from CustomerRegistrationRequest to Customer

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowExceptionWhenAddingCustomerWithExistingEmail() {
        //Given
        String email = "aditya@yahoo.com";
        CustomerRegistrationRequest request =
                new CustomerRegistrationRequest("Aditya", email, 23);

        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //Then
        verify(customerDao, never()).insertCustomer(Mockito.any());
    }

    @Test
    void deleteCustomerById() {
        //Given
        int id = 1;

        when(customerDao.existPersonWithId(id)).thenReturn(true);

        //When
        underTest.deleteCustomerById(id);

        //Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowExceptionWhenCustomerIdDoesNotExistForDeletion() {
        //Given
        int id = 1;

        when(customerDao.existPersonWithId(id)).thenReturn(false);

        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("Id does not exist");

        //Then
        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void updateCustomerById() {
        //Given
        int id = 1;
        String newEmail = "aditya@yahoo.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Aditya", newEmail, 26
        );

        Customer customer = new Customer(
                1, "Abhishek", "abhi@yahoo.com", 25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomerById(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao, times(1)).updateCustomerById(customerArgumentCaptor.capture());
        Customer caputredCustomer = customerArgumentCaptor.getValue();

        assertThat(caputredCustomer.getName()).isEqualTo(request.name());
        assertThat(caputredCustomer.getEmail()).isEqualTo(request.email());
        assertThat(caputredCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void updateOnlyCustomerName() {
        //Given
        int id = 1;
        String name = "Shekhar";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                name, null, null
        );

        Customer customer = new Customer(
                1, "Abhishek", "aditya@yahoo.com", 25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        underTest.updateCustomerById(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao, times(1)).updateCustomerById(customerArgumentCaptor.capture());
        Customer caputredCustomer = customerArgumentCaptor.getValue();

        assertThat(caputredCustomer.getName()).isEqualTo(request.name());
        assertThat(caputredCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(caputredCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void updateOnlyCustomerEmail() {
        //Given
        int id = 1;
        String email = "shekhar@yahoo.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, email, null
        );

        Customer customer = new Customer(
                1, "Abhishek", "aditya@yahoo.com", 25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        //When
        underTest.updateCustomerById(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao, times(1)).updateCustomerById(customerArgumentCaptor.capture());
        Customer caputredCustomer = customerArgumentCaptor.getValue();

        assertThat(caputredCustomer.getName()).isEqualTo(customer.getName());
        assertThat(caputredCustomer.getEmail()).isEqualTo(request.email());
        assertThat(caputredCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void updateOnlyCustomerAge() {
        //Given
        int id = 1;
        int age = 19;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, null, age
        );

        Customer customer = new Customer(
                1, "Abhishek", "aditya@yahoo.com", 25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        underTest.updateCustomerById(id, request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerDao, times(1)).updateCustomerById(customerArgumentCaptor.capture());
        Customer caputredCustomer = customerArgumentCaptor.getValue();

        assertThat(caputredCustomer.getName()).isEqualTo(customer.getName());
        assertThat(caputredCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(caputredCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowExceptionEmailAlreadyTakenWhenUpdatingExistingEmail() {
        //Given
        int id = 1;
        String email = "aditya@yahoo.com";

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Aditya", email, 26
        );

        Customer customer = new Customer(
                1, "Abhishek", "abhi@yahoo.com", 25
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTest.updateCustomerById(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //Then
        verify(customerDao, never()).updateCustomerById(customer);
    }

    @Test
    void willThrowExceptionNoDataChangesWhenUpdatingCustomerDetails() {
        //Given
        int id = 1;
        String email = "aditya@yahoo.com";
        String name = "aditya";
        int age = 25;

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                name, email, age
        );

        Customer customer = new Customer(
                1, name, email, age
        );

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        //When
        assertThatThrownBy(() -> underTest.updateCustomerById(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes are found");

        //Then
        verify(customerDao, never()).updateCustomerById(customer);
    }
    }
    ```
    
<h3>Integration Testing</h3>

1. @SpringBootTest: We will using SpringBootTest(as this load whole applicationContext). While doing IntegrationTest we would require whole applicationContext.
    By default it will not start a server. We can use webEnvironments of @SpringBootTest to define how our test will run. 
    Ex: 
   1. MOCK: It loads web applicationContext and provides mock web environment. Embedded server are not started while using this annotation. If a
        web environment is not available in classpath then this mode fallbacks on to create a regular non-web applicationContext. It can be conjunction of
        @AutoConfigureMockMvc or @AutoConfigureWebTestClient for mock based testing for our web-application.
   2. RANDOM_PORT: This will setup the ITTest server. When you use @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) you test with a real http server. In this case, you need to use a TestRestTemplate.
      This is helpful when you want to test some surrounding behavior related to the web layer.
   3. DEFINED_PORT:
   4. NONE:
2. @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT): This is responsible to creating the IntegrationTest server. 
3. Testing with running server:This would be responsible for making RestApi calls. This would help us to send http request to our server. We have to install webTestClient (it provides actual https call)
    ```
   <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
            <scope>test</scope>
    </dependency>
   ```
4. @Autowired customerController: Never do this. As we do not want to actually call this, we just wanted to make http request.
5. Integration test sample example:
   ```
   package com.panther.journey;

    import com.panther.AbstractTestcontainers;
    import com.panther.customer.Customer;
    import com.panther.customer.CustomerRegistrationRequest;
    import com.panther.customer.CustomerUpdateRequest;
    import com.github.javafaker.Faker;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.core.ParameterizedTypeReference;
    import org.springframework.http.MediaType;
    import org.springframework.test.web.reactive.server.WebTestClient;
    import reactor.core.publisher.Mono;
    
    import java.util.List;
    import java.util.Random;
    import java.util.UUID;
    
    import static org.assertj.core.api.Assertions.assertThat;
    
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    public class CustomerIntegrationTest extends AbstractTestcontainers {

    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_URI = "/api/v1/customers";

    @Test
    void canRegisterACustomer() {

        //1. create a registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@yahoo.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, age
        );

        //2. send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //3. get all customer
        List<Customer> allCustomer = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //4. make sure that customer is present
        Customer expectedCustomer = new Customer(
                name, email, age
        );
        assertThat(allCustomer).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);


        // get customer by Id
        int id = allCustomer.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteCustomer() {
        //1. create a registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@yahoo.com";
        int age = RANDOM.nextInt(1, 100);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, age
        );

        //2. send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //3. get all customer
        List<Customer> allCustomer = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //4. get customer by Id
        int id = allCustomer.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        //5. delete customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();


        //6. check if delete customer still exists or not
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        //1. create a registration request
        String name = "Aditya";
        String email = "aditya1" + "@yahoo.com";
        int age = 19;

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, age
        );

        //2. send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //3. get all customer
        List<Customer> allCustomer = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //4. get customer by Id
        int id = allCustomer.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst()
                .orElseThrow();

        //5. update customer
        String updatedName = "Abhishek";
        String updatedEmail = "abhishek1@yahoo.com";
        int updatedAge = 26;
        CustomerUpdateRequest customerUpdateRequest =
                new CustomerUpdateRequest(updatedName, updatedEmail, updatedAge);

        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        //6. check the updated customer
        Customer actualCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .returnResult().getResponseBody();

        Customer expectedCustomer = new Customer(
                id, updatedName, updatedEmail, updatedAge
        );

        assertThat(actualCustomer).isEqualTo(expectedCustomer);
    }
    }
   ```

6. <h3>Running, Testing with Test Containers also Rabbit Module</h3>
   1. Run docker desktop
   2. to run rabbitMQ in main application :  `docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management`
   3. Terminal: Add the debugging point in unit/integration test so that we can check the DB in docker container else these containers get destroyed after test completes running.
      1. `docker ps` : this shows all the running containers
      2. `docker exec -it container_name bash` : this takes you inside the container and open bash to running command
      3. `psql -U username -d db_name` : this command will help you to connect with postgresql db (`psql -U username -d customer-dao-unit-test`)
      4. `\l` : show list of DB the container has.
      5. `SELECT * from table_name`: we can write the SQL query for table it has.
7. Make sure we make different - different interface of containers for each module we use via TestContainer : refer - https://manerajona.medium.com/testing-microservices-with-testcontainers-88fe40363bb3 and https://stackoverflow.com/questions/57512375/why-the-test-container-with-the-rabbitmq-is-creates-every-time-anew-and-is-not-s