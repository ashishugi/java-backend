
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
    docker exec -it reverent_johnson bash // reverent_johnson -> containerName
    psql -U username -d customer-dao-unit-test // U = username, d =database
    \c customer-dao-unit-test // connect with database
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
    \dt (shows two table customer and flyway_schema_history, this means db migration have taken correctly)
    \d (can also see customer_id_sequence index that we add during db migration in flyway)
    SELECT * FROM customer; (SQL)
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
8. @SpringBootTest: We should never use it for Unit Test because this will brake enitre applicationif not correctly configured.
    and also load whole application Context. In this applicationContext there may be bean that are not required for out unit test.
    Hence our unit test may get slow.
    @SpringBootTest ====> wrong use
    public class CustomerUnitTest{}
9. Abstract class :  A class whos object cannot be created, but this abstract class can only we inherited into some other class.
    We may treat abstract class as a base class.
10. 

<h4>Last Video: 127</h4>