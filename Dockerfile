FROM openjdk:11
MAINTAINER Pablo Edibaldo <alfaroedibaldo@gmail.com>
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
