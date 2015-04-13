package org.shunya.shared.taskSteps;

import org.shunya.shared.AbstractStep;
import org.shunya.shared.StringUtils;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.PunterTask;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.Arrays.asList;

@PunterTask(author = "munish.chandel", name = "ZipStep", description = "Zips a given directory into single archive", documentation = "zipTaskStep.markdown")
public class ZipStep extends AbstractStep {
    @InputParam(required = true, displayName = "Input Output File Pairs", type = "textarea", description = "Enter input/output key=value pairs separated by lines")
    private String inputOutputTuples;

    @InputParam(required = false, displayName = "Compression Level", type = "range", description = "Compression Level (0-9)", misc = "size=2 min=1 max=9 value=6")
    private int compressionLevel;

    @Override
    public boolean run() {
        Logger logger = LOGGER.get();
        AtomicBoolean result = new AtomicBoolean(true);
        if(inputOutputTuples!=null && !inputOutputTuples.isEmpty()) {
            List<String> tuples =asList(inputOutputTuples.split("[\r\n]"));
            tuples.parallelStream().filter(s -> !s.isEmpty()).forEach(s1 -> {
                String[] split = s1.split("[=;,]");
                if (split.length == 2) {
                    String inputDir = split[0].trim();
                    String outputFile = split[1].trim();
                    logger.log(Level.INFO, "zipping Directory " + inputDir + " to location - " + outputFile);
                    try {
                        result.set(result.get() & zipIt(outputFile, inputDir, compressionLevel));
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error Zipping Directory " + inputDir, e);
                        result.set(false);
                    }
                }
            });
        }
        return result.get();
    }

    public boolean zipIt(String zipFile, String inputDir, int compression) {
        byte[] buffer = new byte[1024];
        List<String> fileList = new ArrayList<>();
        try {
            generateFileList(inputDir, new File(inputDir), fileList);
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            zos.setLevel(compression);
            LOGGER.get().fine(() -> "Output to Zip : " + zipFile);
            for (String file : fileList) {
                LOGGER.get().fine(() -> "File Added : " + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);
                FileInputStream in = new FileInputStream(inputDir + File.separator + file);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
            }
            zos.closeEntry();
            //remember close it
            zos.close();
            LOGGER.get().fine(() -> "Zipping is Done");
            return true;
        } catch (IOException ex) {
            LOGGER.get().severe(() -> "Exception while zipping file - " + StringUtils.getExceptionStackTrace(ex));
            ex.printStackTrace();
        }
        return false;
    }

    public void generateFileList(String sourceDir, File node, List<String> fileList) {
        //add file only
        if (node.isFile()) {
            String file = node.getAbsoluteFile().toString();
            fileList.add(file.substring(sourceDir.length() + 1, file.length()));
        }
        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(sourceDir, new File(node, filename), fileList);
            }
        }
    }
}