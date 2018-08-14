# dbgrazer
DBGrazer is a Java/Spring-based web application to visualize database structures and content.

It is licensed under the Apache License Version 2.0, see LICENSE.

To build, first install the parent POMs:
	
	mvn -f pom clean install

Then simply use

	mvn -P <profiles> clean package

where the given profiles specify the plugins to include in the created .war:

	bookmark	Enable user bookmarks
	c3p0		Enable connection pooling using the C3P0 connection pool
	csv			Enable exporting results as CSV
	dbunit		Enable exporting results as DBUnit dataset
	dml			Enable exporting results as INSERT or MERGE statements
	file		Query the local filesystem for directory listings and file contents
	http		Query HTTP servers
	jdbc		Query databases using JDBC
	js			Enable exporting results as JSON
	json		Enable formatting results as JSON
	kafka		Query Apache Kafka servers for topics and messages
	linkedit	Enable the web-based backend link editor
	sql			Enable formatting results as SQL
	tnsnames	Enable exporting/importing link URLs in TNSNames.ora format
	useredit	Enable the web-based user editor
	wiki		Enable formatting results as Wiki pages (Creole syntax)
	xml			Enable formatting results as XML

For installation instructions see web/core/README.txt


Copyright 2018 tweerlei Wruck + Buchmeier GbR - http://www.tweerlei.de/
