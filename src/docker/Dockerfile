FROM openjdk:13-alpine3.9
VOLUME /tmp
ADD *.jar app.jar
RUN sh -c 'touch /app.jar'
ENV  LANG="en_US.UTF-8"
EXPOSE 8888
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
