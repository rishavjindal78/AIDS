package org.shunya.shared.taskSteps;

import org.shunya.shared.AbstractStep;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.OutputParam;
import org.shunya.shared.annotation.PunterTask;

import java.io.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
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
        return extractFolder(inputFile, outputFolder, false);
    }

    public boolean extractFolder(String zipFile, String outputFolder, boolean recursive) {
        try {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            System.out.println(zipFile);
            int BUFFER = 2048;
            File file = new File(zipFile);
            ZipFile zip = new ZipFile(file);
            Enumeration zipFileEntries = zip.entries();
            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                LOGGER.get().log(Level.INFO, entry.getName());
                String currentEntry = entry.getName();
                File destFile = new File(folder, currentEntry);
                //destFile = new File(newPath, destFile.getName());
                File destinationParent = destFile.getParentFile();

                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }

                if (recursive && currentEntry.endsWith(".zip")) {
                    extractFolder(destFile.getAbsolutePath(), outputFolder, recursive);
                }
            }
            return true;
        } catch (IOException ex) {
            LOGGER.get().log(Level.SEVERE, "Error in zip file extraction - ", ex);
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        UnZipStep unZipStep = new UnZipStep();
        unZipStep.extractFolder("C:\\TDM\\dist\\tdm-dist-8.3.0.2-SNAPSHOT.zip", "C:\\TDM\\dist\\tdm-dist-8.3.0.2-SNAPSHOT", false);
    }
}