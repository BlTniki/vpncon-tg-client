#!/bin/sh
mkdir BOOT-INF
mkdir BOOT-INF/classes
echo "server.port = ${SERVER_PORT}" >> BOOT-INF/classes/application.properties
echo "tg.botToken = ${TG_BOT_TOKEN}" >> BOOT-INF/classes/application.properties
echo "tg.botUsername = ${TG_BOT_USERNAME}" >> BOOT-INF/classes/application.properties
echo "VPNconServer.address = ${VPNCONSERVER_ADDRESS}" >> BOOT-INF/classes/application.properties
echo "VPNconServer.domain = ${VPNCONSERVER_DOMAIN}" >> BOOT-INF/classes/application.properties
echo "VPNconServer.login = telegramBot" >> BOOT-INF/classes/application.properties
echo "VPNconServer.password = ${VPNCONSERVER_PASSWORD}" >> BOOT-INF/classes/application.properties
jar uf vpncon.jar BOOT-INF/classes/application.properties
exec java -jar vpnconbot.jar