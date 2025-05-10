SRC_MAIN_DIR := src/main/java
SRC_TEST_DIR := src/test/java
BIN_DIR := bin
JAR_FILE := Linearizz.jar
MAIN_CLS := ccc102.linearizz.test.TerminalMode

SRC_MAIN_FILES := \
	$(wildcard $(SRC_MAIN_DIR)/ccc102/linearizz/*.java) \
	$(wildcard $(SRC_MAIN_DIR)/ccc102/linearizz/tokens/*.java) \
	$(wildcard $(SRC_MAIN_DIR)/ccc102/linearizz/exceptions/*.java) \
	$(wildcard $(SRC_MAIN_DIR)/ccc102/linearizz/system/*.java)

SRC_TEST_FILES := \
	$(wildcard $(SRC_TEST_DIR)/ccc102/linearizz/test/*.java)

.PHONY: build run

####################################

all: build

build-main:
	@echo Compiling source files ...
	@javac -d $(BIN_DIR) -sourcepath $(SRC_MAIN_DIR) $(SRC_MAIN_FILES)

build-test:
	@echo Compiling test files ...
	@javac -d $(BIN_DIR) -classpath $(BIN_DIR) -sourcepath $(SRC_TEST_DIR) $(SRC_TEST_FILES)

build-jar:
	@echo Building 'Linearizz.jar'
	@jar cfe $(JAR_FILE) $(MAIN_CLS) -C $(BIN_DIR) .

build: build-main build-test build-jar

run:
	@java -jar $(JAR_FILE)
