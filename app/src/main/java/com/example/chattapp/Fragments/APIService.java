package com.example.chattapp.Fragments;

import com.example.chattapp.Notifications.MyResponse;
import com.example.chattapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService

{

    @Headers({

            "Content-Type:application/json",

            "Authorization:key=AAAATiFYIsk:APA91bGl6i6RoI5lXSnJ9RZfooHbG9znqNRlGqVVB1igYpoh5Rk15j1jBysGwJyXzTTP6dzeI0V3BYlYFRE5gGzloXiwte77SDbXSyNm9gW4NmCJVkDA0Pcrp8T7gUdgo48N6IbN7dbJ"


    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);


}
