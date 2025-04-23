package btscore.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author JoostMeulenkamp
 */
public class TypeCastUtils {

    private final static Map<Class, List<Class>> castMap = new HashMap<>();

    static {
        castMap.put(Byte.class, Arrays.asList(
                Object.class,
                byte.class,
                short.class,
                int.class,
                long.class,
                float.class,
                double.class,
                String.class
        ));
        castMap.put(Short.class, Arrays.asList(
                Object.class,
                short.class,
                int.class,
                long.class,
                float.class,
                double.class,
                String.class
        ));
        castMap.put(Integer.class, Arrays.asList(
                Object.class,
                int.class,
                long.class,
                float.class,
                double.class,
                String.class
        ));
        castMap.put(Long.class, Arrays.asList(
                Object.class,
                long.class,
                float.class,
                double.class,
                String.class
        ));
        castMap.put(Float.class, Arrays.asList(
                Object.class,
                float.class,
                double.class,
                String.class
        ));
        castMap.put(Double.class, Arrays.asList(
                Object.class,
                double.class,
                String.class
        ));
        castMap.put(Boolean.class, Arrays.asList(
                Object.class,
                boolean.class,
                String.class
        ));
        castMap.put(byte.class, Arrays.asList(
                Object.class,
                Byte.class,
                short.class,
                int.class,
                long.class,
                char.class,
                float.class,
                double.class,
                String.class
        ));
        castMap.put(short.class, Arrays.asList(
                Object.class,
                Short.class,
                byte.class,
                int.class,
                long.class,
                char.class,
                float.class,
                double.class,
                String.class
        ));
        castMap.put(int.class, Arrays.asList(
                Object.class,
                Integer.class,
                byte.class,
                short.class,
                long.class,
                char.class,
                float.class,
                double.class,
                String.class,
                Number.class
        ));
        castMap.put(long.class, Arrays.asList(
                Object.class,
                Long.class,
                byte.class,
                short.class,
                int.class,
                char.class,
                float.class,
                double.class,
                String.class,
                Number.class
        ));
        castMap.put(char.class, Arrays.asList(
                Object.class,
                byte.class,
                short.class,
                int.class,
                long.class,
                float.class,
                double.class,
                String.class
        ));
        castMap.put(float.class, Arrays.asList(
                Object.class,
                Float.class,
                byte.class,
                short.class,
                int.class,
                long.class,
                char.class,
                double.class,
                String.class,
                Number.class
        ));
        castMap.put(double.class, Arrays.asList(
                Object.class,
                Double.class,
                byte.class,
                short.class,
                int.class,
                long.class,
                char.class,
                float.class,
                String.class,
                Number.class
        ));
        castMap.put(boolean.class, Arrays.asList(
                Object.class,
                Boolean.class,
                String.class
        ));
    }



    public static boolean isCastableTo(Class<?> from, Class<?> to) {

        if (to.isAssignableFrom(from)) {
            return true;
        } else if (castMap.containsKey(from) && castMap.get(from).contains(to)) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean contains(Class type) {
        return castMap.containsKey(type);
    }
}
