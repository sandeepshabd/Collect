package sandeep.com.collect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

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
    int END_EVENING_HOUR= 18; // 6 PM
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    Context context =this;
    Timer scheduleTimer ;
    Timer dailyRunTimer ;
    StringBuilder resultMaker = new StringBuilder();
    Calendar mainCalendar = new GregorianCalendar();
    TimerTask doAsynchronousTask;
    TimerTask sendDataTask;
    TimerTask scheduleDataTask;



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

        //Async task to gather data
        final CollectionTask collectionTask = new CollectionTask();

        //data collection task
        doAsynchronousTask = new TimerTask() {
            public void run() {
                collectionTask.execute();
                resultMaker.append("{");
                resultMaker.append(DataClass.singleRunResult);
                resultMaker.append("},");

                int hour = mainCalendar.get( Calendar.HOUR_OF_DAY );
                if(hour>END_EVENING_HOUR){
                    sendDataTask.cancel();
                    sendTask();
                    doAsynchronousTask.cancel();
                    scheduleTimer.cancel();
                    scheduleTimer.purge();
                    scheduleTimer=null;
                }
            }
        };


        //data send task
        sendDataTask = new TimerTask() {
            public void run() {
                sendTask();
            }
        };

        //daily run task
        scheduleDataTask = new TimerTask() {
            public void run() {
                Timer newTime =  new Timer();
                startCollectingSendingData(newTime);
            }
        };


        int hour = mainCalendar.get( Calendar.HOUR_OF_DAY );
        Log.i(TAG,"hour of day:"+hour);
        //process to start only between 8 AM and 6 PM
        scheduleDailyRun();
        if(hour >= START_MORNING_HOUR && hour <END_EVENING_HOUR){
            Log.i(TAG,"start collection");
            collectionStarted = true;
            Timer newTime =  new Timer();
            startCollectingSendingData(newTime);
        }
    }

    private void scheduleDailyRun(){
        Log.i(TAG,"scheduleDailyRun");
        dailyRunTimer = new Timer();
        Calendar date = Calendar.getInstance();
        date.set(
                Calendar.DAY_OF_WEEK,
                Calendar.SUNDAY
        );
        date.set(Calendar.HOUR, START_MORNING_HOUR);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        dailyRunTimer.schedule(scheduleDataTask,
                date.getTime(),
                1000 * 60 * 60 * 24 * 7
        );
    }

    private void sendTask(){
        Log.i(TAG,"sendTask");
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
           // Log.e(TAG,ex.getMessage());
            ex.printStackTrace();
        }

    }

    private void startCollectingSendingData(Timer timeToRun){
        Log.i(TAG,"startCollectingSendingData");
        scheduleTimer= timeToRun;
        scheduleTimer.schedule(doAsynchronousTask, 0, COLLECTION_TIME_MS);
        scheduleTimer.schedule(sendDataTask, 0, SEND_TIME_MS);
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
