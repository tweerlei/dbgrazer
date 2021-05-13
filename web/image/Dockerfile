#
# Build a Docker container that will run the application inside a Tomcat server.
# It will use the example configuration and make the example CSS files available via Tomcat's ROOT webapp.
# To keep the configuration across docker start/stop, mount a volume at /root/.dbgrazer
#
# Usage:
#   mvn -P <profiles> clean package
#   docker build -t dbgrazer .
#   docker run --name dbgrazer --rm -d -p <hostport>:8080 -v <hostpath>:/root/.dbgrazer dbgrazer
#
# For SSL support, replace examples/conf/server.jks with a keystore containing your certificate,
# alias 'tomcat', password 'changeit'.
#
#   docker run --name dbgrazer -p <hostport>:8443 -v <hostpath>:/root/.dbgrazer dbgrazer
#
# To set JVM options to e.g. use a custom SSL trust store, set the CATALINA_OPTS environment variable:
#
#   docker run --name dbgrazer -e CATALINA_OPTS=-Djavax.net.ssl.trustStore=/usr/local/tomcat/conf/trust.jks -p <hostport>:8443 -v <hostpath>:/root/.dbgrazer dbgrazer
# 

# Use Tomcat 9.0
FROM tomcat:9.0-jre8-alpine

# Install GraphViz and MS font installer
RUN apk add --no-cache graphviz msttcorefonts-installer

# Install fonts
RUN update-ms-fonts && fc-cache -f

# Enable SSL and copy certificate
COPY root/usr/local/tomcat/conf/* /usr/local/tomcat/conf/
COPY root/usr/local/bin/* /usr/local/bin/
RUN chmod +x /usr/local/bin/*

# Add required libraries
COPY target/*.jar /usr/local/tomcat/lib/

# Autodeploy webapp
COPY target/dbgrazer-web-*.war /usr/local/tomcat/webapps/dbgrazer.war

# Fix file permissions for running as unprivileged user
RUN chmod -R og+rX /usr/local/tomcat && \
	chmod -R og+w /usr/local/tomcat/logs && \
	chmod -R og+w /usr/local/tomcat/temp && \
	chmod -R og+w /usr/local/tomcat/webapps && \
	chmod -R og+w /usr/local/tomcat/work

# Add config directory
COPY examples /opt/dbgrazer
RUN chmod +x /opt/dbgrazer/bin/*

# Set Tomcat options
ENV CATALINA_OPTS -Xmx512m -XX:-OmitStackTraceInFastThrow -Ddbgrazer.configPath=/opt/dbgrazer -Duser.language=de -Duser.country=DE -Duser.timezone=Europe/Berlin

# Possibly run init.sh and launch Tomcat afterwards
CMD [ "/usr/local/bin/startup.sh" ]
