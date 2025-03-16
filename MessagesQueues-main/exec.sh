#!/bin/bash

if [ "$1" == "h2" ]; then
    echo "Profil H2 sélectionné."
    echo "spring.profiles.active=h2" > src/main/resources/application.properties

    mvn clean install
    mvn spring-boot:run

elif [ "$1" == "mysql" ]; then
    echo "Profil MySQL sélectionné."
    echo "spring.profiles.active=mysql" > src/main/resources/application.properties

    mvn clean install
    docker-compose up --build -d
else
    echo "Erreur : Veuillez fournir un profil valide h2 ou mysql."
    exit 1
fi