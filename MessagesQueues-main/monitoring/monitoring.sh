#!/bin/bash

CONTAINERS_TO_MONITOR=("container1" "container2")

check_container_health() {
    containers=$(docker ps -a --format '{{.ID}} {{.Names}} {{.Status}}')

    if [ $? -ne 0 ]; then
        echo "[ERROR] Failed to fetch container list using Docker CLI"
        return 1
    fi

    echo "[CHECKING] Checking Docker containers..."

    while IFS= read -r container; do
        container_id=$(echo $container | awk '{print $1}')
        container_name=$(echo $container | awk '{print $2}')
        health_status=$(echo $container | awk '{print $3}')

        if [[ " ${CONTAINERS_TO_MONITOR[*]} " =~ " ${container_name} " ]]; then
            if [[ $health_status == "Up" ]]; then
                echo "[HEALTHY] Container $container_name is healthy."
            else
                echo "[UNHEALTHY] Container $container_name is unhealthy! Restarting..."
                restart_container $container_id
            fi
        else
            echo "[SKIPPING CONTAINER] Skipping container $container_name (not in monitoring list)"
        fi
    done <<< "$containers"
}

restart_container() {
    container_id=$1
    docker restart $container_id
    if [ $? -ne 0 ]; then
        echo "[ERROR] Failed to restart container: $container_id"
    else
        echo "[RESTART CONTAINER] Restarted container: $container_id"
    fi
}

while true; do
    check_container_health
    sleep 30
done