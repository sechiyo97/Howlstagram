package com.example.howlstagram.navigation.util

import com.example.howlstagram.navigation.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class FcmPush {

    var JSON = MediaType.parse("application/json; charset=utf-8")
    var url = "https://fcm.googleapis.com/fcm/send"
    var serverKey = "AAAApyEvXDU:APA91bHLoWf9rTlenjS_jdTuwF9OSVv8aTWN2JSwSc4eoiYoJJax_DNDv2aQ1HZXSn5r0BkcnSL0lBn9H30F7qS5vhpR-S5nkt7dHlRKFj96Xa0ybJcY33L2AYG_wGYFktf44RKzodW8"
    var gson : Gson? = null
    var okhttpClient : OkHttpClient? = null

    companion object{
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okhttpClient = OkHttpClient()
    }
    fun sendMessage(destinationUid : String, title : String, message : String){
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                var token = task?.result?.get("pushToken").toString()

                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification.title = title
                pushDTO.notification.body = message

                var body = RequestBody.create(JSON, gson?.toJson(pushDTO))
                var request = Request.Builder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "key="+serverKey)
                        .url(url)
                        .post(body)
                        .build()

                okhttpClient?.newCall(request)?.enqueue(object : Callback{
                    override fun onFailure(call: Call?, e: IOException?) {
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        println(response?.body()?.string())
                    }

                })
            }
        }
    }
}