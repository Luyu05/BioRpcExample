package bio.rpc.serialize;

import bio.rpc.serialize.impl.HessianSerializer;

/**
 * Created by luyu on 2017/11/9.
 */
public abstract class Serializer {

    public abstract <T> byte[] serialize(T obj);
    public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);

    public enum SerializeEnum {

        HESSIAN(new HessianSerializer());

        public final Serializer serializer;
        private SerializeEnum (Serializer serializer) {
            this.serializer = serializer;
        }
        public static SerializeEnum match(String name, SerializeEnum defaultSerializer){
            for (SerializeEnum item : SerializeEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
            return defaultSerializer;
        }
    }
}
