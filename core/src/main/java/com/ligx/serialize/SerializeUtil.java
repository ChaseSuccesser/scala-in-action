package com.ligx.serialize;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: ligongxing.
 * Date: 2017年11月08日.
 */
public class SerializeUtil {

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static Objenesis objenesis = new ObjenesisStd(true);

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            synchronized (SerializeUtil.class) {
                schema = (Schema<T>) cachedSchema.get(clazz);
                if (schema == null) {
                    schema = RuntimeSchema.getSchema(clazz);
                    cachedSchema.put(clazz, schema);
                }
            }
        }
        return schema;
    }

    /**
     * 序列化POJO成byte数组
     *
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) throws IOException {
        if (obj == null) {
            return new byte[0];
        }
        Schema<T> schema = getSchema((Class<T>) obj.getClass());
        if (schema == null) {
            return new byte[0];
        }

        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        byte[] bytes;
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeTo(out, obj, schema, buffer);
            bytes = out.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            buffer.clear();
            if (out != null) {
                out.close();
            }
        }

        return bytes;
    }


    /**
     * byte数组反序列为POJO
     *
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0 || clazz == null) {
            return null;
        }
        Schema<T> schema = getSchema(clazz);
        if (schema == null) {
            return null;
        }
        T t = objenesis.newInstance(clazz);
        ProtostuffIOUtil.mergeFrom(bytes, t, schema);
        return t;
    }
}
