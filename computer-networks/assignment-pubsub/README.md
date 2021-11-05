# CSU33031 Publish-Subscribe Protocol

All Java files are found in `/pubsub`

## To Run

*Requires Docker and Docker Compose*

1. Create Docker network and containers: `docker-compose -f pubsub.yml up -d` (this should be run from this directory and not from `/pubsub`)
2. Run container: `docker exec -it broker /bin/bash`

From within each container:

3. Redirect to the `pubsub` folder: `cd pubsub`

4. Compile Java code: `javac Broker.java`

5. Run Java code: `java Broker`

*Note:* you will need to do steps 2-3 for each of the components, e.g start the `publisher1` container and compile and run `Publisher.java` to start the publisher process

### Running custom Lua script in Wireshark
`Wireshark -X lua_script:custom_protocol.lua pcap_file.pcap`