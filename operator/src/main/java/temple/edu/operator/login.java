package temple.edu.operator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class login extends Activity {

    Button enterbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        enterbutton = (Button) findViewById(R.id.button);
        enterbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }


}
