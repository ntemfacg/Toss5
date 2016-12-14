package com.example.ntemfacg.toss5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

import static android.R.attr.centerX;
import static android.R.attr.x;
import static android.content.Context.SENSOR_SERVICE;
import static com.example.ntemfacg.toss5.R.id.tossButton;

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
        //getActivity().getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button myButton = new Button(getActivity());
        myButton.setText("TOSS DICE");
        myButton.setBackgroundColor(Color.LTGRAY);
        View rootView = inflater.inflate(R.layout.fragment_toss_ground, container, false);
        FrameLayout relativeLayout = (FrameLayout) rootView.findViewById(R.id.toss_ground);

        myButton.setId(tossButton);
        myButton.setLayoutParams(new FrameLayout.LayoutParams(450, 200));
        myButton.setX(500);
        myButton.setY(1600);

        relativeLayout.addView(new CustomDrawableView(getActivity()));
        relativeLayout.addView(myButton);
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

        //setting boundaries within the fragment
        xmax = (float)display.getWidth() - 250;
        ymax = (float)display.getHeight() - 790;
    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                //Set sensor values as acceleration
                yAcceleration = sensorEvent.values[1];
                xAcceleration = sensorEvent.values[2];
                updateDice();
            }
        }
    }

    private void updateDice() {


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
        private float mAngle;
        private static final float CX = 0;
        private static final float CY = 0;
        private static final float RADIUS = 20;
        private static final float BIGRADIUS = 200;
        private static final int NORMAL_COLOR = 0xffffffff;
        private static final int PRESSED_COLOR = 0xffff0000;

        public CustomDrawableView(Context context)
        {
            super(context);
            Bitmap die1 = BitmapFactory.decodeResource(getResources(), R.drawable.one);
            final int maxSize = 250;

            mPaint = new Paint();
            mWoodscroll = 0;  //Background scroll position
            reverseBackground = false;
            mBitmapTouched = false;

            acc = 0.2f; //Acceleration

            speed = 1; //Scrolling background speed



            mCircle = new Path();
            mCircle.addCircle(0, 0, BIGRADIUS, Path.Direction.CW);

            mMatrix = new Matrix();
            mBitmap = Bitmap.createScaledBitmap(die1, maxSize, maxSize, true);
            mWood = BitmapFactory.decodeResource(getResources(), R.drawable.wood);
            mBitmapW = mBitmap.getWidth();
            mBitmapH = mBitmap.getHeight();

        }

        @Override
        public void onSizeChanged (int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            //This event-method provides the real dimensions of this custom view.
            screenH = h;

            mWood = Bitmap.createScaledBitmap(mWood, w, h, true); //Scale background to fit the screen.
            mWoodW = mWood.getWidth();
            mWoodH = mWood.getHeight();

            //Create a mirror image of the background (horizontal flip) - for a more circular background.
            Matrix matrix = new Matrix();  //Like a frame or mould for an image.
            matrix.setScale(-1, 1); //Horizontal mirror effect.
            mWoodReserve = Bitmap.createBitmap(mWood, 0, 0, mWoodW, mWoodH, matrix, true); //Create a new mirrored bitmap by applying the matrix.
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int maxSize = 250;

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
            //Bitmap random;
            int dice [] = {R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.four,R.drawable.five,R.drawable.six};
            //random = dice[new Random().nextInt(dice.length)];
            Random random = new Random();
            int n = random.nextInt(6);

            Bitmap randomdice = BitmapFactory.decodeResource(getResources(), dice[n]);


            mBitmap = Bitmap.createScaledBitmap(randomdice, maxSize, maxSize, true);
            //onDraw.rotate();
            invalidate();
            return true;
        }

        protected void onDraw(Canvas canvas)
        {
            final Bitmap bitmap = mBitmap;
            //Draw scrolling background.
            Rect fromRect1 = new Rect(0, 0, mWoodW - mWoodscroll, mWoodH);
            Rect toRect1 = new Rect(mWoodscroll, 0, mWoodW, mWoodH);

            Rect fromRect2 = new Rect(mWoodW - mWoodscroll, 0, mWoodW, mWoodH);
            Rect toRect2 = new Rect(0, 0, mWoodscroll, mWoodH);

            if (!reverseBackground) {
                canvas.drawBitmap(mWood, fromRect1, toRect1, null);
                canvas.drawBitmap(mWoodReserve, fromRect2, toRect2, null);
            }
            else{
                canvas.drawBitmap(mWood, fromRect2, toRect2, null);
                canvas.drawBitmap(mWoodReserve, fromRect1, toRect1, null);
            }

            //Next value for the background's position.
            if ( (mWoodscroll += speed) >= mWoodW) {
                mWoodscroll = 0;
                reverseBackground = !reverseBackground;
            }
            canvas.drawBitmap(bitmap, xPosition, yPosition, null);

            if (!mBitmapTouched) {
                yPosition += (int) mBitmapSpeed; //Increase or decrease vertical position.
                if (yPosition > (screenH - mBitmapH)) {
                    mBitmapSpeed=(-1)*mBitmapSpeed; //Reverse speed when bottom hit.
                }
                mBitmapSpeed+= acc; //Increase or decrease speed.
            }

            invalidate();

            // setting on touch button

            float w2 = 720;
            float h2 = 1700;
            mMatrix.reset();
            mMatrix.postRotate(mAngle);
            mMatrix.postTranslate(w2, h2);

            canvas.concat(mMatrix);
            mPaint.setColor(Color.TRANSPARENT);
            canvas.drawPath(mCircle, mPaint);

           /* public void rotate(){
            int angle;
            angle= 0;
            int Y = 100;
            int X = 100;

            if (angle++ >360)
                angle =0;

            canvas.rotate(angle, X + (xPosition / 2), Y + (yPosition / 2)); //Rotate the canvas.
            canvas.drawBitmap(mBitmap, X, Y, null);

        }*/
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}


