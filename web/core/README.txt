Installation instructions
=========================

Put JDBC driver JAR files into your app server's lib directory OR add them to the WAR by dropping them into src/main/webapp/WEB-INF/lib.

If needed, adjust networkaddress.cache.ttl and networkaddress.cache.negative.ttl in <JDK home>/jre/lib/security/java.security.

Default path for configuration files is <HOME of app server OS user>/.dbgrazer/.
If you want to specify a different configuration, add
  -Ddbgrazer.configPath=/path/to/configfiles
Relative paths will be resolved against the configPath, so the default main configuration file is <dbgrazer.configPath>/config.properties.

If you just want to use a different main configuration file, add
  -Ddbgrazer.configFile=/path/to/configfiles/config.properties
You can then specify explicit locations for all other files there.

Default layout of the configuration files is:

.dbgrazer/
	config.properties		Main configuration file (see examples/config.properties)
	themes/
		*.properties		Appearance settings for themes
	users/					User configuration files for the file-based userLoaders
	profiles/				User's customizable settings for the file-based userLoaders
	links/					Link configuration files for the file-based connectionLoaders
	schemas/				Schema-specific query definition files for the file-based queryLoaders
	dialects/				Dialect-specific query definition files for the file-based queryLoaders


Tomcat
------

Tomcat does not include the JSTL. You have to add jstl-1.2.jar the same way as the JDBC driver JARs.


Glassfish
---------

Default HTTP request timeout for Glassfish is 900 sec, if needed increase via
  <http request-timeout-seconds="3600" ...>

Add needed SSL root certificates to domain/config/cacerts.jks using Java's keytool (default password is "changeit").


Localization support
--------------------

You can customize the builtin messages by setting dbgrazer.web.messagesFile to the base name of your message file (see examples/messages.properties for frequently used messages).

You can add a new locale (e.g. yourlocale) by localizing all messages from session/src/main/resources/de/tweerlei/dbgrazer/web/messages.properties to messages_yourlocale.properties.
For localized JavaScript messages, you'll have to localize src/main/webapp/scripts/locale.js, put it into your app server's docroot and specify its location as localeJS in messages_yourlocale.properties. 


Theme support
-------------

You can add a new theme by adding the theme base name (e.g. yourtheme) to the dbgrazer.web.themeNames configuration property. The name of the builtin theme, in case you want to keep it, is "theme".
Localized versions of the theme name are read from your localization file (see above) property theme_yourtheme. 
The theme specific properties are looked up in <dbgrazer.web.themePath>/yourtheme.properties (see examples/themes).

Remember that CSS and image files for themes have to be put somewhere into your app server's docroot to be accessible. The actual CSS file location must be specified as appStyle in yourtheme.properties.


REST API
--------

The JDBC web plugin has a REST API, see web/plugins/jdbc-web/examples.
The example scripts use GNU wget; you can tune this with your local .wgetrc file.
