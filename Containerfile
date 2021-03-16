# 1st image is for building purposes only
# this reduce the image size
FROM maven:3.6-jdk-11 AS build

# create app directory
WORKDIR /usr/app

# copy the source, cli scripts and pom
COPY src ./src
COPY scripts ./scripts
COPY pom-bootable.xml .

# build bootable jar
RUN mvn clean package -f pom-bootable.xml

# 2nd image as deploy
FROM postgres:13 AS deploy
LABEL autor="AMI Team1"

# set db env variables
ENV POSTGRES_PASSWORD postgres
ENV POSTGES_USER admin

# set local to german
RUN localedef -i de_DE -c -f UTF-8 -A /usr/share/locale/locale.alias de_DE.UTF-8
ENV LANG de_DE.utf8

# create app directory
WORKDIR /usr/app

# copy only the built bootable jar from the 1st image
COPY --from=build /usr/app/target/monolith-bootable.jar .

# copy sql init scripts
COPY ./scripts/sql/init /docker-entrypoint-initdb.d

# install jre
RUN apt-get update
RUN apt-get install default-jre -y

# set system properties
ENV DATA_GENERATOR_URL http://localhost:8081

# expose ports from wildfly and db
EXPOSE 8080
EXPOSE 9990
EXPOSE 5432