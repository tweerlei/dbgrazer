FROM openjdk:8-jdk AS build

COPY mvnw* pom.xml /build/
COPY .mvn /build/.mvn
COPY pom /build/pom
COPY common /build/common
COPY main /build/main
COPY session /build/session
COPY web /build/web

WORKDIR /build
RUN ./mvnw -f pom clean install
RUN ./mvnw -P bookmark,configedit,c3p0,csv,dbunit,dml,file,http,jdbc,js,json,kafka,kubernetes,ldap,ldif,linkedit,mongodb,sql,template,text,tnsnames,useredit,wiki,xml clean package


# Use Tomcat 9.0
FROM tomcat:9.0-jre8-alpine

COPY --from=build /build/web/image/root /

# Install GraphViz and MS font installer
RUN apk add --no-cache graphviz msttcorefonts-installer && \
	update-ms-fonts && \
	fc-cache -f && \
	update-ca-certificates

# Add required libraries
COPY --from=build /build/web/image/target/*.jar /usr/local/tomcat/lib/

# Autodeploy webapp
COPY --from=build /build/web/image/target/dbgrazer-web-*.war /usr/local/tomcat/webapps/dbgrazer.war

# Fix file permissions for running as unprivileged user
RUN chmod -R og+rX /usr/local/tomcat && \
	chmod -R og+w /usr/local/tomcat/logs && \
	chmod -R og+w /usr/local/tomcat/temp && \
	chmod -R og+w /usr/local/tomcat/webapps && \
	chmod -R og+w /usr/local/tomcat/work

# Add state directory
VOLUME /var/opt/dbgrazer

# Set Tomcat options
ENV CATALINA_OPTS -Xmx512m -XX:-OmitStackTraceInFastThrow -Ddbgrazer.configPath=/opt/dbgrazer -Ddbgrazer.configFile=etc/config.properties -Duser.language=de -Duser.country=DE -Duser.timezone=Europe/Berlin

# Possibly run init.sh and launch Tomcat afterwards
CMD [ "/usr/local/bin/startup.sh" ]
