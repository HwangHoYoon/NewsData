FROM eclipse-temurin:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} newdata.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java", "-jar","/newdata.jar"]