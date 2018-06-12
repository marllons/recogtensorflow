package com.marll.recogtensorflow;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    //Constants
    private static final String TAG = "======= LOGTAG ======= " + MainActivity.class.toString();

    private Camera mCamera;
    private CameraPreview mPreview;
    private ConstraintLayout mCameraView;
    public Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraView = (ConstraintLayout) findViewById(R.id.mCameraView);

        CameraPreview.checkPermissions(context, new CameraPreview.StartCamera() {
            @Override
            public void startCamera() {
                mCamera = CameraPreview.getCameraInstance();
                MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                // Create our Preview view and set it as the content of our activity.
                mPreview = new CameraPreview(context, mCamera);
                mCameraView.addView(mPreview);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CameraPreview.reqPerm(requestCode, grantResults, context, new CameraPreview.StartCamera() {
            @Override
            public void startCamera() {
                mCamera = CameraPreview.getCameraInstance();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                // Create our Preview view and set it as the content of our activity.
                mPreview = new CameraPreview(context, mCamera);
                mCameraView.addView(mPreview);
            }
        });
    }
}

