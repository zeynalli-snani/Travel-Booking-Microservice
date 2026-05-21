# Welcome to Lets Put This Together
***

## Task
The primary challenge of this project is to design a reliable Continuous Integration and Continuous Deployment (CI/CD) pipeline for a distributed microservices application. Managing a single monolithic application is straightforward, but orchestrating five distinct Spring Boot services (Discovery, API Gateway, Flight, Hotel, and Car Rental) requires robust automation. The problem lies in ensuring that every code change is automatically built, rigorously tested, measured for code coverage, containerized, and prepared for deployment without human error or environment conflicts.

## Description
I solved this problem by implementing a fully automated CI/CD pipeline using Jenkins, Maven, and Docker.

First, I containerized each Spring Boot microservice using individual `Dockerfile`s and linked them together using a `docker-compose.yml` file, which establishes a private virtual network and handles Eureka service discovery routing.

For the automation, I configured a `Jenkinsfile` that defines a multi-stage declarative pipeline:
1. **Quality Gate:** Jenkins pulls the source code and executes `mvn clean verify`. This runs all JUnit tests and triggers the JaCoCo plugin to analyze code coverage. The build is strictly configured to fail if code coverage drops below 75%.
2. **Artifact Generation:** Test results and JaCoCo coverage reports are archived as HTML artifacts for visual inspection.
3. **Docker Build & Push:** Jenkins securely logs into Docker Hub, iterates through a predefined list of the microservices, builds the new Docker images, and pushes them to the registry.

## Installation
To run this project, you will need **Docker Desktop**, **Maven**, and **Git** installed on your host machine. The deployment relies on a self-hosted Jenkins server running inside a Docker container to execute the CI/CD pipeline.

1. Clone the repository:
```bash
git clone https://git.us.qwasar.io/taking_care_of_business_213333_t5ux1m/taking_care_of_business.git
cd travel-booking
```
(Optional) To run the tests and build the Java artifacts locally without Docker:

```bash
mvn clean package
```

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

Run the CI Build: On your newly created job page, click Build Now. Jenkins will pull the code, run mvn clean verify to enforce the 75% JaCoCo coverage gate, and push the newly built Docker images to Docker Hub.

Deploy the CD Architecture: Once the Jenkins build is completely green, return to your local PowerShell terminal (inside the travel-booking directory) and spin up the actual microservices.
To spin up the entire microservices architecture locally, utilize the Docker Compose file. This will pull the latest images and start all five containers in detached mode.

```bash
docker-compose up -d
```
Once the containers are running, wait approximately 30-45 seconds for the Spring Boot applications to initialize and register.

Access the Eureka Discovery Dashboard to verify service health: http://localhost:8761

Example API Calls (via Gateway)
Once started, you can access all services through the Gateway (Port 8080):

curl "http://localhost:8080/flights/search?origin=NYC&destination=LAX"

curl "http://localhost:8080/hotels/search?location=LosAngeles"

curl "http://localhost:8080/cars/search?location=LosAngeles"

To cleanly shut down the application and remove the virtual networks:

```bash
docker-compose down
```

### The Core Team
[zeynalli_s]

Made at Qwasar SV -- Software Engineering School
<img alt='Qwasar SV -- Software Engineering School's Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' />