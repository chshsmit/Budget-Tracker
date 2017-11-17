package christophershae.budgettracker;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;

import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


public class Camera_Interface extends Activity {
    TextView item, item2, item3, item4;
    //target for dropping items into categories
    ListView category_target;
    private Camera myCamera;
    private CameraPreview myPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera__interface);

        //defining our textViews
        item = (TextView) findViewById(R.id.item);
        item2 = (TextView) findViewById(R.id.item2);
        item3 = (TextView) findViewById(R.id.item3);
        item4 = (TextView) findViewById(R.id.item4);
        //defining listView
        category_target = (ListView) findViewById(R.id.Category_View);

        //making textView respond to drag Onclicklistner
        item.setOnLongClickListener(longClickListener);
        item2.setOnLongClickListener(longClickListener);
        item3.setOnLongClickListener(longClickListener);
        item4.setOnLongClickListener(longClickListener);
        //making listView respond to drag
        category_target.setOnDragListener(dragListener);

        //create instance of camera
        myCamera = getCamera();
        //create preview and set it as content of our layout
        myPreview = new CameraPreview(this, myCamera);
        FrameLayout Preview = (FrameLayout) findViewById(R.id.camera_preview);
        Preview.addView(myPreview);

/*        Camera.PictureCallback myPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte [] data, Camera camera) {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null){
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }
            }

        };*/
    }

/*    private static File getOutputMediaFile(int mediaTypeImage) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;


    }*/







    //declare and define listener
    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ClipData item_description = ClipData.newPlainText("", "");
            //
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
            v.startDrag(item_description, myShadow, v, 0);
            return true;
        }
    };
    ListView.OnDragListener dragListener = new View.OnDragListener() {

        @Override

        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();
            switch (dragEvent) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    //notifies which view we have entered,
                    // like item or item2 and so on

//                    final View view = (View) event.getLocalState();

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    break;
            }
            return true;
        }
    };


    //check if there is a camera in the device
    private boolean checkCameraInterface(Context context) {
        //if the device has a camera return true else return false
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
    //get instance of the camera object
    public static Camera getCamera()
    {
        Camera myCamera = null;
        try
        {
            myCamera = Camera.open();
        }
        catch(Exception e)
        {
            //camera is not available
            e.printStackTrace();
        }
        return myCamera;
    }
}