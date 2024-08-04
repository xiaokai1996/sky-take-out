# about this sky-take-out project
- https://www.bilibili.com/video/BV1TP411v7v6/
- this is a very simple single spring boot projects
- there is no spring cloud, service register and discover, microservice, gateway
- only use mysql and redis, no mq/kafka/es/mongo
- maven projects, rely on some basic dependencies

# 1. front end deploy
- copy all contents in sky.zip to nginx `html` folder
- edit configuration file `nginx.conf` in nginx `conf` folder, or replace the config with the one in repo
- in nginx folder, type `nginx start` to start the nginx
- visit `http://localhost:80`, notice that the port number is 80 rather than 8080
**tips**'
- download [nginx](http://nginx.org/en/download.html) and unzip to a project space
- may need to open task-manger to kill the existed auto-start running nginx first
- type `./nginx.exe -s stop/reload` to stop/restart the nginx for config refreshing

# 2. mysql install and database init
- create database called `sky_take_out`
- execute `sky.sql` in `database` table to create all tables and prepare data
**tips**
- install [mysql8](https://dev.mysql.com/downloads/mysql/) on localhost or in Docker
- use datagrip or mysql bench or `mysql -uroot -p` to test mysql connection
- if mysql cmd is not found, add `C:\Program Files\MySQL\<your_version>` to the system env path (windows)
- currently mysql5.7 use port 3306, mysql8 use port 3307, password is 12345678
- check user and password carefully in `application.yml` or `application-dev.yml`

# 3. config maven
- download a specific [maven](https://maven.apache.org/download.cgi) version and copy to project space (suggested)
- open idea settings, specify the maven `settings.xml` and dependency repository location
- new a new folder `maven-repo` in project space and specify maven repo here
**tips**
- refresh the `pom.xml` to download all dependencies, if version deprecated, visit [mvnrepository](https://mvnrepository.com/) and update

# 4. config JDK
- download jdk from oracle/idea if needed
- right click on project folder name and set module jdk

# 5. run the backend and test
- open http://localhost:80 to use front end pages to test
- open postman to test some api

# 6. common bugs
- nginx visit url is different from that in code: edit nginx.conf to add route
- jdbc connection error: switch to mysql8 or update the password
- login error, change admin pswd in `employee` table to md5 `e10adc3949ba59abbe56e057f20f883e`
- maven import error, may need to update the version