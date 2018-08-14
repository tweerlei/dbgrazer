#
# Build a Docker container that will run the application inside a Tomcat server.
# It will use the example configuration and make the example CSS files available via Tomcat's ROOT webapp.
# To keep the configuration across docker start/stop, mount a volume at /root/.dbgrazer
#
# Usage:
#   mvn -P <profiles> clean package
#   docker build -t dbgrazer .
#   docker run --name dbgrazer -p <hostport>:8080 -v <hostpath>:/root/.dbgrazer dbgrazer
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
ADD examples/conf/server.xml /usr/local/tomcat/conf/server.xml
ADD examples/conf/server.jks /usr/local/tomcat/conf/server.jks
ADD examples/conf/trust.jks /usr/local/tomcat/conf/trust.jks

# Autodeploy webapp
ADD target/dbgrazer-web-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/dbgrazer.war

# Add static resources
ADD examples/styles/*.css /usr/local/tomcat/webapps/ROOT/

# Setup config directory
ADD examples/*.properties /root/.dbgrazer/
ADD examples/themes/*.properties /root/.dbgrazer/themes/
