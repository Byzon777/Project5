package com.example.project5

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class NasaApod(
    val title: String,
    val explanation: String,
    val url: String
)

interface NasaApiService {
    @GET("planetary/apod")
    fun getRandomApod(
        @Query("api_key") apiKey: String,
        @Query("count") count: Int = 1
    ): Call<List<NasaApod>>
}


class MainActivity : AppCompatActivity() {

    private val apiKey = "MRi45BpBaDIc5YEvSPd0EVBYaSan7QZ5s9pwGq8I"

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var imageView: ImageView
    private lateinit var btnFetch: Button

    private val apiService: NasaApiService by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        imageView = findViewById(R.id.imageView)
        btnFetch = findViewById(R.id.btnFetch)

        btnFetch.setOnClickListener {
            fetchNasaData()
        }
    }

    private fun fetchNasaData() {
        apiService.getRandomApod(apiKey).enqueue(object : Callback<List<NasaApod>> {
            override fun onResponse(call: Call<List<NasaApod>>, response: Response<List<NasaApod>>) {
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val data = response.body()!!.first()
                    tvTitle.text = data.title
                    tvDescription.text = data.explanation
                    Glide.with(this@MainActivity)
                        .load(data.url)
                        .into(imageView)
                }
            }

            override fun onFailure(call: Call<List<NasaApod>>, t: Throwable) {
                tvTitle.text = "Failed to load data"
                tvDescription.text = t.message
            }
        })
    }

}
