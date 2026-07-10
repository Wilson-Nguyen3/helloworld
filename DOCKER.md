# Docker Setup & Deployment Guide

## Prerequisites
- Docker Hub account: https://hub.docker.com
- GitHub repository with Actions enabled

## Step 1: Add Docker Hub Credentials to GitHub Secrets

Your CI workflow needs access to your Docker Hub credentials. Follow these steps:

1. Go to your GitHub repository: https://github.com/Wilson-Nguyen3/helloworld
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add two secrets:

   **Secret 1:**
   - Name: `DOCKER_HUB_USERNAME`
   - Value: `wilsonnguyen3`

   **Secret 2:**
   - Name: `DOCKER_HUB_PASSWORD`
   - Value: Your Docker Hub password (or [personal access token](https://docs.docker.com/security/for-developers/access-tokens/))

## Step 2: Push Changes to Trigger Build

Once secrets are configured:

```bash
git add Dockerfile docker-compose.yml .github/workflows/ci.yml
git commit -m "Add Docker support"
git push
```

The CI workflow will:
1. Build the Spring Boot app
2. Build the Docker image
3. Push to Docker Hub: `wilsonnguyen3/helloworld:latest`

## Step 3: Deploy on Ubuntu Machine

On your Ubuntu machine (172.25.65.138):

```bash
# Pull and run using docker-compose
docker-compose up -d

# Or pull and run manually
docker pull wilsonnguyen3/helloworld:latest
docker run -d -p 8080:8080 wilsonnguyen3/helloworld:latest
```

## Verify It's Running

```bash
# Check container status
docker ps

# View logs
docker logs spring-boot-helloworld

# Test the endpoint
curl http://localhost:8080/
```

## Image Tags

Your images will be tagged as:
- `wilsonnguyen3/helloworld:latest` - Latest from main branch
- `wilsonnguyen3/helloworld:main` - Latest from main branch (git ref)
- `wilsonnguyen3/helloworld:develop` - Latest from develop branch
- `wilsonnguyen3/helloworld:v1.0.0` - Release tags (when you tag releases)
- `wilsonnguyen3/helloworld:sha-<hash>` - Commit hash

## Stop & Clean Up

```bash
# Stop container
docker-compose down

# Or manually
docker stop spring-boot-helloworld
docker rm spring-boot-helloworld

# Remove image
docker rmi wilsonnguyen3/helloworld:latest
```

## Troubleshooting

**Image pull fails:**
```bash
# Verify you're logged in
docker login

# Try pulling manually
docker pull wilsonnguyen3/helloworld:latest
```

**Container won't start:**
```bash
# Check logs
docker logs spring-boot-helloworld

# Verify port 8080 isn't in use
netstat -tlnp | grep 8080
```

**Build fails in GitHub Actions:**
- Check the Actions tab in your GitHub repository
- Verify Docker Hub credentials are set correctly in Secrets
- Ensure your Docker Hub account has permission to push
