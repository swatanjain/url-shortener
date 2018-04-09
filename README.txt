#####   SYSTEM REQUIREMENTS  #####
#
# Runs on almost all Windows and Linux distributions
#
# Requires Java 1.8 (8u60; http://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase8-2177648.html)
# Requires Apache Maven 3.3.9 (http://central.maven.org/maven2/org/apache/maven/apache-maven/3.3.9/apache-maven-3.3.9-bin.zip)
#
# Internet connection (with access to http://repo.maven.apache.org ). Proxy configuration might be required.
#
##################################

1. Download and unpack the url-shortener package from the following link: https://github.com/swatanjain/url-shortener/ by pressing the green link: "Clone or download"
2. Place the url-shortener folder in your home directory.
3. Open the Terminal application.
4. Type: "cd ~/url-shortener" and press return.
5. Type: "mvn clean install -X" and press return.
6. Type: "java -jar target/url-shortener-1.0-RELEASE.jar" and press return.
7. Now the server will try to boot. Wait for message "Started UrlShortenerApp in *.*** seconds"
8. Once up, all service end-points can be accessed via url: http://<host>:<port>/
9. Refer any of below URL for more details on service usage:
     http://<host>:<port>/help (local machine)
       Or
     https://swatanjain.github.io/url-shortener/ (github page)

Note: Application stores persistent data at "~/.urlshortener/data" directory.
