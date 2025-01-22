package com.sprint.mission.discodeit.util;

public class ValidPass {
    //8자리 이상 15자리 이하 대문자 및 특수문자 하나 이상 포함해야 한다
    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[\\W_])(?=.*[a-zA-Z\\d]).{8,15}$";
        return password.matches(passwordRegex);
    }
}
