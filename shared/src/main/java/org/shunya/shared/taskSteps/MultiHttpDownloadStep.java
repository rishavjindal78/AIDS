package org.shunya.shared.taskSteps;

import org.apache.commons.io.IOUtils;
import org.shunya.shared.AbstractStep;
import org.shunya.shared.StringUtils;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.PunterTask;
import org.shunya.shared.utils.InvalidStepInputException;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@PunterTask(author = "munish.chandel", name = "MultiHttpDownloadStep", description = "Downloads multiple files from http(s)", documentation = "MultiHttpDownloadStep.markdown")
public class MultiHttpDownloadStep extends AbstractStep {
    @InputParam(required = true, displayName = "Http Input = File Output Pairs", type = "textarea", description = "Enter HttpInput/FileOutput key=value pairs separated by lines")
    private String inputOutputTuples;

    @InputParam(required = false, displayName = "Download Parallel ?", type = "checkbox", description = "Downloads files in parallel")
    private boolean parallel = true;

    @Override
    public boolean run() {
        if (inputOutputTuples == null || inputOutputTuples.isEmpty())
            throw new InvalidStepInputException("There is no input specified for MultiUnzip Step");
        Stream<String> tuplesStream = asList(inputOutputTuples.split("[\r\n]")).stream();
        AtomicBoolean result = new AtomicBoolean(true);
        if (parallel) {
            getLogger().info("Using Prallel Streams to download the files.");
            tuplesStream = tuplesStream.parallel();
        }
        tuplesStream.forEach(tuple -> {
            if (!tuple.trim().isEmpty()) {
                String[] split = tuple.split("[;=,]");
                if (split.length != 2) {
                    throw new InvalidStepInputException("Each row must consists of <HttpUrl>=<OutputFile> pair !");
                }
                String url = split[0].trim();
                String outputFile = split[1].trim();
                getLogger().log(Level.INFO, "Downloading File " + url + " to location - " + outputFile);
                boolean fileResult = download(url, outputFile);
                result.set(result.get() & fileResult);
            }
        });
        getLogger().log(Level.INFO, "MultiHttpDownload Step is Complete now.");
        return result.get();
    }

    public boolean download(String url, String filePath) {
        File outputFile = new File(filePath);
        if (outputFile.isDirectory()) {
            String fileName = url.substring(url.lastIndexOf("/"));
            outputFile = new File(filePath, fileName);
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            if (url != null && !url.isEmpty() && url.startsWith("https")) {
                // Create a trust manager that does not validate certificate chains
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }};
                // Install the all-trusting trust manager
                final SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                // Create all-trusting host name verifier
                HostnameVerifier allHostsValid = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };

                // Install the all-trusting host verifier
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

                HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
                IOUtils.copy(conn.getInputStream(), fileOutputStream);
            } else {
                java.net.URLConnection conn = new URL(url).openConnection();
                IOUtils.copy(conn.getInputStream(), fileOutputStream);
            }
            return true;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, StringUtils.getExceptionStackTrace(e));
            return false;
        }
    }
}