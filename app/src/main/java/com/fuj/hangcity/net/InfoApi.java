package com.fuj.hangcity.net;


import com.fuj.hangcity.model.info.InfoResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by fuj
 */
public interface InfoApi {
    @GET("index")
    Observable<InfoResult> getInfo(@Query("type") String type, @Query("key") String key);
}
