1. Parts of DevOps:
   1. CI : Continuous Integration - in this we regularly merge the change in central repository which is then build and tested automatically.
   2. CD : Continuous Delivery - automate the software deployment, building, testing and infrastructure positioning. 
   3. Infrastructure as Code
   4. Monitoring and logging

<h4>Docker</h4>
1. Docker is a platform for building, running and shipping application. (https://hub.docker.com/)
2. Developers can easily build and deploy applications running in containers.
3. Local environment is same across any other environment. There won't be any issue if you run it on the other machine. If it works in my machine then it will work in other dev, stage, pre-stage etc environments.
4. Used in CI/CD workflow.
5. Containers: It is a isolated environment for running the applications. Within the container is has everything that our application needs to run. It contain OS, tools, binaries and it contains the software that may require to run the application.
6. our code --> docker image --> containers(we can many multiple container from single image)
7. command to run docker : docker run -d -p 80:80 docker_image_name
8. Docker architecture: 
   1. It follows client-server approach: client is the cli(terminal - cmd) and server is the docker host
   2. Registries:  Place where we keep public and private images.
   3. Docker-Host/server: Inside this we have docker-daemon this responsible to handling the request from the client(terminal - cmd). So when we run a container -> if it is not present on the host, then docker daemon goes to registries to fetch the image and stores in the host. And from this image we run container later. 
9. Docker registries: It is a store and distribution system for docker images. We docker registries, amazon container registries, google container registries , github packages etc.
10. We can pull public images without login, but for private we need to login: docker login

<h4>Jib(from google) - Create docker image </h4>

1. It allows us to build docker image without having docker daemon up and running.
2. https://github.com/GoogleContainerTools/jib
3. add below into plugin: 
   ```
   <plugin>
				<groupId>com.google.cloud.tools</groupId>
				<artifactId>jib-maven-plugin</artifactId>
				<version>3.4.0</version>
				<configuration>
					<from>
						<image>eclipse-temurin:17</image>
						<platforms>
							<platform>
								<architecture>arm64</architecture>
								<os>linux</os>
							</platform>
							<platform>
								<architecture>amd64</architecture>
								<os>linux</os>
							</platform>
						</platforms>
					</from>
					<to>
						<image>docker.io/${docker.username}/${project.artifactId}:${project.version}</image>
						<tags>
							<tag>latest</tag>
						</tags>
					</to>
				</configuration>
			</plugin>
   ```
4. docker login: from terminal to login docker
5. mvn clean package: test + Integration test + 
6. will build image for us + and push image to docker fo us:  mvn clean compile jib:build
7. Pull docker image and run in local:
   1. pull docker image: docker pull ashishkumargupta/customer-api:latest
   2. run: docker run --name customer-api-container --rm -p 8080:8080 ashishkumargupta/customer-api --> (rm: remove the container after running it, -p image_port:local_port_that_where_we_want_to_run)
   3. above cmd will failed as it will not able to connect with DB, as we have give localhost:5432, localhost will refer to the the things inside the container. But our database is not inside that container. 
      To interact with the DB present in other container we should update docker-compose file and also spring.datasource.url
   4. 
8. 

<h4>Last Video: 200</h4>