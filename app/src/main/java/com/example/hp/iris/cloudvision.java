package com.example.hp.iris;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.example.hp.iris.PeopleMarkerActivity.PeopleMarkerCamera;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class cloudvision extends AppCompatActivity implements  TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {

    Bitmap bitmap2,bitmap3;
    ImageView img;
    Uri image;
    RelativeLayout relative3;
    TextToSpeech tvvs2;
    ArrayList<String> arrayList=new ArrayList<String>();
    String callerclass ;
    ArrayList<String> keylist;
    AWSCredentials credentials = new BasicAWSCredentials("AKIAJ3CRZTWIQTKUOQUQ","ATsmUAR5+k0O8oM90DzTYcbjyO3MvgGuRmXNvNJW");

    AmazonS3 s3;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDmxMoFRxFhwCh3ERm3Syy8znqyN0s9hjI";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final String TAG = cloudvision.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.cloudvision);

        relative3=(RelativeLayout)findViewById(R.id.relative3);
        img=(ImageView)findViewById(R.id.imageView);
        s3=new AmazonS3Client(credentials);
        tvvs2=new TextToSpeech(cloudvision.this,cloudvision.this);
        callerclass=getIntent().getExtras().getString("callerclass");
        if(callerclass.equals("cloudcamera")){
            image=Uri.parse(getIntent().getStringExtra("uri"));
            img.setImageURI(image);
        }
        else if(callerclass.equals("PeopleMarkerCamera")){
            image = Uri.parse(getIntent().getStringExtra("uri"));
            img.setImageURI(image);
        }
        bitmap2= BitmapFactory.decodeResource(getResources(),R.drawable.photo_preview);
        tvvs2=new TextToSpeech(cloudvision.this,cloudvision.this);
            try {
            bitmap3=scaleBitmapDown( MediaStore.Images.Media.getBitmap(getContentResolver(), image),500);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        click.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bitmap bitmap =
//                        scaleBitmapDown(bitmap2,200
//                                );
//                //img.setImageBitmap(bitmap);
//                try {
//                    callCloudVision(bitmap3);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Bitmap bitmap =
//                        scaleBitmapDown(bitmap2,200);
               if(callerclass.equals("cloudcamera")){
                   try {
                       callCloudVision(bitmap3);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }else if(callerclass.equals("PeopleMarkerCamera")){
                     new Image_Comparing_Task().execute();
               }

            }
        });
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(cloudvision.this,MainActivity.class));
                return false;
            }
        });

    }
    private void callCloudVision(final Bitmap bitmap) throws IOException
    {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>()
                        {

                            {
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                }
                catch (GoogleJsonResponseException e)
                {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e)
                {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
//                return "Cloud Vision API request failed. Check logs for details.";
                 return "cloud is on the process due to slower network";
            }

            protected void onPostExecute(String result) {
//                Toast.makeText(cloudvision.this, ""+result, Toast.LENGTH_SHORT).show();
                //mImageDetails.setText(result);
//                if(result!=null) {
//                finish();
//                }
                System.out.println("Array="+arrayList.size());
                System.out.println("Arrays+"+arrayList);
                Iterator<String> iterator=arrayList.iterator();
                String done="";
                int flag=0,count=0;
                while(iterator.hasNext())
                {       if(iterator.next().equals("text"))
                        {
                            flag=1;
                            count=1;
                        }
//                      if(iterator.next().equals(null)){
//                          startActivity(new Intent(cloudvision.this,MainActivity.class));
//                      }

//                    System.out.println("Values="+iterator.next());
                }
                if(!tvvs2.isSpeaking()){
                    HashMap<String,String> params=new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                    tvvs2.speak(result,TextToSpeech.QUEUE_ADD,params);
                }
                else{
                    tvvs2.stop();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(count==1)
                    {
                        startActivity(new Intent(cloudvision.this,textscanner.class));
                        finish();
                    }

            }
        }.execute();

//        new AsyncTask<String, Integer, String>(){
//
//            @Override
//            protected String doInBackground(String... strings) {
//                File file=new File("/storage/emulated/0/IRIS/MI_08012018_1026.jpg");
//                byte[] bytes=  new byte[(int) file.length()];
//                try {
//                    FileInputStream fis=new FileInputStream(file);
//                    fis.read(bytes);
//                    fis.close();
//                    System.out.println("The bytes stream is : "+bytes);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute();
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

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null)
        {
            for (EntityAnnotation label : labels)
            {
                arrayList.add(label.getDescription());
                message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        }
        else
        {
            message += "nothing";
        }
        System.out.println("Result : "+arrayList);
        return message;
    }
    @Override
    protected void onDestroy() {
        if (tvvs2 != null) {
            tvvs2.stop();

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
    public class Image_Comparing_Task extends AsyncTask<String, Integer, String>{

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
                                Toast.makeText(cloudvision.this, "Error in uploading..", Toast.LENGTH_SHORT).show();
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
                    System.out.println("Comparison ; " + result.getFaceMatches().get(0).getSimilarity());
                }

            }
            return null;

        }
    }
}
