FROM eclipse-temurin:24-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY ./target/NotificationManager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 4646
ENTRYPOINT ["java", "-jar", "app.jar"]
