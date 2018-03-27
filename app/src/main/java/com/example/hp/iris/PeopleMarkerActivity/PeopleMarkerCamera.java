package com.example.hp.iris.PeopleMarkerActivity;

/**
 * Created by Aravindh balaji on 22-03-2018.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.hp.iris.FileUtil;
import com.example.hp.iris.ImagePathAdapter;
import com.example.hp.iris.Main2Activity;
import com.example.hp.iris.MyLocationGetter;
import com.example.hp.iris.R;
import com.example.hp.iris.cloudvision;
import com.example.hp.iris.textscanner;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;


/**
 * Created by HP on 9/6/2017.
 */

public class PeopleMarkerCamera extends AppCompatActivity implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener{
    CameraSource cameraSource;
    SurfaceView cameraView;
    Image image;
    ImageView imageView;
    Bitmap bmp,bmp2;
    String path;
    public Uri imageuri;
    Intent intent;
    TextToSpeech tvvs;
    //BarcodeDetector barcodeDetector;
    SurfaceHolder surfaceHolder;
    byte[] bytes1;
    String peopleCvar ;
    AmazonS3 s3;
    ArrayList<String> keylist;
    File compressedImage,actualImage;

    AWSCredentials credentials = new BasicAWSCredentials("AKIAJ3CRZTWIQTKUOQUQ","ATsmUAR5+k0O8oM90DzTYcbjyO3MvgGuRmXNvNJW");
    final int RequestCameraPermissionID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera1);
        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        imageView=(ImageView)findViewById(R.id.imageView);
        tvvs=new TextToSpeech(PeopleMarkerCamera.this,PeopleMarkerCamera.this);
//        surfaceHolder = cameraView.getHolder();cloudcamera.
//        surfaceHolder.addCallback(this);
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        final FaceDetector detector = new FaceDetector.Builder(getApplicationContext()).build();
        s3 = new AmazonS3Client(credentials);
        cameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

        peopleCvar = getIntent().getExtras().getString("PeopleIdentifier");
        System.out.println("bow"+peopleCvar);


        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(PeopleMarkerCamera.this,
                                new String[]{Manifest.permission.CAMERA},
                                RequestCameraPermissionID);
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }



        });
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        detector.release();
                        cameraSource.release();
//                       bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        //bmp.compress(Bitmap.CompressFormat.PNG,bytes);
//                        File dir=
//                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//                        System.out.println("bit is "+bytes);
//                        System.out.println("LOL 1"+dir);
//                        File output = new File(dir, "CameraContentDemo.jpeg");
//                        System.out.println("LOL 1"+output);
//                        imageUri=Uri.fromFile(output);
//                        System.out.println("LOL 1"+imageUri);
//
//                        Toast.makeText(MainActivity.this, "Picture Captured"+imageUri, Toast.LENGTH_SHORT).show();
//
//                        Log.d("BITMAP", bmp.getWidth() + "x" + bmp.getHeight());
//                        bytes1=bytes;

                        if(peopleCvar.equals("peopleMarker")){
                            if(!tvvs.isSpeaking()){
                                HashMap<String,String> params=new HashMap<String, String>();
                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                                tvvs.speak("Picture capture long press to enable save their identity.",TextToSpeech.QUEUE_ADD,params);
                            }
                            else{
                                tvvs.stop();
                            }
                        }
                        else if(peopleCvar.equals("peopleIdentity")){
                            if(!tvvs.isSpeaking()){
                                HashMap<String,String> params=new HashMap<String, String>();
                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                                tvvs.speak("Picture capture long press to enable verify their identity.",TextToSpeech.QUEUE_ADD,params);
                            }
                            else{
                                tvvs.stop();
                            }
                        }
                        image =new Image();
                        image.setBytes(ByteBuffer.wrap(bytes));

                        Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        int rotation = camera1.this.getWindowManager().getDefaultDisplay().getRotation();
