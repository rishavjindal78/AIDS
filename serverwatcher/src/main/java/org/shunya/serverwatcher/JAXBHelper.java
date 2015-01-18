package org.shunya.serverwatcher;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class JAXBHelper {
    public static void persistServerAppConfig(String folder, Class<ServerApp> aClass, ServerApp... retroApps) throws JAXBException, IOException {
        for (ServerApp retroApp : retroApps) {
            final String fileName = retroApp.getId() + ".xml";
            JAXBContext context = JAXBContext.newInstance(aClass);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(retroApp, new FileWriter(new File(folder, fileName)));
        }
    }

    public static byte[] convertServerAppToByteArray(Class<ServerApp> aClass, ServerApp app) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(aClass);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(10000);
        marshaller.marshal(app, outputStream);
        return outputStream.toByteArray();
    }

    public static ServerApp loadServerAppConfig(InputStream stream, Class<ServerApp> aClass) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(aClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        ServerApp serverApp = (ServerApp) unmarshaller.unmarshal(new StreamSource(stream));
        return serverApp;
    }

}
