# Service Poller - vertx project

## Running the application - 3 options

### 1. Already online here: https://service-poller.herokuapp.com/
  - Probably it will take a couple of seconds for the app to start up first time
### 2. Starting locally directly from the jar that's already build
- Make sure java 1.8 is installed and added into classpath 
- Download the jar file from https://github.com/ioanvranau/service-poller/raw/main/src/main/resources/service-poller-1.0.0-SNAPSHOT-fat.jar
- After navigating to download directory just run
- `java -jar service-poller-1.0.0-SNAPSHOT-fat.jar`
- wait for `Routes for existing urls has been created` message
  - that will start the web app/backend on default: `http://localhost:5000/`
  - simply open a web browser on `http://localhost:5000/` and check the app
  - note that it will use a remote mysql database on `db4free.net`
- You could also add webAppPort, dbhostname and dbport to the jar args:
  - `java -jar service-poller-1.0.0-SNAPSHOT-fat.jar 5001 localhost 3306`
  - wait for `Routes for existing urls has been created` message
  - If you run the app like this, please run before the `sql` from `service-poller\src\main\resources\databaseSetup.sql` on a local mysql database

### 3. Build the app
- Make sure java 1.8/maven is installed and added into classpath 
- If for some reason node/npm/yarn aren't installed by maven try to install them manually
- From root directory:
  - `mvn clean package`
  - run the sql from https://github.com/ioanvranau/service-poller/blob/main/src/main/resources/databaseSetup.sql on a local mysql database.
    - It will auto create the user, database and it will add some data.
  - cd target
  - `java -jar service-poller-1.0.0-SNAPSHOT-fat.jar 5000 localhost 3306`
  - wait for `Routes for existing urls has been created` message
    - web app will run on the default port `5000` using a `localhost` `mysql` instance on port `3306`
    - navigate to http://localhost:5000/ to see the app

### 4. Some details about the functionality
- This app will display a couple of user added urls and will do polling on each service.
- We will have a option to start polling for all services, and also s slider to increase/decrease the refresh rate
    - we could also start/stop each service poll individually
- All the statuses are randomly generated but persisted each time the frontend will do a `GET` into stats table that could be used later.
- Services could be added/removed/updated.

### 5. Some details about the implementation
- Technologies used: `java`, `maven`, `vertx`, `react`, `css`, `mysql`
- This is my very first vertx project so I could improve a lot
- Backend it has a model for service (`ServiceUrl`), a `UrlService` for functionality and 2 repositories(for ServiceUrl and service stats)
- Also `MainVerticle` it is used for the vertx app where we define the routes and database connection setup
- Frontend is build based on `react-hooks` and it is located in `frontend` folder
  - `App.js` is the actual application where most of the actions are added. Displaying the cards, add/remove options. Start/Stop all services.
  - `Api.js` has all the api related stuff
  - `UrlCard` is a simple card that can do polling for the path that it is assigned to. Also could be deleted directly.
  - We have also some css files for all the styles in the app
