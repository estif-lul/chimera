.PHONY: setup test lint

BACKEND_DIR := backend

ifeq ($(wildcard $(BACKEND_DIR)/pom.xml),$(BACKEND_DIR)/pom.xml)
SETUP_CMD := mvn clean install -DskipTests
TEST_CMD := mvn test
LINT_CMD := mvn checkstyle:check
else ifeq ($(wildcard $(BACKEND_DIR)/gradlew),$(BACKEND_DIR)/gradlew)
SETUP_CMD := ./gradlew build -x test
TEST_CMD := ./gradlew test
LINT_CMD := ./gradlew check
else
$(error No supported Java build file found under $(BACKEND_DIR))
endif

setup:
	cd $(BACKEND_DIR) && $(SETUP_CMD)

test:
	cd $(BACKEND_DIR) && $(TEST_CMD)

lint:
	cd $(BACKEND_DIR) && $(LINT_CMD)