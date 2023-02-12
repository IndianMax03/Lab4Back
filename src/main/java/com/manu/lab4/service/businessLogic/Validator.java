package com.manu.lab4.service.businessLogic;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public final class Validator {

    private static final int MAX_VALUE_LENGTH = 12;

    private static final double X_MAX_VALUE = 4;
    private static final double X_MIN_VALUE = -4;
    private static final double Y_MAX_VALUE = 5;
    private static final double Y_MIN_VALUE = -5;
    private static final Double[] SOURCE_R_VALUES = { -4d, -3d, -2d, -1d, 0d, 1d, 2d, 3d, 4d };
    private static final Set<Double> R_VALUES = new HashSet<>(Arrays.asList(SOURCE_R_VALUES));

    private Validator(){}

    public static boolean calculate(double x, double y, int r) {
        if (r > 0) {
            boolean firstQuater = (x >= 0) && (y >= 0) && (x*x + y*y <= r*r);
            boolean secondQuater = false;
            boolean thirdQuater = (x <= 0) && (y <= 0) && (y >= ( (-x/2) - (double) r/2) );
            boolean fourthQuater = (x >= 0) && (y <= 0) && (x <= r) && (y >= -r);
            return firstQuater || secondQuater || thirdQuater || fourthQuater;
        }
        if (r < 0) {
            r = -r;
            boolean firstQuater = (x >= 0) && (y >= 0) && (y <= ( (-x/2) + (double) r/2 ));
            boolean secondQuater = ( x <= 0) && (y >= 0) && (x >= -r) && (y <= r);
            boolean thirdQuater = (x <= 0) && (y <= 0) && (x*x + y*y <= r*r);
            boolean fourthQuater = false;
            return firstQuater || secondQuater || thirdQuater || fourthQuater;
        }
        return x == 0 && y == 0;
    }

    public static boolean checkCoordinatesValidity(double x, double y, double r) {
        boolean xValidity = (x >= X_MIN_VALUE) && (x <= X_MAX_VALUE) && (Double.valueOf(x).toString().length() <= MAX_VALUE_LENGTH);
        boolean yValidity = (y >= Y_MIN_VALUE) && (y <= Y_MAX_VALUE) && (Double.valueOf(y).toString().length() <= MAX_VALUE_LENGTH);
        boolean rValidity = R_VALUES.contains(r);
        return xValidity && yValidity && rValidity;
    }

    public static boolean checkUsernameAndPassword(String username, String password) {
        boolean isUsernameContainsSpaces = username.contains(" ");
        boolean isPasswordContainsSpaces = password.contains(" ");
        return !isUsernameContainsSpaces && !isPasswordContainsSpaces;
    }


}
