import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class FCMHelper {

    fun sendNotification(tokens: List<String>, title: String, message: String) {
        val serverKey = "AAAANcIN3Ys:APA91bGbYqyVLk6W8e3Be2QghV59KYHF2_ksATesoMAUme16Fcrv5-ZbHxMTpCw4QDP3Z3IOc7COV_JAGAxzAdwjJLyc4QGfeezVBfSOn6Vb55M31rPX9n4MaGf9dlnK6sVvUkKH89eV" // The Server key from firebase
        val url = "https://fcm.googleapis.com/fcm/send"

        val client = OkHttpClient()

        val json = JSONObject().apply {
            put("registration_ids", JSONArray(tokens))
            put("notification", JSONObject().apply {
                put("title", title)
                put("body", message)
            })
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "key=$serverKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                // Handle the response here
            }

            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                // Handle the failure here
            }
        })
    }
}
