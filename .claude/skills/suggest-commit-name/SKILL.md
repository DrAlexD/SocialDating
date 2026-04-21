---
name: suggest-commit-name
description: Analyze a commit diff (or staged changes) and propose a better commit title with tags
argument-hint: [ commit-hash ]
disable-model-invocation: true
---

Analyze the diff described below, ignoring any existing commit title/description, then follow the steps.

**Determine the source:**

- If `$ARGUMENTS` is non-empty, run:
    1. `git show $ARGUMENTS --stat`
    2. `git show $ARGUMENTS`
- If `$ARGUMENTS` is empty, run:
    1. `git diff --staged --stat`
    2. `git diff --staged`
- If staged diff is also empty, run `git diff --stat` and `git diff` (unstaged changes).

**Title rules:**

- One sentence, past tense, written as a completed action, in English
- Maximum 50 characters (count carefully — if the title exceeds 50 chars, shorten it)
- Start with a strong action verb from this preferred list: Added, Fixed, Removed, Updated, Replaced, Renamed,
  Extracted, Refactored, Moved, Enabled, Disabled, Simplified, Extended, Restricted, Exposed, Hidden
- Do **not** use weak filler words: "with", "also", "just", "some"
- Do **not** use gerunds (-ing form) as the main verb — prefer past tense ("Added" not "Adding")
- If the title references a class name, filename, or function name, wrap it in backticks (e.g., `MyClass`, `Utils.kt`,
  `doSomething`). Class names and filenames must always start with a capital letter.

**Tags** — choose one or more that best describe the changes:

- `architecture` — Introduces **new** structural elements that did not exist before: new layers, modules, base classes,
  interfaces, or design patterns. Do **not** use when reorganizing existing code (use `refactoring`) or changing
  build/deploy config (use `build`/`infrastructure`). Combine with `enhancement` when new structure also exposes new
  user-visible capabilities.
- `build` — Client-side build tooling only: Gradle build config (plugins, compiler options, build variants — but not
  `dependencies {}` blocks), client-side CI/CD pipelines, lint/compiler config. Do **not** use for dependency
  additions/updates or version catalog changes (use `dependencies`). Do **not** use for server/deployment
  infrastructure (use `infrastructure`).
- `data` — Changes to client-side or server-side data models, entities, or database schemas (migrations, DAO, repository
  contracts). Do **not** use for server provisioning or environment config (use `infrastructure`).
- `dependencies` — Adding, updating, or removing library/SDK dependencies: version bumps, new library additions,
  dependency removals. Covers both client-side and server-side dependency declarations. Do **not** use for
  build tooling changes unrelated to dependencies (use `build`).
- `documentation` — Improvements or additions to documentation.
- `enhancement` — A new capability observable by end-users or API consumers: new screens, endpoints, commands, or
  behaviors. Do **not** use for internal restructuring with no externally visible effect (use `architecture` or
  `refactoring`). Do **not** combine with `refactoring` (refactoring does not change behavior). Combine with
  `architecture` when new structure is also introduced. Combine with `ui` when the new feature also involves significant
  visual work.
- `fix` — Corrects incorrect behavior. Do **not** combine with `security` — if the primary motivation is security, use
  `security` instead.
- `infrastructure` — Server/deployment/environment infrastructure: Docker, Kubernetes, cloud config, server
  provisioning, server-side CI runners, environment variables. Do **not** use for client build files (use `build`) or
  data model changes (use `data`).
- `performance` — Changes whose primary goal is measurable speed, memory, or efficiency improvement: query optimization,
  caching, lazy loading, reduced allocations. Do **not** use when the speedup is a side-effect of a fix or refactor (use
  `fix`/`refactoring` instead).
- `refactoring` — Reorganizes or cleans up **existing** code without adding new structure or changing behavior. Do
  **not** use when introducing new abstractions or layers (use `architecture`). Do **not** combine with `enhancement`
  (refactoring does not change behavior).
- `security` — Security-motivated changes: authentication, authorization, input validation, vulnerability fixes. Do
  **not** combine with `fix` — use instead of `fix` when the primary motivation is security.
- `test` — Adding or updating tests.
- `ui` — Visual-only changes: layout, styling, colors, spacing, animations. For new screens or components that also add
  behavior, combine with `enhancement`.

**Output format:**

**Title:** <best proposed title>

**Alternatives:**

- <alternative title 2> — <one phrase why this wording differs>
- <alternative title 3> — <one phrase why this wording differs>

**Tags:** `tag1`, `tag2`, …

**Reasoning:**

- **Title** — <one sentence explaining what drove this wording choice>
- `tag1` — <description of what in the diff justifies this tag>
- `tag2` — <description of what in the diff justifies this tag>