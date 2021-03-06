package com.example.ntemfacg.toss5;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.Random;

import static android.content.Context.SENSOR_SERVICE;
import static com.example.ntemfacg.toss5.R.id.tossButton;

/**
 * Created by pheon on 11/12/2016.
 */

public class TossCoin extends Fragment implements SensorEventListener{
    TossCoin.CustomDrawableView mCustomDrawableView = null;
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

        Button myButton = new Button(getActivity());
        myButton.setText("TOSS COIN");
        myButton.setBackgroundColor(Color.LTGRAY);
        //getActivity().requestWindowFeature(Window.FEATURE_ACTION_BAR);
        //getActivity().getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View rootView = inflater.inflate(R.layout.fragment_toss_coin, container, false);
        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.toss_coin);

        myButton.setId(tossButton);
        myButton.setLayoutParams(new FrameLayout.LayoutParams(450, 200));
        myButton.setX(500);
        myButton.setY(1600);

        relativeLayout.addView(new TossCoin.CustomDrawableView(getActivity()));
        //RelativeLayout.addView(myButton);
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

        mCustomDrawableView = new TossCoin.CustomDrawableView(getActivity());
        //setContentView(mCustomDrawableView);
        // setContentView(R.layout.main);

        //Calculate Boundry
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        xmax = (float)display.getWidth() - 350;
        ymax = (float)display.getHeight() - 900;
    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                //Set sensor values as acceleration
                yAcceleration = sensorEvent.values[1];
                xAcceleration = sensorEvent.values[2];
                updateCoin();
            }
        }
    }

    private void updateCoin() {


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

    public class CustomDrawableView extends View
    {
        int mWoodW;
        int mWoodH;
        int speed;
        int screenH;
        float acc;
        int mBitmapW;
        int mBitmapH;
        int mBitmapSpeed;
        int mWoodscroll;
        private Paint mPaint;
        private Path mSmallCircle;
        private Path mCircle;
        Bitmap mWoodReserve;
        Boolean reverseBackground;
        Boolean mBitmapTouched;
        private Matrix mMatrix;
        private static final float BIGRADIUS = 200;

        public CustomDrawableView(Context context)
        {
            super(context);

            mPaint = new Paint();
            mWoodscroll = 0;  //Background scroll position
            reverseBackground = false;
            mBitmapTouched = false;

            acc = 1.0f; //Acceleration

            speed = 1; //Scrolling background speed

            Bitmap coin = BitmapFactory.decodeResource(getResources(), R.mipmap.heads);
            final int maxSize = 400;
            mMatrix = new Matrix();
            mBitmap = Bitmap.createScaledBitmap(coin, maxSize, maxSize, true);
            mWood = BitmapFactory.decodeResource(getResources(), R.drawable.sky);
            mBitmapW = mBitmap.getWidth();
            mBitmapH = mBitmap.getHeight();

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int maxSize = 350;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    xPosition = (int) event.getX() - mBitmapW/2;
                    yPosition = (int) event.getY() - mBitmapH/2;

                    mBitmapTouched = true;
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    xPosition = (int) event.getX() - mBitmapW/2;
                    yPosition = (int) event.getY() - mBitmapH/2;

                    break;
                }

                case MotionEvent.ACTION_UP:
                    mBitmapTouched = false;
                    mBitmapSpeed = 0;
                    break;
            }

            int coins [] = {R.mipmap.heads,R.mipmap.tails};
            Random random = new Random();
            int n = random.nextInt(2);
            Bitmap randomcoin = BitmapFactory.decodeResource(getResources(), coins[n]);


            mBitmap = Bitmap.createScaledBitmap(randomcoin, maxSize, maxSize, true);
            //onDraw.rotate();
            invalidate();
            return true;

        }

        protected void onDraw(Canvas canvas)
        {
            final Bitmap bitmap = mBitmap;
            canvas.drawBitmap(mWood, 0, 0, null);
            canvas.drawBitmap(bitmap, xPosition, yPosition, null);
            if (!mBitmapTouched) {
                yPosition += (int) mBitmapSpeed; //Increase or decrease vertical position.
                if (yPosition > (screenH - mBitmapH)) {
                    mBitmapSpeed=(-1)*mBitmapSpeed; //Reverse speed when bottom hit.
                }
                mBitmapSpeed+= acc; //Increase or decrease speed.
            }
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
