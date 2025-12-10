#!/usr/bin/env bash

docker compose up -d
docker exec -t ollama ollama pull llama3.2

curl http://localhost:11434/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
        "model": "llama3.2",
        "messages": [{"role":"user","content":"Hej! Säg att detta fungerar."}]
      }'

docker exec ollama ollama pull embeddinggemma
