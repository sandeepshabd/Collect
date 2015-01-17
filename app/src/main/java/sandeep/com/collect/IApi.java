package sandeep.com.collect;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;


/**
 * Created by sandeepshabd on 1/17/15.
 */
public interface IApi {
    @POST("/collection")
    void postCollectionData(@Body String result); //Sync call - fire and forget
   // void postData(@Body String result,  Callback<JSONObject> callback); //Async
}