//                        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
//                            Matrix matrix = new Matrix();
//                            matrix.postRotate(90);
//                            // create a rotated version and replace the original bitmap
//                            picture = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);
//                        }

                        MediaStore.Images.Media.insertImage(getContentResolver(), picture, "NiceCameraExample", "NiceCameraExample test");

                        path=MediaStore.Images.Media.insertImage(getContentResolver(), picture, "NiceCameraExample", "NiceCameraExample test");


                        File mediaStorageDir= new File(Environment.getExternalStorageDirectory()+"/IRIS/");
                        if(!mediaStorageDir.exists()){
                            if(!mediaStorageDir.mkdirs()){
                                System.out.println("BOw");
                            }
                        }
                        String timestamp=new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date()) ;

                        File medialfile;
                        String ImageName= "MI_"+timestamp+".jpg";
                        medialfile=new File(mediaStorageDir.getPath()+File.separator+ImageName);

                        File picFile=medialfile;

                        try {
                            FileOutputStream fos=new FileOutputStream(picFile);
                            picture.compress(Bitmap.CompressFormat.JPEG,90,fos);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(PeopleMarkerCamera.this, e.toString(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(PeopleMarkerCamera.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        actualImage= new File(picFile.getPath());
                        try {
                            actualImage = FileUtil.from(getApplicationContext(),Uri.fromFile(actualImage));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Bowwwwww"+actualImage.getAbsoluteFile());
                        try {
                            compressedImage = new Compressor(PeopleMarkerCamera.this)
                                    .setMaxWidth(640)
                                    .setMaxHeight(480)
                                    .setQuality(75)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .setDestinationDirectoryPath(Environment.getExternalStorageDirectory()+"/IRIS/")
                                    .compressToFile(actualImage);

                            setCompressedImage();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }

                        System.out.println("path is bow :"+compressedImage.getPath());


                        ImagePathAdapter.path=compressedImage.getPath();
                        Toast.makeText(PeopleMarkerCamera.this,compressedImage.getPath() , Toast.LENGTH_SHORT).show();


                    }

                });
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                bytes= stream.toByteArray();

//


                //cameraSource.takePicture(CameraSource.ShutterCallback, CameraSource.PictureCallback,null);
            }
        });
        cameraView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                intent=new Intent(MainActivity.this,Main2Activity.class);
//                Toast.makeText(MainActivity.this, "Uri"+imageUri, Toast.LENGTH_SHORT).show();
//                intent.putExtra("img",bytes1);
//                startActivity(intent);
//                finish();
                System.out.println("bow ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!tvvs.isSpeaking()){
                            HashMap<String,String> params=new HashMap<String, String>();
                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                            tvvs.speak("Vision enabled , press on screen to process",TextToSpeech.QUEUE_ADD,params);
                            if(path!=null) {
                                imageuri = Uri.parse(path);

//                                Intent intt = new Intent(PeopleMarkerCamera.this,cloudvision.class);
//                                intt.putExtra("uri", imageuri.toString());
//                                Toast.makeText(PeopleMarkerCamera.this, "" + imageuri, Toast.LENGTH_SHORT).show();
//                                startActivity(intt);
//                                finish();
                                System.out.println("bow "+peopleCvar);
                                if(peopleCvar.equals("peopleIdentity")){
                                     Intent intent = new Intent(PeopleMarkerCamera.this,PeopleIdentifier.class);
                                     intent.putExtra("uri", imageuri.toString());
                                     intent.putExtra("callerclass","PeopleMarkerCamera");
                                     startActivity(intent);
                                }
                                else if(peopleCvar.equals("peopleMarker")){
                                    Intent intent = new Intent(PeopleMarkerCamera.this,PeopleMarker.class);
                                    intent.putExtra("cv","1");
                                    startActivity(intent);
                                }
                            }
                        }
                        else{
                            tvvs.stop();
                        }


                    }
                });

                return false;
            }
        });



        //detector.setProcessor(new MultiProcessor.Builder<Face>());
        //detector.setProcessor(new MultiProcessor.Builder<Face>().build(new GraphicFaceTrackerFactory()));

    }
    private void setCompressedImage() {



        Toast.makeText(this, "Compressed image save in " + compressedImage.getPath(), Toast.LENGTH_LONG).show();
        Log.d("Compressor", "Compressed image save in " + compressedImage.getPath());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            break;
        }
    }
    @Override
    protected void onDestroy() {
        if(tvvs!=null)
        {
            tvvs.stop();
            tvvs.shutdown();

            tvvs=null;
        }


        super.onDestroy();
    }



    @Override
    public void onInit(int status) {

    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {

    }
    public  boolean onKeyDown(int keyCode,KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            event.startTracking();



            return true;
        }
        return super.onKeyDown(keyCode,event);

    }
    public boolean onKeyLongPress(int keycode,KeyEvent event){
        if(keycode==KeyEvent.KEYCODE_VOLUME_UP){
            startActivity(new Intent(this,MyLocationGetter.class));
            return true;
        }
        return onKeyLongPress(keycode, event);
    }


}
