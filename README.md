# Mobility Choices Transportation Mode Detection
Transportation Mode Detection for Mobility Choices is a Java Project, which offers a REST-API and is able to recognize on the basis of coordinates which transportation vehicle was used to travel.

## Requirements
- Java 8
- MySql 5.7
- Payara (tested on 4 and 5)

## Installation of the Java-Project
1. create a java project
2. clone repository into the java project
```
git clone https://github.com/MobilityChoicesProject/mobility_choices_tmd .
```
3. open the project with your IDE (recommended is Intellij
https://www.jetbrains.com/idea/)
4. install all the maven dependencies
5. Configure the IP-Addresses for the Mobility Choices Server in the file `<ProjectFolder>/TMDServiceEndpoint/src/main/java/at/fhv/tmddemoservice/TMDService.java`.
We need both, the IP-Address and the DNS name, because otherwise some HTTPS-Certificates won't work.
6. build the WAR-File with the maven `install` command

## Installation and deployment of the WAR-File
This manual contains all steps to deploy the TMD service. It has been tested on a freshly installed Linux Mint and Ubuntu OS.

### Java
First a Java 8 Runtime is required. Since OpenJdk currently only requires a Java SE the following Oracle version must be used:
https://wiki.ubuntuusers.de/Java/Installation/Oracle_Java/Java_8/#PPA 

### JavaEE Server
Payara is recommended for the Java EE Server. Payara is based on Glassfish and can be configured via a web interface.
Payara can be downloaded from the following address:
https://www.payara.fish/downloads

### Database
The Database is used to store specific map-data (e.g. busstations, railwaystation, ...). The Data is fetched once per month from OpenStreetMap (https://www.openstreetmap.org)
#### MySql
Install a MySql Server (https://wiki.ubuntuusers.de/MySQL/)
A root user is already created during the installation.
After the installation you should activate the accessibility via the network.

#### Import schema and data
After the installation of the MySql server, the `data_gis` schema with the data contained in it can be imported. The tool MySql Workbench (https://www.mysql.com/products/workbench/) is suitable for this task.
The `data_gis.sql` file is located in your project folder compressed in a RAR-File `ProjectFolder/Database/data_gis.rar`.
Once you have connected to the MySql server using the MySql Workbench, you can import the supplied `gis_data.sql` file. You can find the option under "Server/Data import"


After clicking on import you have to choose `Import from Self-Contained File` and specify the path to the file. A Default Target Schema must not be specified, since the `gis_data` schema is used anyway. It is important that the
`Dump Structure and Data` setting is selected before clicking `Start import`.

After the import, the database is ready for the Tmd service.

### Set up Payara 
The following Payara instruction was made with Payara 4. The TMD Service is also runnable on Payara 5, but some configuration steps might be a bit different.
#### MySql Java Connector
In order for Payara to establish a connection with the MySql server, the java Mysql connector is required.
This can be downloaded from the following website.
https://dev.mysql.com/downloads/connector/j/5.1.html

After the connector is downloaded, the contained jar files have to be copied into the `lib` directory of the used Payara domain.
You can read further information about the connector on this page
https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-usagenotes-glassfish-config.html

#### JDBC Connection Pool
The Connection Pool can easily be created via the Web Configurator. For this you have to start Payara and then go to the following website:
http://localhost:4848

You can start the payara server with a terminal. Go to the folder where you installed payara and open the `bin` folder. For example `C:\payara5\bin`
Open the `asadmin` file and run the following command: `start-domain domain1`. `Domain1` is the default domain, which gets created automatically.

After the mentioned command the payara server should be running and you can go to the website
http://localhost:4848
and see the Web Configurator.

Once you are in the Web Configurator, go to the page `Resources/JDBC/JDBC Connection Pools` and click on `New` and type in the following information.

- Pool Name: <Poolname> (e.g. MySqlPool)
- Ressource Type: ConnectionPoolDataSource
- Database Driver Vendor: MySql
- Introspect = false

Then you can click `Next` and set the following three properties:

- URL = jdbc:mysql://&lt;adresse>:&lt;port>/gis_data
(e.g. jdbc:mysql://127.0.0.1:3306/gis_data)
- User = root
- Password = &lt;password for root>

Instead of the root user you can of course use another MySQL user. This user should have all necessary permissions.
It is best to give him all `Object Rights` for the gis_data schema.

After the three properties have been set, the pool can be created by clicking on `Finnish`.
With the `Ping` button you can check if the pool works without errors.

The pool must be specified as a JDBC resource so that it can be used within the application. To do this, change to `Resources/JDBC/JDBC Resources`.
Here you select `jdbc/__default` and replace the previous pool name with the one you created before (e.g. MySqlPool).

#### Thread Pool Configuration
Another setting that needs to be changed on the Payara server is the core size value of the default ManagedScheduledExecutorService.
To do this, go to the page `Resources/Concurrent Resources/Managed Scheduled Executor Services/'concurrent/__defaultManagedScheduledExecutorService'`
and change the &lt;Core Size> value from 0 to 5.

#### Increase maximum heap size
In order to avoid an OutOfMemoryException for large requests, the maximum heap size should be increased for security reasons.
To do this, switch to the JVM General Settings: `Configurations/server-config/JVM Settings`.
On this settings page you switch to the JVM Options tab. You should now find a `-Xmx*` property in the properties. This property should be rewritten to
`-Xmx2048m`, which increases the maximum heap size to 2 GB.

#### Default Timeout
The Payara server has a default timeout of 900 seconds (15 minutes).
However, it can happen with very large tracks that the evaluation takes longer than 15 minutes,
so that no interrupted exception is thrown, the timeout value must be increased.

At Configuration → Server-Config → Network Config → http-listener → Http-Tab the timeout value can be set.
This value should be set to 3600, so that the thread is only interrupted after 60 minutes.

### Deployment
After all settings are configured, you should restart the payara server.
Now you can deploy the built WAR-File. The WAR-File is in your java project under the following path `ProjectFolder/TMDServiceEndpoint\target\TMDService-1.3.19.war`.

In order to deploy the WAR-File go to `Applications` and click on the `Deploy...` Button. Then select the WAR-File and change the Context Root to `/TMDService`.
Now you can click `OK` in the upper right corner.

If everything was sucessfully, the TMD-Server should be up and running.

To test if the service is running correctly you can either go to the webinterface `http://localhost:8080/TMDService/` or go to the page
`127.0.0.1:8080/TMDService/rest/TMD_Service/ping` and see if you get a successful response.

### Configuration of the TMD-Service
You can configure a couple of parameters on the TMD-Webinterface.

The Webinterface is available under the URL `http://localhost:8080/TMDService/`.

The default credentials for the TMD-Webinterface are:
```
Username: tmd_admin
Password: S\KZOW+WCwkeFDZZS\.uS<<"l#![p[
```

### REST - API
The communication with the TMD-Server is over an REST api. The API offers the following interfaces:

#### Ping
```
Path: /TMD_Service/ping
Method: GET
Returns: String if the ping was sucessful
```

#### Merge Segments
```
Path: /TMD_Service/mergeSameSegments
Method: POST
Body: Array of SegmentEntity as JSON
Returns: Array of SegmentEntity as JSON
```

#### Classify Queue
```
Path: /TMD_Service/classifyQueue
Method: POST
Body: RequestEntity as JSON
Returns: String which tells you if the Track was successfully enqueued in the queue
```
The Classify Queue method directly sends a response that the Track was successfully received. After the track is
classified another request is opened to the NodeJS-Server and the classified track is sent in the body.

#### Classify
```
Path: /TMD_Service/classify
Method: POST
Body: RequestEntity as JSON
Returns: ResponseEntity as JSON
```
The Classify method classifies the Track, the connection stays open until the classification is finished.
This can result in very long requests. This method is mostly used for testing purposes. 
For the production environment the `Classify Queue` interface should be used.

## Licence 
``` 
https://www.gnu.org/licenses/agpl-3.0 
```
