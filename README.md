# AIEngineeringLevelOne

Spring Boot foundation for the Level One retrieval-augmented generation (RAG) application. The project includes Spring Web, Spring AI, Bean Validation, Actuator health checks, centralized API error handling, JSON structured logging, and environment-driven model configuration.

## AI Agent Test

Hello, world! This change was created from Jira work item SCRUM-5.

## Prerequisites

- JDK 21 or newer
- Maven 3.9 or newer
- An OpenAI API key only when AI integration is enabled

## Configuration

The application starts safely without an external model by default. Copy `.env.example` into the environment configuration used by your shell or IDE; do not commit a populated `.env` file.

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `AI_MODEL_CHAT` | `none` | Chat provider; set to `openai` to enable the model |
| `OPENAI_API_KEY` | empty | OpenAI credential; required when AI is enabled |
| `OPENAI_BASE_URL` | `https://api.openai.com` | OpenAI-compatible API base URL |
| `OPENAI_CHAT_MODEL` | `gpt-4.1-mini` | Chat model name |
| `SERVER_PORT` | `8080` | HTTP port |
| `LOG_FORMAT` | `logstash` | Spring Boot structured console format |

PowerShell example:

```powershell
$env:AI_MODEL_CHAT = "openai"
$env:OPENAI_API_KEY = "your-api-key"
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

## Project layout

- `src/main/java/com/productsquads/aiengineering`: application source
- `src/main/resources/application.yml`: environment mappings and operational defaults
- `src/test`: automated application and configuration tests
- `AGENTS.md`: repository guidance for coding agents
- `SKILL.md`: repeatable development workflow
- `MEMORY.md`: durable project decisions

## Security

Credentials are read from environment variables and are never required in source files. `.gitignore` excludes `.env` files, local IDE state, build output, logs, and common private-key formats. If a secret is accidentally committed, revoke it immediately and remove it from Git history.
