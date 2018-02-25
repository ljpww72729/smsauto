package com.ljpww72729.smsauto;

/**
 * Created by LinkedME06 on 2/24/18.
 */

public class SmsInfo {
    // 收件人
    private String address = "";
    // 信息
    private String message = "";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
