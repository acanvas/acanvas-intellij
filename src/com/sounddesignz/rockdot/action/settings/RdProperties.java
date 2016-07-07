package com.sounddesignz.rockdot.action.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.util.Properties;

/**
 * Created by ndoehring on 07.07.16.
 */
public class RdProperties {

    private Properties properties;
    private String contents;
    final VirtualFile configFile;

    public RdProperties(String fileName, Project project) {
        configFile = VfsUtil.findRelativeFile("config/release/" + fileName, project.getBaseDir());

        try {
            // Read properties into String (so we replace by Regex)
            String currentLine;
            BufferedReader br = new BufferedReader(new FileReader(configFile.getPath()));
            StringBuilder stringBuilder = new StringBuilder();
            while ((currentLine = br.readLine()) != null) {
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
            }

            contents = stringBuilder.toString();


            // Read properties into Properties (for better lookup)
            BufferedReader br2 = new BufferedReader(new FileReader(configFile.getPath()));
            properties = new Properties();
            properties.load(br2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {

            FileWriter fw = new FileWriter(configFile.getPath());
            BufferedWriter bw = new BufferedWriter(fw);

            ApplicationManager.getApplication().runWriteAction(
                    () -> {
                        try{
                            configFile.setBinaryContent(contents.getBytes());
                        }
                        catch (IOException ee){
                            ee.printStackTrace();
                        }
                    });


        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        String keyEscaped = key.replaceAll("\\.", "\\\\.");
        contents = contents.replaceAll(key + "\\s*=.*",key + " = " + value);
        //contents = contents.replaceAll("project.title\\s=.*",key + " = " + value);
        //contents = contents.replaceAll("project\\.copyright\\s=.*",key + " = " + value);
    }
}
