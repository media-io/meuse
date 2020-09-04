FROM clojure:openjdk-11-lein as build-env

ADD . /app
WORKDIR /app

RUN lein uberjar

# -----------------------------------------------------------------------------

FROM openjdk:11

RUN groupadd -r meuse && useradd -r -s /bin/false -g meuse meuse
RUN mkdir /app
COPY --from=build-env /app/target/uberjar/meuse-*-standalone.jar /app/meuse.jar

RUN chown -R meuse:meuse /app

RUN apt-get update && \
    apt-get -y upgrade && \
    apt-get install -y git && \
    useradd --create-home meuse

USER meuse

ENTRYPOINT ["java"]

CMD ["-jar", "/app/meuse.jar"]
