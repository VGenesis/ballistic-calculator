package com.example.demo.logic;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class RetentionFileChooser {
    FileChooser chooser;
    String configFilePath;
    String format = "";

    final FileChooser.ExtensionFilter[] extensions = {
            new FileChooser.ExtensionFilter("Excel (.xlsx)", ".xlsx"),
            new FileChooser.ExtensionFilter("CSV (.csv)", ".csv"),
            new FileChooser.ExtensionFilter("JSON (.json)", ".json")
    };

    public RetentionFileChooser() {
        this.chooser = new FileChooser();
        this.chooser.getExtensionFilters().addAll(Arrays.asList(extensions));
        this.chooser.setSelectedExtensionFilter(this.chooser.getExtensionFilters().get(0));

        this.format = this.chooser.getSelectedExtensionFilter().getExtensions().get(0);
    }

    public String getFormat() {return format;}

    public File showSaveDialog(Stage stage, Optional<String> title){
        this.chooser.setTitle(title.orElse("Save file..."));
        File file = this.chooser.showSaveDialog(stage);

        this.format = this.chooser.getSelectedExtensionFilter().getExtensions().get(0);

        if(!file.getAbsolutePath().endsWith(format)){
            file = new File(file.getAbsolutePath() + format);
        }

        return file;
    }

    public File showOpenDialog(Stage stage, Optional<String> title){
        this.chooser.setTitle(title.orElse("Load file..."));
        File file = this.chooser.showOpenDialog(stage);

        this.format = this.chooser.getSelectedExtensionFilter().getExtensions().get(0);
        return file;
    }

}

