ssh -p 2222 -L 8080:localhost:16250 s465635@helios.cs.ifmo.ru
httpd -f ~/httpd-root/conf/httpd-conf.conf -k start
pkill httpd
localhost:8080
java -jar -DFCGI_PORT=16251 app.jar
