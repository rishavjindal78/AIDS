package org.shunya.server;

import org.hibernate.internal.util.SerializationHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class Utility {
    public static <T> T clone(Class<T> clazz, T dtls) {
        T clonedObject = (T) SerializationHelper.clone((Serializable) dtls);
        return clonedObject;
    }

    public static void main(String[] args) throws IOException {
        int i = 0;
//        while (i < 10) {
            String password = "tanuja";
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);
            System.out.println(hashedPassword);
            i++;
//        }

        //Encode base 64
        String encodedBytes = Base64.getEncoder().encodeToString("JavaTips.net".getBytes("utf-8"));
        System.out.println("encodedBytes " + encodedBytes);
        //Decode base 64
        byte[] decodedBytes = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAepJREFUeNrEk79PFEEUx9/uDDd7v/AAQQnEQokmJCRGwc7/QeM/YGVxsZJQYI/EhCChICYmUJigNBSGzobQaI5SaYRw6imne0d2D/bYmZ3dGd+YQKEHYiyc5GUyb3Y+77vfeWNpreFfhvXfAWAAJtbKi7dff1rWK9vPHx3mThP2Iaipk5EzTg8Qmru38H7izmkFHAF4WH1R52654PR0Oamzj2dKxYt/Bbg1OPZuY3d9aU82VGem/5LtnJscLxWzfzRxaWNqWJP0XUadIbSzu5DuvUJpzq7sfYBKsP1GJeLB+PWpt8cCXm4+2+zLXx4guKiLXWA2Nc5ChOuacMEPv20FkT+dIawyenVi5VcAbcigWzXLeNiDRCdwId0LFm5IUMBIBgrp8wOEsFlfeCGm23/zoBZWn9a4C314A1nCoM1OAVccuGyCkPs/P+pIdVIOkG9pIh6YlyqCrwhRKD3GygK9PUBImIQQxRi4b2O+JcCLg8+e8NZiLVEygwCrWpYF0jQJziYU/ho2TUuCPTn8hHcQNuZy1/94sAMOzQHDeqaij7Cd8Dt8CatGhX3iWxgtFW/m29pnUjR7TSQcRCIAVW1FSr6KAVYdi+5Pj8yunviYHq7f72po3Y9dbi7CxzDO1+duzCXH9cEPAQYAhJELY/AqBtwAAAAASUVORK5CYII=");
        Files.write(Paths.get("d:/tmp/test111.png"), decodedBytes);
        System.out.println("decodedBytes " + new String(decodedBytes));
    }
}
