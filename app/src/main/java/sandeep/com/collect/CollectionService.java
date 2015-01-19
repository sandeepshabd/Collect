package sandeep.com.collect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class CollectionService extends Service {

    private static final String TAG="CollectionService";

    boolean collectionStarted = false;

    long COLLECTION_TIME_MS = 10000; // 10 seconds
    long SEND_TIME_MS = 60000; // 1 minute

    int START_MORNING_HOUR= 8; //8 AM
    int START_MORNING_MINUTE= 0; //8 AM
    int START_MORNING_SECONDS= 0; //8 AM
    int START_MORNING_MILLIS= 0; //8 AM

    int END_EVENING_HOUR= 18; // 6 PM
    int END_EVENING_MINUTE= 0;
    //int TEST_TIME= 18;

    long SCHEDULEDING_TIME_IN_MS = 1000 * 60 * 60 * 24 * 7;
    //long SCHEDULEDING_TIME_IN_MS = 1000*60*4;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    Context context =this;
    Timer scheduleTimer = new Timer();
    Timer dailyRunTimer ;
    StringBuilder resultMaker = new StringBuilder();
    Calendar mainCalendar = new GregorianCalendar();
    TimerTask scheduleDataTask;
    SendTask sendDataTask ;
    CollectionAsyncTask collectionAsyncTask;
    final CollectionTask collectionTask = new CollectionTask();

    public CollectionService() {}

    @Override
    public void onCreate() {
        Log.i(TAG,"create");
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        SensorData sensorListener = new SensorData();
        mSensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(sensorListener, magnetometer, SensorManager.SENSOR_DELAY_UI);

        //daily run task
        scheduleDataTask = new TimerTask() {
            public void run() {
                Log.i(TAG,"Scheduled data sending task for everyday.");
               // TEST_TIME= TEST_TIME+3; -- for test runs
               // END_EVENING_MINUTE=TEST_TIME; -- for test runs
                startCollectingSendingData();
           }
        };

        mainCalendar = new GregorianCalendar();
        int current_hour = mainCalendar.get( Calendar.HOUR_OF_DAY );
        int current_minute = mainCalendar.get( Calendar.MINUTE );
        Log.i(TAG,"hour of day:"+current_hour+":"+current_minute);
        Log.i(TAG,"START TIME:"+START_MORNING_HOUR+":"+START_MORNING_MINUTE+" END TIME:"+END_EVENING_HOUR+":"+END_EVENING_MINUTE);
        //process to start only between 8 AM and 6 PM

        if((current_hour >= START_MORNING_HOUR && current_minute >= START_MORNING_MINUTE)
                && (current_hour <= END_EVENING_HOUR && current_minute < END_EVENING_MINUTE)){
            Log.i(TAG,"start collection now as it is within the time range.");
            collectionStarted = true;
            startCollectingSendingData();
        }else{
            Log.i(TAG,"Not running the task as current time is outside range.");
        }
        dailyRunTimer= new Timer();
        scheduleDailyRun();
    }

    private void scheduleDailyRun(){

        Calendar date = new GregorianCalendar();
        int nowHour= date.get(Calendar.HOUR_OF_DAY);
        if(nowHour >=START_MORNING_HOUR ){
            date.add(Calendar.DATE, 1);
        }

        date.set(Calendar.HOUR_OF_DAY, START_MORNING_HOUR);
        date.set(Calendar.MINUTE, START_MORNING_MINUTE);

        //date.set(Calendar.HOUR_OF_DAY, 23);-- test runs
       // date.set(Calendar.MINUTE, TEST_TIME);-- test runs

        date.set(Calendar.SECOND, START_MORNING_SECONDS);
        date.set(Calendar.MILLISECOND, START_MORNING_MILLIS);

        Log.i(TAG,"scheduleDailyRun - daily run. Start:"+ date.get(Calendar.DATE)+"-"+date.get(Calendar.HOUR_OF_DAY)+":"+date.get(Calendar.MINUTE)+" Every ms:"+SCHEDULEDING_TIME_IN_MS);

        dailyRunTimer.schedule(scheduleDataTask,
                date.getTime(),SCHEDULEDING_TIME_IN_MS
        );
    }

    private void sendTask(){
        Log.i(TAG,"sendTask function");
        String result = "["+resultMaker.toString()+
                        "{"+DataClass.singleRunResult+"}]";
        DataClass.singleRunResult="";
        resultMaker=  new StringBuilder();
        Log.i(TAG,"result:"+result);
        try{
           // RestClient.get().postCollectionData(result,new BackendResponse());
            JSONArray jsnobject = new JSONArray(result);
            RestClient.get().postCollectionData(jsnobject);
        }catch(Exception ex){
           Log.e(TAG,ex.getMessage());
        }

    }

    private void startCollectingSendingData(){
        Log.i(TAG,"startCollectingSendingData function call. ");

        try {
            scheduleTimer = new Timer();
            //data send task
            sendDataTask = new SendTask();
            collectionAsyncTask = new CollectionAsyncTask();
            scheduleTimer.schedule(collectionAsyncTask, 0, COLLECTION_TIME_MS);
            scheduleTimer.schedule(sendDataTask, 0, SEND_TIME_MS);
        }catch(Exception e){
            Log.e(TAG,"Timer task is already scheduled.");
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"destroying the timer.");
        try{
            scheduleTimer.cancel();
            scheduleTimer.purge();
            dailyRunTimer.cancel();
            dailyRunTimer.purge();
        }catch(Exception e){

        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class CollectionAsyncTask extends TimerTask{
        public void run() {
            Log.i(TAG,"doAsynchronousTask to collect the data.");
            collectionTask.execute();
            resultMaker.append("{");
            resultMaker.append(DataClass.singleRunResult);
            resultMaker.append("},");
            mainCalendar = new GregorianCalendar();
            int hour = mainCalendar.get( Calendar.HOUR_OF_DAY );
            int minute =mainCalendar.get( Calendar.MINUTE);
            Log.i(TAG,"current time:"+hour +":"+minute);
            Log.i(TAG,"Stopping time:"+END_EVENING_HOUR+":"+END_EVENING_MINUTE);
            if(hour>=END_EVENING_HOUR && minute>=END_EVENING_MINUTE){
                Log.i(TAG, "Time is past the collection time. Should stop collection.");
                sendDataTask.cancel();
                sendTask();
                this.cancel();
                scheduleTimer.cancel();
                scheduleTimer.purge();

                scheduleTimer=null;
                //collectionAsyncTask= null;
                sendDataTask=null;
                Log.i(TAG,"Everything stopped for today.");
            }
        }
    }

    private class SendTask extends TimerTask{
        public void run() {
            Log.i(TAG,"Scheduled send task");
            sendTask();
        }
    }

    private class CollectionTask {

        DateFormat timeFormat = new SimpleDateFormat("yyyyy-mm-dd HH:mm:ss:SSSZ");
        //get current date time with Date()

         protected void execute(){
             Log.i(TAG,"start execute collection service");
             Date time = new Date();
             GPSGatherData.getLocationData(context);
             StringBuilder collectBuilder = new StringBuilder();
             DataClass.singleRunResult=collectBuilder
             .append("\"deviceId\":\"")
             .append(UserProfile.phoneNumber)
             .append("\",")
             .append(" \"emailId\":\"")
             .append(UserProfile.emailAddress)
             .append("\",")
             .append(" \"time\":")
             .append("\"")
             .append(timeFormat.format(time))
             .append("\"")
             .append(",")
             .append(DataClass.gps)
             .append(",")
             .append(DataClass.accelerometerData)
             .append(",")
             .append(DataClass.orientationData).toString();
         }
    }

}
