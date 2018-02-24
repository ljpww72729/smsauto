package com.ljpww72729.smsauto;

import java.util.regex.Pattern;

/**
 * 校验工具类
 *
 * Created by LinkedME06 on 2/23/18.
 */

public class VerifyUtils {

    /**
     * 校验是否为手机号
     *
     * @param phoneNumber 手机号字符串
     * @return true:是 false:否
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().length() < 0) {
            return false;
        }
        //手机号码正则表达式
        String PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        return Pattern.matches(PHONE_NUMBER_REG, phoneNumber);
    }
}
