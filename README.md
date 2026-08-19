# AIEngineeringLevelOne

Spring Boot and Svelte 5 foundation for the Level One retrieval-augmented generation (RAG) application. The project includes a three-tab web interface, Spring Web, Spring AI with interchangeable chat and embedding providers, in-memory vector storage, Bean Validation, Actuator health checks, centralized API error handling, JSON structured logging, and environment-driven model configuration.
Spring Boot foundation for the Level One retrieval-augmented generation (RAG) application. The project includes Spring Web, Spring AI with interchangeable chat and embedding providers, in-memory vector storage, Bean Validation, Actuator health checks, centralized API error handling, JSON structured logging, and environment-driven model configuration.

## AI Agent Test

Hello, world! This change was created from Jira work item SCRUM-5.

## Prerequisites

- JDK 21 or newer
- Maven 3.9 or newer
- Node.js 22.12 or newer with npm
- Ollama running locally with the `llama3.2:3b` chat model and `nomic-embed-text` embedding model

## Configuration

Pull and start the local model before running the application:

```shell
ollama pull llama3.2:3b
ollama pull nomic-embed-text
ollama serve
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
| `SERVER_PORT` | `8080` | HTTP port |
| `LOG_FORMAT` | `logstash` | Spring Boot structured console format |

PowerShell example:

```powershell
$env:OLLAMA_CHAT_MODEL = "llama3.2:3b"
mvn spring-boot:run
```

OpenAI-compatible provider example:

```powershell
$env:SPRING_PROFILES_ACTIVE = "openai-compatible,local-rag"
$env:OPENAI_BASE_URL = "https://api.openai.com"
$env:OPENAI_API_KEY = "<set-in-your-shell-or-secret-store>"
$env:OPENAI_CHAT_MODEL = "gpt-4o-mini"
mvn spring-boot:run
```

The OpenAI-compatible profile fails startup when its base URL, API key, or model is missing. Configuration errors name the missing setting but never log credential values.

### RAG profiles

Both RAG profiles use Spring AI's in-process `SimpleVectorStore`; no Docker container or external vector database is required. Use `local-rag` for Ollama embeddings or `remote-rag` for an OpenAI-compatible embedding API. The embedding profile is independent of the chat profile, so `ollama,remote-rag` and `openai-compatible,local-rag` are supported combinations.

For an OpenAI-compatible embedding API, activate `remote-rag` and supply its values through the environment or a secret store:

```powershell
$env:SPRING_PROFILES_ACTIVE = "ollama,remote-rag"
$env:EMBEDDING_OPENAI_BASE_URL = "https://api.openai.com"
$env:EMBEDDING_OPENAI_API_KEY = "<set-in-your-shell-or-secret-store>"
$env:EMBEDDING_OPENAI_MODEL = "text-embedding-3-small"
mvn spring-boot:run
```

Startup validates embedding provider names, HTTP(S) endpoints, and credential presence without logging secret values. Vector data is held only in application memory and is cleared whenever the application restarts. Persistent production vector storage is outside the scope of this implementation.

### Embed a document

Send document text and optional metadata to the ingestion endpoint. The configured embedding provider creates the embedding and the application stores it in the in-memory vector store:

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/api/v1/documents" `
  -ContentType "application/json" `
  -Body '{"content":"Spring AI supports retrieval-augmented generation.","metadata":{"source":"documentation"}}'
```

A successful request returns HTTP `201 Created`:

```json
{
  "documentId": "generated-document-id",
  "status": "embedded"
}
```

`content` is required and limited to 20,000 characters. `metadata` is optional and limited to 50 entries. Embedded documents remain available only until the application restarts.

## Build and test

```shell
mvn clean verify
```

The Maven build runs `npm ci` and the Svelte production build automatically, then packages the generated frontend assets into the Spring Boot JAR.

### Frontend development

For Svelte-only development, start Spring Boot on port 8080 and run Vite in another terminal:

```shell
cd frontend
npm install
npm run dev
```

Vite serves the UI on `http://localhost:5173` and proxies `/api`, `/ask`, and `/actuator` to Spring Boot.

## Start the application

```shell
mvn spring-boot:run
```

Open `http://localhost:8080/` for the Svelte home page. Its three tabs call:

- `POST /api/v1/chat` for direct chat-model diagnostics
- `POST /api/v1/documents` for document embedding and in-memory storage
- `POST /ask` for similarity retrieval followed by a grounded model response

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

Before generating an `/ask` response, the application embeds the question, searches the in-memory vector store for up to three similar documents, and supplies their text to the chat model as grounding context. Ingest documents through `POST /api/v1/documents` before asking related questions. When no documents are available, the prompt directs the model to report that the available documents do not provide the answer.

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
- `frontend`: Svelte 5 source, Vite configuration, and pinned npm dependencies
- `AGENTS.md`: repository guidance for coding agents
- `SKILL.md`: repeatable development workflow
- `MEMORY.md`: durable project decisions

## Security

Configuration overrides are read from environment variables. `.gitignore` excludes `.env` files, local IDE state, build output, logs, and common private-key formats. If a secret is accidentally committed, revoke it immediately and remove it from Git history.
