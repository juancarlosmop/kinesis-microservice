FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl
VOLUME /tmp
EXPOSE 8080
ADD target/produce-kinesis.jar produce-kinesis.jar
ENTRYPOINT ["java","-jar","/produce-kinesis.jar"]