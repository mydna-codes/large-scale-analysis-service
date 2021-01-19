FROM openjdk:11-jre-slim

ENV KUMULUZEE_VERSION=not_set
ENV KUMULUZEE_ENV_NAME=dev
ENV KUMULUZEE_ENV_PROD=false

RUN mkdir /app
WORKDIR /app

ADD ./business-logic/target /app

EXPOSE 8080

CMD ["java", "-cp", "classes:dependency/*", "com.kumuluz.ee.EeApplication"]