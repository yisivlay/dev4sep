# DEV4Sep: A Platform for RESTful API and Microservice.
Microservice with Spring Boot 2.6.4, Gradle 7.3.2 and Java17

## Requirements
* Spring Boot 2.6.4
* Gradle 7.3.2
* Java >= 17
* Tomcat 9.0.58
* MySQL Connector 8.0.28

## SECURITY
NOTE: The HTTP Basic and OAuth2 authentication schemes are mutually exclusive. 
You can't enable them both at the same time. 
Checks these settings on startup and will fail if more than one authentication scheme is enabled.

### HTTP Basic Authentication Configuration
By default it is configured with a HTTP Basic Authentication scheme, so you actually don't have to do anything if you want to use it. 
But if you would like to explicitly choose this authentication scheme then there are two ways to enable it:

Open: application.properties to change it:
```
    spring.profiles.active=basicauth
```

## API Documentation
### Request HTTP Basic Authentication
> #### Headers 
> DEV4Sep-Platform-TenantId: default
> <br>
> Content-Type: application/json
> <br>
> <br>
> username: admin
> <br>
> password: admin@4123!
* POST:https://localhost:8444/dev4sep/api/v1/authentication?username=admin&password=admin@4123!