package org.shunya.server;

import org.hibernate.internal.util.SerializationHelper;

import java.io.Serializable;

public class Utility {
    public static <T> T clone(Class<T> clazz, T dtls) {
        T clonedObject = (T) SerializationHelper.clone((Serializable) dtls);
        return clonedObject;
    }
}
