mvn clean compile quarkus:dev
java -cp target\quarkus-app\lib\main\com.h2database.h2-2.2.224.jar org.h2.tools.Server -tcp -web -ifNotExists
