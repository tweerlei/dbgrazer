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
COPY examples/conf/server.xml /usr/local/tomcat/conf/server.xml
COPY examples/conf/server.jks /usr/local/tomcat/conf/server.jks
COPY examples/conf/trust.jks /usr/local/tomcat/conf/trust.jks

# Add required libraries
COPY target/*.jar /usr/local/tomcat/lib/

# Autodeploy webapp
COPY target/dbgrazer-web-*.war /usr/local/tomcat/webapps/dbgrazer.war

# Setup config directory
COPY examples/*.properties /root/.dbgrazer/
COPY examples/themes/*.properties /root/.dbgrazer/themes/

# Add static resources
COPY examples/static/*.css /root/.dbgrazer/static/
#COPY examples/static/*.png /root/.dbgrazer/static/

# Set Tomcat options
ENV CATALINA_OPTS -Xmx512m -XX:-OmitStackTraceInFastThrow -Duser.language=de -Duser.country=DE -Duser.timezone=Europe/Berlin
