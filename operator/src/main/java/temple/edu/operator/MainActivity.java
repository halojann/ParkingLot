package temple.edu.operator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {
    TextView testview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testview = (TextView)findViewById(R.id.textView);

    }


    public void doPost(View view){
        try {
            //URL url = new URL("http://10.109.106.250:8000/accounts/hello");
            URL HttpUrl = new URL("http://10.109.106.250:8000/accounts/hello");
            HttpURLConnection conn = (HttpURLConnection) HttpUrl.openConnection();

            conn.setRequestMethod("POST");
            conn.setReadTimeout(9000);
            OutputStream out = conn.getOutputStream(); //新建输出流对象
            String content = "name="+"fcl"+"&age="+"24";//传递对象
            out.write(content.getBytes());//将传递对象转为字符流写入输出流中
            Log.d("if try","yes");
            //下面是对于服务器返回数据的处理
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while((str=reader.readLine())!=null){
                sb.append(str);
            }
            //System.out.println(sb.toString());
            testview.setText(sb);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    private void pushNotification(View view) {
//        JSONObject jPayload = new JSONObject();
//        JSONObject jNotification = new JSONObject();
//        JSONObject jData = new JSONObject();
//        try {
//            jNotification.put("title", "Google I/O 2016");
//            jNotification.put("body", "Firebase Cloud Messaging (App)");
//            jNotification.put("sound", "default");
//            jNotification.put("badge", "1");
//            jNotification.put("click_action", "OPEN_ACTIVITY_1");
//
//            jData.put("picture_url", "http://opsbug.com/static/google-io.jpg");
//
////            switch(type) {
////                case "tokens":
////                    JSONArray ja = new JSONArray();
////                    ja.put("AAAARH98fcE:APA91bGQkGZacKx4rqvdjVsHqLhmCSxLm_uZmvUopNJ2d_L5IVQtmI1lk32vwVaogCE5ZkB5kZZxK8xb3VSbcYktqZeXn33wbbcnUWxj-h7b3p_7NNL-Zba6Ios8-hEPVhlX9sGbL9TU");
//////                    ja.put(FirebaseInstanceId.getInstance().getToken());
////                    jPayload.put("registration_ids", ja);
////                    break;
////                case "topic":
////                    jPayload.put("to", "/topics/news");
////                    break;
////                case "condition":
////                    jPayload.put("condition", "'sport' in topics || 'news' in topics");
////                    break;
//////                default:
//////                    jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
////            }
//
//            jPayload.put("priority", "high");
//            jPayload.put("notification", jNotification);
//            jPayload.put("data", jData);
//
//            URL url = new URL("http://10.109.106.250:8000/accounts/hello");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            //conn.setRequestProperty("Authorization", AUTH_KEY);
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setDoOutput(true);
//
//            // Send FCM message content.
//            OutputStream outputStream = conn.getOutputStream();
//            outputStream.write(jPayload.toString().getBytes());
//
//            // Read FCM response.
//            InputStream inputStream = conn.getInputStream();
//            final String resp = inputStream.toString();
//
//            Handler h = new Handler(Looper.getMainLooper());
//            h.post(new Runnable() {
//                @Override
//                public void run() {
//                    testview.setText(resp);
//                }
//            });
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }
//    }
}
