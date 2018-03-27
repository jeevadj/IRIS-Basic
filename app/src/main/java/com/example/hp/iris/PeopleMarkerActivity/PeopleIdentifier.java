package com.example.hp.iris.PeopleMarkerActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.hp.iris.BlindFit;
import com.example.hp.iris.ImagePathAdapter;
import com.example.hp.iris.MainActivity;
import com.example.hp.iris.R;
import com.example.hp.iris.cloudvision;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PeopleIdentifier extends AppCompatActivity implements  TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {


    Bitmap bitmap2,bitmap3;
    ImageView img;
    Uri image;

    AmazonS3 s3;
    RelativeLayout relative3;
    TextToSpeech tvvs2;
    ArrayList<String> arrayList=new ArrayList<String>();
    private static final String CLOUD_VISION_API_KEY = "AIzaSyADtvO9_3vK3TkkHW5imY4t25AXaE8Gkis";
    //private static final String CLOUD_VISION_API_KEY = "AIzaSyA9QTLqarpVUbwJo4lmvRytydD_l5buea4";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    String callerclass ;
    ArrayList<String> keylist;
    AWSCredentials credentials = new BasicAWSCredentials("AKIAJ3CRZTWIQTKUOQUQ","ATsmUAR5+k0O8oM90DzTYcbjyO3MvgGuRmXNvNJW");

    private static final String TAG = cloudvision.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_people_identifier);

        relative3=(RelativeLayout)findViewById(R.id.relative3);
        img=(ImageView)findViewById(R.id.peopleidentifier);
        image=Uri.parse(getIntent().getStringExtra("uri"));
        img.setImageURI(image);
        s3=new AmazonS3Client(credentials);
        bitmap2= BitmapFactory.decodeResource(getResources(),R.drawable.photo_preview);
        tvvs2=new TextToSpeech(PeopleIdentifier.this,PeopleIdentifier.this);
        try {
            bitmap3=scaleBitmapDown( MediaStore.Images.Media.getBitmap(getContentResolver(), image),500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Bitmap bitmap =
//                        scaleBitmapDown(bitmap2,200);

                    new Image_Comparing_Task().execute();


            }
        });
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(PeopleIdentifier.this,MainActivity.class));
                return false;
            }
        });

    }
    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
    @Override
    protected void onDestroy() {
        if (tvvs2 != null) {
            tvvs2.stop();
            tvvs2.shutdown();

            tvvs2 = null;
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status)
    {


    }

    @Override
    public void onUtteranceCompleted(String utteranceId)
    {

    }
    public class Image_Comparing_Task extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {



            keylist = new ArrayList<>();


            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName("irismec");
            ObjectListing objectListing = s3.listObjects(listObjectsRequest);
            for(S3ObjectSummary objectSummary:objectListing.getObjectSummaries()){
                System.out.println("Key :"+objectSummary.getKey().toString());
                keylist.add(objectSummary.getKey());
            }

            if(s3.doesObjectExist("irismec","tmp.jpg")){
                s3.deleteObject("irismec","tmp.jpg");
            }
            else{
                TransferUtility transferUtility =new TransferUtility(s3,getBaseContext());
                ObjectMetadata metadata=new ObjectMetadata();
                Map<String, String> usermetadata= new HashMap<>();
                usermetadata.put("imgRek","tmp");

                metadata.setUserMetadata(usermetadata);

                TransferObserver transferObserver = transferUtility.upload("irismec","tmp.jpg",new File(ImagePathAdapter.path),metadata);

                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        System.out.println(" state changed");
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                        int percentage = (int) (bytesCurrent/bytesTotal)*100;
                        System.out.println(" onProgressChanged "+percentage);

                        if(percentage == 100){
                            System.out.println("bow");
                            new Image_Compare_task().execute();
                        }
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        System.out.println(" ERROR "+ex);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PeopleIdentifier.this, "Error in uploading..", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }


            return null;
        }
    }
    public class Image_Compare_task extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {

            final AmazonRekognitionClient rekognitionClient=new AmazonRekognitionClient(credentials);
            rekognitionClient.setRegion(Region.getRegion(Regions.US_WEST_2));

            for(int i=0 ; i< keylist.size();i++) {
                if(keylist.get(i).equals("tmp.jpg")){

                }else{
                    CompareFacesRequest compareFacesRequest = new CompareFacesRequest()
                            .withSourceImage(new com.amazonaws.services.rekognition.model.Image().withS3Object(new S3Object().withName("tmp.jpg").withBucket("irismec")))
                            .withTargetImage(new com.amazonaws.services.rekognition.model.Image().withS3Object(new S3Object().withName(keylist.get(i)).withBucket("irismec")))
                            .withSimilarityThreshold(78f);

                    CompareFacesResult result = rekognitionClient.compareFaces(compareFacesRequest);
                    if(!result.getFaceMatches().isEmpty()){
                        System.out.println("Comparison ; " + result.getFaceMatches().get(0).getSimilarity());

                        if(result.getFaceMatches().get(0).getSimilarity() >=75){
                            if(!tvvs2.isSpeaking()){
                                String keyy = keylist.get(i);
                                HashMap<String,String> params=new HashMap<String, String>();
                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                                String[] splited = keyy.split("_");


                                tvvs2.speak(splited[0]+""+splited[1],TextToSpeech.QUEUE_ADD,params);

                                Intent intent = new Intent(PeopleIdentifier.this,PeopleIdentifier.class);
                                intent.putExtra("uri", image.toString());
                                intent.putExtra("callerclass","PeopleIdentifier");
                                startActivity(intent);
                                finish();

                            }
                            else{
                                tvvs2.stop();
                            }
                        }

                    }
                    else{
                        if(i == keylist.size()-1){
                            if(!tvvs2.isSpeaking()){
                                String keyy = keylist.get(i);
                                HashMap<String,String> params=new HashMap<String, String>();
                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                                String[] splited = keyy.split("_");


                                tvvs2.speak("The person is not an authorised person",TextToSpeech.QUEUE_ADD,params);
                                startActivity(new Intent(PeopleIdentifier.this,BlindFit.class));
                                finish();
                            }
                            else{
                                tvvs2.stop();
                            }
                        }
                    }
                                   }

            }
            return null;

        }
    }
}
