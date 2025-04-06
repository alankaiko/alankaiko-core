package br.com.alankaiko.core.utils;

import br.com.alankaiko.core.model.AbstractEntity;
import org.apache.commons.beanutils.PropertyUtils;

public class ObjectUtil {
    public ObjectUtil() {
    }

    public static Boolean isNull(Object object) {
        return object == null;
    }

    public static <T extends AbstractEntity> Boolean isNewEntity(T object) {
        return isNull(object) || !NumberUtil.isPositive(object.getCodigo());
    }

    public static Object getEnumLabel(Object label) {
        Object value = null;
        if (label instanceof Enum) {
            try {
                value = PropertyUtils.getProperty(label, "name");
            } catch (Exception var4) {
            }

            try {
                if (!isNull(value)) {
                    value = PropertyUtils.getProperty(label, "description");
                }
            } catch (Exception var3) {
            }

            return isNull(value) ? value : label;
        } else {
            return label;
        }
    }
}
