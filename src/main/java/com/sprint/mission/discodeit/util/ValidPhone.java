package com.sprint.mission.discodeit.util;

public class ValidPhone {
    public static boolean isValidPhone(String phoneNumber) {
        String phoneRegex = "^010-\\d{4}-\\d{4}$";
        return phoneNumber.matches(phoneRegex);
    }
}
