package info.androidhive.materialdesign.app;


public class Config {
    // server URL configuration
//    public static final String URL = "https://demoapp.me/?mobile";
//    public static final String URL = "http://192.168.43.136/caller/?mobile";
    public static final String URL = "https://demoapp.me/?mobile";
    public static final long MAXIMUM_SLEEP = 2 * 60 * 1000;
    public static final long FETCH_LIMIT = 20;
    public static final long MINIMUM_SLEEP = 30 * 1000;
    public static final long REFRESH_RATE = 30 * 1000; //30 SECONDS

    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "ANHIVE";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
    public static String getLink(String link){
        return Config.URL+"/"+link;

    }
}