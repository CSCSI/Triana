FROM keyz182/ubuntu-lxde-novnc

RUN apt-get update \
    && apt-get install -y --force-yes tzdata wget java-common \
    && apt-get autoclean \
    && apt-get autoremove \
    && rm -rf /var/lib/apt/lists/*

RUN wget https://cdn.azul.com/zulu/bin/zulu8.86.0.25-ca-jdk8.0.452-linux_amd64.deb
RUN apt install ./zulu8.86.0.25-ca-jdk8.0.452-linux_amd64.deb
ADD triana-app/dist /triana/
ADD Docker/triana.supervisor.conf /etc/supervisor/conf.d/
ADD Docker/50-Triana-Copy.sh /etc/startup.aux/
RUN chmod +x /etc/startup.aux/50-Triana-Copy.sh

WORKDIR /
ENTRYPOINT ["/startup.sh"]
