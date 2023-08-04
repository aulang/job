package cn.aulang.job.core.utils;

import cn.aulang.job.core.enums.DataType;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 * 字段类型帮助类
 *
 * @author wulang
 */
public class FieldUtils {

    public static DataType getType(Field field) {
        Class<?> clazz = field.getType();

        if (isArray(field)) {
            if (clazz.getComponentType() != null) {
                return getType(clazz.getComponentType());
            } else if (field.getGenericType() instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                if (types != null && types.length == 1 && types[0] instanceof Class<?>) {
                    return getType((Class<?>) types[0]);
                }
            }
        } else {
            return getType(clazz);
        }

        return DataType.UNKNOWN;
    }

    public static DataType getType(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == Boolean.TYPE) {
                return DataType.BOOLEAN;
            } else if (clazz == Byte.TYPE) {
                return DataType.INTEGER;
            } else if (clazz == Short.TYPE) {
                return DataType.INTEGER;
            } else if (clazz == Integer.TYPE) {
                return DataType.INTEGER;
            } else if (clazz == Long.TYPE) {
                return DataType.INTEGER;
            } else if (clazz == Float.TYPE) {
                return DataType.FLOAT;
            } else if (clazz == Double.TYPE) {
                return DataType.FLOAT;
            }
        }

        if (clazz == String.class) {
            return DataType.STRING;
        }

        if (clazz == Integer.class || clazz == Long.class) {
            return DataType.INTEGER;
        }

        if (clazz == Float.class || clazz == Double.class || clazz == BigDecimal.class) {
            return DataType.FLOAT;
        }

        if (clazz == Boolean.class) {
            return DataType.BOOLEAN;
        }

        if (clazz == Date.class) {
            return DataType.DATE;
        }

        return DataType.UNKNOWN;
    }

    public static boolean isArray(Field field) {
        return field.getType().isArray()
                || (field.getGenericType() instanceof GenericArrayType)
                || Collection.class.isAssignableFrom(field.getType());
    }
}