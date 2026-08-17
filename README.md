# AIEngineeringLevelOne

Spring Boot foundation for the Level One retrieval-augmented generation (RAG) application. The project includes Spring Web, Spring AI with local Ollama, Bean Validation, Actuator health checks, centralized API error handling, JSON structured logging, and environment-driven model configuration.

## AI Agent Test

Hello, world! This change was created from Jira work item SCRUM-5.

## Prerequisites

- JDK 21 or newer
- Maven 3.9 or newer
- Ollama running locally with the `llama3.2:3b` model

## Configuration

Pull and start the local model before running the application:

```shell
ollama pull llama3.2:3b
ollama serve
```

Copy `.env.example` into the environment configuration used by your shell or IDE when overriding defaults; do not commit a populated `.env` file.

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `AI_MODEL_CHAT` | `ollama` | Spring AI chat provider |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Local Ollama API URL |
| `OLLAMA_CHAT_MODEL` | `llama3.2:3b` | Local chat model name |
| `OLLAMA_TEMPERATURE` | `0.2` | Model response randomness |
| `SERVER_PORT` | `8080` | HTTP port |
| `LOG_FORMAT` | `logstash` | Spring Boot structured console format |

PowerShell example:

```powershell
$env:OLLAMA_CHAT_MODEL = "llama3.2:3b"
mvn spring-boot:run
```

## Build and test

```shell
mvn clean verify
```

## Start the application

```shell
mvn spring-boot:run
```

Confirm that the service is healthy:

```shell
curl http://localhost:8080/actuator/health
```

The response includes `{"status":"UP"}` when the service is running.

Ask the local model a question:

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/api/v1/chat" `
  -ContentType "application/json" `
  -Body '{"message":"Reply with a short greeting."}'
```

The JSON response identifies the `ollama` provider and `llama3.2:3b` model alongside the generated answer.
The request body must be valid JSON and must contain a non-empty `message` field.

### Maven plugin troubleshooting

The Spring Boot Maven plugin prefix contains a hyphen. Use:

```shell
mvn spring-boot:run
```

Do not use `mvn springboot:run`; Maven treats `springboot` as a different plugin prefix and reports `No plugin found for prefix 'springboot'`.

## Project layout

- `src/main/java/com/productsquads/aiengineering`: application source
- `src/main/resources/application.yml`: environment mappings and operational defaults
- `src/test`: automated application and configuration tests
- `AGENTS.md`: repository guidance for coding agents
- `SKILL.md`: repeatable development workflow
- `MEMORY.md`: durable project decisions

## Security

Configuration overrides are read from environment variables. `.gitignore` excludes `.env` files, local IDE state, build output, logs, and common private-key formats. If a secret is accidentally committed, revoke it immediately and remove it from Git history.
