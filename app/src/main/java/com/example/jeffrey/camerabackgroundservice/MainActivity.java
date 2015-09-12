package com.example.jeffrey.camerabackgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;

import static com.example.jeffrey.camerabackgroundservice.CameraService.startActionFoo;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Camera mCamera;
    private CameraPreview mPreview;
    private TextView faceCount;
    private TextView confidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        faceCount = (TextView) findViewById(R.id.face_count);
        confidence = (TextView) findViewById(R.id.confidence);
/*
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KILLSERVICE);
        bManager.registerReceiver(bReceiver, intentFilter);
*/
        final Intent CameraIntent = new Intent(getApplicationContext(), CameraUI.class);
        startService(CameraIntent); //start photo-taking service
    }
/*
    public static final String KILLSERVICE = "DESTROY_CAMERA_SERVICE";

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(KILLSERVICE)) {

                //Do something with the string
            }
        }
    };
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //release camera when done
    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), CameraUI.class));
    }
}
