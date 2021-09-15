package pers.vincent.vertxboot.http;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Vincent
 * @date: 2021/9/15
 */
public class DefaultTypeConverter {
    private static final Map<Class<?>, Object> primitiveDefaultValues = new HashMap<Class<?>, Object>() {
        {
            Map<Class<?>, Object> map = new HashMap<>();
            map.put(Boolean.TYPE, Boolean.FALSE);
            map.put(Byte.TYPE, Byte.valueOf((byte) 0));
            map.put(Short.TYPE, Short.valueOf((short) 0));
            map.put(Character.TYPE, new Character((char) 0));
            map.put(Integer.TYPE, Integer.valueOf(0));
            map.put(Long.TYPE, Long.valueOf(0L));
            map.put(Float.TYPE, new Float(0.0f));
            map.put(Double.TYPE, new Double(0.0));
            map.put(BigInteger.class, new BigInteger("0"));
            map.put(BigDecimal.class, new BigDecimal(0.0));
        }
    };

    public static Object convertValue(Object value, Class<?> toType) {
        if (value != null) {
            if (toType == Integer.class) {
                return integerValue(value);
            }
            if (toType == Integer.TYPE) {
                return intValue(value);
            }
            if (toType == Double.class) {
                return doubleValue(value);
            }
            if (toType == Double.TYPE) {
                return dValue(value);
            }
            if (toType == Boolean.class) {
                return booleanValue(value);
            }
            if (toType == Boolean.TYPE) {
                return boolValue(value);
            }
            if (toType == Long.class) {
                return longValue(value);
            }
            if (toType == Long.TYPE) {
                return lValue(value);
            }
            if (toType == Float.class) {
                return floatValue(value);
            }
            if (toType == Float.TYPE) {
                return fValue(value);
            }
            if (toType == BigDecimal.class) {
                return bigDecValue(value);
            }
            if (toType == String.class) {
                return stringValue(value);
            }
            if (Enum.class.isAssignableFrom(toType)) {
                return enumValue(value, toType);
            }
        } else {
            if (toType.isPrimitive()) {
                return primitiveDefaultValues.get(toType);
            } else {
                return null;
            }
        }
        throw new RuntimeException("Unrecognized type:" + toType.getName());
    }

    public static Integer integerValue(Object value) {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return null;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).intValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1 : 0;
        }
        return Integer.parseInt(value.toString().trim());

    }

    public static int intValue(Object value) {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return 0;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).intValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1 : 0;
        }
        return Integer.parseInt(value.toString().trim());
    }

    public static Boolean booleanValue(Object value) {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return null;
        }
        Class<?> c = value.getClass();
        if (c == Boolean.class)
            return (Boolean) value;
        if (c == Character.class) {
            return (Character) value != 0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        if (c == String.class) {
            return value.toString().equals("true") || value.toString().equals("1") || value.toString().equals("yes");
        }
        return null;
    }

    public static boolean boolValue(Object value) {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return false;
        }
        Class<?> c = value.getClass();
        if (c == Boolean.class) {
            return (Boolean) value;
        }
        if (c == Character.class) {
            return (Character) value != 0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        if (c == String.class) {
            return value.toString().equals("true") || value.toString().equals("1") || value.toString().equals("yes");
        }
        return false;
    }

    public static Long longValue(Object value) throws NumberFormatException {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return null;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).longValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1L : 0L;
        }
        return Long.parseLong(value.toString().trim());
    }

    public static long lValue(Object value) throws NumberFormatException {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return 0L;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).longValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1L : 0L;
        }
        return Long.parseLong(value.toString().trim());
    }

    public static Double doubleValue(Object value) throws NumberFormatException {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return null;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).doubleValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1.0 : 0.0;
        }
        return Double.parseDouble(value.toString().trim());
    }

    public static Double dValue(Object value) throws NumberFormatException {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return 0.0;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).doubleValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1.0 : 0.0;
        }
        return Double.parseDouble(value.toString().trim());
    }

    public static Float floatValue(Object value) throws NumberFormatException {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return null;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).floatValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1.0f : 0.0f;
        }
        return Float.parseFloat(value.toString().trim());
    }

    public static float fValue(Object value) throws NumberFormatException {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return 0.0f;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number) value).floatValue();
        }
        if (c == Boolean.class) {
            return (Boolean) value ? 1.0f : 0.0f;
        }
        return Float.parseFloat(value.toString().trim());
    }

    public static BigDecimal bigDecValue(Object value) throws NumberFormatException {
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            return null;
        }
        Class<?> c = value.getClass();
        if (c == BigDecimal.class) {
            return (BigDecimal) value;
        }
        if (c == BigInteger.class) {
            return new BigDecimal((BigInteger) value);
        }
        if (c.getSuperclass() == Number.class) {
            return new BigDecimal(((Number) value).doubleValue());
        }
        if (c == Boolean.class) {
            return BigDecimal.valueOf((Boolean) value ? 1 : 0);
        }
        if (c == Character.class) {
            return BigDecimal.valueOf(((Character) value).charValue());
        }
        return new BigDecimal(value.toString().trim());
    }

    public static String stringValue(Object value) {
        String result;
        if (value == null) {
            result = null;
        } else {
            result = value.toString().trim();
        }
        return result;
    }

    public static Enum<?> enumValue(Object value, Class toClass) {
        Enum<?> result = null;
        if (value == null || StringUtils.isBlank(String.valueOf(value).trim())) {
            result = null;
        } else if (value instanceof String) {
            result = Enum.valueOf(toClass, (String) value);
        }
        return result;
    }
}
