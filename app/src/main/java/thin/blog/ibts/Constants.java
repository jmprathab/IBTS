package thin.blog.ibts;


public class Constants {
    public static final String SHARED_PREFS_USER_DATA = "user_data";
    public static final String USER_DATA_NAME = "name";
    public static final String USER_DATA_MOBILE = "mobile";
    public static final String USER_DATA_PASSWORD = "password";
    public static final String USER_DATA_EMAIL = "email";
    public static final String USER_DATA_USER_ID = "user_id";
    public static final String SUCCESSFUL_LOGIN_HISTORY = "successful_login_history";
    public static final String SUCCESSFUL_REGISTRATION_HISTORY = "successful_registration_history";

    //URL Addresses for Server
    public static final String LOGIN;
    public static final String SIGNUP;

    //for testing
    //set localhost = false for testing from webhost
    private static final Boolean localhost = true;

    private static final String ADDRESS;


    static {
        if (localhost) {
            ADDRESS = "http://192.168.1.2/ibts/";
        } else {
            ADDRESS = "http://www.thin.comyr.com/";
        }
        LOGIN = ADDRESS + "login.php";
        SIGNUP = ADDRESS + "register.php";
    }
}
