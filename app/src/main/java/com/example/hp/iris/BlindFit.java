package com.example.hp.iris;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.hp.iris.PeopleMarkerActivity.PeopleSelect;

import java.util.HashMap;

/**
 * Created by HP on 3/22/2018.
 */

public class BlindFit extends AppCompatActivity  implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {
    Button feelpad;
    Boolean flag=false;
    TextToSpeech tvvs;
    CardView vision,textscanner,barcodescanner,peoplemarker,facedetection,call,message;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.blindfitlayout);
        vision=(CardView)findViewById(R.id.vision);
        textscanner=(CardView)findViewById(R.id.textscanner);
        barcodescanner=(CardView)findViewById(R.id.qrscanner);
        peoplemarker=(CardView)findViewById(R.id.peoplemarker);
        facedetection=(CardView)findViewById(R.id.facedetection);
        call=(CardView)findViewById(R.id.call);
        message=(CardView)findViewById(R.id.message);
        tvvs=new TextToSpeech(BlindFit.this,BlindFit.this);


        feelpad=(Button)findViewById(R.id.feelpad);
        feelpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                flag=true;

            }
        });
        vision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("you have selected Vision Option, to enter long press in the same place",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }
                else
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("Vision",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }

            }
        });
        vision.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("Opening vision Camera",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                    flag=false;
                    startActivity(new Intent(BlindFit.this,cloudcamera.class));

                }
                return false;
            }
        });
        textscanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("you have selected textscanner scanning Option, to enter long press in the same place",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }
                else{
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("TextScanner",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }

            }
        });
        textscanner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("Entered Camera",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                    flag=false;
                    startActivity(new Intent(BlindFit.this,textscanner.class));

                }
                return false;
            }
        });

       facedetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("you have selected FaceDetection Option, to enter long press in the same place",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }
                else{
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("facedetection",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }

            }
        });
        facedetection.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(flag==true)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!tvvs.isSpeaking()){
                                HashMap<String,String> params=new HashMap<String, String>();
                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                                tvvs.speak("Opening Facedetection Camera click to take picute and long click to enable face detection",TextToSpeech.QUEUE_ADD,params);
                            }
                            else{
                                tvvs.stop();
                            }
                            flag=false;
                            startActivity(new Intent(BlindFit.this,camera1.class));


                        }
                    });
                }
                return false;
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("you have selected Call Option, to enter long press in the same place",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }
                else{
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("call",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }

            }
        });
        call.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("Click on the contact to hear the name .",TextToSpeech.QUEUE_ADD,params);
                        tvvs.speak("Long press on the contact to call the name ",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                    flag=false;
                    Intent intent =new Intent(BlindFit.this,BeforeMessage.class);
                    intent.putExtra("Activity","Call");
                    startActivity(intent);

                    finish();
                }
                return false;
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("you have selected Message Option, to enter long press in the same place",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }
                else{
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("message",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }

            }
        });
        message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("Click on the contact to hear the name .",TextToSpeech.QUEUE_ADD,params);
                        tvvs.speak("Long press on the contact to Message the name ",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                    flag=false;
                    Intent intent =new Intent(BlindFit.this,BeforeMessage.class);
                    intent.putExtra("Activity","Message");
                    startActivity(intent);

                    finish();
                }
                return false;
            }
        });


        peoplemarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("People Select Option",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }

                }
                else{
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("peoplemarker",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }

            }
        });
       peoplemarker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("Options Loaded Regrading People Tracking Feature",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                    flag=false;
                    startActivity(new Intent(BlindFit.this, PeopleSelect.class));

                }
                return false;
            }
        });
        barcodescanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("You have selected barcode option please long press to open",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }

                }
                else{
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("qrcodeScanner",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                }

            }
        });
        barcodescanner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(flag==true)
                {
                    if(!tvvs.isSpeaking()){
                        HashMap<String,String> params=new HashMap<String, String>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"sampleText");
                        tvvs.speak("Entered Camera",TextToSpeech.QUEUE_ADD,params);
                    }
                    else{
                        tvvs.stop();
                    }
                    flag=false;
                    startActivity(new Intent(BlindFit.this, com.example.hp.iris.barcode.class));

                }
                return false;
            }
        });
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
            startActivity(new Intent(BlindFit.this,MyLocationGetter.class));
            return true;
        }
        return onKeyLongPress(keycode, event);
    }
}
