ENV_FILE      := $(if $(wildcard .env),--env-file .env,)
COMPOSE_INFRA := docker compose $(ENV_FILE) -f docker/compose.infra.yml
COMPOSE_APP   := docker compose $(ENV_FILE) -f docker/compose.yml

.PHONY: infra infra-down infra-reset test

infra:       ; $(COMPOSE_INFRA) up -d --wait
infra-down:  ; $(COMPOSE_INFRA) down
infra-reset: ; $(COMPOSE_INFRA) down -v && $(COMPOSE_INFRA) up -d --wait
test:        ; mvn -B verify
