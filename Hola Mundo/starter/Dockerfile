# Este Dockerfile usa "multi-stage builds" para aligerar drásticamente
# el tamaño de la imagen en ejcución, incluyendo Maven sólo para compilar
# Ver https://docs.docker.com/develop/develop-images/multistage-build/)

# Fase de construcción
FROM maven:3.6.3-openjdk-17 AS build

# Copiar código fuente y manifiesto pom.xml
WORKDIR /app
COPY src ./src
COPY pom.xml .

# Construir paquete
RUN mvn package -DskipTests;

# Fase de ejecución
FROM openjdk:17-alpine

#Bajar fonts
#RUN apk add --update ttf-dejavu

ENV SERVER_PORT 5000

ENV JAR_FILE starter-1.0.0-SNAPSHOT-fat.jar

USER root
RUN echo "America/Mexico_City" > /etc/timezone

COPY --from=build /app/target/$JAR_FILE .

EXPOSE 5000

# Se usa "sh -c" para sortear esta limitante: https://github.com/docker/docker/issues/5509 - variable not expanded
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar $JAR_FILE -id starter --redirect -output"]

