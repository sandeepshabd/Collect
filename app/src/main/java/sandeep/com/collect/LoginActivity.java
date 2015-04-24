package sandeep.com.collect;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

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

                try {
                    Log.i(TAG,"data confirmed.");
                    Intent intent = new Intent(context, MainActivity.class);
                    UserProfile.emailAddress = emailAddr.getText().toString();
                    UserProfile.phoneNumber = phoneNumber.getText().toString();
                        if (Patterns.EMAIL_ADDRESS.matcher(UserProfile.emailAddress).matches()
                                && Patterns.PHONE.matcher(UserProfile.phoneNumber).matches()) {
                            Toast.makeText(context, "Things looks good starting service.", Toast.LENGTH_SHORT).show();
                            createpref();
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "Please use a valid email and phone.", Toast.LENGTH_SHORT).show();
                        }
                }catch(Exception e){
                    Log.e(TAG,"Error in getting login details",e);
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

        String phone ="";
        try {
            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            phone = tMgr.getLine1Number();
        }catch(Exception e){
            Log.e(TAG,"Phone number not found:"+e.getMessage());
        }
        return phone;
    }

    private String getEmailAddress(){
        String email="";
        try{
            Account[] accounts = AccountManager.get(this).getAccounts();
            email=accounts[0].name;
        }catch(Exception e){
            Log.e(TAG,"Email not found:"+e.getMessage());
        }

        return email;
    }

    private void createpref(){

        SharedPreferences preferences = getSharedPreferences(CollectionConstant.ProfileFile, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phone", UserProfile.phoneNumber); // value to store
        editor.putString("email", UserProfile.emailAddress);
        editor.commit();
        Log.i(TAG,"strored:"+UserProfile.emailAddress+":"+UserProfile.phoneNumber);
    }
}
