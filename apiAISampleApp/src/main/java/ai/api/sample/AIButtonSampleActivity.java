package ai.api.sample;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/*
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ai.api.AIConfiguration;
import ai.api.GsonFactory;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import ai.api.ui.AIButton;

public class AIButtonSampleActivity extends Activity implements AIButton.AIButtonListener {

    public static final String TAG = AIButtonSampleActivity.class.getName();

    private AIButton aiButton;
    private TextView resultTextView;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    TextToSpeech t1;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_aibutton_sample);
        TTS.init(getApplicationContext());
// custom tts t1

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
//
        t1.speak("sam at your service   sir", TextToSpeech.QUEUE_FLUSH, null);

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        aiButton = (AIButton) findViewById(R.id.micButton);

        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                Config.SUBSCRIPTION_KEY, AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        config.setRecognizerStartSound(getResources().openRawResourceFd(R.raw.test_start));
        config.setRecognizerStopSound(getResources().openRawResourceFd(R.raw.test_stop));
        config.setRecognizerCancelSound(getResources().openRawResourceFd(R.raw.test_cancel));

        aiButton.initialize(config);
        aiButton.setResultsListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    @Override
    protected void onPause() {
        super.onPause();

        // use this method to disconnect from speech recognition service
        // Not destroying the SpeechRecognition object in onPause method would block other apps from using SpeechRecognition service
        aiButton.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // use this method to reinit connection to recognition service
        aiButton.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_aibutton_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onResult");
                resultTextView.setMovementMethod(new ScrollingMovementMethod());
                resultTextView.setText(gson.toJson(response));
                String jsonstr = gson.toJson(response);



                String weather = "a";


                //sam code  for json processing
                try {
                    JSONObject jsonObj = new JSONObject(jsonstr);
                    JSONObject jobjResult = jsonObj.getJSONObject("result");
                    String action = jobjResult.getString("action");
                    JSONObject jobjParameters = jobjResult.getJSONObject("parameters");
//introduction
                    if (action.equals("sam_introduction")) {
                        t1.speak("hello sir     ", TextToSpeech.QUEUE_FLUSH, null);
                        t1.speak("i am sam   your virtual assistant", TextToSpeech.QUEUE_FLUSH, null);
                        t1.speak("i was created in heisenberg lab as part of engineering project ", TextToSpeech.QUEUE_FLUSH, null);
                        t1.speak("my capabilities are at your disposal", TextToSpeech.QUEUE_FLUSH, null);

                    }
// surveilance

                    if (action.equals("inphone.surveillance")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.1.103"));
                        startActivity(browserIntent);
                    }
// smart alarm
                    if (action.equals("clock.alarm_set")) {

                        String time = jobjParameters.getString("time");
                        //Toast.makeText(getApplicationContext(), action, Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), time, Toast.LENGTH_LONG).show();
                        String[] seperatedTime = time.split(":");
                        int hour = Integer.parseInt(seperatedTime[0]);
                        int min = Integer.parseInt(seperatedTime[1]);
                        createAlarm("Alarm is set", hour, min);
                    }
//@@@@ international weather
                    if (action.equals("wether_search")) {
                        String weather_info;
                        weather_info= String.format("http://www.api.ai/search?q=%s", weather);

                        t1.speak(weather_info, TextToSpeech.QUEUE_FLUSH, null);
                    }

//intelligent websearch

                    if (action.equals("web.search")) {

                        String searchQuery = jobjParameters.getString("q");
                        try {
                            String serveiceName = jobjParameters.getString("service_name");
                            if (serveiceName.equals("Google")) {
                                String searchInk = String.format("http://www.google.com/search?q=%s", searchQuery);
                                Uri uri = Uri.parse(searchInk);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                final String speachPhrase = String.format("googling for %s", searchQuery);
                                t1.speak(speachPhrase, TextToSpeech.QUEUE_FLUSH, null);
                                startActivity(intent);

                            }
                            if (serveiceName.equals("Bing")) {
                                String searchInk = String.format("http://www.bing.com/search?q=%s", searchQuery);
                                Uri uri = Uri.parse(searchInk);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                final String speachPhrase = String.format("searching in bing for %s", searchQuery);
                                t1.speak(speachPhrase, TextToSpeech.QUEUE_FLUSH, null);
                                startActivity(intent);
                            }

                        } catch (Exception e) {
                            String searchInk = String.format("http://www.google.com/search?q=%s", searchQuery);
                            Uri uri = Uri.parse(searchInk);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            final String speachPhrase = String.format("googling for %s", searchQuery);
                            t1.speak(speachPhrase, TextToSpeech.QUEUE_FLUSH, null);
                            startActivity(intent);
                        }
                    }
//@@@direct search
                    if (action.equals("what is"))
                    {
                        String quer = jobjParameters.getString("q");
                        String search_res=String.format("http://www.api.ai/search?q=%s", quer);
                        t1.speak(search_res, TextToSpeech.QUEUE_FLUSH, null);
                    }
//@@@direct who is search
                    if (action.equals("who is"))
                    {
                        String quer = jobjParameters.getString("q");
                        String search_res=String.format("http://www.api.ai/search?q=%s", quer);
                        t1.speak(search_res, TextToSpeech.QUEUE_FLUSH, null);
                    }
///@@@international News
                    if(action.equals("news1"))
                    {
                        String news_info;
                        String quer = jobjParameters.getString("q");
                        news_info= String.format("http://www.api.ai/search?q=%s",quer);
                        t1.speak(news_info, TextToSpeech.QUEUE_FLUSH, null);
                    }
// music playback
                    if (action.equals("media.music_play")) {
                        String artist = jobjParameters.getString("artist");
                        playSearchArtist(artist);
                    }
// opening apps ----facebook
                    if (action.equals("apps.open")) {
                        String appName = jobjParameters.getString("app_name");
                        if (appName.equals("facebook")) {
                            Intent intent = new Intent("android.intent.category.LAUNCHER");
                            intent.setClassName("com.facebook.katana", "com.facebook.katana.LoginActivity");
                            startActivity(intent);
                        }
                    }

// calling contacts
                    if(action.equals("call.call"))
                    {
                        String Phone_number = jobjParameters.getString("q");
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +"919496825525"));
                        startActivity(intent);
                    }

                } catch (Exception e) {
                    t1.speak("sorry i didnot catch that", TextToSpeech.QUEUE_FLUSH, null);
                }

                Log.i(TAG, "Received success response");

                // this is example how to get different parts of result object
                final Status status = response.getStatus();
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());

                final Result result = response.getResult();
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());

                Log.i(TAG, "Action: " + result.getAction());
                final String speech = result.getFulfillment().getSpeech();
                Log.i(TAG, "Speech: " + speech);
                TTS.speak(speech);

                final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
                }

                final HashMap<String, JsonElement> params = result.getParameters();
                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
            }

        });
    }


    //function to create alarms
    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, "Alarm is set")
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    //dunction to play artist music
    public void playSearchArtist(String artist) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,
                MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist);
        intent.putExtra(SearchManager.QUERY, artist);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onError");
                resultTextView.setText(error.toString());
            }
        });
    }

    @Override
    public void onCancelled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onCancelled");
                resultTextView.setText("");
            }
        });
    }
}
   /* @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AIButtonSample Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ai.api.sample/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AIButtonSample Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ai.api.sample/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
*/