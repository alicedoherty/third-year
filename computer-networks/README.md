# CSU33031 Publish-Subscribe Protocol

## To Run

*Requires Docker and Docker Compose*

1. Create Docker network and containers: `docker-compose -f pubsub.yml up -d`

2. Run container: `docker exec -it broker /bin/bash`

3. Compile Java code: `javac Broker.java`

4. Run Java code: `java Broker`

*Note:* you will need to do steps 2-3 for each of the components, i.e repeat with the publisher and subscriber containers and compile and run the relevant Java code.