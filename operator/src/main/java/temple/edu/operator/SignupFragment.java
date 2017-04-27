package temple.edu.operator;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class SignupFragment extends Fragment implements OnClickListener
{
    SharedPreferences settings;
    TextView acc,psw,conpsw,email,phone,lotname,address,total_number,price_info,start,end;
    private Button button_login,button_signup;
    final SignupFragment.HttpProcess httpProcess = new SignupFragment.HttpProcess();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        settings=getActivity().getSharedPreferences("account", Context.MODE_PRIVATE);
        acc = (TextView) view.findViewById(R.id.suname);
        psw = (TextView) view.findViewById(R.id.spasswd);
        conpsw = (TextView) view.findViewById(R.id.sconfirmpsw);
        email = (TextView) view.findViewById(R.id.semail);
        phone = (TextView) view.findViewById(R.id.sphone);
        lotname = (TextView) view.findViewById(R.id.lotname);
        address = (TextView) view.findViewById(R.id.address);
        total_number =(TextView)view.findViewById(R.id.totalnumber);
        price_info = (TextView)view.findViewById(R.id.price_info);
        start = (TextView)view.findViewById(R.id.start_time);
        end=(TextView)view.findViewById(R.id.end_time);
        button_signup = (Button) view.findViewById(R.id.button_signup);
        button_login = (Button) view.findViewById(R.id.button_login);
        button_signup.setOnClickListener(this);
        button_login.setOnClickListener(this);
        return view ;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){

            case R.id.button_login:
                LoginFragment fOne = new LoginFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction tx = fm.beginTransaction();
                tx.replace(R.id.id_content, fOne, "ONE");
                tx.addToBackStack(null);
                tx.commit();
                break;
            case R.id.button_signup:
                JSONObject send = new JSONObject();
                try {

                    send.put("username",acc.getText().toString()) ;
                    send.put("password",psw.getText().toString());
                    send.put("password2",conpsw.getText().toString());
                    send.put("email",email.getText().toString());
                    send.put("phone",phone.getText().toString());
                    send.put("price_info",price_info.getText().toString());
                    send.put("lotname",lotname.getText().toString());
                    send.put("address",address.getText().toString());
                    send.put("total_number",total_number.getText().toString());
                    send.put("start_time",start.getText().toString());
                    send.put("close_time",end.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                httpProcess.setcontent("http://ssh.missingrain.live:8001/registration/register_operator/",send);
                httpProcess.execute();
                break;

        }
    }



    public class HttpProcess extends AsyncTask<String, Void, String> {
        String destination =null;
        JSONObject content = null;
        @Override
        protected String doInBackground(String... params) {

            String result="";

            try {

                URL url = new URL(destination);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                //connection.setChunkedStreamingMode(0);
                connection.setRequestMethod("POST");
                Log.d("ready to send: ",content.toString());
                connection.getOutputStream().write(content.toString().getBytes());//把数据以流的方式写给服务器。
                connection.getOutputStream().close();
                int code = connection.getResponseCode();
                Log.i("code==" ,String.valueOf(code));
                Log.i("status code: ",String.valueOf(connection.getResponseCode()));
                InputStream in = connection.getInputStream();
                //Read Result
                int val;
                while ((val=in.read()) > 0) {
                    result += (char) val;
                }
                in.close();

            } catch(Exception e) {
                e.printStackTrace();
            }
            return result;
        }



        @Override
        protected void onPostExecute(String result)
        {
            JSONObject out = null;
            try {
                out = new JSONObject(result);
                if(out.getString("status").equals("11")){
                    Toast.makeText(getActivity(),"Sign up successful",Toast.LENGTH_LONG).show();
                    try{
                        ((login)getActivity()).savePublic(out.getString("public_key"));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    LoginFragment fOne = new LoginFragment();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction tx = fm.beginTransaction();
                    tx.replace(R.id.id_content, fOne, "ONE");
                    tx.addToBackStack(null);
                    tx.commit();

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("username",acc.getText().toString());
                    editor.putString("password",psw.getText().toString());
                    editor.commit();
                }
                else{
                    Toast.makeText(getActivity(),"Sign up failed",Toast.LENGTH_LONG).show();
                }



//                    Intent gotomap = new Intent(getActivity(),com.example.user.parkinglot.login.class);
//                    gotomap.putExtra("username",acc.getText().toString());
//                    gotomap.putExtra("password",psw.getText().toString());
//                    gotomap.putExtra("confirm password",conpsw.getText().toString());
//                    gotomap.putExtra("email",email.getText().toString());
//                    gotomap.putExtra("phone",phone.getText().toString());
//                    startActivity(gotomap);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        protected void setcontent(String dest, JSONObject cont){
            destination = dest;
            content = cont;
        }

    }


}