package christophershae.budgettracker;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import android.Manifest;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Camera_Interface extends AppCompatActivity implements View.OnClickListener
{
    //variables to use for the camera
    private ImageView imageView;
    Button  captureButton;

    final int CAMERA_CAPTURE = 1;
    final int CROP_PIC = 2;
    private Uri picUri;
    private Gallery display;

    //array to display image
    List<String> receiptImages;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String Gallery_ImagePath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Budget-Tracker/";
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera__interface);

        //Firebase stuff
        firebaseAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
        imageLinks = new ArrayList<>();
        //allImages = new ArrayList<>();

        //toolbar setup
        Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);

        //initialize our Gallery
        display = (Gallery) findViewById(R.id.gallery1);

        //Initialize list of Images
        receiptImages = null;
        //get images
        receiptImages = RetriveCapturedImagePath();
        //initialiaze the button to take a photo and set on its listner
        captureButton= (Button) findViewById(R.id.Photo_B);
        captureButton.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            captureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        final ImageAdapter myAdapter = new ImageAdapter(this, receiptImages);

        imageView =(ImageView)findViewById(R.id.imageview);
        TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
        int itemBackground = a.getResourceId(R.styleable.
                MyGallery_android_galleryItemBackground, 0);
        imageView.setBackgroundResource(itemBackground);

        display.setAdapter(myAdapter);
        display.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                Toast.makeText(getBaseContext(),"Receipt" +" "+ (position + 1) + " Selected",
                        Toast.LENGTH_SHORT).show();


                BitmapFactory.Options myOptions = new BitmapFactory.Options();
                //disable dithering mode
                myOptions.inDither = false;
                //if needed to free memory Bitmap can be clear
                myOptions.inPurgeable = true;
                myOptions.inInputShareable = true;
                myOptions.inTempStorage=new byte[32 * 1024];

                FileInputStream images;
                Bitmap myView;

                try
                {
                    images = new FileInputStream(new File(receiptImages.get(position)));

                    if (images != null)
                    {


                        myView = BitmapFactory.decodeFileDescriptor(images.getFD(), null, myOptions);

                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(myView);
                        imageView.setId(position);
                        //sets backgropund to gray

                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                
            }
        });
        if(receiptImages != null)
        {
            display.setAdapter(myAdapter);
        }



        //Getting the current weeks index
        currentWeeksDate = Utils.decrementDate(new Date());

        mFireBaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("We are getting data from the database");
                currentWeeksBudget = dataSnapshot.child(userId).child(currentWeeksDate).getValue(WeekLongBudget.class);  //This instantiates this weeks budget
                currentWeeksBudget.calculateTotal();

                try {
                    totalPhotoCount = dataSnapshot.child(userId).child("photoCount").getValue(Integer.class);
                }
                catch(NullPointerException e){
                        totalPhotoCount = 0;
                        System.out.println(totalPhotoCount);
                }

                System.out.println("This is the current weeks start date: ");
                System.out.println(currentWeeksBudget.getStartDate());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("You arent reDING CORRECTLTY");
            }
        } );
    }
    public void onClick(View v)
    {
        if (v.getId() == R.id.Photo_B)
        {
            try {
                // use standard intent to capture an image
                Intent takePhoto = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                // we will handle the returned data in onActivityResult
                startActivityForResult(takePhoto, CAMERA_CAPTURE);
            } catch (ActivityNotFoundException anfe) {
                Toast toast = Toast.makeText(this, "YOUR PHONE DOESN'T SUPPORT CAMERA FUNCTIONALITY",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mStorageRef;
    private String userId;
    private String currentWeeksDate;
    private WeekLongBudget currentWeeksBudget;
    public int totalPhotoCount;
    private ArrayList<Uri> imageLinks;


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE)
            {
                // get the Uri for the captured image
                picUri = data.getData();
                performCrop();
            }
            // user is returning from cropping the image
            else if (requestCode == CROP_PIC)
            {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
//                currentWeeksBudget.addImageToList(thePic);
//                mFireBaseDatabase.child(userId).child(currentWeeksDate).setValue(currentWeeksBudget);


                //store in the file path
                currentWeeksBudget.increasePhotoCount();
                totalPhotoCount += 1;
                mFireBaseDatabase.child(userId).child(currentWeeksDate).setValue(currentWeeksBudget);
                mFireBaseDatabase.child(userId).child("photoCount").setValue(totalPhotoCount);
                String imageInformation = totalPhotoCount+".jpeg";;
                File imageDir = new File(Gallery_ImagePath);
                imageDir.mkdirs();
                String path = Gallery_ImagePath + imageInformation +".jpeg";
                try {
                    FileOutputStream out = new FileOutputStream(path);
                    thePic.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();

                    Uri imageUri = Uri.fromFile(new File(path));
                    System.out.println(mStorageRef == null);
                    StorageReference storageReference = mStorageRef.child("images/users/" + userId +"/"+imageInformation);
                    storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            imageLinks.add(downloadUrl);
                            System.out.println("Succesfully added to URI list");


                            toastMessage("Upload Success");
                            //mProgressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMessage("Upload Failed");
                            //mProgressDialog.dismiss();
                        }
                    });

                } catch (FileNotFoundException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                receiptImages = null;
                receiptImages = RetriveCapturedImagePath();
                if(receiptImages!=null) {
                    display.setAdapter(new ImageAdapter(this, receiptImages));
                }
            }
        }
    }



    private List<String> RetriveCapturedImagePath()
    {
        List <String> myImages = new ArrayList<String>();
        File myShots = new File(Gallery_ImagePath);
        if (myShots.exists()) {
            File[] files=myShots.listFiles();
            Arrays.sort(files);


            //currentWeeksBudget.clearImages();
            for(int i=0; i<files.length; i++){

                File file = files[i];
                if(file.isDirectory())
                    continue;


                myImages.add(file.getPath());
            }
        }
        return myImages;
    }
    //class for our image adapter
    public class ImageAdapter extends BaseAdapter
    {
        Context context;
        private List<String> galleryPhotos;
        int itemBackground;
        public ImageAdapter(Context c, List<String> Pictures)
        {
            context = c;
            galleryPhotos = Pictures;

            // sets a grey background and wraps around the images
            TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();

        }
        @Override
        public int getCount()
        {
            if(galleryPhotos != null) {
                return galleryPhotos.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position)
        {
            return position;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView tView;
            BitmapFactory.Options myOptions = new BitmapFactory.Options();
            //disable dithering mode
            myOptions.inDither = false;
            //if needed to free memory Bitmap can be clear
            myOptions.inPurgeable = true;
            myOptions.inInputShareable = true;
            myOptions.inTempStorage=new byte[32 * 1024];
            if (convertView == null)
            {
                tView = new ImageView(context);
                tView.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.
                        MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                tView.setPadding(0, 0, 0, 0);
            }
            else
            {
                tView= (ImageView) convertView;
            }
            FileInputStream images = null;
            Bitmap myView;
            try
            {
                images = new FileInputStream(new File(galleryPhotos.get(position)));

                if (images != null) {
                    myView = BitmapFactory.decodeFileDescriptor(images.getFD(), null, myOptions);
                    tView.setImageBitmap(myView);

                    tView.setId(position);
                    tView.setLayoutParams(new Gallery.LayoutParams(500, 500));

                    tView.setBackgroundResource(itemBackground);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {

                if(images!=null) {
                    try {
                        images.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return tView;
        }
    }

    /*crop operarion*/
    private void performCrop()
    {
        // take care of exceptions
        try
        {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe)
        {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //ToolBar function to setup res/menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Settings) {
            Intent setting = new Intent(Camera_Interface.this, SettingsActivity.class);
            startActivity(setting);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}