# Gidd api
## Getting started

This project requires java jdk 11 and maven.

Clone the repository.
```
git clone https://gitlab.stud.idi.ntnu.no/idatt2016-scrumteam-7/idatt2106-2021-7-be.git
```
Add a the file application.properties under src/main/resources. The file should contail the following:
```
server.port=8081

api.secret=<secretJwtValue>
security.salt=<secretSaltValue>

logging.level.root=INFO
logging.level.no.ntnu.idatt210620217.giddapi=TRACE

spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://mysql-ait.stud.idi.ntnu.no/<username>
spring.datasource.username=<username>
spring.datasource.password=<password>

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<username/email>
spring.mail.password=<password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Run api.
```
cd idatt2106-2021-7-be
mvn exec:java -Dexec.mainClass="no.ntnu.idatt210620217.giddapi.GiddApiApplication"
```
Or use ide to run api.
