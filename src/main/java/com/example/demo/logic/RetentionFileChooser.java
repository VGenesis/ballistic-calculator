package com.example.demo.logic;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class RetentionFileChooser {
    FileChooser chooser;
    String configFilePath;
    String format;

    String defaultFormat;

    public RetentionFileChooser(){
        this.chooser = new FileChooser();
    }

    public void setConfigFilePath(String configFilePath) {this.configFilePath = configFilePath;}

    public void setFormat(String format) {this.format = format;}

    public File showOpenDialog(Stage stage, Optional<String> title){
        this.setConfig(title);
        File file = this.chooser.showOpenDialog(stage);
        if(file != null) this.updateConfigOptions(file);
        return file;
    }

    public File showSaveDialog(Stage stage, Optional<String> title){
        this.setConfig(title);
        File file = this.chooser.showSaveDialog(stage);
        if(file != null) this.updateConfigOptions(file);
        return file;
    }

    private void setConfig(Optional<String> title){
        if(configFilePath != null){
            String[] configOptions = this.getConfigOptions();
            System.out.println(Arrays.toString(configOptions));
            this.chooser.setInitialDirectory(new File(configOptions[0]));
            this.chooser.setInitialFileName(configOptions[1]);
        }
        this.chooser.setTitle(title.orElse("Select file"));
    }

    private String[] getConfigOptions(){
        String[] res = new String[]{"", ""};
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.configFilePath));

            String os = System.getProperty("os.name");
            String line = br.readLine();
            while(line != null){
                if(line.contains(os)){
                    int separator = line.indexOf("=");
                    defaultFormat = line.substring(separator+1).trim();
                }
                if(line.contains(format)){
                    int separator = line.indexOf("=");
                    int index = (line.contains("folder"))? 0 : 1;
                    res[index] = line.substring(separator+1).trim();
                }

                line = br.readLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(Objects.equals(res[0], "")) res[0] = defaultFormat;
        return res;
    }

    private void updateConfigOptions(File file){
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.configFilePath));
            ArrayList<String> lines = new ArrayList<>();
            String line = br.readLine();
            while(line != null){
                lines.add(line);
                line = br.readLine();
            }
            br.close();

            for(int i = 0; i < lines.size(); i++){
                String l = lines.get(i);
                if(l.contains(this.format)){
                    int separator = l.indexOf("=");
                    boolean isFolder = l.contains("folder");
                    String key = l.substring(0, separator+1);
                    String path = file.getAbsolutePath();

                    int filenameIndex = 0;
                    int currentIndex;
                    while((currentIndex = path.indexOf("/", filenameIndex + 1)) != -1)
                        filenameIndex = currentIndex;

                    if(isFolder){
                        String directory = path.substring(0, filenameIndex+1);
                        String newLine = key + directory;
                        lines.set(i, newLine);
                    }else{
                        String filename = path.substring(filenameIndex+1);
                        String newLine = key + filename;
                        lines.set(i, newLine);
                    }
                }
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.configFilePath));
            bw.flush();
            for(String l : lines){
                bw.write(l);
                bw.write("\n");
                System.out.println(l);
            }
            bw.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
