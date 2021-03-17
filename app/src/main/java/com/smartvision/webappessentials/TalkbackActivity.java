package com.smartvision.webappessentials;
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.speech.RecognitionListener;
//import android.speech.RecognizerIntent;
//import android.speech.SpeechRecognizer;
////import android.view.View;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;

//import com.chaquo.python.PyObject;
//import com.chaquo.python.Python;
//import com.chaquo.python.android.AndroidPlatform;
//
//import java.util.ArrayList;
//import static android.Manifest.permission.RECORD_AUDIO;

public class TalkbackActivity extends AppCompatActivity {

//    private SpeechRecognizer speechRecognizer;
//    private Intent intentRecognizer;
      private TextView textView;
     // EditText Et1, Et2;
      //Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.talkback);
//
//        Et1 = (EditText)findViewById(R.id.et1);
//        Et2 = (EditText)findViewById(R.id.et1);
//        btn = (Button)findViewById(R.id.btn);
        textView = (TextView) findViewById(R.id.textView);
       // try {
//            if (!Python.isStarted()) {
//                Python.start(new AndroidPlatform(this));
//
//                Python py = Python.getInstance();
//                PyObject pyobj = py.getModule("script"); // name of our python file
//                PyObject obj = pyobj.callAttr("test"); // name of the function
//
//                //set the text
//                textView.setText(obj.toString());

//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        PyObject obj = pyobj.callAttr("main", Et1.getText().toString(), Et2.getText().toString());
//                        textView.setText(obj.toString());
//                    }
//                });
            //}
//        //giving reference
//        PyObject obj = null;
//
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


//        try {
//            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
//
//            textView = findViewById(R.id.textView);
//            intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//            intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//            speechRecognizer.setRecognitionListener(new RecognitionListener() {
//                @Override
//                public void onReadyForSpeech(Bundle params) {
//
//                }
//
//                @Override
//                public void onBeginningOfSpeech() {
//
//                }
//
//                @Override
//                public void onRmsChanged(float rmsdB) {
//
//                }
//
//                @Override
//                public void onBufferReceived(byte[] buffer) {
//
//                }
//
//                @Override
//                public void onEndOfSpeech() {
//
//                }
//
//                @Override
//                public void onError(int error) {
//
//                }
//
//                @Override
//                public void onResults(Bundle results) {
//                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                    String string = "";
//                    if (matches != null) {
//                        string = matches.get(0);
//                        textView.setText(string);
//                    }
//                }
//
//                @Override
//                public void onPartialResults(Bundle partialResults) {
//
//                }
//
//                @Override
//                public void onEvent(int eventType, Bundle params) {
//
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void StartButton(View view) {
//        speechRecognizer.startListening(intentRecognizer);
//    }
//
//    public void StopButton(View view) {
//        speechRecognizer.stopListening();
//    }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
