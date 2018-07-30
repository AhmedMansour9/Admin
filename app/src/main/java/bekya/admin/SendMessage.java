package bekya.admin;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import bekya.admin.Common.Common;
import bekya.admin.Model.MyResponse;
import bekya.admin.Model.Notification;
import bekya.admin.Model.Sender;
import bekya.admin.Remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {
    EditText  name, descrip;
    Button sendMessage;
APIService mService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        mService = Common.getFCMClient();
        name = findViewById(R.id.NameMsg);
        descrip = findViewById(R.id.descripMsg);
        sendMessage = findViewById(R.id.btnsend);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog waitingdialog = new ProgressDialog(SendMessage.this);
                waitingdialog.setTitle("يتم التحميل ..");
                waitingdialog.show();
                //create message
                Notification notification = new Notification(name.getText().toString(),descrip.getText().toString());
                Sender toTopic = new Sender();
                toTopic.to = new StringBuilder("/topics/").append(Common.topicName).toString();
                toTopic.notification = notification;
                mService.sendMessage(toTopic)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.isSuccessful())
                                    waitingdialog.dismiss();
                                Toast.makeText(SendMessage.this, "message sent", Toast.LENGTH_SHORT).show();
                                finish();

                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                waitingdialog.dismiss();
                                Toast.makeText(SendMessage.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();


                            }
                        });


            }
        });
    }
}
