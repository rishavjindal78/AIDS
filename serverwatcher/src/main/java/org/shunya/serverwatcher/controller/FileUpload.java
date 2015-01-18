package org.shunya.serverwatcher.controller;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.shunya.serverwatcher.JAXBHelper;
import org.shunya.serverwatcher.ServerApp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "FileUploadServlet", urlPatterns = "/fileUpload")
public class FileUpload extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            response.getWriter().println("You are not trying to upload<br/>");
            return;
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // Set factory constraints
        factory.setSizeThreshold(100 * 1024);
        factory.setRepository(new File("tmp"));

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(100 * 1024);

        try {
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                if (item.isFormField()) {
                    System.out.println("Form field " + name + " with value " + Streams.asString(stream) + " detected.");
                } else {
                    System.out.println("File field " + name + " with file name " + item.getName() + " detected.");
//                    WebAppContext context = WebAppContext.getWebAppContext(request.getServletContext());
//                    ServerHealthService healthManager = context.getServerHealthManager();
//                    ServerApp serverApp = JAXBHelper.loadServerAppConfig(stream, ServerApp.class);
//                    healthManager.addServerApp(serverApp);
//                    healthManager.scheduleJob(serverApp);
//                    JAXBHelper.persistServerAppConfig((String) request.getServletContext().getAttribute("server-app-path"), ServerApp.class, serverApp);
//                    stream.close();
//                    response.getWriter().println("Success");
                }
            }
        } catch (Exception e) {
            response.getWriter().println("Error Loading Config");
            e.printStackTrace();
        }
    }
}
