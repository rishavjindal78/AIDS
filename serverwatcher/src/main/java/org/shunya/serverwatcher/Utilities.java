package org.shunya.serverwatcher;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;

public class Utilities implements Serializable {
    public static long safeParseLong(long id, String parameter) {
        try {
            id = Long.parseLong(parameter);
        } catch (Exception e) {
        }
        return id;
    }

    public static void main(String[] args) throws IOException {
        List<FileSystemProvider> fs= FileSystemProvider.installedProviders();
//        Files.getFileStore(Paths.get("c:\\")).getUnallocatedSpace()/1024/1024;
    }
}