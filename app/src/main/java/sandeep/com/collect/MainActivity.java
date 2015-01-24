package sandeep.com.collect;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends Activity {
    private static final String TAG="MainActivity";

    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private Context ctx;


   int START_MORNING_HOUR= CollectionConstant.START_MORNING_HOUR; //8 AM
   int START_MORNING_MINUTE= CollectionConstant.START_MORNING_MINUTE; //8 AM

    int START_MORNING_SECONDS= CollectionConstant.START_MORNING_SECONDS; //8 AM
    int START_MORNING_MILLIS= CollectionConstant.START_MORNING_MILLIS; //8 AM
    long SCHEDULEDING_TIME_IN_MS = CollectionConstant.SCHEDULEDING_TIME_IN_MS;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG,"start service");
        ctx = this;

        RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
        Button stopButton = (Button)mainLayout.findViewById(R.id.main_stop_button);
        Button closeButton = (Button)mainLayout.findViewById(R.id.close_button);
        startService(new Intent(getApplicationContext(),CollectionService.class));
        scheduleDailyRun();
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(),CollectionService.class));
            }
        });


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
              //  stopService(new Intent(getApplicationContext(),CollectionService.class));
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void scheduleDailyRun() {
        Log.i(TAG,"Register alarm for daily run");
        Intent alarmIntent = new Intent(ctx, CollectionAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ctx, 0, alarmIntent, 0);

        Calendar date = new GregorianCalendar();
        int nowHour= date.get(Calendar.HOUR_OF_DAY);
        if(nowHour >=START_MORNING_HOUR ){
            date.add(Calendar.DATE, 1);
        }

        date.set(Calendar.HOUR_OF_DAY, START_MORNING_HOUR);
        date.set(Calendar.MINUTE, START_MORNING_MINUTE);

        //date.set(Calendar.HOUR_OF_DAY, 20);//-- test runs
       //  date.set(Calendar.MINUTE, 33);//-- test runs


        date.set(Calendar.SECOND, START_MORNING_SECONDS);
        date.set(Calendar.MILLISECOND, START_MORNING_MILLIS);

        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), SCHEDULEDING_TIME_IN_MS, pendingIntent);
        Log.i(TAG,"repeating alarm set");
        Toast.makeText(this, getString(R.string.collectionAlarmSet), Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm() {
        if (manager != null) {
            manager.cancel(pendingIntent);
            Toast.makeText(this, "Collection Alarm Canceled", Toast.LENGTH_SHORT).show();
        }
    }
}
