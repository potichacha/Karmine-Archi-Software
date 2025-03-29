#!/bin/bash

TOPIC_ID=18
NB_MESSAGES=10

echo "🚀 Génération de $NB_MESSAGES messages pour le Topic $TOPIC_ID..."

for i in $(seq 1 $NB_MESSAGES); do
  curl -s -X POST http://localhost:9090/topics/$TOPIC_ID/messages \
    -H "Content-Type: application/json" \
    -d "{\"content\":\"Message $i\"}" > /dev/null

  echo "📨 Message $i envoyé"
done

echo "🎉 Tous les messages ont été générés !"