package com.letiko.opengldemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.letiko.opengldemo.R;


public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;    // Set the display time, in milliseconds (or extract it out as a configurable parameter)
    private final int SPLASH_FADEIN_LENGTH = 500;    // Set the display time, in milliseconds
    private final int SPLASH_FADEOUT_LENGTH = 1000;    // Set the display time, in milliseconds
    private WebView eulaview;
    private ViewGroup mainview;
    private LinearLayout llayout;
    private Animation fadein, fadeout;
    private AnimationSet animationSet1, animationSet2;
    private boolean isLogoLetikoEnabled, isEulaEnabled;
    private Handler handler = new Handler(); // Set handler to switch splash screens
    private String lang = null, eulatext = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Runnable showEula = new Runnable() {
            @Override
            public void run() {
                showEULA();
            }
        };

        isLogoLetikoEnabled = true;
        isEulaEnabled = true;

        animationSet1 = new AnimationSet(true);
        fadein = new AlphaAnimation(0.0f, 1.0f);
        fadein.setDuration(SPLASH_FADEIN_LENGTH);
        fadeout = new AlphaAnimation(1.0f, 0.0f);
        fadeout.setStartOffset(1000);
        fadeout.setDuration(SPLASH_FADEOUT_LENGTH);
        animationSet1.addAnimation(fadein);
        animationSet1.addAnimation(fadeout);

        eulatext = getResources().getString(R.string.eula_text);
	// 1st handler
        handler.postDelayed(showEula, SPLASH_DISPLAY_LENGTH);
        setContentView(R.layout.splash_logo_letiko);
        llayout = (LinearLayout) findViewById(R.id.splashlogoletiko);
        llayout.startAnimation(animationSet1);
    }


    protected void showEULA() {
        Button button;
        TextView tv;
        setContentView(R.layout.splash_eula);
        eulaview = (WebView) findViewById(R.id.agreementView);
        eulaview.setBackgroundColor(0xFFe5e5e5);

        eulaview.loadDataWithBaseURL("", eulatext, "text/html", "UTF-8", "");

        // Disable text selection from within WebView
        eulaview.setOnLongClickListener(new WebView.OnLongClickListener() {
            public boolean onLongClick(View v) {
                return true;
            }
        });
        tv = (TextView) findViewById(R.id.textheader);
        button = (Button) findViewById(R.id.agreementButton);

        tv.setText((getResources().getString(R.string.eula_header)));
        button.setText((getResources().getString(R.string.eula_button_ok)));
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.this.finish();
                Intent inte = new Intent();
                inte.setClass(getBaseContext(), DashboardActivity.class);
                startActivity(inte);
            }
        });
    }
}
