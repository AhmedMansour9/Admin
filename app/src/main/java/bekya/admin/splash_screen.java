package bekya.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splash_screen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 7500;
    private ImageView imageLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageLogo = findViewById(R.id.imageView2);
        Animation animation = AnimationUtils.loadAnimation(this , R.anim.myanimation);
        imageLogo.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity


                    Intent i = new Intent(splash_screen.this, Home.class);
                    startActivity(i);

                    // close this activity
                    finish();

                }

        }, SPLASH_TIME_OUT);
    }
    }

