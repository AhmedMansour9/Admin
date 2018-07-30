package bekya.admin.Common;


import bekya.admin.Remote.APIService;
import bekya.admin.Remote.RetrofitClient;

/**
 * Created by
 * on 10/2/2017.
 */

public class Common {

    public static final int PICKUP_IMAGE_REQUEST = 71;
    public static String topicName = "News";
    public static final String Base_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMClient() {
        return RetrofitClient.getClient(Base_URL).create(APIService.class);

    }
}
