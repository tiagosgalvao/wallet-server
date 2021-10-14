# gRPC Wallet Server Application

Me, Tiago Galvao hereby declare: that this code is authentic created by my own authorship, utilizing the listed technologies below. 
It might or not have also been resulted from for some proof of concept (Poc), personal purposes or even selection process, although the company(ies) 
involved won`t be disclosed.
Probably there is a chance that during the development process some references, tutorials, videos from the web or books supported me in order to support 
best practices resulting in a good application.
 
## Some commonly utilized references:

- [Spring](https://docs.spring.io/spring/docs/current/spring-framework-reference/)
- [Baeldung](https://www.baeldung.com/spring-tutorial)

## Requirements

For building and running the application you need:

- [Spring Boot](http://projects.spring.io/spring-boot)
- [JDK 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Gradle](https://gradle.org)

## Introduction
This guide walks you through the process of creating an application that accesses relational JPA data through gRPC calls.

## About the application

This is a wallet server application that will keep the track of a users monetary balance in the system. Storing separate balance for different currencies.
There are three main features: `Deposit` `Withdraw` `Balance`

## Application Requirements (business rules)
* The wallet server will keep track of a users monetary balance in the system. There should be separate balance for different currencies.

* The wallet server must expose the interface described below via gRPC.

Interfaces

##Deposit

Deposit funds to the users wallet.

```Input```

• User id

• Amount

• Currency (allowed values are EUR, USD, GBP)

```Output```

• No output needed Errors

• Unknown currency Withdraw

##Withdraw

Withdraw funds from the user wallet.

```Input```

• User id

• Amount

• Currency (allowed values are EUR, USD, GBP)

```Output```

• No output needed Errors

• Unknown currency, insufficient funds Balance

##Balance

Get the users current balance.

```Input```

• User id Output

• The balance of the users account for each currency

##Integration Test
```
1. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
2. Make a deposit of USD 100 to user with id 1.
3. Check that all balances are correct
4. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
5. Make a deposit of EUR 100 to user with id 1.
6. Check that all balances are correct
7. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
8. Make a deposit of USD 100 to user with id 1.
9. Check that all balances are correct
10. Make a withdrawal of USD 200 for user with id 1. Must return "ok".
11. Check that all balances are correct
12. Make a withdrawal of USD 200 for user with id 1. Must return "insufficient_funds".
```

## Programming Languages

This project is authored in Java.

## Dependencies

*   [Apache Kafka](https://kafka.apache.org/) - Distributed streaming platform
* 	[Flyway](https://flywaydb.org/) - Version control for database
* 	[Git](https://git-scm.com/) - Free and Open-Source distributed version control system
* 	[Lombok](https://projectlombok.org/) - Never write another getter or equals method again, with one annotation your class has a fully featured builder, 
Automate your logging variables, and much more
* 	[CockroachDB](https://www.cockroachlabs.com/) - Highly evolved database
* 	[Spring Boot](https://spring.io/projects/spring-boot) - Framework to ease the bootstrapping and development of new Spring Applications
* 	[TestContainers](https://www.testcontainers.org/) - Lightweight, throwaway instances of common databases

## Running the application locally (pre-req)

At first, you will need some support applications running such as event streaming platform(Apache Kafka) and the database(CockroachDB).
To make it easy as test environment you can use the docker-compose file available in the project.
*   [Docker-compose](https://docs.docker.com/compose/gettingstarted/) - Docker Compose first steps - just need to execute the file `docker-compose.yml`
* Command: `docker-compose up -d` (root folder)

* Creating wallet-server_cockroachdb_1 ... done
* Creating wallet-server_zookeeper_1   ... done
* Creating wallet-server_kafka_1       ... done
* Creating wallet-server_schema-registry_1 ... done
* Creating wallet-server_ksqldb-server_1      ... done
* Creating wallet-server_schema-registry-ui_1 ... done
* Creating wallet-server_kafka-rest-proxy_1   ... done
* Creating wallet-server_kafka-topics-ui_1    ... done
* Creating wallet-server_ksqldb-cli_1         ... done

Instead of using docker another option could be point the wallet server to standalone zookeeper, kafka and mysql servers, just changing the configurations at
application.yml

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. 
One way is to execute the `main` method in the`com.galvao.wallet.WalletApplication` class from your IDE

NB! to run locally local profile must be set

using command line `-Dspring.profiles.active=local`

Alternatively you can use the [Spring Boot Gradle plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/html/) like so:

```shell
./gradlew bootRun -Dspring.profiles.active=local 
```

* compiles Java classes to the /target directory
* copies all resources to the /target directory
* starts an embedded Apache Tomcat server

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
    │       └── application-local.yml           # Overriding configuration specifc to local environment
    └── test
        ├── java                                # Sample Testcases
        └── resources
            └── application-test.yml
```

## Database - Flyway Migration

To bootstrap a cockroach db
Run built-in sql client:
```
docker-compose exec cockroachdb ./cockroach sql --insecure
```

Run in CLI:
```
CREATE DATABASE wallet;
```
```
CREATE USER app_user;
```
```
GRANT ALL ON DATABASE wallet TO app_user;
```
```
SHOW USERS;
```

## External Tools Used

* [BloomRPC](https://appimage.github.io/BloomRPC/) - gRPC client
* [Kafka UI](http://localhost:9094/) - UI application for query kafka topics using schema registry

## Testing the application

Now that the application is running, you can test it. 
The following examples use the tool `BloomRPC`. Import the file `Transaction.proto` located at
`/src/main/proto` in BloomRPC to test the calls. 

```
BloomRPC URL: localhost:9090
```

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

It's necessary for now to have the docker-compose up, it will be created for now some test-topics using the same kafka, but can be mocked also.
The database used during tests will be created only during the execution using cockroachDB test-container, therefore the data will be disposed afterwards.

The server application has a very high test coverage, that guarantees that all the scenarios are covered.