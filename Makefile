APP_NAME="BCalc"
APP_TYPE="deb"
APP_VERSION="1.1.1"

JARFOLDER=out/artifacts/demo_jar
JARFILE=demo.jar

MODULE_PATH=javafx-jmods-21.0.1
MODULES=javafx.base,javafx.controls,javafx.fxml,javafx.graphics

ICON=icon.png

MAIN_APP=com.example.demo.App

JAVA_SRC=src/main/java/com/example/demo
JAVA_SRCS=$(wildcard $(JAVA_SRC)/**/*.java)

deb: $(JAVA_SRCS)
	jpackage -n $(APP_NAME) -t $(APP_TYPE) --app-version $(APP_VERSION) -i $(JARFOLDER) --main-jar $(JARFILE) \
			 -d $(JARFOLDER) -p $(MODULE_PATH) --add-modules $(MODULES) --icon $(ICON)

msi: $(JAVA_SRCS)
	jpackage -n $(APP_NAME) -t "msi" --app-version $(APP_VERSION) -i $(JARFOLDER) --main-jar $(JARFILE) \
			 -d $(JARFOLDER) -p $(MODULE_PATH) --add-modules $(MODULES) --icon $(ICON)
