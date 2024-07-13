 package com.example.weatherkt

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherkt.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


 class MainActivity : AppCompatActivity() {
     private val binding:ActivityMainBinding by lazy {
         ActivityMainBinding.inflate(layoutInflater)
 }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root) //R.layout.activity_main
        fetchWeatherData("Mumbai")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        SearchCity()
    }

     private fun SearchCity() {
         val searchView = binding.searchView
         searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
             android.widget.SearchView.OnQueryTextListener {
             override fun onQueryTextSubmit(query: String?): Boolean {
                 if (query != null) {
                     fetchWeatherData(query)
                 }
                 return true
             }

             override fun onQueryTextChange(newText: String?): Boolean {
                 // Handle text change if needed
                 return false
             }
         })
     }


     private fun fetchWeatherData(cityName :String) {
         val retrofit = Retrofit.Builder()
             .addConverterFactory(GsonConverterFactory.create())
             .baseUrl("https://api.openweathermap.org/data/2.5/")
             .build().create(ApiInterface::class.java)

         val response = retrofit.getWeatherData(cityName,"4f46ee2e089d66b9fdc009856b5dade2","metric")
         response.enqueue(object : Callback<WeatherApp> {

             override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                 val responseBody = response.body()
                 if(response.isSuccessful && responseBody !=null){
                     val temperature = responseBody.main.temp.toString()
                     val humidity =  responseBody.main.humidity.toString()
                     val wspeed = responseBody.wind.speed
                     val sunrise = responseBody.sys.sunrise.toLong()
                     val sunset = responseBody.sys.sunset.toLong()
                     val seaLevel = responseBody.visibility
                     val pressure = responseBody.main.pressure
                     val condition  = responseBody.weather.firstOrNull()?.main?:"Unknown"

                     binding.humidity.text = "$humidity%"
                     binding.sunrise.text = "${time(sunrise)}"
                     binding.sunset.text = "${time(sunset)}"
                     binding.pressure.text = "$pressure mbar"
                     binding.windspeed.text = "$wspeed m/s"
                     binding.textView2.text = "$temperature\u00B0C"
                     binding.condition.text = "$condition"
                     binding.day.text = dayName(System.currentTimeMillis())
                     binding.date.text = date()
                     binding.textView3.text = "$cityName"

                     changeImage(condition)
                 }
             }

             override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                 TODO("Not yet Implimented")
             }
         })

     }

     private fun changeImage(condition: String) {
         when(condition){
             "Sunny","Clear Sky","Clear" ->{
                 binding.root.setBackgroundResource(R.drawable.sunshine)
                 binding.lottieAnimationView.setAnimation(R.raw.sunny)
             }

             "Partly Clouds","Clouds","Mist","Haze","Foggy","Overcast" ->{
                 binding.root.setBackgroundResource(R.drawable.cloudy)
                 binding.lottieAnimationView.setAnimation(R.raw.cloudy)
             }

             "Thunder" ->{
                 binding.root.setBackgroundResource(R.drawable.thunr)
                 binding.lottieAnimationView.setAnimation(R.raw.thunder)
             }
             "Moderate Rain","Light Rain","Drizzle","Showers","Heavy Rain" ->{
                 binding.root.setBackgroundResource(R.drawable.rainwea)
                 binding.lottieAnimationView.setAnimation(R.raw.rain)
             }
             "Moderate Snow","Light Snow","Heavy Snow","Blizzard" ->{
                 binding.root.setBackgroundResource(R.drawable.snowy)
                 binding.lottieAnimationView.setAnimation(R.raw.snow)
             }
         }
     }
     private fun time(timestamp: Long): String{
         val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
         return sdf.format(Date(timestamp*1000))
     }

     private fun date(): String{
         val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
         return sdf.format(Date())
     }
    private fun dayName(timestamp:Long): String{
         val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
         return sdf.format(Date())
     }
}

