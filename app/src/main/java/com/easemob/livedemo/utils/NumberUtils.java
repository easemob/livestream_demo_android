package com.easemob.livedemo.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
    private static final double THOUSAND = 1000.0;
    private static final double MILLIONS = 1000000.0;
    private static final double BILLION = 1000000000.0;
    private static final String THOUSAND_UNIT = "K";
    private static final String MILLION_UNIT = "M";
    private static final String BILLION_UNIT = "B";


    public static String amountConversion(int amount) {
        String result = String.valueOf(amount);
        double value = 0;
        double tempValue = 0;
        double remainder = 0;

        if (amount > THOUSAND && amount <= MILLIONS) {
            tempValue = amount / THOUSAND;
            remainder = amount % THOUSAND;
            if (remainder < (THOUSAND / 2)) {
                value = formatNumber(tempValue, 2, false);
            } else {
                value = formatNumber(tempValue, 2, true);
            }
            if (value == THOUSAND) {
                result = zeroFill(value / THOUSAND) + MILLION_UNIT;
            } else {
                result = zeroFill(value) + THOUSAND_UNIT;
            }
        } else if (amount > MILLIONS && amount <= BILLION) {
            tempValue = amount / MILLIONS;
            remainder = amount % MILLIONS;

            if (remainder < (MILLIONS / 2)) {
                value = formatNumber(tempValue, 2, false);
            } else {
                value = formatNumber(tempValue, 2, true);
            }
            if (value == MILLIONS) {
                result = zeroFill(value / MILLIONS) + BILLION_UNIT;
            } else {
                result = zeroFill(value) + MILLION_UNIT;
            }
        } else if (amount > BILLION) {
            tempValue = amount / BILLION;
            remainder = amount % BILLION;

            if (remainder < (BILLION / 2)) {
                value = formatNumber(tempValue, 2, false);
            } else {
                value = formatNumber(tempValue, 2, true);
            }
            result = zeroFill(value) + BILLION_UNIT;
        }
        return result;
    }


    public static Double formatNumber(double number, int decimal, boolean rounding) {
        BigDecimal bigDecimal = new BigDecimal(number);

        if (rounding) {
            return bigDecimal.setScale(decimal, RoundingMode.HALF_UP).doubleValue();
        } else {
            return bigDecimal.setScale(decimal, RoundingMode.DOWN).doubleValue();
        }
    }

    public static String zeroFill(double number) {
        String value = String.valueOf(number);

        if (value.contains(".")) {
            String decimalValue = value.substring(value.indexOf(".") + 1);
            if (decimalValue.length() < 2) {
                value = value + "0";
            }
        }
        return value;
    }
}
