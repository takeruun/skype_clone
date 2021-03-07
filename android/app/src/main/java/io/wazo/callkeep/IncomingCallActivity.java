package io.wazo.callkeep;
import Android.R.*;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.wazo.callkeep.Constants;

public class IncomingCallActivity extends Activity {

    private SharedPreferences mSharedPreferences;
    private String username;
    private String callUser;
    private String TAG = "IncomingCallActivity";

    //private Pubnub mPubNub;
    private TextView mCallerID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
    
        this.username = "user_name";

        Bundle extras = getIntent().getExtras();
        if (extras==null){

            Toast.makeText(this, "Need to pass username to IncomingCallActivity in intent extras call_user.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        this.callUser = "call_user";

        Log.wtf(TAG, "username: "+username);
        Log.wtf(TAG, "callUser: "+callUser);

        this.mCallerID = (TextView) findViewById(R.id.caller_id);
        this.mCallerID.setText(this.callUser);

        //this.mPubNub  = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        //this.mPubNub.setUUID(this.username);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incoming_call, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Publish a hangup command if rejecting call.
     * @param view
     */
    public void rejectCall(View view){
        /*
        JSONObject hangupMsg = PnPeerConnectionClient.generateHangupPacket(this.username);
        this.mPubNub.publish(this.callUser,hangupMsg, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                finish();
            }
        });
        */
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
                /*
        if(this.mPubNub!=null){
            this.mPubNub.unsubscribeAll();
        }
        */
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    };
}