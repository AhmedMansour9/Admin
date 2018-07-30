package bekya.admin.Remote;

import bekya.admin.Model.MyResponse;
import bekya.admin.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by ahmed on 1/16/2018.
 */

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAK8lWUHM:APA91bGEYlrZ0ZO_siwuvjUHPW1RKafnXslecIYEAt9bXVZ0qUmedFSgCNKXo96QHyFMvxZnUB6Q23ZY66s2cFP40rFqibmYb0NR5UDq7hhZ1ZGdIHiVNm8QwjxnrKN0umDFsQvX-Ev9"
    })
    @POST("fcm/send")
    Call<MyResponse> sendMessage(@Body Sender body);
}
