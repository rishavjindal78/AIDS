package org.shunya.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ReadLargeFile {
    public static void main(String[] args) throws IOException {
        final Path path = Paths.get("C:\\temp");
        final Stream<Path> files = Files.list(path);
        files.forEach((t) -> extractErrors(t.toFile().getAbsolutePath()));
//      extractErrors("C:\\temp\\tomcat7-stdout.2014-03-05.log");
    }

    public static void extractErrors(String fileName) {
        if (!fileName.toLowerCase().endsWith(".log"))
            return;
        System.out.println("Processing File = " + fileName);
        String sCurrentLine;
        try (PrintWriter bw = new PrintWriter(new FileWriter(fileName + ".err"));
             BufferedReader br = new BufferedReader(new FileReader(fileName), 1000000);) {
            long line = 0;
            boolean show = false;
            int activeLines = 0;
            int maxLines = 200;
            while ((sCurrentLine = br.readLine()) != null) {
                ++line;
                if (sCurrentLine.contains("ERROR")) {
                    show = true;
                    activeLines = 0;
                }
                if (show) {
                    ++activeLines;
                    if (activeLines > maxLines)
                        show = false;
                    bw.println(sCurrentLine);
                    System.out.println(sCurrentLine);
                }
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}