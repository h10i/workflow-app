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