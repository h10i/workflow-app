# DEVELOPERS

## Development Setup

### Database

1. Create volume

    ```shell
    podman volume create wf-pgdata
    ```

2. Run PostgreSQL container

    ```shell
    podman run -d \
      --name wf-postgres \
      -e POSTGRES_USER=devuser \
      -e POSTGRES_PASSWORD=devpass \
      -e POSTGRES_DB=devdb \
      -v wf-pgdata:/var/lib/postgresql/data \
      -p 5432:5432 \
      docker.io/library/postgres:17
    ```

## Testing Memo

### API

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Auth

```shell
curl -v -X POST -H "Content-Type: application/json" -d '{"emailAddress": "test@example.com", "password": "PASSWORD"}' http://localhost:8080/v1/auth/token
$JWT = ""

curl -v -H "Authorization: Bearer $JWT" http://localhost:8080/v1/accounts/me
```

```shell
$refreshToken = ""
curl -v -X POST --cookie "refreshToken=$refreshToken" http://localhost:8080/v1/auth/refresh-token
curl -v -X DELETE --cookie "refreshToken=$refreshToken" -H "Authorization: Bearer $JWT" http://localhost:8080/v1/auth/revoke
curl -v -X DELETE -H "Authorization: Bearer $JWT" http://localhost:8080/v1/revoke/all
```

## Git Rules

### Branch

#### Main Branches

| branch    | description                                                                                                                                                                                                                                |
|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `main`    | The production-ready branch that always reflects the latest released version. Only stable and tested code is merged into `main`, typically from `release` or `hotfix` branches. Tags for production releases are created from this branch. |
| `develop` | The integration branch for ongoing development. All feature branches are created from and merged back into `develop`. It reflects the latest completed development work that is ready for staging or further testing.                      |

#### Supporting Branches

| branch     | description                                                                                                                                                                                                                                                                                          | example                  |
|------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------|
| `feature`  | Used for developing new features or enhancements that are planned for a future release. A `feature` branch is created from the `develop` branch.                                                                                                                                                     | `feature/registration`   |
| `hotfix`   | Used for immediate patches to the production (`main`) branch to fix critical bugs. A `hotfix` branch is created from the `main` branch. After applying the fix, the branch should be merged into both `main` and `develop`.                                                                          | `hotfix/fix-login-crash` |
| `refactor` | Used for improving the internal structure of the code without changing its external behavior. This includes addressing code smells, improving readability, optimizing performance, and resolving warnings. A `refactor` branch is typically created from the `develop` branch.                       | `refactor/code-cleanup`  |
| `release`  | Used to prepare for a production release by finalizing versioning, testing, and minor bug fixes. A `release` branch is created from `develop` when the development for a new version is feature-complete. It serves as a staging branch. The branch should be merged into both `main` and `develop`. | `release/v1.0.0`         |

#### Git Graph

```mermaid
    gitGraph:
        commit tag:"v1.0.0"
        branch develop
        branch hotfix/A
        branch feature/B
        branch feature/C
        checkout feature/B
        commit
        checkout hotfix/A
        commit
        checkout feature/B
        commit
        checkout feature/C
        commit
        checkout main
        merge hotfix/A tag:"v1.0.1"
        checkout develop
        merge hotfix/A
        checkout develop
        merge feature/B
        branch release/v1.1.0
        commit
        checkout main
        merge release/v1.1.0 tag:"v1.1.0"
        checkout develop
        merge release/v1.1.0
        checkout feature/C
        commit
```

### Commit Message

#### Format

```plaintext
<type>: <subject>
<BLANK>
<body>
```

Write commit messages in English.

#### Type

| type     | description                                                                                            |
|----------|--------------------------------------------------------------------------------------------------------|
| build    | Changes that affect the build system or external dependencies                                          |
| feat     | A new feature                                                                                          |
| fix      | A bug fix                                                                                              |
| docs     | Documentation only changes                                                                             |
| style    | Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc) |
| refactor | A code change that neither fixes a bug nor adds a feature                                              |
| perf     | A code change that improves performance                                                                |
| test     | Adding missing or correcting existing tests                                                            |
| chore    | Changes to the build process or auxiliary tools and libraries such as documentation generation         |

#### Subject

Summarize the commit content concisely in the first line (Subject Line).

ex.

```plaintext
fix: resolve issue with user authentication
```

#### Body

After the Subject Line, add a blank line and describe the details.

ex.

```plaintext
fix: resolve issue with user authentication

The user authentication process was failing due to an incorrect database query.
This commit corrects the query and resolves the issue.

Affected components:
- User authentication module
- Database connection
```
