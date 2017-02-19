package com.martiancitizen.football;

import java.util.function.Supplier;


public class Utilities {

    public static final Supplier<AssertionError> OVE = () -> new AssertionError("Object validator not called");

    public static <T> T requiredArg(T arg) {
        if (arg == null) {
            throw new IllegalArgumentException("missing argument");
        }
        if (arg instanceof String && ((String) arg).isEmpty()) {
            throw new IllegalArgumentException("empty string argument");
        }
        return arg;
    }

    public static <T> boolean isSupplied(T arg) {
        if (arg == null) {
            return false;
        }
        if (arg instanceof String && ((String) arg).isEmpty()) {
            return false;
        }
        return true;
    }

    public static String convertEmptyToNull(String arg) {
        return arg == null ? null : (arg.isEmpty() ? null : arg);
    }
}
