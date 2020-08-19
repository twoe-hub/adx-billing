FROM openjdk:8-alpine

COPY target/uberjar/adx-billing.jar /adx-billing/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/adx-billing/app.jar"]
