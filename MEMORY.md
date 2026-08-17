# Project Memory

- Baseline runtime: Java 21, Spring Boot 4.1.x, Spring AI 2.0.x, Maven 3.9+.
- The application must start without external credentials by default; AI auto-configuration is activated only with `AI_MODEL_CHAT=openai`.
- OpenAI-compatible configuration is supplied through environment variables documented in `README.md` and `.env.example`.
- Actuator health is exposed at `/actuator/health`; other actuator endpoints remain unexposed by default.
- Console logs use Spring Boot's built-in Logstash JSON structured format unless `LOG_FORMAT` overrides it.
