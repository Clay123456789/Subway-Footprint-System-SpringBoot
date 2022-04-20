FROM openjdk:13-alpine3.9
VOLUME /tmp
ADD *.jar app.jar
RUN sh -c 'touch /app.jar'
ENV  LANG="en_US.UTF-8"
EXPOSE 8888
#解决时区问题
ENV TZ Asia/Shanghai
RUN ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
