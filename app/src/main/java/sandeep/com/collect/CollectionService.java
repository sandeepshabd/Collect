package sandeep.com.collect;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;

import java.net.SocketException;
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

    long COLLECTION_TIME_MS = CollectionConstant.COLLECTION_TIME_MS; // 10 seconds
    long SEND_TIME_MS = CollectionConstant.SEND_TIME_MS; // 1 minute

    int START_MORNING_HOUR= CollectionConstant.START_MORNING_HOUR; //8 AM
    int START_MORNING_MINUTE= CollectionConstant.START_MORNING_MINUTE; //8 AM


    int END_EVENING_HOUR= CollectionConstant.END_EVENING_HOUR; // 6 PM
    int END_EVENING_MINUTE= CollectionConstant.END_EVENING_MINUTE;
    //int TEST_TIME= 18;


    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    Context context =this;
    Timer scheduleTimer = new Timer();

    StringBuilder resultMaker = new StringBuilder();
    Calendar mainCalendar = new GregorianCalendar();

    SendTask sendDataTask ;
    CollectionAsyncTask collectionAsyncTask;
    final CollectionTask collectionTask = new CollectionTask();



    public CollectionService(){

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        SensorData sensorListener = new SensorData();
        mSensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(sensorListener, magnetometer, SensorManager.SENSOR_DELAY_UI);

        mainCalendar = new GregorianCalendar();
        int current_hour = mainCalendar.get( Calendar.HOUR_OF_DAY );
        int current_minute = mainCalendar.get( Calendar.MINUTE );
        Log.i(TAG,"hour of day:"+current_hour+":"+current_minute);
        Log.i(TAG,"START TIME:"+START_MORNING_HOUR+":"+START_MORNING_MINUTE+" END TIME:"+END_EVENING_HOUR+":"+END_EVENING_MINUTE);
        //process to start only between 8 AM and 6 PM

        if((current_hour > START_MORNING_HOUR ||(current_hour == START_MORNING_HOUR && current_minute >= (START_MORNING_MINUTE-5)) )
                && (current_hour < END_EVENING_HOUR || (current_hour == END_EVENING_HOUR && current_minute < END_EVENING_MINUTE) )){
            Log.i(TAG,"start collection now as it is within the time range.");
            collectionStarted = true;
            startCollectingSendingData();
        }else{
            Log.i(TAG,"Not running the task as current time is outside range.");
        }
        return super.onStartCommand(intent,flags,startId);
    }

//    @Override
//    protected void onHandleIntent(Intent workIntent) {
//        Log.i(TAG,"onHandleIntent msg received.");
//        Toast.makeText(this, "Starting collection ", Toast.LENGTH_SHORT).show();
//        //TODO - destroy scheduled task and timer on stop button click intent.
//
//    }

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
           RestClient.get().postCollectionData(jsnobject); //TODO - remove once awake system is working.
        }catch(SocketException ex){
            Log.e(TAG,"error in sending data.");
        }catch(Exception ex){
           Log.e(TAG,"error in sending data.");
        }

    }

    private void startCollectingSendingData(){
        Log.i(TAG,"startCollectingSendingData function call. ");

        try {
            scheduleTimer = new Timer();
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
        Toast.makeText(this, "Service is closing", Toast.LENGTH_SHORT).show();
        try{
          //  scheduleTimer.cancel();
         //   scheduleTimer.purge();
          //  stopSelf();
        }catch(Exception e){

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"Bind service is called.");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class CollectionAsyncTask extends TimerTask{
        public void run() {
            Log.i(TAG,"doAsynchronousTask to collect the data.");
            try {
                collectionTask.execute();
                resultMaker.append("{");
                resultMaker.append(DataClass.singleRunResult);
                resultMaker.append("},");
                mainCalendar = new GregorianCalendar();

            }catch(Exception e){
                Log.e(TAG,"error in starting the collection wake lock.");
            }
            int hour = mainCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = mainCalendar.get(Calendar.MINUTE);
            Log.i(TAG, "current time:" + hour + ":" + minute);
            Log.i(TAG, "Stopping time:" + END_EVENING_HOUR + ":" + END_EVENING_MINUTE);
            if(hour>END_EVENING_HOUR || (hour==END_EVENING_HOUR && minute>=END_EVENING_MINUTE)){
                try {
                    Log.i(TAG, "Time is past the collection time. Should stop collection.");
                    sendDataTask.cancel();
                    sendTask();
                    this.cancel();
                    scheduleTimer.cancel();
                    scheduleTimer.purge();

                    scheduleTimer = null;
                    //collectionAsyncTask= null;
                    sendDataTask = null;
                    stopSelf();
                    CollectionAlarmReceiver.release();
                    Log.i(TAG, "Everything stopped for today.");
                }catch(Exception e){
                    Log.e(TAG,"error in closing the collection");
                }
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
