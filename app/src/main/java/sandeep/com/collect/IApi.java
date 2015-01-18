package sandeep.com.collect;

import com.squareup.okhttp.Response;


import org.json.JSONArray;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;


/**
 * Created by sandeepshabd on 1/17/15.
 */
public interface IApi {
    @POST("/")
    Response postCollectionData(@Body JSONArray result); //Sync call - fire and forget
   // Response postCollectionData(@Body String result,  BackendResponse<Response> callback); //Async
}
