package com.example.hoanglong.capstonefpt.api;

import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleUserInfo;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by hoanglong on 10/08/2016.
 */

public interface ServerAPI {
//    @GET("api/directions/json?key=AIzaSyAl_-UNqx3oY3vVPB21ieVHaGeenes2Hb8&transit_mode=bus&transit_routing_preference=less_walking")
//    Call<RouteList> getDistanceDuration(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);
//
//    @GET("api/geocode/json?sensor=true&key=AIzaSyAl_-UNqx3oY3vVPB21ieVHaGeenes2Hb8")
//    Call<PlaceDetail> getPlaceID(@Query("latlng") String latlng);

//    @GET("api/BusStops")
//    Call<List<BusStopDB>> getAllBusStop();
//
    @POST("getScheduleEmployeeInfo")
    Call<ScheduleUserInfo> getScheduleEmployeeInfo(@Body JsonObject email);

    @POST("getScheduleStudent")
    Call<ScheduleUserInfo> getScheduleStudent(@Body JsonObject email);
//
//    @POST("api/Users")
//    Call<UserAccount> loginViaEmail2(@Body JsonObject email);

}

