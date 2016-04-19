package bfh.ti.i2c_jni_template;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by philipp on 18.04.16.
 */
public class TextSpeech implements TextToSpeech.OnInitListener {

    public TextToSpeech tts;
    public TextSpeech(MainI2cActivity con){
        tts = new TextToSpeech(con, this);
    }


    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.ENGLISH);
    }

    public void SpeakOut(String message)
    {
        SpeakOut(message, Locale.ENGLISH);
    }

    public void SpeakOut(String message, Locale loc)
    {
        tts.setLanguage(loc);
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    }
}
