# Variables
JAVA_FILES = Interface.java TestInterface.java
CLASS_FILES = Interface.class TestInterface.class
JAVAC = javac
JAVA = java
SQLPLUS = sqlplus
DB_USER = zouggars
DB_PASS = zouggars
DB_CONN = oracle1.ensimag.fr:1521:oracle1
MAIN_CLASS = TestInterface

# Targets
all: compile

compile:
	$(JAVAC) $(JAVA_FILES)

run: compile
	$(JAVA) $(MAIN_CLASS)

db-init:
	@echo "Initializing database schema..."
	$(SQLPLUS) $(DB_USER)/$(DB_PASS)@$(DB_CONN) @baseDeDonnee.sql

db-fill:
	@echo "Populating database with initial data..."
	$(SQLPLUS) $(DB_USER)/$(DB_PASS)@$(DB_CONN) @RemplissageBDD.sql

clean:
	@echo "Cleaning up compiled files..."
	rm -f $(CLASS_FILES)

# Convenience targets
db-reset: db-init db-fill
	@echo "Database reset complete."

test: compile
	@echo "Running the program for testing..."
	$(JAVA) $(MAIN_CLASS)

help:
	@echo "Available targets:"
	@echo "  all          - Compile the Java files"
	@echo "  compile      - Compile the Java files"
	@echo "  run          - Run the main program"
	@echo "  db-init      - Initialize the database schema"
	@echo "  db-fill      - Populate the database with sample data"
	@echo "  db-reset     - Reset the database (schema + data)"
	@echo "  clean        - Remove all compiled files"
	@echo "  test         - Compile and run the main program"
	@echo "  help         - Show this help message"
