---
name: ai-engineering-level-one
description: Build and verify the Spring Boot and Spring AI RAG application in this repository.
---

# AI Engineering Level One workflow

1. Read `AGENTS.md`, `README.md`, and `MEMORY.md` before editing.
2. Trace the requested behavior through configuration, application code, and tests.
3. Keep model credentials in environment variables; default tests to `AI_MODEL_CHAT=none`.
4. Run `mvn clean verify`.
5. When startup behavior changes, run the packaged JAR and verify `/actuator/health` returns `UP`.
6. Review `git diff` for generated files or secrets before committing.
