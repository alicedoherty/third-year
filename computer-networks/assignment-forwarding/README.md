# CSU33031 Flow Forwarding Protocol

All Java files are found in `/flow-forwarding`

## To Run

*Requires Docker and Docker Compose*

1. Create Docker network and containers: `docker-compose -f flow-forwarding.yml up -d` (this should be run from this directory and not from `/flow-forwarding`)
2. Run container: `docker exec -it E1 /bin/bash`

From within each container:

3. Redirect to the `flow-forwarding` folder: `cd flow-forwarding`

4. Compile Java code: `javac EndNode.java`

5. Run Java code: `java EndNode`

*Note:* you will need to do steps 2-5 for each of the components and run the relevant Java file inside each

My topology includes:
- 4 EndNodes (E1, E2, E3, E4)
- 4 Routers (R1, R2, R3, R4)
- 1 Controller (controller) - make sure to run the controller code before any of the routers

To take down the network: `docker-compose -f flow-forwarding.yml down`