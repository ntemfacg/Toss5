package com.example.ntemfacg.toss5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by pheon on 11/12/2016.
 */
public class TossGround extends Fragment implements SensorEventListener {

    CustomDrawableView mCustomDrawableView = null;
    ShapeDrawable mDrawable = new ShapeDrawable();
    public float xPosition, xAcceleration,xVelocity = 0.0f;
    public float yPosition, yAcceleration,yVelocity = 0.0f;
    public float xmax,ymax;
    private Bitmap mBitmap;
    private Bitmap mWood;
    private SensorManager sensorManager = null;
    public float frameTime = 0.400f;

    /** Called when the activity is first created. */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getActivity().requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getActivity().getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View rootView = inflater.inflate(R.layout.fragment_toss_ground, container, false);
        FrameLayout relativeLayout = (FrameLayout) rootView.findViewById(R.id.toss_ground);
        relativeLayout.addView(new CustomDrawableView(getActivity()));
        return rootView;
    }

    @Override
    public void onStart()
    {

        super.onStart();

        //Set FullScreen & portrait


        // Get a reference to a SensorManager
        sensorManager = (SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);

        mCustomDrawableView = new CustomDrawableView(getActivity());
        //setContentView(mCustomDrawableView);
        // setContentView(R.layout.main);

        //Calculate Boundry
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        xmax = (float)display.getWidth() - 250;
        ymax = (float)display.getHeight() - 250;
    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                //Set sensor values as acceleration
                yAcceleration = sensorEvent.values[1];
                xAcceleration = sensorEvent.values[2];
                updateBall();
            }
        }
    }

    private void updateBall() {


        //Calculate new speed
        xVelocity += (xAcceleration * frameTime);
        yVelocity += (yAcceleration * frameTime);

        //Calc distance travelled in that time
        float xS = (xVelocity/2)*frameTime;
        float yS = (yVelocity/2)*frameTime;

        //Add to position negative due to sensor
        //readings being opposite to what we want!
        xPosition -= xS;
        yPosition -= yS;

        if (xPosition > xmax) {
            xPosition = xmax;
            xVelocity = 0;
        } else if (xPosition < 0) {
            xPosition = 0;
            xVelocity = 0;
        }
        if (yPosition > ymax) {
            yPosition = ymax;
            yVelocity = 0;
        } else if (yPosition < 0) {
            yPosition = 0;
            yVelocity = 0;
        }
    }

    // I've chosen to not implement this method
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop()
    {
        // Unregister the listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    private class CustomDrawableView extends View
    {
        public CustomDrawableView(Context context)
        {
            super(context);
            Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.six);
            final int maxSize = 250;
            mBitmap = Bitmap.createScaledBitmap(ball, maxSize, maxSize, true);
            mWood = BitmapFactory.decodeResource(getResources(), R.drawable.wood);

        }

        protected void onDraw(Canvas canvas)
        {
            final Bitmap bitmap = mBitmap;
            canvas.drawBitmap(mWood, 0, 0, null);
            canvas.drawBitmap(bitmap, xPosition, yPosition, null);
            invalidate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}


