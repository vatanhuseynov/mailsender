package io.getarrays.userservice.utils;

public class EmailUtils {

    public static String getEmailMessage(String name, String host, String token){
        return "hello " + name + "\n\n your account has been created. Please click the link  below to verify your account.\n\n"
                + getVerificationUrl(host,token);
    }

    public static String getVerificationUrl(String host, String token) {

        return host + "/api/users?token=" +token;                              // url e gore confirmationUserAccount endpointi isleyecek
    }

}
