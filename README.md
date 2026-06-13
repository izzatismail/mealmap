# MealMap

A practical recipe and meal planning app with automatic shopping list generation.

## Features
- Browse and search recipes from Spoonacular API
- Plan meals for the week
- Auto-generate optimized shopping lists
- Track pantry inventory
- Save favorite recipes

## Tech Stack

**Backend:** Spring Boot 3.x + PostgreSQL
**Mobile:** Kotlin Multiplatform (Android Compose + iOS SwiftUI)
**DevOps:** Docker + GitHub Actions

## Getting Started

See [AGENTS.md](./AGENTS.md) for development guidelines and security practices.

### Local Development

```bash
# Prerequisites
- JDK 21+
- PostgreSQL 15+
- Docker & Docker Compose
- Gradle

# Set up environment
cp .env.example .env
# Edit .env with your Spoonacular API key

# Run with Docker
docker-compose up

# API available at http://localhost:8080
```

### Project Phases

- **Phase 1:** Backend API foundation
- **Phase 2:** Database & core services
- **Phase 3:** Docker & CI/CD
- **Phase 4:** Mobile foundation
- **Phase 5:** Complete MVP features
- **Phase 6:** Polish & deployment

See [AGENTS.md](./AGENTS.md) for detailed checkpoints.

## Architecture

- REST API with JWT authentication
- Spoonacular recipe integration with local caching
- Shared KMP business logic across mobile platforms
- PostgreSQL for data persistence

## Security

All secrets managed via environment variables. See [AGENTS.md](./AGENTS.md) for non-negotiable security rules.

## License

MIT