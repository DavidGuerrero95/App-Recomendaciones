FROM openjdk:12
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} Recomendaciones.jar
ENTRYPOINT ["java","-jar","/Recomendaciones.jar"]