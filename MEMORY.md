# Project Memory

- Baseline runtime: Java 21, Spring Boot 4.1.x, Spring AI 2.0.x, Maven 3.9+.
- The default chat provider is local Ollama at `http://localhost:11434`, using `llama3.2:3b`; both values are environment-overridable.
- Chat providers are selected without code changes through Spring profiles: `ollama` is the default, while `openai-compatible` requires external `OPENAI_BASE_URL`, `OPENAI_API_KEY`, and `OPENAI_CHAT_MODEL` values.
- RAG profiles always use Spring AI's in-process `SimpleVectorStore`: `local-rag` uses Ollama embeddings and `remote-rag` uses OpenAI-compatible embeddings. Vector data is cleared on restart; persistent production storage is out of scope.
- `POST /api/v1/documents` accepts required document `content` plus optional `metadata`, embeds it with the active provider, and returns the generated document ID with HTTP 201.
- The application exposes `POST /api/v1/chat` as the initial validated Spring AI integration endpoint.
- The user-facing question API is `POST /ask` with `{"question":"..."}` and an `{"answer":"..."}` response; provider failures return a sanitized HTTP 502 error.
- The `/ask` flow performs vector similarity search first (top three documents), then supplies the retrieved text to the chat model as grounding context.
- The default home page is a simple Svelte 5 interface with separate tabs for direct chat, document embedding, and grounded questions; Maven builds it with Vite into the Spring Boot JAR.
- Actuator health is exposed at `/actuator/health`; other actuator endpoints remain unexposed by default.
- Console logs use Spring Boot's built-in Logstash JSON structured format unless `LOG_FORMAT` overrides it.
