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