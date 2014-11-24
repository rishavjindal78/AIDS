package org.shunya.bot.engine;

import java.io.*;
import java.nio.file.FileSystems;

public class JavaSerializer {
    public static void save(MemoryApiState apiState) {
        FileOutputStream fos;
        ObjectOutputStream out;
        try {
            File file = new File(FileSystems.getDefault().getPath(System.getProperty("user.home")).toString(), "telegram.api");
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(apiState);
            out.close();
            System.out.println("ApiState saved successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static MemoryApiState load() {
        FileInputStream fis;
        ObjectInputStream in;
        try {
            File file = new File(FileSystems.getDefault().getPath(System.getProperty("user.home")).toString(), "telegram.api");
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
