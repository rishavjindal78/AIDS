package org.shunya.serverwatcher;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ServerAppLoader {
    private String folderPath;

    public ServerAppLoader(String folderPath) {
        this.folderPath = folderPath;
    }

    public List<ServerApp> getServerApps(){
        List<ServerApp> serverAppList=new ArrayList<>(10);
        File file =new File(folderPath);
        if(file.isDirectory()){
            File[] xmls = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
            for (File xml : xmls) {
                try {
                    serverAppList.add(JAXBHelper.loadServerAppConfig(new FileInputStream(xml), ServerApp.class));
//                    System.out.println("added xml = " + xml);
                } catch (JAXBException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return serverAppList;
    }
}
