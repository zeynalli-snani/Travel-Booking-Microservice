# Welcome to Lets Put This Together
***

## Task
The primary challenge of this project is to design a reliable Continuous Integration and Continuous Deployment (CI/CD) pipeline for a distributed microservices application. Managing a single monolithic application is straightforward, but orchestrating five distinct Spring Boot services (Discovery, API Gateway, Flight, Hotel, and Car Rental) requires robust automation. The problem lies in ensuring that every code change is automatically built, rigorously tested, measured for code coverage, containerized, and prepared for deployment without human error or environment conflicts.

## Description
I solved this problem by implementing a fully automated CI/CD pipeline using Jenkins, Maven, and Docker.

First, I containerized each Spring Boot microservice using individual `Dockerfile`s and linked them together using a `docker-compose.yml` file, which establishes a private virtual network and handles Eureka service discovery routing.

The project is a Java 11 and Spring Boot 2.3 based microservices system composed of:
- `discovery-service`
- `api-gateway`
- `flight-service`
- `hotel-service`
- `car-rental-service`

The architecture follows a classic service discovery pattern:
- `discovery-service` runs Eureka Server
- all business services register themselves in Eureka
- `api-gateway` exposes a single public entry point on port `8080`
- all backend traffic is routed internally through Docker networking

For the automation, I configured a `Jenkinsfile` that defines a multi-stage declarative pipeline:
1. **Quality Gate:** Jenkins pulls the source code and executes `mvn clean verify`. This runs all JUnit tests and triggers the JaCoCo plugin to analyze code coverage. The build is strictly configured to fail if code coverage drops below 75%.
2. **Artifact Generation:** Test results and JaCoCo coverage reports are archived as HTML artifacts for visual inspection.
3. **Docker Build & Push:** Jenkins securely logs into Docker Hub, iterates through a predefined list of the microservices, builds the new Docker images, and pushes them to the registry.
4. **Deployment to AWS EC2:** Jenkins connects to a remote Ubuntu EC2 server through SSH, copies the latest `docker-compose.yml`, pulls the newest images from Docker Hub, and restarts the full stack using Docker Compose.

Additional improvements added to the project:
- meaningful unit tests for the core logic of `flight-service`, `hotel-service`, and `car-rental-service`
- controller layer tests using Spring test slices and `@MockBean`
- safer bootstrap tests for `discovery-service` and `api-gateway`
- JaCoCo code coverage integration at Maven parent level
- enforced coverage threshold of `75%` for `service` and `controller` classes
- restart policies in Docker Compose so containers automatically recover after EC2 reboot

This means the project can now be verified at three levels:
- local Maven verification with tests and coverage
- local Docker Compose execution
- remote AWS deployment through Jenkins

## Installation
To run this project, you will need **Docker Desktop**, **Maven**, and **Git** installed on your host machine. The deployment relies on a self-hosted Jenkins server running inside a Docker container to execute the CI/CD pipeline.

Recommended environment:
- Java `11`
- Maven `3.8+`
- Docker Desktop with Docker Compose support
- Jenkins running in a Docker container
- Docker Hub account for image publishing
- optional AWS EC2 instance for deployment

1. Clone the repository:
```bash
git clone https://git.us.qwasar.io/taking_care_of_business_213333_t5ux1m/taking_care_of_business.git
cd travel-booking
```
(Optional) To run the tests and build the Java artifacts locally without Docker:

```bash
mvn clean verify
```

This command will:
- compile all five services
- run all JUnit tests
- generate JaCoCo coverage reports
- fail the build if the coverage rule is not satisfied

Spin Up the Jenkins Server
We use a Dockerized Jenkins instance to handle the continuous integration. Run the following command to start Jenkins, mapping it to port 8085 on your host machine:

