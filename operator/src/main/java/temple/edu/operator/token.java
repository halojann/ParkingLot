package temple.edu.operator;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class token extends Activity {
    String str;
    NfcAdapter mNfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] intentFiltersArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
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


    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onResume(){
        super.onResume();
        if (NfcAdapter.getDefaultAdapter(this) != null) {
                NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
                // Check to see that the Activity started due to an Android Beam
                if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                    //processBeam(getIntent());
                    //textView.setText(getIntent().toString());
                    processBeam(getIntent());
                }
        }
    }
    @Override
    public void onPause(){
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
    protected void processBeam(Intent intent){

    }

    protected PublicKey getpublickey(Uri keypath){
        PublicKey PublicKey = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("my-prvkey.pem"));
            String s = br.readLine();
            str = "";
            s = br.readLine();
            while (s.charAt(0) != '-'){
                str += s + "\r";
                s = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //BASE64 base64decoder = new BASE64Decoder();
        byte[] b = Base64.decode(str,Base64.DEFAULT);

//生成私匙
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(b);

        //PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(b);
//PublicKey privateKey = kf.generatePublic(keySpec);
        try {
            PublicKey =  kf.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return PublicKey;
    }

    protected Boolean verifySignature(byte[] data,byte[] token,PublicKey publicKey){
        Signature s = null;
        boolean valid = false;
        try {
            s = Signature.getInstance("SHA256withRSA");
            s.initVerify(publicKey);
            s.update(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        //s.initVerify(((PrivateKeyEntry) entry).getCertificate());
        //s.update(data);
        try {
            valid = s.verify(token);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return valid;
    }
}
