# Project Memory

- Baseline runtime: Java 21, Spring Boot 4.1.x, Spring AI 2.0.x, Maven 3.9+.
- The default chat provider is local Ollama at `http://localhost:11434`, using `llama3.2:3b`; both values are environment-overridable.
- Chat providers are selected without code changes through Spring profiles: `ollama` is the default, while `openai-compatible` requires external `OPENAI_BASE_URL`, `OPENAI_API_KEY`, and `OPENAI_CHAT_MODEL` values.
- RAG infrastructure is selected independently through `local-rag` (local Chroma plus Ollama embeddings) or `remote-rag` (Chroma Cloud plus OpenAI-compatible embeddings); startup validates configuration and can opt into a Chroma heartbeat check.
- The application exposes `POST /api/v1/chat` as the initial validated Spring AI integration endpoint.
- The user-facing question API is `POST /ask` with `{"question":"..."}` and an `{"answer":"..."}` response; provider failures return a sanitized HTTP 502 error.
- Actuator health is exposed at `/actuator/health`; other actuator endpoints remain unexposed by default.
- Console logs use Spring Boot's built-in Logstash JSON structured format unless `LOG_FORMAT` overrides it.
