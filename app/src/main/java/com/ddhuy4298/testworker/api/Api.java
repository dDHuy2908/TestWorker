package com.ddhuy4298.testworker.api;

import com.ddhuy4298.testworker.models.Receiver;

import retrofit2.http.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {
    @Headers({
            "Content-type:application/json",
            "Authorization:key=AAAAU0PxunU:APA91bFbwKQ9qcV_tz31kXUyPdqEX_JOAuAD234VYDRjzw8kd6vLkV0mka5YYDmcmmWfYsKVOmcvywkl8hlfrfyKJIhkw_giZ7uTVxwyUCWaZVSL6LYZ9zmDdTPajp_9BTcoiNXeY5RB"
    })
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body Receiver body);
}
