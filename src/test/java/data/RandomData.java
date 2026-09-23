package data;

import java.util.Random;

public class RandomData {
    static Random random = new Random();

    public static String createUniqueProductName() {
        return "product" + System.currentTimeMillis();
    }

    public static int randomInt(int a, int b) {
        return random.nextInt(a, b);
    }
}
