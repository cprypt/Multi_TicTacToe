mkdir -p out
javac -d out server/*.java client/*.java
jar cvfm TTTServer.jar server-manifest.txt -C out server
jar cvfm TTTClient.jar client-manifest.txt -C out client