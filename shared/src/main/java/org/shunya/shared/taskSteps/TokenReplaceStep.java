package org.shunya.shared.taskSteps;

import org.shunya.shared.AbstractStep;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.PunterTask;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.logging.Level;

@PunterTask(author = "munishc", name = "TokenReplaceStep", description = "Replace token in file using regex", documentation = "docs/TextSamplerDemoHelp.html")
public class TokenReplaceStep extends AbstractStep {
    @InputParam(required = true, displayName = "File Path", type = "input", description = "Type absolute file path for token substitution")
    private String filePath;

    @InputParam(required = true, displayName = "Key Value Pairs", type = "textarea", description = "Properties with regex keys")
    private String properties;

    @Override
    public boolean run() {
        try {
            LOGGER.get().log(Level.INFO, "Processing file - " + filePath);
            replaceInFile(filePath, properties);
            LOGGER.get().log(Level.INFO, "Successfully Processed file - " + filePath);
            return true;
        } catch (IOException e) {
            LOGGER.get().log(Level.SEVERE, "Error Processing file - " + filePath, e);
            return false;
        }
    }

    private void replaceInFile(String filePath, String properties) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        InputStream is = new ByteArrayInputStream(properties.getBytes());
        Properties prop = new Properties();
        prop.load(is);
        String replaceAll = replaceAll(new String(bytes), prop);
        Files.write(Paths.get(filePath), replaceAll.getBytes(), StandardOpenOption.WRITE);
    }


    public String replaceAll(String input, Properties pairs) {
        for (String key : pairs.stringPropertyNames()) {
            input = input.replaceAll(key, pairs.getProperty(key));
        }
        return input;
    }
}