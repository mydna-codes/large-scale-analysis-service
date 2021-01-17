FROM openjdk:11-jre-slim

ENV KUMULUZEE_VERSION=not_set
ENV KUMULUZEE_ENV_NAME=dev
ENV KUMULUZEE_ENV_PROD=false

RUN mkdir /app
WORKDIR /app

ADD ./api/target/large-scale-analysis.jar /app

EXPOSE 8080

CMD ["java", "-jar", "large-scale-analysis.jar", "com.kumuluz.ee.EeApplication"]