```PowerShell
docker run -d -p 8085:8080 -p 50000:50000 --name jenkins-server -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock jenkins/jenkins:lts
```
(Note: The 'docker.sock' volume mount is required so the Jenkins container can use your host's Docker engine to build the microservice images).

Unlock Jenkins
When Jenkins starts for the first time, it generates a secure initial administrator password. Retrieve this password by checking the container logs:

```PowerShell
docker logs jenkins-server
```
Look for the section in the logs that says Please use the following password to proceed to installation, copy the alphanumeric string, and navigate to http://localhost:8085 in your browser to unlock Jenkins.

Initial Setup & Plugins
- Install Suggested Plugins: When prompted by the Jenkins UI, select "Install suggested plugins" to get the standard CI/CD toolset.

- Create Admin User: Follow the setup wizard to create your permanent administrator account and confirm the instance URL (http://localhost:8085).

- Install Required Custom Plugins: Navigate to Manage Jenkins -> Manage Plugins -> Available and install the following required plugins for this pipeline:

- Docker Pipeline (Allows Jenkins to build and push images)

- JaCoCo (Draws the code coverage trend graphs)

- SSH Agent or SSH Credentials support (Allows Jenkins to connect to the EC2 deployment server)

- Git plugin (Usually included in the suggested plugins)

- Pipeline plugin family (Usually included in the suggested plugins)

Configure Jenkins Tools
- JDK installation named `java11`
- Maven installation named `maven`
- Docker installation named `docker`

Configure Jenkins Credentials
- Docker Hub credentials with the id `dockerhub-credentials`
- SSH private key credentials with the id `ec2-ssh-key`

The EC2 SSH credential should use:
- username: `ubuntu`
- private key: the EC2 `.pem` file converted or pasted into Jenkins credentials

Configure the Pipeline Job Branch
If the reviewer wants to test a specific branch such as `dev`, make sure the Jenkins job tracks that branch in `Pipeline script from SCM`.
The script path must remain:

```text
Jenkinsfile
```

Create the Pipeline Job
On the Jenkins dashboard, click New Item.

Name the project travel-booking-pipeline and select Pipeline, then click OK.

Scroll down to the Pipeline section.

Set the Definition to Pipeline script from SCM.

Set the SCM to Git.

Provide your repository URL.

Ensure the Script Path is set to Jenkinsfile (this tells Jenkins to read the automation steps from the file in the repository).

Click Save.


## Usage

Run the CI Build: On your newly created job page, click Build Now. Jenkins will pull the code, run `mvn clean verify` to enforce the `75%` JaCoCo coverage gate, build and push the Docker images to Docker Hub, then deploy the latest stack to the configured AWS EC2 server.

Expected Jenkins stage flow:
1. Quality Gate
2. Artifact Generation
3. Docker Build & Push
4. Deploy To EC2

#### Very Important
Reviewer note:
- the pipeline uses `mvn clean verify` in the Quality Gate stage
- in Maven, `verify` is a later lifecycle phase than both `test` and `package`
- this means the pipeline effectively executes the build and unit test phases before finishing the quality gate
- in other words, the same command covers the behavior of both `mvn test` and `mvn package`, while also enforcing JaCoCo coverage validation

If the build is successful, Jenkins should:
- publish test reports
- publish JaCoCo coverage data
- push fresh Docker images for all services
- update the AWS deployment automatically

Docker Hub Images Used By The Deployment
- `iamnotsnani/discovery-service:latest`
- `iamnotsnani/api-gateway:latest`
- `iamnotsnani/flight-service:latest`
- `iamnotsnani/hotel-service:latest`
- `iamnotsnani/car-rental-service:latest`

Deploy the CD Architecture: Once the Jenkins build is completely green, return to your local PowerShell terminal (inside the travel-booking directory) and spin up the actual microservices.
To spin up the entire microservices architecture locally, utilize the Docker Compose file. This will pull the latest images and start all five containers in detached mode.

```bash
docker-compose up -d
```
Once the containers are running, wait approximately 30-45 seconds for the Spring Boot applications to initialize and register.

Access the Eureka Discovery Dashboard to verify service health: http://localhost:8761

Access the API Gateway locally:
- http://localhost:8080

Useful local service ports:
- `8761` Eureka Discovery
- `8080` API Gateway
- `8081` Flight Service
- `8082` Hotel Service
- `8083` Car Rental Service

Example API Calls (via Gateway)
Once started, you can access all services through the Gateway (Port 8080):

curl "http://localhost:8080/flights/search?origin=NYC&destination=LAX"

curl "http://localhost:8080/hotels/search?location=LosAngeles"

curl "http://localhost:8080/cars/search?location=LosAngeles"

To cleanly shut down the application and remove the virtual networks:

```bash
docker-compose down
```

## AWS Deployment

The application is also configured to be deployed to AWS on a single EC2 instance, which is the simplest and most cost-effective option for a student project.

### Why EC2 Was Chosen
- the application already works correctly with Docker Compose
- the project requirement includes Jenkins and Docker images
- a single EC2 server is easier to manage than ECS or Kubernetes
- it avoids unnecessary cloud complexity for demonstration and testing purposes

### AWS Deployment Topology
- Region: `eu-central-1`
- Instance type: `t3.small`
- OS: Ubuntu
- Public entry point: API Gateway on port `8080`
- Internal orchestration: Docker Compose
- Stable access: Elastic IP assigned to the EC2 instance

### Jenkins To AWS Deployment Flow
1. Jenkins runs tests and coverage checks
2. Jenkins builds all Docker images
3. Jenkins pushes images to Docker Hub
4. Jenkins connects to EC2 over SSH
5. Jenkins uploads the latest `docker-compose.yml`
6. Jenkins executes:
   - `docker compose pull`
   - `docker compose up -d`
7. The EC2 server starts the updated containers

### Remote Deployment Directory
On the EC2 instance, the application is deployed from:

```bash
/home/ubuntu/travel-booking
```

### Example Manual EC2 Commands
If a tester or reviewer wants to inspect the deployment manually:

```bash
cd /home/ubuntu/travel-booking
sudo docker compose pull
sudo docker compose up -d
sudo docker ps
sudo docker compose logs -f
```

## Testing And Coverage

The project now contains automated tests for all services.

### What Is Tested
- `flight-service`
  - service search logic
  - airport code normalization
  - result limiting
  - controller endpoint response
- `hotel-service`
  - hotel API mapping
  - fallback behavior
  - invalid JSON handling
  - controller endpoint response
- `car-rental-service`
  - car filtering by location/origin
  - mapping and pricing logic
  - result limiting
  - controller endpoint response
- `discovery-service`
  - bootstrap configuration validation
- `api-gateway`
  - bootstrap configuration validation

### Coverage Rule
The JaCoCo quality gate is configured to require at least:

```text
75% line coverage
```

This threshold is enforced for:
- `service` classes
- `controller` classes

If coverage drops below this threshold, Jenkins fails the build automatically.

### Coverage Report Location
After running:

```bash
mvn clean verify
```

JaCoCo reports can be found in each module under:

```bash
target/site/jacoco/
```

## Reviewer Guide

For a tester or reviewer, the easiest validation flow is:

1. Run `mvn clean verify`
2. Confirm the build passes
3. Open JaCoCo artifacts or local coverage reports
4. Run `docker-compose up -d`
5. Wait for startup
6. Open Eureka at `http://localhost:8761`
7. Test the API Gateway endpoints
8. Optionally run the Jenkins pipeline and verify the AWS deployment

Recommended reviewer checks:
- all five containers start successfully
- services appear in Eureka
- gateway routes requests correctly
- Jenkins fails if tests or coverage fail
- Jenkins pushes images and deploys without manual intervention

Sample review endpoints:

```bash
curl "http://localhost:8080/flights/search?origin=NYC&destination=LAX"
curl "http://localhost:8080/hotels/search?location=Seattle"
curl "http://localhost:8080/cars/search?location=Tokyo"
```

If testing the deployed AWS version, use the EC2 public IP instead of `localhost`.

## Notes

- The project intentionally uses Java 11 and an older Spring Boot / Spring Cloud stack for compatibility with the chosen environment.
- The deployment strategy was designed to preserve that compatibility and avoid unnecessary framework upgrades.
- The Docker Compose file includes restart policies so the containers come back automatically after server reboot.
- The API Gateway is the only endpoint that needs to be used publicly for normal testing.

### The Core Team
[zeynalli_s]

Made at Qwasar SV -- Software Engineering School
<img alt='Qwasar SV -- Software Engineering School's Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' />
