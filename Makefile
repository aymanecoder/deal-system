.PHONY: help build run test clean docker-up docker-down docker-logs db-reset deploy

help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build the project
	./mvnw clean package -DskipTests

run: ## Run the application
	./mvnw spring-boot:run

test: ## Run tests
	./mvnw test

clean: ## Clean build artifacts
	./mvnw clean

docker-up: ## Start PostgreSQL using Docker Compose
	docker compose up -d

docker-down: ## Stop PostgreSQL using Docker Compose
	docker compose down

docker-logs: ## View Docker Compose logs
	docker compose logs -f

db-reset: ## Reset the database (WARNING: This will delete all data)
	docker compose down -v
	docker compose up -d
	@echo "Waiting for PostgreSQL to be ready..."
	@sleep 5

deploy: build ## Build and run the application with Docker
	docker compose up --build

start: docker-up run ## Start database and application

stop: docker-down ## Stop database and application

all: clean build test ## Clean, build, and test

