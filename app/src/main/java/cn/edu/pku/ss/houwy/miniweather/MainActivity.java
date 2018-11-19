package cn.edu.pku.ss.houwy.miniweather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


import cn.edu.pku.ss.houwy.app.MyApplication;
import cn.edu.pku.ss.houwy.bean.City;
import cn.edu.pku.ss.houwy.bean.TodayWeather;
import cn.edu.pku.ss.houwy.util.NetUtil;

public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private  static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTv,climateTv,windTv,city_name_Tv;
    private ImageView weatherImg,pmImg;
    private ProgressBar mprobar;
    private ImageView mTitleLocation;

    public LocationClient mLocationClient;
    private MyLocationListener myListener = new MyLocationListener();

    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;
    //为引导页增加小圆点
    private ImageView[] dots;
    private int[] ids = {R.id.img1,R.id.img2};
    private TextView week_today, temperature, climate, wind,
            week_today1, temperature1, climate1, wind1,
            week_today2, temperature2, climate2, wind2,
            week_today3, temperature3, climate3, wind3,
            week_today4, temperature4, climate4, wind4,
            week_today5, temperature5, climate5, wind5;

    private ImageView future_img,future_img1,future_img2,future_img3,future_img4,future_img5;




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
        //
        week_today = views.get(0).findViewById(R.id.future_day1);
        temperature = views.get(0).findViewById(R.id.future_temperature1);
        climate = views.get(0).findViewById(R.id.future_climate1);
        wind = views.get(0).findViewById(R.id.future_wind1);
        future_img = views.get(0).findViewById(R.id.future_img1);
        //
        week_today1 = views.get(0).findViewById(R.id.future_day2);
        temperature1 = views.get(0).findViewById(R.id.future_temperature2);
        climate1 = views.get(0).findViewById(R.id.future_climate2);
        wind1 = views.get(0).findViewById(R.id.future_wind2);
        future_img1 = views.get(0).findViewById(R.id.future_img2);
        //
        week_today2 = views.get(0).findViewById(R.id.future_day3);
        temperature2 = views.get(0).findViewById(R.id.future_temperature3);
        climate2 = views.get(0).findViewById(R.id.future_climate3);
        wind2 = views.get(0).findViewById(R.id.future_wind3);
        future_img2 = views.get(0).findViewById(R.id.future_img3);
        //
        week_today3 = views.get(1).findViewById(R.id.future_day1);
        temperature3 = views.get(1).findViewById(R.id.future_temperature1);
        climate3 = views.get(1).findViewById(R.id.future_climate1);
        wind3 = views.get(1).findViewById(R.id.future_wind1);
        future_img3 = views.get(1).findViewById(R.id.future_img1);
        //
        week_today4 = views.get(1).findViewById(R.id.future_day2);
        temperature4 = views.get(1).findViewById(R.id.future_temperature2);
        climate4 = views.get(1).findViewById(R.id.future_climate2);
        wind4 = views.get(1).findViewById(R.id.future_wind2);
        future_img4 = views.get(1).findViewById(R.id.future_img2);
        //
        week_today5 = views.get(1).findViewById(R.id.future_day3);
        temperature5 = views.get(1).findViewById(R.id.future_temperature3);
        climate5 = views.get(1).findViewById(R.id.future_climate3);
        wind5 = views.get(1).findViewById(R.id.future_wind3);
        future_img5 = views.get(1).findViewById(R.id.future_img3);


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

        //
        week_today.setText("N/A");
        temperature.setText("N/A");
        climate.setText("N/A");
        wind.setText("N/A");
        //
        week_today1.setText("N/A");
        temperature1.setText("N/A");
        climate1.setText("N/A");
        wind1.setText("N/A");
        //
        week_today2.setText("N/A");
        temperature2.setText("N/A");
        climate2.setText("N/A");
        wind2.setText("N/A");
        //
        week_today3.setText("N/A");
        temperature3.setText("N/A");
        climate3.setText("N/A");
        wind3.setText("N/A");
        //
        week_today4.setText("N/A");
        temperature4.setText("N/A");
        climate4.setText("N/A");
        wind4.setText("N/A");
        //
        week_today5.setText("N/A");
        temperature5.setText("N/A");
        climate5.setText("N/A");
        wind5.setText("N/A");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        mprobar = (ProgressBar) findViewById(R.id.title_progressbar);

        //创建LocationClient实例
        mLocationClient = new LocationClient(getApplicationContext());
        //注册定位监听器，当获取到位置信息时，会回调这个监听器
        mLocationClient.registerLocationListener(myListener);

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

        mTitleLocation = (ImageView) findViewById(R.id.title_location);
        initLocation();

        //初始化两个滑动页面
        initViews();
        //初始化小圆点
        initDots();

        //初始化界面
        initView();
    }

    void initDots(){
        dots = new ImageView[views.size()];
        for(int i = 0;i < views.size();i++){
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }

    private void initViews(){
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.future_weather_1,null));
        views.add(inflater.inflate(R.layout.future_weather_2,null));
        vpAdapter = new ViewPagerAdapter(views,this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);

    }

    public class ViewPagerAdapter extends PagerAdapter{

        private Context context;
        private List<View> views;

        //重写构造方法
        public ViewPagerAdapter(List<View> views, Context context){
            this.views = views;
            this.context = context;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(views.get(position));
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this,SelectCity.class);
            startActivityForResult(i,1);
        }

        if(view.getId() == R.id.title_update_btn){
//            mUpdateBtn.setVisibility(View.GONE);
//            mprobar.setVisibility(View.VISIBLE);

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

        if(view.getId() == R.id.title_location){


            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
                Log.d("myWeather","网络ok");
                Toast.makeText(MainActivity.this,"网络Ok",Toast.LENGTH_LONG).show();
                requestLocation();
                queryWeatherCode(myListener.now_cityCode);
            }
            else{
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

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span = 1000;
        option.setScanSpan(0);
        //option.setScanSpan(5000); //设置每5秒更新当前位置
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop(); //活动被销毁时停止定位
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    //View进行页面切换
    @Override
    public void onPageSelected(int i) { //i:新的页面位置
        for(int a = 0;a < ids.length;a++){
            if(a == i){
                dots[a].setImageResource(R.drawable.page_indicator_1);
            }else{
                dots[a].setImageResource(R.drawable.page_indicator_2);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class MyLocationListener implements BDLocationListener{
        public String now_city;
        public String now_cityCode;
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            String city = bdLocation.getCity();
            now_city = city.replace("市","");

            List<City> mCityList;
            MyApplication myApplication;
            myApplication = MyApplication.getInstance();
            mCityList = myApplication.getCityList();

            for (City c:mCityList){
                if(c.getCity().equals(now_city)){
                    now_cityCode = c.getNumber();
                    Log.d("location_code",now_cityCode);
                }
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
                        }else if(xmlPullParser.getName().equals("fengli") && fengliCount == 1){
                            eventType = xmlPullParser.next();
                            todayWeather.setWind(xmlPullParser.getText());
                            fengliCount++;
                        }else if(xmlPullParser.getName().equals("fengli") && fengliCount == 2){
                            eventType = xmlPullParser.next();
                            todayWeather.setWind1(xmlPullParser.getText());
                            fengliCount++;
                        }else if(xmlPullParser.getName().equals("fengli") && fengliCount == 3){
                            eventType = xmlPullParser.next();
                            todayWeather.setWind2(xmlPullParser.getText());
                            fengliCount++;
                        }else if(xmlPullParser.getName().equals("fengli") && fengliCount == 4){
                            eventType = xmlPullParser.next();
                            todayWeather.setWind3(xmlPullParser.getText());
                            fengliCount++;
                        }else if(xmlPullParser.getName().equals("fengli") && fengliCount == 5){
                            eventType = xmlPullParser.next();
                            todayWeather.setWind4(xmlPullParser.getText());
                            fengliCount++;
                        }else if(xmlPullParser.getName().equals("fengli") && fengliCount == 6){
                            eventType = xmlPullParser.next();
                            todayWeather.setWind5(xmlPullParser.getText());
                            fengliCount++;
                        }else if(xmlPullParser.getName().equals("date") && dateCount == 0){
                            eventType = xmlPullParser.next();
                            todayWeather.setDate(xmlPullParser.getText());
                            dateCount++;
                        }else if(xmlPullParser.getName().equals("date") && dateCount == 1){
                            eventType = xmlPullParser.next();
                            todayWeather.setWeek_today(xmlPullParser.getText());
                            dateCount++;
                        }else if(xmlPullParser.getName().equals("date") && dateCount == 2){
                            eventType = xmlPullParser.next();
                            todayWeather.setWeek_today1(xmlPullParser.getText());
                            dateCount++;
                        }else if(xmlPullParser.getName().equals("date") && dateCount == 3){
                            eventType = xmlPullParser.next();
                            todayWeather.setWeek_today2(xmlPullParser.getText());
                            dateCount++;
                        }else if(xmlPullParser.getName().equals("date") && dateCount == 4){
                            eventType = xmlPullParser.next();
                            todayWeather.setWeek_today3(xmlPullParser.getText());
                            dateCount++;
                        }else if(xmlPullParser.getName().equals("date") && dateCount == 5){
                            eventType = xmlPullParser.next();
                            todayWeather.setWeek_today4(xmlPullParser.getText());
                            dateCount++;
                        }else if(xmlPullParser.getName().equals("date") && dateCount == 6){
                            eventType = xmlPullParser.next();
                            todayWeather.setWeek_today5(xmlPullParser.getText());
                            dateCount++;
                        }else if(xmlPullParser.getName().equals("high") && highCount == 0){
                            eventType = xmlPullParser.next();
                            todayWeather.setHigh(xmlPullParser.getText());
                            highCount++;
                        }else if(xmlPullParser.getName().equals("high") && highCount == 1){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureH(xmlPullParser.getText());
                            highCount++;
                        }else if(xmlPullParser.getName().equals("high") && highCount == 2){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureH1(xmlPullParser.getText());
                            highCount++;
                        }else if(xmlPullParser.getName().equals("high") && highCount == 3){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureH2(xmlPullParser.getText());
                            highCount++;
                        }else if(xmlPullParser.getName().equals("high") && highCount == 4){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureH3(xmlPullParser.getText());
                            highCount++;
                        }else if(xmlPullParser.getName().equals("high") && highCount == 5){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureH4(xmlPullParser.getText());
                            highCount++;
                        }else if(xmlPullParser.getName().equals("high") && highCount == 6){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureH5(xmlPullParser.getText());
                            highCount++;
                        }else if(xmlPullParser.getName().equals("low") && lowCount == 0){
                            eventType = xmlPullParser.next();
                            todayWeather.setLow(xmlPullParser.getText());
                            lowCount++;
                        }else if(xmlPullParser.getName().equals("low") && lowCount == 1){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureL(xmlPullParser.getText());
                            lowCount++;
                        }else if(xmlPullParser.getName().equals("low") && lowCount == 2){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureL1(xmlPullParser.getText());
                            lowCount++;
                        }else if(xmlPullParser.getName().equals("low") && lowCount == 3){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureL2(xmlPullParser.getText());
                            lowCount++;
                        }else if(xmlPullParser.getName().equals("low") && lowCount == 4){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureL3(xmlPullParser.getText());
                            lowCount++;
                        }else if(xmlPullParser.getName().equals("low") && lowCount == 5){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureL4(xmlPullParser.getText());
                            lowCount++;
                        }else if(xmlPullParser.getName().equals("low") && lowCount == 6){
                            eventType = xmlPullParser.next();
                            todayWeather.setTemperatureL5(xmlPullParser.getText());
                            lowCount++;
                        }else if(xmlPullParser.getName().equals("type") && typeCount == 0){
                            eventType = xmlPullParser.next();
                            todayWeather.setType(xmlPullParser.getText());
                            typeCount++;
                        }else if(xmlPullParser.getName().equals("type") && typeCount == 1){
                            eventType = xmlPullParser.next();
                            todayWeather.setClimate(xmlPullParser.getText());
                            typeCount++;
                        }else if(xmlPullParser.getName().equals("type") && typeCount == 2){
                            eventType = xmlPullParser.next();
                            todayWeather.setClimate1(xmlPullParser.getText());
                            typeCount++;
                        }else if(xmlPullParser.getName().equals("type") && typeCount == 3){
                            eventType = xmlPullParser.next();
                            todayWeather.setClimate2(xmlPullParser.getText());
                            typeCount++;
                        }else if(xmlPullParser.getName().equals("type") && typeCount == 4){
                            eventType = xmlPullParser.next();
                            todayWeather.setClimate3(xmlPullParser.getText());
                            typeCount++;
                        }else if(xmlPullParser.getName().equals("type") && typeCount == 5){
                            eventType = xmlPullParser.next();
                            todayWeather.setClimate4(xmlPullParser.getText());
                            typeCount++;
                        }else if(xmlPullParser.getName().equals("type") && typeCount == 6){
                            eventType = xmlPullParser.next();
                            todayWeather.setClimate5(xmlPullParser.getText());
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

void setWeatherImg(String type,ImageView imgView){
        switch(type){
            case "暴雪":
                imgView.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "暴雨":
                imgView.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                imgView.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                imgView.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                imgView.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                imgView.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                imgView.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                imgView.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "沙尘暴":
                imgView.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                imgView.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                imgView.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                imgView.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                imgView.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                imgView.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨加雪":
                imgView.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                imgView.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                imgView.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                imgView.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                imgView.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            case "晴":
                imgView.setImageResource(R.drawable.biz_plugin_weather_qing);
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
        setWeatherImg(todayWeather.getType(),weatherImg);

        //future_day1
        week_today.setText(todayWeather.getWeek_today());
        climate.setText(todayWeather.getClimate());
        wind.setText(todayWeather.getWind());
        low_t = todayWeather.getTemperatureL();
        high_t = todayWeather.getTemperatureH();
        temperature.setText(low_t.substring(low_t.lastIndexOf(" ")) + "~" + high_t.substring(high_t.lastIndexOf(" ")));
        setWeatherImg(todayWeather.getClimate(),future_img);


        //future_day2
    week_today1.setText(todayWeather.getWeek_today1());
    climate1.setText(todayWeather.getClimate1());
    wind1.setText(todayWeather.getWind1());
    low_t = todayWeather.getTemperatureL1();
    high_t = todayWeather.getTemperatureH1();
    temperature1.setText(low_t.substring(low_t.lastIndexOf(" ")) + "~" + high_t.substring(high_t.lastIndexOf(" ")));
    setWeatherImg(todayWeather.getClimate1(),future_img1);
    //future_day3
    week_today2.setText(todayWeather.getWeek_today2());
    climate2.setText(todayWeather.getClimate2());
    wind2.setText(todayWeather.getWind2());
    low_t = todayWeather.getTemperatureL2();
    high_t = todayWeather.getTemperatureH2();
    temperature2.setText(low_t.substring(low_t.lastIndexOf(" ")) + "~" + high_t.substring(high_t.lastIndexOf(" ")));
    setWeatherImg(todayWeather.getClimate2(),future_img2);

    //future_day4
    week_today3.setText(todayWeather.getWeek_today3());
    climate3.setText(todayWeather.getClimate3());
    wind3.setText(todayWeather.getWind3());
    low_t = todayWeather.getTemperatureL3();
    high_t = todayWeather.getTemperatureH3();
    temperature3.setText(low_t.substring(low_t.lastIndexOf(" ")) + "~" + high_t.substring(high_t.lastIndexOf(" ")));
    setWeatherImg(todayWeather.getClimate3(),future_img3);

//    //future_day5
//    week_today4.setText(todayWeather.getWeek_today4());
//    climate4.setText(todayWeather.getClimate4());
//    wind4.setText(todayWeather.getWind4());
//    low_t = todayWeather.getTemperatureL4();
//    high_t = todayWeather.getTemperatureH4();
//    temperature4.setText(low_t.substring(low_t.lastIndexOf(" ")) + "~" + high_t.substring(high_t.lastIndexOf(" ")));
//    setWeatherImg(todayWeather.getClimate()4,future_img4);
//
//    //future_day6
//    week_today5.setText(todayWeather.getWeek_today5());
//    climate5.setText(todayWeather.getClimate5());
//    wind5.setText(todayWeather.getWind5());
//    low_t = todayWeather.getTemperatureL5();
//    high_t = todayWeather.getTemperatureH5();
//    temperature5.setText(low_t.substring(low_t.lastIndexOf(" ")) + "~" + high_t.substring(high_t.lastIndexOf(" ")));
//    setWeatherImg(todayWeather.getClimate5(),future_img5);
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
}
}
