# ── Variables (overridable by CI: make deploy JAR_VERSION=x IMAGE_TAG=y) ──────
JAR_VERSION ?= $(shell grep '^version=' ../../gradle.properties 2>/dev/null | cut -d= -f2)
GIT_SHA     ?= $(shell git rev-parse --short HEAD)
IMAGE_TAG   ?= $(JAR_VERSION)-$(GIT_SHA)

IMAGE     := blog:$(IMAGE_TAG)
NAMESPACE := blog

# ── Phony targets ──────────────────────────────────────────────────────────────
.PHONY: build image import deploy clean all

## build  — compile fat jar (run on CI / local dev machine)
build:
	cd ../.. && ./gradlew :kuma-project:kuma-project-blog:bootJar -x test

## image  — build docker image (run on server)
image:
	docker build --build-arg JAR_VERSION=$(JAR_VERSION) -t $(IMAGE) .

## import — load image into k3s containerd (run on server)
import:
	docker save $(IMAGE) | k3s ctr images import -

## deploy — rolling update + wait (run on server)
deploy:
	kubectl set image deployment/blog-background \
	  blog-background=$(IMAGE) -n $(NAMESPACE)
	kubectl rollout status deployment/blog-background \
	  -n $(NAMESPACE) --timeout=120s

## clean  — remove old local docker images, keep latest 3 (run on server)
clean:
	@docker images blog --format "{{.Tag}}" | sort -rV | tail -n +4 \
	  | xargs -r -I{} docker rmi "blog:{}" 2>/dev/null || true

## all    — full server-side pipeline: image → import → deploy → clean
all: image import deploy clean
