package mg.studio.weatherappdesign;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private String json = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnClick(null);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        String temp = savedInstanceState.getString("json");
        btnClick(null);
        if(!temp.isEmpty()){
            UpdateUI((new Gson()).fromJson(temp, Weather.class));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("json", json);

    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "https://api.openweathermap.org/data/2.5/forecast?id=1814905&APPID=2ed289adacf61cf4896b1cf3bdf92dca";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String temperature) {
            //Update the temperature displayed
            json = temperature;
            Gson gson = new Gson();
            final Weather weather = gson.fromJson(temperature, Weather.class);
            UpdateUI(weather);
            Toast.makeText(getBaseContext(),"Weather Updated" ,Toast.LENGTH_SHORT).show();
        }

    }
    private void UpdateUI(Weather weather){
/**
 * 日期 小时最接近的list的索引index(差半小时不到)
 */
        int index = StandUtils.getWeatherListIndex(weather);
        //location
        ((TextView)findViewById(R.id.tv_location)).setText(weather.city.getName());
        //temperature_of_the_day
        ((TextView) findViewById(R.id.temperature_of_the_day)).setText(StringCast(weather.list.get(index).getMain().getTemp() - 273.15));
        //date
        ((TextView)findViewById(R.id.tv_date)).setText(StandUtils.getDate());
        //Day 5天的信息
        ArrayList<String> arrayList = StandUtils.getWeekList();
        ((TextView)findViewById(R.id.today)).setText(arrayList.get(0));
        //改变图标
        ArrayList<Integer>  resourcesList = StandUtils.getResourceList(index, weather);
        ((ImageView)findViewById(R.id.img_weather_condition)).setImageResource(resourcesList.get(0));
        /**
         * 给forecast 组设置图标和日期
         *
         */
        for(int i = 0; i < 4 ; i++){
            ViewGroup child = (ViewGroup) ((ViewGroup)findViewById(R.id.layoutOfForecast)).getChildAt(i);
            ((ImageView)child.getChildAt(0)).setImageResource(resourcesList.get(i + 1));
            ((TextView)child.getChildAt(1)).setText(arrayList.get(i + 1));
        }


    }

    /**
     * double 小数前转换成string
     * @param temp
     * @return
     */
    private String StringCast(double temp){
        return Double.toString(temp).substring(0,  Double.toString(temp).lastIndexOf('.'));
    }
}
