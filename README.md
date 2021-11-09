# Service Poller
    Make sure mvn/java 1.8 are installed before running this app

##Running the application - three options

###1. Already online here: https://service-poller.herokuapp.com/
  - Probably it will take a couple of seconds for the app to start up first time
###2. Starting locally directly from the jar that's already build
  
- Download the jar file from
- `service-poller\src\main\resources\service-poller-1.0.0-SNAPSHOT-fat.jar`
- After navigating to download directory just run
- `java -jar service-poller-1.0.0-SNAPSHOT-fat.jar`
  - that will start the web app/backend on default: `http://localhost:5000/`
  - simply open a web browser on `http://localhost:5000/` and check the app
  - note that it will use a remote mysql database on `db4free.net`
- You could also add webAppPort, dbhostname and dbport to the jar args:
  - `java -jar service-poller-1.0.0-SNAPSHOT-fat.jar 5001 localhost 3306`
  - If you run the app like this, please run before the `sql` from `service-poller\src\main\resources\databaseSetup.sql` on a local mysql database

###3. Build the app

- From root directory:
  - mvn clean package
    - if you see some errors on this step, make sure you have node/npm installed
    - most probably the command should auto install it
  - run the sql from `service-poller\src\main\resources\databaseSetup.sql` on a local mysql database. It will auto create user,db and some data 
  - cd target
  - `java -jar service-poller-1.0.0-SNAPSHOT-fat.jar 5000 localhost 3306`
    - web app will run on the default port `5000` using a `localhost` `mysql` instance on port `3306`
    - navigate to http://localhost:5000/ to see the app