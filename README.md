# service-poller
    Make sure mvn/java 1.8 are installed before running this app

- From root directory:
  - mvn clean package
    - if you see some errors on this step, make sure you have node/npm installed
    - most probably the command should auto install it
  - cd target
  - java -jar service-poller-1.0.0-SNAPSHOT-fat.jar
    - it will run on the default port 5000
    - if you want a different port just append the port number 
      - java -jar service-poller-1.0.0-SNAPSHOT-fat.jar 5001
    - navigate to http://localhost:5000/ to see the app