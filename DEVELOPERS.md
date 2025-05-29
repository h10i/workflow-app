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

### Auth

```
curl -X POST -H "Content-Type: application/json" -d '{"mailAddress": "test@example.com", "password": "PASSWORD"}' http://localhost:8080/token

$JWT = ""

curl -v -H "Authorization: Bearer $JWT" http://localhost:8080/messages
curl -v http://localhost:8080/messages -H "Authorization: Bearer $JWT" -H "Content-Type: text/plain" -d "Hello World"
```
