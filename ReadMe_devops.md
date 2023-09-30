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
6. will build image for us + and push image to docker for us:  `mvn clean compile jib:build`
7. Pull docker image and run in local:
   1. pull docker image: `docker pull ashishkumargupta/customer-api:latest`
   2. run: `docker run --name customer-api-container --rm -p 8080:8080 ashishkumargupta/customer-api` --> (rm: remove the container after running it, -p image_port:local_port_that_where_we_want_to_run)
   3. above cmd will failed as it will not able to connect with DB, as we have give localhost:5432, localhost will refer to the the things inside the container. But our database is not inside that container. 
      To interact with the DB present in other container we should update docker-compose file and also spring.datasource.url
   4. docker network ls: 
   5. `docker run --name customer-api-container --rm -p 8080:8080 --network spring-boot-example_db ashishkumargupta/customer-api --spring.datasource.url=jdbc:postgresql://db:5432/customer`
8. Understanding running docker image vs running application :
   1. Running docker image: When you run docker image(basically running your application on docker) you run it by docker command or docker-compose.yml file. 
      Docker image / application image has nothing to do with your local repo/project changes. It will pull the changes that we did pushed to docker hub. Local application.yml file has nothing to do with it.
      When you run that docker image(docker compose up -d). It will take the ports and configuration that was defined in the application/docker image which was lastest pushed to docker. 
      Definitely we can add some environment variable to override the properties(like: ports, database or datasource etc), these changes can be added to docker command while running application or you can edit docker-compose.yml. 
      running docker compose - go project root where docker compose file is located: `docker compose up -d`
      see docker running instances: `docker ps`
      close docker compose all instances/containers: `docker compose down`
      get logs of a particular container: `docker logs container_name`
   2. Running local project/application: When you run your local application it has nothing to do with docker-compose.yml file.
      Your application will run as per the configurations that you have defined in application.yml.
9. Understanding docker-compose.yml: 
   1. services: It says the number of services we have. Inside this we defined the different service/container/image that we will be configuring. 
      Below we have 3 as db, queue and customer-api. There are the service name given we can then use this as a variable later.
   2. container_name: This is the name given by us for the container that we run
   3. image: It is the image that our container must pull/ run code from.
   4. environment: Inside this we can give the environment variable that container/image takes.
   5. ports: "a:b" --> a : It is the port that we can use in our browser/localhost, b: It is the port that our container actually uses, so to connect or add data etc we need to connect with actual container ports only.
      Ex: for db: "5332:5432", here we can access the db locally via port 5332, but to actually save the data we must connect with actual port that db/postgres uses hence we have override it as SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/customer(port: 5432) for datasource. Similarly in application.yml we used
      jdbc:postgresql://localhost:5432/customer(localhost) but here out database service name is **db** hence used the same to connect with the service.
      Similarly: queue: "5562:5672", rabbitmq was running at port 5662 locally, but inside container it was running at 5672, hence to push and consume queue data we need to connect to actual container port hence we **SPRING_RABBITMQ_PORT: 5672**, also SPRING_RABBITMQ_HOST: queue, because in application.yml we have given **localhost**, but here our service name was **queue**.
   6. Networks - As container works in complete isolation from each other, but to interact with each other we have to make them in same network. Here in our project we have to interact our customer-api with rabbitmq and postgresql. Hence we have to keep it in a same network to access and interact with each others data. Here the network name give is **db**.
   7. depends_on: It mean which services we need to run before running this current service. Below postgres(db) and rabbitmq(queue) must run before customer-api application to run. Hence it depends upon db, queue.
   8. Volumes: It is filesystem or storage made to make the data persist and save that are generated by containers. This data is stored and can be reused again even when we run container again. We provide the path of particular container here to save.
   ```
   services:
   db:
    container_name: postgres
    image: postgres
    environment:
     POSTGRES_USER: username
     POSTGRES_PASSWORD: password
     PGDATA: /data/postgres
    volumes:
     - db:/data/postgres
    ports:
     - "5332:5432"
    networks:
     - db
    restart: unless-stopped
   queue:
     container_name: rabbitmq
     image: rabbitmq:3-management-alpine
     environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
     ports:
     - "5662:5672"
     - "15662:15672"
     networks:
     - db
     restart: unless-stopped
   customer-api:
     container_name: customer-api
     image: ashishkumargupta/customer-api
     environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/customer
      SPRING_RABBITMQ_HOST: queue
      SPRING_RABBITMQ_PORT: 5672
     ports:
     - "8088:8080"
     networks:
     - db
     depends_on:
     - db
     - queue
     restart: unless-stopped
   
   networks:
    db:
     driver: bridge
   
   volumes:
    db:
   ```
10. qer

<h4>Last Video: 205</h4>