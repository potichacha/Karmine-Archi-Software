echo "test"
if [ "$1" == "h2" ]; then
    echo "Profil H2 sélectionné."
    sed -i 's/^spring.profiles.active=.*/spring.profiles.active=h2/' src/main/resources/application.properties
elif [ "$1" == "mysql" ]; then
    echo "Profil MySQL sélectionné."
    sed -i 's/^spring.profiles.active=.*/spring.profiles.active=mysql/' src/main/resources/application.properties
else
    echo "Erreur : Veuillez fournir un profil valide (h2 ou mysql)."
    exit 1
fi

# Construire et démarrer l'application
mvn clean install
docker-compose up --build -d

if [ "$1" == "mysql" ]; then
    echo "Waiting for MySQL to initialize..."
    until docker exec dbase mysqladmin ping -h localhost --silent; do
        sleep 5
    done

    echo "Creating database..."
    docker exec -i dbase mysql -u root -proot <<EOF
CREATE DATABASE IF NOT EXISTS dbase;
EOF

    echo "Database dbase created or already exists."
fi

echo "Deployment and database initialization complete!"
