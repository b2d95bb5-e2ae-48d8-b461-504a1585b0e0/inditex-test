# Similar Products – Load and Resilience Testing

This project provides an environment for testing performance, latency handling, and resilience of the Similar Products service using Docker Compose and k6.

## Prerequisites

Make sure the following are installed:

- Docker  
- Docker Compose  
- (Optional) k6 — not required when running k6 inside Docker as shown below

## Starting the Environment

To build and start all required services, run:

```bash
docker-compose up --build -d
```

This will:

- Build all containers  
- Start them in detached mode  
- Prepare the system for testing

## Running k6 Tests

Once all services are running, execute the k6 test script:

```bash
docker-compose run --rm k6 run scripts/test.js
```

This will:

- Launch a temporary Docker container for k6  
- Run `scripts/test.js` inside it  
- Remove the container when finished

## Project Structure

```
scripts/
  └── test.js        # k6 performance and latency test script
docker-compose.yml    # service definitions
```

## Notes

- Always run `docker-compose up --build -d` before executing k6 tests.  
- A local installation of k6 is not required when using the Docker command above.  
- If you modify the service definitions or test scripts, re-run with `--build`.
