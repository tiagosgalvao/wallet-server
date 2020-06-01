# Wallet Server Application

Minimal [Spring Boot](http://projects.spring.io/spring-boot)

## Requirements

For building and running the application you need:

- [JDK 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Gradle](https://gradle.org)

## Introduction
This guide walks you through the process of creating an application that accesses relational JPA data through gRPC calls.

## About the application

This is a wallet server application that will keep the track of a users monetary balance in the system. Storing separate balance for different currencies.
There are three main features: `Deposit` `Withdraw` `Balance`

# Requirements
* The wallet server will keep track of a users monetary balance in the system. There should be separate balance for different currencies.

## Programming Languages

This project is authored in Java.

## Dependencies

*   [Apache Kafka](https://kafka.apache.org/) - Distributed streaming platform
* 	[Flyway](https://flywaydb.org/) - Version control for database
* 	[Git](https://git-scm.com/) - Free and Open-Source distributed version control system
*   [Google JIB](https://cloud.google.com/blog/products/gcp/introducing-jib-build-java-docker-images-better) - Jib is a fast and simple container image builder 
that handles all the steps of packaging your application into a container image
* 	[Lombok](https://projectlombok.org/) - Never write another getter or equals method again, with one annotation your class has a fully featured builder, 
Automate your logging variables, and much more
* 	[MySQL](https://www.mysql.com/) - Open-Source Relational Database Management System
* 	[Spring Boot](https://spring.io/projects/spring-boot) - Framework to ease the bootstrapping and development of new Spring Applications
* 	[TestContainers](https://www.testcontainers.org/) - Lightweight, throwaway instances of common databases

## Running the application locally (pre-requirements)

At first you will need some support applications running such as stream platform and the database.
To make it easy as test environment you can use the docker-compose file available in the project.
*   [Docker-compose](https://docs.docker.com/compose/gettingstarted/) - Docker Compose first steps - just need to execute the file `docker-compose.yml`
* Command: `docker-compose -f docker-compose.yml up -d`

* Starting wallet-server_zookeeper_1 ... done
* Starting wallet-server_kafka_1     ... done
* Starting wallet-server_db_1        ... done

Instead of using docker another option could be point the wallet server to standalone zookeeper, kafka and mysql servers, just changing the configurations at
application.yml

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the 
`com.galvao.wallet.WalletApplication` class from your IDE.

Alternatively you can use the [Spring Boot Gradle plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/) like so:

```shell
./gradlew bootRun
```

* compiles Java classes to the /target directory
* copies all resources to the /target directory
* starts an embedded Apache Tomcat server

* Important: to run the integration tests for now is necessary to start the compose in order to have kafka running, 
the kafka test-containers dependency was added to the project but would be necessary to mock all the returns to make it effectively working,
instead for now it is in the project just to show as example as it could be used.

## Folder structure + important files

```bash
.
├── README.md                                   # Important! Read before changing configuration
├── build.gradle
├── settings.gradle
└── src
    ├── main
    │   ├── java                                # gRPC service
    │   ├── proto                               # contains the protocol buffer file
    │   └── resources
    │       ├── application.yml                 # Common application configuration runnning using docker configs
    │       └── application-LOCAL.yml           # Overriding configuration specifc to local environment
    └── test
        ├── java                                # Sample Testcases
        └── resources
            └── application-TEST.yml
```

## Database Migration (Setup to run with a standalone local instance of the database)

Mysql Docker locally [reference](https://dev.mysql.com/doc/mysql-installation-excerpt/8.0/en/docker-mysql-getting-started.html)
```
$ docker pull mysql/mysql-server:latest
```
```
$ docker run --name=mysql1 -e MYSQL_ROOT_HOST=% -p 3306:3306 -d mysql/mysql-server:latest
```
generate password
```
$ docker logs mysql1 2>&1 | grep GENERATED
```
access db using generated password
``` 
$ docker exec -it mysql1 mysql -uroot -p 
```
alter root password
```
mysql> ALTER USER 'root'@'%' IDENTIFIED BY 'password';
```
create the schema
```
mysql> create schema wallet;
```

To make sure the database is up to date, checkout the latest version of the project and from the root folder run:
```
$ ./gradlew migrateLocal
```

## External Tools Used

* [BloomRPC](https://appimage.github.io/BloomRPC/) - gRPC client
* [Kafka Tool](http://www.kafkatool.com/) - GUI application for managing and using Apache Kafka clusters 

## Testing the application

Now that the application is running, you can test it. The following examples use the tool `BloomRPC`. The file `Transaction.proto` located at
`/src/main/proto` can be imported in BloomRPC to test the calls. 

## `deposit` method

request:
{
  "userId": 1,
  "amount": 10,
  "currency": 2
}

response:
{}

Possible errors:
* Unknown currency
* Amount must be greater than 0.
* UserId is mandatory.

## `withdraw` method

request:
{
  "userId": 1,
  "amount": 10,
  "currency": 2
}

response:
{}

Possible errors:
* Unknown currency
* Insufficient funds
* Amount must be greater than 0.
* UserId is mandatory.

## `balance` method

request:
{ "userId": 1 }

response:
{
  "userId": "1",
  "gbpAmount": 0,
  "eurAmount": 0,
  "usdAmount": 0
}

Possible errors:
* User % not found

## Integration tests

Is necessary for now to have the docker-compose up, will be created for now some test-topics using the same kafka, but can be mocked also.
The database used during tests will me created only during the execution using the mysql test-container, so data will be disposed afterwards.

The server application has a very high test coverage, that guarantees that all the scenarios are covered.