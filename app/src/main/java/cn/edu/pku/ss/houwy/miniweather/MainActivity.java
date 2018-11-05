package cn.edu.pku.ss.houwy.miniweather;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.LogRecord;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


import cn.edu.pku.ss.houwy.bean.TodayWeather;
import cn.edu.pku.ss.houwy.util.NetUtil;

public class MainActivity extends Activity implements View.OnClickListener{
    private  static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTv,climateTv,windTv,city_name_Tv;
    private ImageView weatherImg,pmImg;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };


    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality
        );
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature
        );
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
            Log.d("myWeather","网络ok");
            Toast.makeText(MainActivity.this,"网络Ok",Toast.LENGTH_LONG).show();
        }
        else{
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this,SelectCity.class);
            startActivityForResult(i,1);
        }

        if(view.getId() == R.id.title_update_btn){

            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
                Log.d("myWeather","网络Ok");
                queryWeatherCode(cityCode);
            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }

        }
        initView();
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){//获取从selectCity获取的数据，并更新当前页面
        if(requestCode == 1 && resultCode == 10){//该组合为selectCity的返回值
            String newCityCode = data.getStringExtra("cityCode");//获取附加消息
            Log.d("myWeather","选择的城市代码为"+newCityCode);
            //获取sharedPreferences对象
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            //获取editor对象
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
            editor.putString("main_city_code",newCityCode);//存储城市代码
            editor.commit();//提交修改

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
                Log.d("myWeather","网络Ok");
                queryWeatherCode(newCityCode);
            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }
        }
    }



    private void queryWeatherCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("myWeather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();//得到数据
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str = reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather",responseStr);
                    todayWeather = parseXML(responseStr);
                    if(todayWeather != null){//成功解析后
                        Log.d("myWeather",todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);//成功后数据传给UI线程
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();//关闭连接
                    }
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
    try{
        XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
        XmlPullParser xmlPullParser = fac.newPullParser();
        xmlPullParser.setInput(new StringReader(xmldata));
        int eventType = xmlPullParser.getEventType();
        Log.d("myWeather","parseXML");
        while(eventType != XmlPullParser.END_DOCUMENT){
            switch(eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if(xmlPullParser.getName().equals("resp")){
                       todayWeather = new TodayWeather();
                    }
                    if(todayWeather != null){
                        if(xmlPullParser.getName().equals("city")){
                            eventType = xmlPullParser.next();
                            todayWeather.setCity(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("updatetime")){
                            eventType = xmlPullParser.next();
                            todayWeather.setUpdatetime(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("shidu")){
                            eventType = xmlPullParser.next();
                            todayWeather.setShidu(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("wendu")){
                            eventType = xmlPullParser.next();
                            todayWeather.setWendu(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("pm25")){
                            eventType = xmlPullParser.next();
                            todayWeather.setPm25(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("quality")){
                            eventType = xmlPullParser.next();
                            todayWeather.setQuality(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0){
                            eventType = xmlPullParser.next();
                            todayWeather.setFengxiang(xmlPullParser.getText());
                            fengxiangCount++;
                        }else if(xmlPullParser.getName().equals("fengli") && fengliCount == 0){
                            eventType = xmlPullParser.next();
                            todayWeather.setFengli(xmlPullParser.getText());
                            fengliCount++;
                        }else if(xmlPullParser.getName().equals("date") && dateCount == 0){
                            eventType = xmlPullParser.next();
                            todayWeather.setDate(xmlPullParser.getText());
                            dateCount++;
                        }else if(xmlPullParser.getName().equals("high") && highCount == 0){
                            eventType = xmlPullParser.next();
                            todayWeather.setHigh(xmlPullParser.getText());
                            highCount++;
                        }else if(xmlPullParser.getName().equals("low") && lowCount == 0){
                            eventType = xmlPullParser.next();
                            todayWeather.setLow(xmlPullParser.getText());
                            lowCount++;
                        }else if(xmlPullParser.getName().equals("type") && typeCount == 0){
                            eventType = xmlPullParser.next();
                            todayWeather.setType(xmlPullParser.getText());
                            typeCount++;
                        }
                        break;
                    }

                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = xmlPullParser.next();
        }

    }catch (XmlPullParserException e){
        e.printStackTrace();

    }catch(IOException e){
        e.printStackTrace();

    }
    return todayWeather;
}

void setWeatherImg(String type){
        switch(type){
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨加雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            default:
                break;

        }
}

void updateTodayWeather(TodayWeather todayWeather){
        String low_t,high_t;
        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度"+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        low_t = todayWeather.getLow();
        high_t = todayWeather.getHigh();
        temperatureTv.setText(low_t.substring(low_t.lastIndexOf(" ")) + "~" + high_t.substring(high_t.lastIndexOf(" ")));
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力："+todayWeather.getFengli());

        setWeatherImg(todayWeather.getType());
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
}



}
