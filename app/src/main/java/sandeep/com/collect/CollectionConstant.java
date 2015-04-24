package sandeep.com.collect;

/**
 * Created by sandeepshabd on 1/19/15.
 */
public class CollectionConstant {

    public final static String URL ="http://ec2-54-69-41-196.us-west-2.compute.amazonaws.com:32123";

    public final static long COLLECTION_TIME_MS = 4000; // 4 seconds - each sample time.
    public final static long SEND_TIME_MS = 120000; // 2 minute - upload time
    public final static int MIN_COLLECTION_SPEED = 5; //if speed remains less then this speed, consider it as zero.

    public final static int START_MORNING_HOUR= 6; //6 AM
    public final static int START_MORNING_MINUTE= 00; //8 AM
    public final static int START_MORNING_SECONDS= 0; //8 AM
    public final static int START_MORNING_MILLIS= 0; //8 AM

    public final static int END_EVENING_HOUR= 19; // 7 PM
    public final static int END_EVENING_MINUTE= 00;
    public final static long SCHEDULEDING_TIME_IN_MS=1000 * 60 * 60 * 24;

    public final static String ProfileFile = "profile";
    //public static int TEST_TIME_END= 00;
}
