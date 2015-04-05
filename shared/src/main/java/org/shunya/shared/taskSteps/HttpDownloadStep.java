package org.shunya.shared.taskSteps;

import org.apache.commons.io.IOUtils;
import org.shunya.shared.AbstractStep;
import org.shunya.shared.StringUtils;
import org.shunya.shared.annotation.InputParam;
import org.shunya.shared.annotation.OutputParam;
import org.shunya.shared.annotation.PunterTask;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.logging.Level;

@PunterTask(author = "munishc", name = "HttpGetTask", description = "Plays HTTP GET Request on the given URL.", documentation = "src/main/resources/docs/TextSamplerDemoHelp.html")
public class HttpDownloadStep extends AbstractStep {
    @InputParam(required = false, type = "url", displayName = "Http Url", description = "enter httpUrl here")
    private String httpUrl;
    @InputParam(required = false, type = "url", displayName = "Https Url", description = "enter httpsUrl here")
    private String httpsUrl;
    @InputParam(required = true, displayName = "Local Path", description = "Local Folder Path")
    private String localPath;
    @InputParam(required = true, displayName = "File Name", description = "file name")
    private String fileName;

    @OutputParam(displayName = "Http Response")
    private String httpResponse;

    @Override
    public boolean run() {
        boolean status = false;
        LOGGER.get().log(Level.INFO, httpUrl == null ? httpsUrl : httpUrl);
        if (fileName == null || fileName.isEmpty()) {
            if (httpsUrl != null && !httpsUrl.isEmpty()) {
                fileName = httpsUrl.substring(httpsUrl.lastIndexOf("/"));
            } else {
                fileName = httpUrl.substring(httpUrl.lastIndexOf("/"));
            }
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(localPath, fileName));) {
            if (httpsUrl != null && !httpsUrl.isEmpty()) {
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

                HttpsURLConnection conn = (HttpsURLConnection) new URL(httpsUrl).openConnection();
                IOUtils.copy(conn.getInputStream(), fileOutputStream);
            } else {
                java.net.URLConnection conn = new URL(httpUrl).openConnection();
                IOUtils.copy(conn.getInputStream(), fileOutputStream);
            }
            status = true;
            httpResponse = "Success";
        } catch (Exception e) {
            LOGGER.get().log(Level.SEVERE, StringUtils.getExceptionStackTrace(e));
            httpResponse = "Fail";
        }
        return status;
    }
}