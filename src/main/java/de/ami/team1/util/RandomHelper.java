package de.ami.team1.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

public class RandomHelper {
    private static final Random random = new Random();

    /**
     * @param min the smallest possible integer
     * @param max the biggest possible integer
     * @return a random double between the given borders
     */
    public static int getInteger(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * @param min the smallest possible long
     * @param max the biggest possible long
     * @return a random long between the given borders
     */
    public static long getLong(long min, long max) {
        return min + (long) (Math.random() * (max - min));
    }

    /**
     * @param min the smallest possible double
     * @param max the biggest possible double
     * @return a random double between the given borders
     */
    public static double getDouble(double min, double max) {
        return ((max - min) * random.nextDouble()) + min;
    }

    /**
     * @param min the smallest possible float
     * @param max the biggest possible float
     * @return a random float between the given borders
     */
    public static float getFloat(float min, float max) {
        return ((max - min) * random.nextFloat()) + min;
    }

    /**
     * @param chance the chance that true will be returned in percent
     * @return true or false, depending on whether the probability hit the given chance or not
     */
    public static boolean hitPercentChance(double chance) {
        return getDouble(0., 100.) <= chance;
    }

    /**
     * returns a random local date that represents a date of birth.
     * @param minYearsOld the min age in years
     * @param maxYearsOld the max age in years
     * @return a random date of birth, the returned date is between the given years ago
     */
    public static LocalDate getDateOfBirth(int minYearsOld, int maxYearsOld) {
        long minDate = (long) (new Date().getTime() - (maxYearsOld * 365.25 * 24 * 60 * 60 * 1000));
        long maxDate = (long) (new Date().getTime() - (minYearsOld * 365.25 * 24 * 60 * 60 * 1000));

        return LocalDate.ofInstant(new Date(getLong(minDate, maxDate)).toInstant(), ZoneId.systemDefault());
    }

    /**
     * @param min a int between o and 254
     * @param max a int between 1 and 255
     * @return return a color as hex string including the heading #.
     *         The each rbg color is chosen randomly between the given boarders
     */
    public static String getHexColor(int min, int max) {
        int r = getInteger(min, max);
        int g = getInteger(min, max);
        int b = getInteger(min, max);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    /**
     * @return return a color as hex string including the heading #.
     *         The each rbg color is chosen randomly between 0,255.
     *         This function call is the same as RandomHelper.getHexColor(0, 255)
     * @see RandomHelper#getHexColor(int, int)
     */
    public static String getHexColor() {
        return getHexColor(0, 255);
    }
}
