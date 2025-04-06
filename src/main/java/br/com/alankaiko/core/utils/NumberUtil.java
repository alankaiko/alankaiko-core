package br.com.alankaiko.core.utils;

import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class NumberUtil {
    public static Boolean isPositive(BigDecimal bigDecimal) {
        return !isNull(bigDecimal) && BigDecimal.ZERO.compareTo(bigDecimal) < 0;
    }

    public static Boolean isNegative(BigDecimal bigDecimal) {
        return !isNull(bigDecimal) && bigDecimal.signum() == -1;
    }

    public static Boolean isNull(BigDecimal bigDecimal) {
        return bigDecimal == null;
    }

    public static Boolean isPositive(Integer integer) {
        return !isNull(integer) && integer > 0;
    }

    public static Boolean isNull(Integer integer) {
        return integer == null;
    }

    public static Boolean isPositive(Long long1) {
        return !isNull(long1) && long1 > 0L;
    }

    public static Boolean isNull(Long long1) {
        return long1 == null;
    }

    public static BigDecimal getBigDecimal(BigDecimal value) {
        return ObjectUtil.isNull(value) ? BigDecimal.ZERO : value;
    }

    public static Integer getInteger(Integer value) {
        return ObjectUtil.isNull(value) ? 0 : value;
    }

    public static Long getLong(Long value) {
        return ObjectUtil.isNull(value) ? 0L : value;
    }

    public static String getBigDecimalFormatted(BigDecimal bigDecimal, int decimalPlaces, boolean currency) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(LocaleContextHolder.getLocale());
        if (currency) {
            numberFormat = NumberFormat.getCurrencyInstance(LocaleContextHolder.getLocale());
        }

        numberFormat.setGroupingUsed(true);
        numberFormat.setMinimumFractionDigits(decimalPlaces);
        numberFormat.setMaximumFractionDigits(decimalPlaces);
        return numberFormat.format(bigDecimal);
    }
}
