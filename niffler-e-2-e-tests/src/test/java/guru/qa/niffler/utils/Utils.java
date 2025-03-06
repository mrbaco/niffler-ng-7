package guru.qa.niffler.utils;

import java.util.UUID;

public class Utils {

    public static String randomString() {
        return UUID.randomUUID().toString().split("-")[0];
    }

}
