#!/bin/bash

TOPIC_ID=18

echo "📥 Récupération des messages du topic $TOPIC_ID..."

# Récupère les IDs des messages avec grep & sed
MESSAGE_IDS=$(curl -s http://localhost:9090/topics/$TOPIC_ID/messages | grep -o '"id":[0-9]*' | sed 's/"id"://g' | sort -n | uniq)

echo "👀 Lecture des messages..."
for id in $MESSAGE_IDS; do
  curl -s http://localhost:9090/messages/$id > /dev/null
  echo "📖 Message $id lu"
done

echo "✅ Tous les messages du topic $TOPIC_ID ont été lus."