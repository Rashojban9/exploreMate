FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9091

# Run the application with JVM memory limits optimized for 512MB container
# -Xmx192m: Max heap 192MB
# -Xms128m: Initial heap 128MB
# -XX:MaxMetaspaceSize=64m: Limit metaspace
# -XX:+UseG1GC: Use G1 garbage collector for smaller footprint
ENTRYPOINT ["java", "-Xmx192m", "-Xms128m", "-XX:MaxMetaspaceSize=64m", "-XX:+UseG1GC", "-jar", "app.jar"]
