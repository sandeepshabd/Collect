package sandeep.com.collect;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.regex.Pattern;


public class LoginActivity extends Activity {
    Context context;
    TextView emailAddr;
    TextView phoneNumber;
    private static final String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_login);
        RelativeLayout loginLayout = (RelativeLayout)findViewById(R.id.activity_login);
        emailAddr = (TextView) loginLayout.findViewById(R.id.email);
        phoneNumber = (TextView) loginLayout.findViewById(R.id.phone);
        emailAddr.setText(getEmailAddress());
        phoneNumber.setText(getPhoneNumber());
        context= this;

        Button confirmButton = (Button)loginLayout.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                UserProfile.emailAddress=emailAddr.getText().toString();
                UserProfile.phoneNumber= phoneNumber.getText().toString();
               // intent.putExtra("EMAIL", getEmailAddress());
               // intent.putExtra("PHONE", getPhoneNumber());
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(UserProfile.emailAddress).matches())
                if(Patterns.EMAIL_ADDRESS.matcher(UserProfile.emailAddress).matches()
                        && Patterns.PHONE.matcher(UserProfile.phoneNumber).matches()){
                    startActivity(intent);
                }

            }
        });

        Button exitButton = (Button)loginLayout.findViewById(R.id.exitLogin);
       exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getPhoneNumber(){
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    private String getEmailAddress(){
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        return accounts[0].name;
    }
}
