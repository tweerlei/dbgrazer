FROM openjdk:8-jdk AS build

COPY mvnw* pom.xml /build/
COPY .mvn /build/.mvn
COPY pom /build/pom
COPY main /build/main
COPY session /build/session
COPY web /build/web

WORKDIR /build
RUN ./mvnw -f pom clean install
RUN ./mvnw -P bookmark,configedit,c3p0,csv,dbunit,dml,file,http,jdbc,js,json,kafka,kubernetes,ldap,ldif,linkedit,mongodb,sql,template,text,tnsnames,useredit,wiki,xml clean package


# Use Tomcat 9.0
FROM tomcat:9.0-jre8-alpine

# Install GraphViz and MS font installer
RUN apk add --no-cache graphviz msttcorefonts-installer && \
	update-ms-fonts && \
	fc-cache -f

# Enable SSL and copy certificate
COPY --from=build /build/web/image/root/usr/local/tomcat/conf/* /usr/local/tomcat/conf/
COPY --from=build /build/web/image/root/usr/local/bin/* /usr/local/bin/

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

# Add config directory
COPY --from=build /build/web/image/root/opt/dbgrazer /opt/dbgrazer

# Add state directory
COPY --from=build /build/web/image/root/var/opt/dbgrazer /var/opt/dbgrazer
VOLUME /var/opt/dbgrazer

# Set Tomcat options
ENV CATALINA_OPTS -Xmx512m -XX:-OmitStackTraceInFastThrow -Ddbgrazer.configPath=/opt/dbgrazer -Duser.language=de -Duser.country=DE -Duser.timezone=Europe/Berlin

# Possibly run init.sh and launch Tomcat afterwards
CMD [ "/usr/local/bin/startup.sh" ]
