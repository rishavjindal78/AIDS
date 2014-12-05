package org.shunya.server.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class JavaSerializer {
    private static final Logger logger = LoggerFactory.getLogger(JavaSerializer.class);
    public static void save(MemoryApiState apiState, String folder) {
        FileOutputStream fos;
        ObjectOutputStream out;
        try {
            File file = new File(folder, "telegram.api");
            logger.info("Saving Telegram API Key to Path = " + file.getAbsolutePath());
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(apiState);
            out.close();
            System.out.println("ApiState saved successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static MemoryApiState load(String folder) {
        FileInputStream fis;
        ObjectInputStream in;
        try {
            File file = new File(folder, "telegram.api");
            logger.info("Loading Telegram API Key from Path = " + file.getAbsolutePath());
            fis = new FileInputStream(file);
            in = new ObjectInputStream(fis);
            MemoryApiState p = (MemoryApiState) in.readObject();
            in.close();
            p.start(false);
            return p;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
