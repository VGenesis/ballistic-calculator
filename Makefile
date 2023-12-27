APP_NAME="BCalc"
APP_TYPE="deb"
APP_VERSION="1.1.1"

JAR_FOLDER=out/artifacts/demo_jar
JAR_FILE=demo.jar
MAIN_CLASS=com.example.demo.App

DEST=pkg

MODULE_PATH=javafx-jmods-21.0.1
MODULES=javafx.base,javafx.controls,javafx.fxml,javafx.graphics

ICON=icon.png

MAIN_APP=com.example.demo.App

JAVA_SRC=src/main/java/com/example/demo
JAVA_SRCS=$(wildcard $(JAVA_SRC)/**/*.java)

deb: $(JAVA_SRCS)
	mkdir -p $(DEST)
	jpackage \
		--name $(APP_NAME) \
		--type deb \
		--app-version $(APP_VERSION) \
		--input $(JAR_FOLDER) \
		--main-jar $(JAR_FILE) \
		--main-class $(MAIN_CLASS) \
		--dest $(DEST) \
		--icon $(ICON) \
		--module-path $(MODULE_PATH) \
		--add_modules $(MODULES)

msi: $(JAVA_SRCS)
	mkdir -p $(DEST)
	jpackage \
		--name $(APP_NAME) \
		--type msi \
		--app-version $(APP_VERSION) \
		--input $(JAR_FOLDER) \
		--main-jar $(JAR_FILE) \
		--main-class $(MAIN_CLASS) \
		--dest $(DEST) \
		--icon $(ICON) \
		--module-path $(MODULE_PATH) \
		--add_modules $(MODULES) \
		--win-shortcut \
		--win-menu \
		--win-dir-chooser
