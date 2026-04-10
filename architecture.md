# Architecture

The vision is a system that indepedent of online services, running on its own hardware, can chat about life based on a person's memories.

## Quality attributes

### Security
Local models to avoid dependency on online services. The latter can be used for comparison and benchmarking.

### Testability
Use Docker for easy setup when developing and testing. 

## Description

### Deployment for testing and development
See [docker-compose.yml](core/docker-compose.yml) for details. 

The main application is built using Spring Boot and runs in a Docker container. It is responsible for handling user interactions and managing the overall functionality of the system.

Ollama runs in another container and provides the AI models for embeddings and chatting. First Ollama starts and then the models are downloaded once.

The database is Postgres with a vector extension and runs in a third container. The memories are stored as paragraphs in the relational database and the embeddings are stored in the vector extension.
