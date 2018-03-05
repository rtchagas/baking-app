package com.rtchagas.udacity.bakingtime.util;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.rtchagas.udacity.bakingtime.core.Recipe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SerializationUtil {

    /**
     * Read the object from Base64 string.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T fromString(@NonNull String s) {
        try {
            byte[] data = Base64.decode(s, Base64.DEFAULT);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return (T) o;
        }
        catch (Exception ex) {
            Log.w("BackingApp", "Couldn't deserialize the object", ex);
        }

        return null;
    }

    /**
     * Write the object to a Base64 string.
     */
    @Nullable
    public static String toString(@NonNull Serializable object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        }
        catch (Exception ex) {
            Log.w("BackingApp", "Couldn't serialize the object", ex);
        }

        return null;
    }

}
