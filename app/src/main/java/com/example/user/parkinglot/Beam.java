package com.example.user.parkinglot;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Beam extends Activity {
    byte[] token;
    NfcAdapter mNfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] intentFiltersArray;
    NdefMessage msg=null;
    TextView textView1,textView2,textView3,locationNumber;
    Button commingButton,leavingButton;
    String transactionNumber = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
        textView1=(TextView)findViewById(R.id.textView) ;
        textView2=(TextView)findViewById(R.id.textView2) ;
        textView3=(TextView)findViewById(R.id.textView3) ;
        locationNumber=(TextView)findViewById(R.id.textView10) ;
        commingButton = (Button)findViewById(R.id.button4);
        leavingButton = (Button)findViewById(R.id.button2);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndefFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {ndefFilter};

    }


    protected  void sendleaving(View view){
        if(transactionNumber == null){
            Toast.makeText(this,"please firstly arriving parking lot",Toast.LENGTH_LONG).show();
            return;
        }
//        now = new Date(System.currentTimeMillis());
//        SimpleDateFormat formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd, hh:mm:ss");
        NdefMessage leavingmessage = new NdefMessage(new NdefRecord[]{
                NdefRecord.createExternal("com.example.user.parkinglot","lev",transactionNumber.getBytes())
        });
        mNfcAdapter.setNdefPushMessage(leavingmessage,this,this);
    }

    protected void sendcoming(View view){
        if (msg==null){
        }
        else {

            mNfcAdapter.setNdefPushMessage(msg, this, this);
        }
    }

    private void sendToken(Intent intent){
        JSONObject info = null;
        try {

            info = new JSONObject();
            info.put("username",intent.getStringExtra("username"));
            info.put("parkinglotname",intent.getStringExtra("parkinglotname"));
            info.put("start",intent.getStringExtra("start"));
            info.put("end",intent.getStringExtra("end"));
            info.put("transaction_no",intent.getIntExtra("transaction_no",0));
            info.put("token",intent.getStringExtra("token"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        transactionNumber = new String(String.valueOf(intent.getIntExtra("transaction_no",0)));
        //Date start=null,end=null;
        //start.setTime(intent.getStringExtra("start"));
        //end.setTime(intent.getLongExtra("end",0));
        //textView1.setText("username:  "+intent.getStringExtra("username"));
        //textView2.setText("parkinglot:  "+intent.getStringExtra("parkinglot"));
        textView3.setText("duration:   "+ intent.getStringExtra("start")+"    --    "+intent.getStringExtra("end"));
        //SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        msg = new NdefMessage(new NdefRecord[]{
                NdefRecord.createExternal("com.example.user.parkinglot","com",String.valueOf(info).getBytes())
        });
        Log.d("nfc",info.toString());


    }



    private void processBeam(Intent intent){
        this.setIntent(intent);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String messageString = new String(msg.getRecords()[0].getPayload());
        locationNumber.setText(messageString);
    }


    @Override
    public void onResume(){
        super.onResume();
        setIntent(getIntent());
        if (NfcAdapter.getDefaultAdapter(this) != null) {
            NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
            // Check to see that the Activity started due to an Android Beam
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                //processBeam(getIntent());
                //textView.setText(getIntent().toString());
                processBeam(getIntent());
            }
            else{
                sendToken(getIntent());
            }
        }
    }
    @Override
    public  void onPause(){
        super.onPause();
        if (mNfcAdapter != null) {
            try {
                // Disable foreground dispatch:
                mNfcAdapter.disableForegroundDispatch(this);
            } catch (NullPointerException e) {
                // Drop NullPointerException that is sometimes thrown
                // when NFC service crashed
            }
        }
    }
    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
            processBeam(intent);
        }
        else{
            sendToken(intent);
        }
    }


}
