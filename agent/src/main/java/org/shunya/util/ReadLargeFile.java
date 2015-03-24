package org.shunya.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ReadLargeFile {
    public static void main(String[] args) throws IOException, InterruptedException {
        long millis = 66506;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        System.out.println(hms);

        Instant start = Instant.now();
        Thread.sleep(66553);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println(duration.toMinutes()+"m :"+duration.getSeconds()+"s"); // prints PT1M3.553S

//        final Path path = Paths.get("C:\\temp");
//        final Stream<Path> files = Files.list(path);
//        files.forEach((t) -> extractErrors(t.toFile().getAbsolutePath()));
//      extractErrors("C:\\temp\\tomcat7-stdout.2014-03-05.log");
    }

    public static void extractErrors(String fileName) {
        if (!fileName.toLowerCase().endsWith(".log"))
            return;
        System.out.println("Processing File = " + fileName);
        String sCurrentLine;
        try (PrintWriter bw = new PrintWriter(new FileWriter(fileName + ".err"));
             BufferedReader br = new BufferedReader(new FileReader(fileName), 1000000)) {
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
                    if (activeLines > maxLines) {
                        show = false;
                        System.out.println("\n\n\n\n+++++++++++End of Exception++++++++++\n\n\n\n ");
                        bw.println("\n\n\n\n+++++++++++End of Exception++++++++++\n\n\n\n ");
                    }
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