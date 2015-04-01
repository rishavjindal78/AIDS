package org.shunya.server;

import org.hibernate.internal.util.SerializationHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;

public class Utility {
    public static <T> T clone(Class<T> clazz, T dtls) {
        T clonedObject = (T) SerializationHelper.clone((Serializable) dtls);
        return clonedObject;
    }

    public static void main(String[] args) {
        int i = 0;
//        while (i < 10) {
            String password = "tanuja";
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);
            System.out.println(hashedPassword);
            i++;
//        }

    }
}
