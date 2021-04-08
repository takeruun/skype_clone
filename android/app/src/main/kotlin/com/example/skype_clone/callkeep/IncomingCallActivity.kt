package com.example.skype_clone.callkeep

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.example.skype_clone.CallKeepPlugin
import com.example.skype_clone.R
import com.example.skype_clone.callkeep.Constants.EXTRA_CALLER_NAME
import com.example.skype_clone.callkeep.Constants.SKYPE_PREF

private const val TAG = "IncomingCallActivity"

class IncomingCallActivity : FragmentActivity() {
    private var sharedPreferences: SharedPreferences? = null
    private var username: String? = null
    private var callUser: String? = null

    //private Pubnub mPubNub;
    private var mCallerID: TextView? = null

    private var notifyManager: NotificationManager? = null

    init {
        Log.d(TAG, "Constructor")
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = this.getSharedPreferences(SKYPE_PREF, Context.MODE_PRIVATE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)
        username = "userName"

        // set this flag so this activity will stay in front of the keyguard
        val flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        window.addFlags(flags)
        callUser = sharedPreferences!!.getString(EXTRA_CALLER_NAME, "caller name")

        Log.wtf(TAG, "username: $username")
        Log.wtf(TAG, "callUser: $callUser")
        mCallerID = findViewById<View>(R.id.caller_name) as TextView
        mCallerID!!.text = callUser

        val notificationChannelId = this.intent!!.extras?.getInt("NOTIFICATION_ID")
        notifyManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notifyManager!!.cancelAll()

        //this.mPubNub  = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        //this.mPubNub.setUUID(this.username);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_incoming_call, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //if (id == R.id.action_settings) {
        //    return true;
        //}
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun acceptCall(view: View?) {
        Log.d(TAG, "acceptCall")
        CallKeepPlugin.getStatus()
    }

    /**
     * Publish a hangup command if rejecting call.
     * @param view
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun rejectCall(view: View?) {
        Log.d(TAG, "endCall")
        /*
        JSONObject hangupMsg = PnPeerConnectionClient.generateHangupPacket(this.username);
        this.mPubNub.publish(this.callUser,hangupMsg, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                finish();
            }
        });
        */
        val conn = CallConnectionService.getConnection("019100-192819") ?: return
        conn.onDisconnect()
        //stopService(Intent(applicationContext, RingSoundService::class.java))
        finish()
    }

    public override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
        //stopService(Intent(applicationContext, RingSoundService::class.java))
        /*
        if(this.mPubNub!=null){
            this.mPubNub.unsubscribeAll();
        }
        */
    }

    public override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        //stopService(Intent(applicationContext, RingSoundService::class.java))
    }
}