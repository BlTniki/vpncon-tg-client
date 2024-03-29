FROM openjdk:17-jdk-alpine
COPY build/libs/VPNconTGclient*SNAPSHOT.jar vpnconbot.jar
COPY ./init.sh init.sh
RUN chmod 777 init.sh
ENV SERVER_PORT ${SERVER_PORT}
ENV TG_BOT_TOKEN ${TG_BOT_TOKEN}
ENV TG_BOT_USERNAME ${TG_BOT_USERNAME}
ENV VPNCONSERVER_ADDRESS ${VPNCONSERVER_ADDRESS}
ENV VPNCONSERVER_DOMAIN ${VPNCONSERVER_DOMAIN}
ENV VPNCONSERVER_PASSWORD ${VPNCONSERVER_PASSWORD}
ENTRYPOINT ["./init.sh"]