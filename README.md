# Spring Boot Custom OAuth Server

## Technology
 * Apache CXF (JAX-RS)
 * Spring Boot (MVC)
 * Spring Framework (DI)
    * All beans are singleton by default
 * Jackson (JSON)
 * Logback (Logging)
 * Hibernate (JPA)
 * Swagger2 (Documentation)
 * JUnit (Testing)
 * Lombok (Syntactic Sugar)
 * Mapstruct (Compile-time mapping)
 * AspectJ (AOP Compile Time Weaving)
 * Spring Security (Resource Server + Authorization Server)
    * https://spring.io/guides/tutorials/spring-boot-oauth2/

## Specifications 
   * OAuth 2.0
   
## IDE Setup
* Install Plugins
    * Plugins to Install
        * Lombok plugin for IntelliJ
        * Mapstruct plugin for IntelliJ
        
* Automatic Restarts
    * This project has springloaded (devtools) added as part of the Maven plugins. This allows an application to be restarted automatically after changes are made.
    * In order for this to work in IntelliJ you must
        * Turn on Automatic Builds 
            * Go into Preferences. Find Build, Execution, Deployment --> Compiler 
            * Select the checkbox with Build Project Automatically. 
            * Click Ok.
        * Enable Automake when app is running
            * Type ```Command + Shift + A``` and type ```registry```.
            * Find and enable the property ```compiler.automake.allow.when.app.running```

* Database
    * In Memory - H2 Database
    
