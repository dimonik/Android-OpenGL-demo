package com.letiko.opengldemo.ui;

//import android.app.ActionBar;

//import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.Menu;
//import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.letiko.opengldemo.R;
import com.letiko.opengldemo.utils.LoggerConfig;
import com.letiko.opengldemo.render.AnotherGLSurfaceView;

//import android.support.v4.;


public class DashboardActivity extends Activity {
    private static final String LOG_TAG = "DashboardActivity";
    private final int ID_MENU_ABOUT = 1, ID_MENU_CLOSE = 2;
    private AnotherGLSurfaceView anotherGLSurfaceView;
    private String menuAboutTitle, menuCloseTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        //Checking If the System Supports OpenGL ES 2.0
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportsEs2) {
            if (LoggerConfig.ON) Log.i(LOG_TAG, "OpenGL ES 2.0 is supported");
        }

        setContentView(R.layout.dashboard_activity);
        getActionBar().setHomeButtonEnabled(false);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Log.i(LOG_TAG, "Display DPI = " + Float.toString(metrics.density));

        anotherGLSurfaceView = (AnotherGLSurfaceView) findViewById(R.id.anotherglsurfaceview);

	    /*
        ActionBar bar = getActionBar();
	    if (bar != null) {Log.i(LOG_TAG, "BAR is HERE");};
	    bar.hide();
	    */
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        anotherGLSurfaceView.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        anotherGLSurfaceView.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuAboutTitle = getResources().getString(R.string.menu_about_title);
        menuCloseTitle = getResources().getString(R.string.menu_close_title);
        menu.add(0, ID_MENU_ABOUT, 0, menuAboutTitle)
                .setIcon(R.drawable.menu_about)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, ID_MENU_CLOSE, 0, menuCloseTitle)
                .setIcon(R.drawable.menu_close)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //method is called right before the menu is shown
        menu.findItem(ID_MENU_ABOUT).setTitle(menuAboutTitle);
        menu.findItem(ID_MENU_CLOSE).setTitle(menuCloseTitle);
        return super.onPrepareOptionsMenu(menu);
    }

    ;

    /*
     @Override
     void invalidateOptionsMenu(){};
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == ID_MENU_ABOUT) {
            Toast.makeText(this, "OpenGL ES 2.0 demo by Dmytro Nikandrov", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == ID_MENU_CLOSE) {
            DashboardActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


}