FROM paketobuildpacks/quarkus:latest

USER root
RUN apt-get update && apt-get install -y sudo

USER cnb
