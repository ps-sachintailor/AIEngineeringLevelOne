# AIEngineeringLevelOne

Spring Boot foundation for the Level One retrieval-augmented generation (RAG) application. The project includes Spring Web, Spring AI with interchangeable chat and embedding providers, Chroma vector storage, Bean Validation, Actuator health checks, centralized API error handling, JSON structured logging, and environment-driven model configuration.

## AI Agent Test

Hello, world! This change was created from Jira work item SCRUM-5.

## Prerequisites

- JDK 21 or newer
- Maven 3.9 or newer
- Ollama running locally with the `llama3.2:3b` chat model and `nomic-embed-text` embedding model
- ChromaDB 1.x running locally, or credentials for a Chroma Cloud instance

## Configuration

Pull and start the local model before running the application:

```shell
ollama pull llama3.2:3b
ollama pull nomic-embed-text
ollama serve
```

Start Chroma locally in a separate terminal:

```shell
docker run --rm --name chroma -p 8000:8000 ghcr.io/chroma-core/chroma:1.0.0
```

Copy `.env.example` into the environment configuration used by your shell or IDE when overriding defaults; do not commit a populated `.env` file.

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | `ollama,local-rag` | Combine one chat profile (`ollama` or `openai-compatible`) with one RAG profile (`local-rag` or `remote-rag`) |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Local Ollama API URL |
| `OLLAMA_CHAT_MODEL` | `llama3.2:3b` | Local chat model name |
| `OLLAMA_TEMPERATURE` | `0.2` | Model response randomness |
| `OLLAMA_EMBEDDING_MODEL` | `nomic-embed-text` | Local embedding model used by `local-rag` |
| `OPENAI_BASE_URL` | none | OpenAI-compatible API base URL |
| `OPENAI_API_KEY` | none | OpenAI-compatible API key |
| `OPENAI_CHAT_MODEL` | none | OpenAI-compatible chat model name |
| `EMBEDDING_OPENAI_BASE_URL` | none | OpenAI-compatible embedding API base URL used by `remote-rag` |
| `EMBEDDING_OPENAI_API_KEY` | none | OpenAI-compatible embedding API key |
| `EMBEDDING_OPENAI_MODEL` | none | OpenAI-compatible embedding model name |
| `CHROMA_HOST` | `http://localhost` (`local-rag`) | Chroma server host including scheme |
| `CHROMA_PORT` | `8000` locally, `443` remotely | Chroma server port |
| `CHROMA_KEY_TOKEN` | none | Chroma Cloud API token; never commit a populated value |
| `CHROMA_TENANT_NAME` | `SpringAiTenant` locally | Chroma tenant; required for `remote-rag` |
| `CHROMA_DATABASE_NAME` | `SpringAiDatabase` locally | Chroma database; required for `remote-rag` |
| `CHROMA_COLLECTION_NAME` | `rag_documents` | Chroma collection used by the application |
| `CHROMA_INITIALIZE_SCHEMA` | `false` | Create the configured tenant, database, and collection at startup |
| `CHROMA_CONNECTIVITY_CHECK_ENABLED` | `false` | Call Chroma's heartbeat during startup and fail fast if unavailable |
| `SERVER_PORT` | `8080` | HTTP port |
| `LOG_FORMAT` | `logstash` | Spring Boot structured console format |

PowerShell example:

```powershell
$env:OLLAMA_CHAT_MODEL = "llama3.2:3b"
mvn spring-boot:run
```

OpenAI-compatible provider example:

```powershell
$env:SPRING_PROFILES_ACTIVE = "openai-compatible"
$env:OPENAI_BASE_URL = "https://api.openai.com"
$env:OPENAI_API_KEY = "<set-in-your-shell-or-secret-store>"
$env:OPENAI_CHAT_MODEL = "gpt-4o-mini"
mvn spring-boot:run
```

The OpenAI-compatible profile fails startup when its base URL, API key, or model is missing. Configuration errors name the missing setting but never log credential values.

### RAG profiles and connectivity

Use `local-rag` with Ollama embeddings and a local Chroma instance. Set `CHROMA_INITIALIZE_SCHEMA=true` on the first run if the configured collection does not exist. The embedding profile is independent of the chat profile, so `ollama,remote-rag` and `openai-compatible,local-rag` are also supported combinations.

For Chroma Cloud and an OpenAI-compatible embedding API, activate `remote-rag` and supply all remote values through the environment or a secret store:

```powershell
$env:SPRING_PROFILES_ACTIVE = "ollama,remote-rag"
$env:CHROMA_HOST = "https://api.trychroma.com"
$env:CHROMA_PORT = "443"
$env:CHROMA_KEY_TOKEN = "<set-in-your-shell-or-secret-store>"
$env:CHROMA_TENANT_NAME = "<tenant>"
$env:CHROMA_DATABASE_NAME = "<database>"
$env:EMBEDDING_OPENAI_BASE_URL = "https://api.openai.com"
$env:EMBEDDING_OPENAI_API_KEY = "<set-in-your-shell-or-secret-store>"
$env:EMBEDDING_OPENAI_MODEL = "text-embedding-3-small"
mvn spring-boot:run
```

Startup always validates provider names, HTTP(S) endpoints, required cloud identifiers, and credential presence without logging secret values. To perform the testable Chroma heartbeat during startup, enable the check before launching:

```powershell
$env:CHROMA_CONNECTIVITY_CHECK_ENABLED = "true"
mvn spring-boot:run
```

The application calls `GET /api/v2/heartbeat` on the configured Chroma host and port and fails startup with a sanitized error if the service is unavailable.

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
  -Uri "http://localhost:8080/ask" `
  -ContentType "application/json" `
  -Body '{"question":"Reply with a short greeting."}'
```

The JSON response contains a non-empty generated `answer`. The request body must be valid JSON and must contain a non-empty `question` field of at most 4,000 characters. Model-provider failures return a controlled HTTP 502 response without exposing provider details.

The original `/api/v1/chat` endpoint remains available for provider and model diagnostics.

For a quick browser test, open:

```text
http://localhost:8080/api/v1/chat?message=Explain%20RAG%20briefly
```

Opening `http://localhost:8080/api/v1/chat` without a query parameter sends a default greeting prompt.

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
