package com.marll.recogtensorflow;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final int REQUEST_PERMISSION = 101;
    private static String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                           Manifest.permission.CAMERA};
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusableInTouchMode(true);
        setFocusable(true);
        requestFocus();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        mCamera.cancelAutoFocus();
                    }
                });
            }
        });

        // deprecated setting, but required on Android versions prior to 3.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public static void checkPermissions(Context context, StartCamera st) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (arePermissionsEnabled(context)) {
                    st.startCamera();
                } else {
                    requestMultiplePermissions(context);
                }
            }
        } else {
            Toast.makeText(context, "Camera não reconhecida, tente novamente.", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean arePermissionsEnabled(Context context) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void requestMultiplePermissions(Context context) {
        Activity activity = (Activity) context;
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        ActivityCompat.requestPermissions(activity, remainingPermissions.toArray(new String[remainingPermissions.size()]), REQUEST_PERMISSION);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    mCamera.cancelAutoFocus();
                }
            });
        } catch (IOException e) {
            Log.d(TAG, "Erro ao setar o preview da câmera: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        //////// *********** IMPORTANTE ************ \\\\\\\\\
        // set preview size and make any resize, rotate or  \\
        // reformatting changes here                        \\
        /////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            mCamera.setDisplayOrientation(90);
        } else {
            mCamera.setDisplayOrientation(0);
        }

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    mCamera.cancelAutoFocus();
                }
            });

        } catch (Exception e) {
            Log.d(TAG, "Erro ao setar o preview da câmera: " + e.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void reqPerm(int requestCode, @NonNull int[] grantResults, Context context, StartCamera st){
        Activity activity = (Activity) context;
        if(requestCode == REQUEST_PERMISSION){
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])){
                        new AlertDialog.Builder(context)
                                .setMessage("Você precisa permitir o acesso para ter total uso do app!")
                                .setPositiveButton("Permitir", (dialog, which) -> requestMultiplePermissions(context)) //onclick button w lambda expression
                                .setNegativeButton("Sair do app", (dialog, which) -> System.exit(0)) //onclick button w lambda expression
                                .create()
                                .show();
                    }
                    return;
                }
            }
            st.startCamera();
        }
    }
    public interface StartCamera {
        void startCamera();
    }
}
