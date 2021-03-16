#!/bin/bash

echo ami/team1 - Kontaktnachverfolgung
echo starte monolith

podman network create net-t1

podman run -p 8080:8080 --net=host --name ami-team1-monolith --rm -it ami/team1/monolith \
	& sleep 10 && podman container exec -it ami-team1-monolith bash -c "HOSTNAME='localhost' ; java -jar /usr/app/monolith-bootable.jar" \
	& sleep 30 && firefox "localhost:8080"