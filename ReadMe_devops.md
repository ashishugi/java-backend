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

<h4>AWS</h4>

We have docker image, now we can deploy this image into cloud. AWS is a cloud service which allows us to deploy our service without us owning the physical server, dbs etc.
<img width="1249" alt="Screenshot 2023-10-01 at 5 33 11 PM" src="https://github.com/ashishugi/java-react-fullstack/assets/46626591/2a31ff71-a22a-4890-a6b3-a3b78d7ee03d">


1. React/Angular application --> https request --> aws cloud --> VPC --> Public subnet(Load Balancer) --> Private subnet (DB, EC2 etc)
   1. VPC: virtual private network
   2. Public subnet: anyone can access this
   3. Private subnet: not everyone can access here.
   4. EC2: Virtual computer in aws
   5. ECS: AWS Elastic container service to run docker containers.
2. AWS Elastic Beanstalk: It allows us to build the application without knowing the AWS very much. It is an end-to-end web application management. It is an easy-to-use service for deploying and scaling web applications and services developed Java, .Net, PHP, NodeJs, Go, Python, Ruby and Docker on familier servers such as Apache, Nginx, Passenger and IIS.
   1. You simply upload your code and Elastic Beanstalk automatically handles the deployment, from capacity provisioning, load balancing, and automatic scaling to web application health monitoring, with ongoing fully managed patch and security updates.
3. EC2:
   1. Creating Key-Pair: AWS -> EC2 -> key-pair -> create new key-pair - use RSA, .pem, - create -> this will download a .pem file(contains private key) -> this RSA key will help us to connect with EC2 with our local terminal using SSH(similarly as we do with github ssh)
   2. Save this key pair: --> go to root -> mkdir keypairs -->  move the downloaded file here (`mv ~/Downloads/file_name.pem keypairs`)
4. Running elastic beanstalk: https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/create_deploy_docker_ecs.html
   1. Creating Application:  AWS -> elastic Beanstalk -> create application -> application_name, tags(name: any-tag, environment: test ), platform: docker, platformVersion: recommended, platformBranch: ECS running on 64bit amazon linux 2, application_code : upload the code(make Dockerrun.aws.json file) -> source code origin (select Dockerrun.aws.json file).
   2. application code tags(name: customer-api) -> configure more options -> presets (single instance - free tier) -> software(keep same) -> instances (same) ->EC2 security group(default group) -> save -> capacity (auto scaling, enviroment type - single or load balance(in case of load balance there would be multiple instance to balance and scale up) - keep single instance for now)(Processor : arm64)(instanceType: t4g.micro) -> save
   3. load balance(fill if required later - right now not required) -> security (virtual machine permission: select you key_pair public key created on aws from drop down) -> save -> Database (Engine: postgresql)(version - select latest 14.5)(instance class: db.t2.small or db.t3.small)(storage: 20)(username: username, password: password - as given for postgres in application.yml)(availability: low) (database deletion policy: delete)-> save
   4. Network (VPC :default)(check public IP address checkbox)(Instance subnet: eu-west-1a, eu-west-1b)(database subnet: eu-west-1a, eu-west-1b) -> save -> **create APP**
   5. After creating app -> if we go ECS -> cluster -> one cluster is created (inside this cluster one EC2 is also created + 1 RDS database )
   6. Database -> AWS -> RDS -> 1 db also created (this database is not publicly available, it is only available once we are inside VPC (virtual private cloud))
   7. If you see the logs now from ECS logs, there it would be some db connection issue, this is because in our Dockerrun.aws.json there is wrong datasource url("jdbc:postgresql://TODO:5432/customer"), we have to correct it. AWS -> RDS -> DB instances -> Find DB identifier -> copy the endpoint -> update Dockerrun.aws.json(jdbc:postgresql://COPIED_VALUE_OF_ENDPOINT:5432/customer) -> redeploy it(ECS -> cluster -> upload and deploy -> upload file -> upload Dockerrun.aws.json file -> deploy)
   8. After above step you will connect with DB but still get error as "FATAL error 'customer' db does not exist" -> AWS -> EC2 -> find our EC2 -> copy public IP address -> open local terminal of system -> go inside the keypair/  folder where we have stored the private key -> inside this keypair we can ssh the EC2 -> before that we have to give permission that only we can read-write this file run this cmd (`chmod 600 keypair_file.pem`), this is to be run only once after you have the permission ->  `ssh -i keypair_file.pem ec2-user@public_ip_address` -> we get inside the EC2 instance of our application in the terminal
      `sudo -i` (will make our cmd as root user) -> `docker ps`(here we can see our instance image ex: amazon/amazon-ecs-agent:latest, also our image as well ashishkumargupta/customer-api) -> to fix the issue -> `docker run --rm -it postgres:alpine bash` -> `psql -U username -d postgres -h host_endpoint_that_we_used_above` -> will ask for password (password) -> we get inside postgres -> `\l` -> `create database customer;` (similar as we use to do in docker) -> cntrl + d 2times to quit -> now we can check the logs error would be removed. 
   9. Now if you again connect with data base (`psql -U username -d postgres -h host_endpoint_that_we_used_above`) and `select * from customer;` we will get the entries/rows from database. You can also test you api, ECS -> cluster -> url
   10. Although we have connected directly to our machine (EC2) from our local, but usually we do not do this. We have a tool **BASTIONS** as a intermediate. We ssh/connect with BASTIONS and then BASTIONS connect with further machines. BASTIONS is located at Public subnet . USER -> BASTIONS -> OUR EC2, DB etc.
   11. CloudFormation: It is responsible for making all the resources. AWS -> cloudFormation -> open your deployed resource -> you can see all the resource made for deploying this application. If you click on view in Designer then it will open a mind-map of the resources used.
   12. creating ECS basically creates a cluster with basic need and machine ex: EC2 etc. 
   13. About ECS(Elastic container service similar as Docker container hub): If we are not using Kubernetes(EKS) then we must use ECS.  AWS  -> containers -> Elastic Container Service ->  
5. From above creation of AWS resource we have done it via UI. But all the above things can be done using tools like - terraform(HashiCrop) - written in golang, cloudFormation, Pulumi etc.
6. Similar as above companies usually make different environment(ECS) like pre-prod, staging, prod etc. to different purpose like testing etc.
7. EKS (Elastic Kubernetes service): It similar alternate way to deploy your application/service.

<h4>Github Actions (CI/CD - Automation)</h4>


<h4>Last Video: 236</h4>
