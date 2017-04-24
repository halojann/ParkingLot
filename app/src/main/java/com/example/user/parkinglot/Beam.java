package com.example.user.parkinglot;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.Toast;

public class Beam extends Activity implements NfcAdapter.CreateNdefMessageCallback {
    byte[] token;
    NfcAdapter mNfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] intentFiltersArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
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

    private void sendToken(){


    }

    private void processBeam(Intent intent){



    }

    @Override
    public void onResume(){
        super.onResume();
        super.onResume();
        if (NfcAdapter.getDefaultAdapter(this) != null) {
            NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
            // Check to see that the Activity started due to an Android Beam
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                //processBeam(getIntent());
                //textView.setText(getIntent().toString());
                processBeam(getIntent());
            }
            else{
                sendToken();
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
    }
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] {
                        NdefRecord.createExternal("com.example.user.parkinglot", "tkn", token)
//                new NdefRecord[] { NdefRecord.createMime(
//                        "application/acer.example.com.nfcbeam", keyfortransfer)
                });
        return msg;
    }

}
