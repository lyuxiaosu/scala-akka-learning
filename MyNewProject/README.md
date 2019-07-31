## Compile ## 
``` sbt compile ```
## Run ##
First, export PORT on your environment  
``` export PORT=2551 ```   
Then,  set CLASSPATH to the path of the configuration files    
``` set CLASSPATH=src/main/resource ```   
Then you can run     
``` sbt -Dconfig.resource=seed.conf run ```    
