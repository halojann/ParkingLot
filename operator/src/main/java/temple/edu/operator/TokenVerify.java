package temple.edu.operator;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Acer on 26/4/2017.
 */

public class TokenVerify {
    PublicKey pk;
    //String input;

    public boolean verifytoken(InputStream pubkey,String signature, String content){
        try {
            loadPublicKey(pubkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verifySignature(content.getBytes(), Base64.decode(signature,Base64.DEFAULT),pk);
    }



    private  void  loadPublicKey(InputStream in) throws Exception{
        try {
            BufferedReader br= new BufferedReader(new InputStreamReader(in));
            String readLine= null;
            StringBuilder sb= new StringBuilder();
            while((readLine= br.readLine())!=null){
                if(readLine.charAt(0)=='-'){
                    continue;
                }else{
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPublicKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }
    private  void loadPublicKey(String publicKeyStr) throws Exception{
        try {
            byte[] buffer= Base64.decode(publicKeyStr,Base64.DEFAULT);
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);
            pk= (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    protected static Boolean verifySignature(byte[] data,byte[] token,PublicKey publicKey) {
        Signature s = null;
        boolean valid = false;
        try {
            s = Signature.getInstance("SHA1withRSA");
            s.initVerify(publicKey);
            s.update(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        try {
            valid = s.verify(token);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return valid;
    }

}
