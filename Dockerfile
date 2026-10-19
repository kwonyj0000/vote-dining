FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY Server.java .
COPY web/ web/
RUN javac Server.java
EXPOSE 8080
CMD ["java", "Server"]
