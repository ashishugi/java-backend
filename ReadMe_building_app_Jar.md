1. Refer: https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/
2. Spring boot maven plugin: The Spring Boot Maven Plugin provides Spring Boot support in Apache Maven. It allows you to package executable jar or war archives, run Spring Boot applications, generate build information and start your Spring Boot application prior to running integration tests.
3. cmd: 
   1. mvn clean : clean the target folder created
   2. mvn package: generates the target folder and jar for the application.
   3. mvn clean install: delete the target and reinstall the dependency
   4. java -jar: cmd to run jar
   5. java -jar target/spring-boot-example-0.0.1-SNAPSHOT.jar: run application 
4. Surefire Plugin: This plugin meade to be used only with unit test not with Integration / acceptance test. The Surefire Plugin is used during the test phase of the build lifecycle to execute the unit tests of an application. It generates reports in two different file formats: plain text(.txt) or .xml
   By default, these files are generated in ${basedir}/target/surefire-reports/TEST-*.xml
   By default surefire run all the Test which ends with 'Test', hence to remove the IntegrationTest from surefire report, we have two methods: 
   1. just rename our CustomerIntegrationTest --> to --> CustomerIT
   2. add following plugin to exclude your files of IntegrationTest
5. Now for running tests: 
   1. Surefire: used to running and generating report for unit test
   2. Failsafe: used to the case of integration tests. 
   3. pre-integration-test: this is just before integration test starts running, we have used random port to run integration tests
   4. Got few errors: Failed to execute goal org.springframework.boot:spring-boot-maven-plugin , Could not contact Spring Boot application via JMX on port 9001. Please make sure that no other process is using that port:
      We have added solution from references: https://stackoverflow.com/questions/45400701/failed-to-execute-goal-org-springframework-bootspring-boot-maven-plugin1-5-6-r , https://www.codeproject.com/Questions/5360596/Failed-to-execute-goal-org-springframework-boot-sp
   5. Right side in intelliJ  --> maven icon --> spring-boot-example(project name) -> lifecycle -> verify : runs and shows count of integration tests only.
   6. Right side in intelliJ  --> maven icon --> spring-boot-example(project name) -> lifecycle -> test : runs and shows count of unit tests only.
   ```
   <build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<executions>
					<execution>
						<id>pre-integration-test</id>
						<goals>
							<goal>start</goal>
						</goals>
						<configuration>
							<arguments>
								<argument>--server.port=${tomcat.http.port}</argument>
							</arguments>
							<skip>true</skip>
						</configuration>
					</execution>
					<execution>
						<id>post-integration-test</id>
						<goals>
							<goal>stop</goal>
						</goals>
					</execution>
					<execution>
						<phase>none</phase>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-failsafe-plugin</artifactId>
				<configuration>
					<includes>
						<include>**/*IntegrationTest.java</include>
						<include>**/*IT.java</include>
					</includes>
					<systemPropertyVariables>
						<test.server.port>${tomcat.http.port}</test.server.port>
					</systemPropertyVariables>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>build-helper-maven-plugin</artifactId>
				<executions>
					<execution>
						<id>reserve-tomcat-port</id>
						<goals>
							<goal>reserve-network-port</goal>
						</goals>
						<phase>process-resources</phase>
						<configuration>
							<portNames>
								<portName>tomcat.http.port</portName>
							</portNames>
						</configuration>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>**/*IntegrationTest.java</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>
   ```