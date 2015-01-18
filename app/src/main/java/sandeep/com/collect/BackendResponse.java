package sandeep.com.collect;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.RetrofitError;

/**
 * Created by sandeepshabd on 1/18/15.
 */
public class BackendResponse<Response> implements Callback{

    private final String TAG = "BackendResponse";

    public void onFailure(Request request, IOException e){
        Log.e(TAG,"request:"+request.toString());
    }

    public void onResponse(com.squareup.okhttp.Response response) {
        Log.i(TAG,"request:"+response.toString());
    }
}
