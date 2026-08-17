---
name: ai-engineering-level-one
description: Build and verify the Spring Boot and Spring AI RAG application in this repository.
---

# AI Engineering Level One workflow

1. Read `AGENTS.md`, `README.md`, and `MEMORY.md` before editing.
2. Trace the requested behavior through configuration, application code, and tests.
3. Keep Ollama connection and model settings in environment variables; the local default is `llama3.2:3b`.
4. Run `mvn clean verify`.
5. When startup or model behavior changes, verify `/actuator/health` returns `UP` and send a prompt to `/api/v1/chat`.
6. Review `git diff` for generated files or secrets before committing.
