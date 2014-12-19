package org.shunya.shared.taskSteps;

import org.shunya.shared.AbstractStep;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.OutputParam;
import org.shunya.shared.annotation.PunterTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@PunterTask(author = "munishc", name = "UnZipStep", description = "Echo's the input data to SOP", documentation = "docs/TextSamplerDemoHelp.html")
public class UnZipStep extends AbstractStep {
    @InputParam(required = true, displayName = "Input File", type = "input", description = "Enter input zip file with absolute path")
    private String inputFile;

    @InputParam(required = true, displayName = "Output Folder", type = "input", description = "Enter output directory where you want to expand")
    private String outputFolder;

    @Override
    public boolean run() {
        LOGGER.get().log(Level.INFO, "unzipping File " + inputFile + " to location - " + outputFolder);
        return unZipIt(inputFile, outputFolder);
    }

    public boolean unZipIt(String zipFile, String outputFolder) {
        byte[] buffer = new byte[1024];
        try {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                System.out.println("file unzip : " + newFile.getAbsoluteFile());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            return true;
        } catch (IOException ex) {
            LOGGER.get().log(Level.SEVERE, "Error in zip file extraction - ", ex);
        }
        return false;
    }
}