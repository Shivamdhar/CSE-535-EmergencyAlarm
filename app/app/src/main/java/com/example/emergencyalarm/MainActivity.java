package com.example.emergencyalarm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    boolean notify = true;
    private Uri fileUri;
    int count = 1;
    private ImageButton sensebtn;
    private ImageButton uploadbtn;
    private Button optionsBtn;
    private Vibrator vibrator;
    private SeekBar seekBar;
    private MediaRecorder mRecorder;
    private static final String LOG_TAG = "AudioRecording";
    private static String mFileName = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    final Context context = this;
    private View myView = null;
    private String preferenceFile = "com.example.emergencyAlarm.";


    boolean whichColor = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensebtn = findViewById(R.id.imageButton);
        sensebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordAudio();
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences(preferenceFile+getString
                (R.string.app_name), 0);
        final SharedPreferences.Editor editor = pref.edit();


        seekBar = findViewById(R.id.seekBar); // initiate the Seek bar

        int maxValue=seekBar.getMax();

        int seekBarValue= seekBar.getProgress();

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            //int seekBarValue= seekBar.getProgress();
            int vibrationAmplitude = (progressChangedValue*255)/100;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                editor.putInt(StaticKeys.VIBRATION_AMPLITUDE_KEY, (progressChangedValue*255)/100);
                editor.commit();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "percent of Vibration set is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
                vibrationAmplitude = (progressChangedValue*255)/100;
                //vibrateMobile(vibrationAmplitude);
            }


        });

        /* flash screen color code starts
        * This involves setting bg to white as
        * that will be the toggle color
        *
        * */
        myView = (View) findViewById(R.id.my_view);
        myView.setBackgroundColor(Color.WHITE);

        RadioGroup rg = (RadioGroup) findViewById(R.id.Radiogroup);

        handleVisibliity();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(preferenceFile+getString
                        (R.string.app_name), 0);
                SharedPreferences.Editor editor = pref.edit();
                switch(checkedId){
                    case R.id.GreenButton:
                        editor.putInt(StaticKeys.FLASH_SCREEN_TOGGLE_COLOR_KEY, Color.GREEN);
                        break;
                    case R.id.BlackButton:
                        editor.putInt(StaticKeys.FLASH_SCREEN_TOGGLE_COLOR_KEY, Color.BLACK);
                        break;
                    case R.id.RedButton:
                        editor.putInt(StaticKeys.FLASH_SCREEN_TOGGLE_COLOR_KEY, Color.RED);
                        break;
                }
                editor.commit();
            }
        });

        optionsBtn = findViewById(R.id.optionsButton);
        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });



        uploadbtn = findViewById(R.id.imageButton2);
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadTask up1 = new UploadTask();
                Toast.makeText(getApplicationContext(),"Starting to Upload",Toast.LENGTH_LONG).show();
                up1.execute();
            }
        });
    }


    public void vibrateMobile(int vibrationAmplitude){
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(StaticKeys.VIBRATIONS_DURATION, vibrationAmplitude));
        } else {
            vibrator.vibrate(StaticKeys.VIBRATIONS_DURATION);
        }
    }

