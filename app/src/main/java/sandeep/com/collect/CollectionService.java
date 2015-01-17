package sandeep.com.collect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class CollectionService extends Service {

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    Context context =this;
    private static final String TAG="CollectionService";
    Timer timer = new Timer();
    StringBuilder resultMaker = new StringBuilder();
    boolean collectionStarted = false;
    Calendar mainCalendar = new GregorianCalendar();
    TimerTask doAsynchronousTask;
    TimerTask sendDataTask;


    public CollectionService() {

    }

    @Override
    public void onCreate() {
        Log.i(TAG,"create");
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        SensorData sensorListener = new SensorData();
        mSensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(sensorListener, magnetometer, SensorManager.SENSOR_DELAY_UI);

        final CollectionTask collectionTask = new CollectionTask();
        doAsynchronousTask = new TimerTask() {
            public void run() {
                collectionTask.execute();
                resultMaker.append("{");
                resultMaker.append(DataClass.singleRunResult);
                resultMaker.append("},");

                int hour = mainCalendar.get( Calendar.HOUR_OF_DAY );
                if(hour>17){
                    sendDataTask.cancel();
                    sendTask();
                    doAsynchronousTask.cancel();
                }
            }
        };

        sendDataTask = new TimerTask() {
            public void run() {
                sendTask();
            }
        };


        String am_pm;
        int hour = mainCalendar.get( Calendar.HOUR_OF_DAY );
        Log.i(TAG,"hour of day:"+hour);
        //process to start only between 8 AM and 6 PM
        if(hour >= 8 && hour <18){
            Log.i(TAG,"start collection");
            collectionStarted = true;
            startCollectingSendingData(doAsynchronousTask,sendDataTask);
        }else{
            Log.i(TAG,"schedule run");
            scheduleDailyRun(doAsynchronousTask,sendDataTask);
        }
    }

    private void scheduleDailyRun(TimerTask doAsynchronousTask,TimerTask sendDataTask){
        Log.i(TAG,"scheduleDailyRun");
        Timer timer = new Timer();
        Calendar date = Calendar.getInstance();
        date.set(
                Calendar.DAY_OF_WEEK,
                Calendar.SUNDAY
        );
        date.set(Calendar.HOUR, 8);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        timer.schedule(
                doAsynchronousTask,
                date.getTime(),
                1000 * 60 * 60 * 24 * 7
        );
        timer.schedule(
                sendDataTask,
                date.getTime(),
                1000 * 60 * 60 * 24 * 7
        );
    }

    private void sendTask(){
        Log.i(TAG,"sendTask");
        String result = "{\"phone\":\""+UserProfile.phoneNumber+
                        "\",\"email\":\""+UserProfile.emailAddress+
                        "\",\"data\":["+resultMaker.toString()+
                        "{"+DataClass.singleRunResult+"}]}";
        resultMaker=  new StringBuilder();
        Log.i(TAG,"result:"+result);
        try{
            RestClient.get().postCollectionData(result);
        }catch(Exception ex){
            Log.e(TAG,ex.getMessage());
        }

    }

    private void startCollectingSendingData(TimerTask doAsynchronousTask,TimerTask sendDataTask){
        Log.i(TAG,"startCollectingSendingData");
        timer.schedule(doAsynchronousTask, 0, 3000);
        timer.schedule(sendDataTask, 0, 3000*20*1);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"destroying the timer.");
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class CollectionTask {

        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        //get current date time with Date()
        Date time = new Date();


         protected void execute(){
             Log.i(TAG,"start execute collection service");
             GPSGatherData.getLocationData(context);
             StringBuilder collectBuilder = new StringBuilder();
             DataClass.singleRunResult=collectBuilder.append(" \"time\":")
             .append("\"")
             .append(timeFormat.format(time))
             .append("\"")
             .append(", \"gps\":")
             .append(DataClass.gps)
             .append(", \"acc\":")
             .append(DataClass.accelerometerData)
             .append(", \"orient\":")
             .append(DataClass.orientationData).toString() ;
         }
    }

}
