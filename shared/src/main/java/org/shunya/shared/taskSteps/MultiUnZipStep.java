package org.shunya.shared.taskSteps;

import org.shunya.shared.AbstractStep;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.PunterTask;
import org.shunya.shared.utils.InvalidStepInputException;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.Arrays.asList;

@PunterTask(author = "munish.chandel", name = "MultiUnZipStep", description = "Unzip multiple files in single step", documentation = "MultiUnzipTaskStep.markdown")
public class MultiUnZipStep extends AbstractStep {
    @InputParam(required = true, displayName = "Input Output File Pairs", type = "textarea", description = "Enter input/output key=value pairs separated by lines")
    private String inputOutputTuples;

    @InputParam(required = false, displayName = "unzip recursively ?", type = "checkbox", description = "Unzip files recursively ?")
    private boolean recursive = false;

    @Override
    public boolean run() {
        if(inputOutputTuples==null || inputOutputTuples.isEmpty())
            throw new InvalidStepInputException("There is no input specified for MultiUnzip Step");
        List<String> tuples = asList(inputOutputTuples.split("[\r\n]"));
        AtomicBoolean result = new AtomicBoolean(true);
        tuples.parallelStream().forEach(tuple -> {
            String[] split = tuple.split("[;=,]");
            if(split.length != 2){
                throw new InvalidStepInputException("Each row must consists of <zipFile>=<outputDir> pair ?");
            }
            String zipFile = split[0];
            String outputDir = split[1];
            LOGGER.get().log(Level.INFO, "unzipping File " + zipFile + " to location - " + outputDir);
            boolean fileResult = extractFolder(zipFile, outputDir, recursive);
            result.set(result.get() & fileResult);
        });
        LOGGER.get().log(Level.INFO, "MultiUnzip Step is Complete now.");
        return result.get();
    }

    public boolean extractFolder(String zipFile, String outputFolder, boolean recursive) {
        File file = new File(zipFile);
        try (ZipFile zip = new ZipFile(file)) {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            LOGGER.get().log(Level.INFO, "Processing file - " + zipFile);
            int BUFFER = 2048;
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
        MultiUnZipStep unZipStep = new MultiUnZipStep();
        unZipStep.extractFolder("C:\\TDM\\dist\\tdm-dist-8.3.0.2-SNAPSHOT.zip", "C:\\TDM\\dist\\tdm-dist-8.3.0.2-SNAPSHOT", false);
    }
}