//    UPLOAD AUDIO METHODS =========================================================================


    public class UploadTask extends AsyncTask<String, String, String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            try {

                String url = "http://6bbfcd54.ngrok.io/api/v1/upload";
                String charset = "UTF-8";

                System.out.println(fileUri.getPath());
                File files = new File(fileUri.getPath());
                String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
                String CRLF = "\r\n"; // Line separator required by multipart/form-data.

                URLConnection connection;

                connection = new URL(url).openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                try (
                        OutputStream output = connection.getOutputStream();
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                ) {
                    // Send audio file.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + files.getName() + "\"").append(CRLF);
                    writer.append("Content-Type: audio/3gp; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
                    writer.append(CRLF).flush();
                    FileInputStream vf = new FileInputStream(files);
                    try {
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        while ((bytesRead = vf.read(buffer, 0, buffer.length)) >= 0)
                        {
                            output.write(buffer, 0, bytesRead);
                        }
                    }catch (Exception exception)
                    {
                        Log.d("Error", String.valueOf(exception));
                        publishProgress(String.valueOf(exception));
                    }

                    output.flush(); // Important before continuing with writer!
                    writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

                    // End of multipart/form-data.
                    writer.append("--" + boundary + "--").append(CRLF).flush();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                System.out.println(connection.getContent());
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String decodedString;
                while((decodedString = in.readLine()) != null){
                    System.out.println(decodedString);
                    if(decodedString.equals("True")){
                        handleNotification();
                    }
                }
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... text) {
            Toast.makeText(getApplicationContext(), "In Background Task " + text[0], Toast.LENGTH_LONG).show();
        }

    }



//    RECORD AUDIO METHODS =========================================================================

    private void recordAudio() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CSE535Project/SoundRecordings/";
        File dir = new File(mFileName);

        if(!dir.exists()) {
            if(dir.mkdirs());
        }

        mFileName += "AudioRecording" + count + ".3gp";


        if (CheckPermissions()) {
            sensebtn.setEnabled(false);
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
            mRecorder.start();
            Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    sensebtn.setEnabled(true);
                    count++;
                    fileUri = Uri.fromFile(new File(mFileName));
                    Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_LONG).show();
                }
            }, 5000);
        } else {
            RequestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,
                RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    public void handleNotification(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(preferenceFile+getString
                (R.string.app_name), 0);
        SharedPreferences.Editor editor = pref.edit();
        if(pref.getBoolean(StaticKeys.VIBRATE_KEY,false)){
                vibrateMobile(pref.getInt(StaticKeys.VIBRATION_AMPLITUDE_KEY,
                        StaticKeys.VIBRATION_DEFAULT_AMPLITUDE));
        }
        if(pref.getBoolean(StaticKeys.FLASH_SCREEN_KEY,false)){
            startScreenFlash(pref.getInt(StaticKeys.FLASH_SCREEN_TOGGLE_COLOR_KEY, Color.BLACK));
        }
        if(pref.getBoolean(StaticKeys.FLASH_LIGHT_KEY,false)){
            flashLight();
        }
        myView.setBackgroundColor(Color.WHITE);
    }


    public void startScreenFlash(final int color){
        new Thread(new Runnable() {
            public void run() {
                int i = 0;
                while (i < StaticKeys.TIME_TO_FLASH * 1000 / StaticKeys.FLASH_FLICKER_INTERVAL) {
                    try {
                        Thread.sleep(StaticKeys.FLASH_FLICKER_INTERVAL);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateColor(color);
                    i++;
                    whichColor = !whichColor;
                }
            }
        }).start();
    }

    @Override
    protected void onResume()
    {
        handleVisibliity();
        super.onResume();
    }

    private void handleVisibliity(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(preferenceFile+getString
                (R.string.app_name), 0);
        SharedPreferences.Editor editor = pref.edit();
        RadioGroup rg = (RadioGroup) findViewById(R.id.Radiogroup);
        if(!pref.getBoolean(StaticKeys.VIBRATE_KEY,false)){
            seekBar.setVisibility(View.INVISIBLE);
            findViewById(R.id.textView4).setVisibility(View.INVISIBLE);
        }else{
            seekBar.setVisibility(View.VISIBLE);
            findViewById(R.id.textView4).setVisibility(View.VISIBLE);
        }
        if(!pref.getBoolean(StaticKeys.FLASH_SCREEN_KEY,false)){
            rg.setVisibility(View.INVISIBLE);
        }else{
            rg.setVisibility(View.VISIBLE);
        }
    }

    public void updateColor(final int color) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (whichColor)
                    myView.setBackgroundColor(Color.WHITE);
                else
                    myView.setBackgroundColor(color);
            }
        });
    }


    public void flashLight()
    {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String myString = "010101010101010101010101010101";
        long blinkDelay = 50; //Delay in ms
        for (int i = 0; i < myString.length(); i++) {
            if (myString.charAt(i) == '0') {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, true);
                    }

                } catch (CameraAccessException e) {
                }
            } else {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, false);
                    }

                } catch (CameraAccessException e) {
                }
            }
            try {
                Thread.sleep(blinkDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
