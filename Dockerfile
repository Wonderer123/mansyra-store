# Build and run the Mansyra Java server
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY app ./app
COPY json-20240303.jar .
RUN javac -cp .:json-20240303.jar -d out app/*.java

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/out ./
COPY json-20240303.jar .
COPY static ./static
COPY newusers.json cart.json orders.json ./
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-cp", ".:json-20240303.jar", "app.Main"]
