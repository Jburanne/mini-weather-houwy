package cn.edu.pku.ss.houwy.miniweather;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import cn.edu.pku.ss.houwy.app.MyApplication;
import cn.edu.pku.ss.houwy.bean.City;
import cn.edu.pku.ss.houwy.app.MyApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.content.ContentValues.TAG;

public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;
    private ListView mListView;
    private TextView mTitle;
    private SearchView mSearchView;

    private ArrayAdapter mAdapter;

    private ArrayList<City> cityList;
    private ArrayList<String> data;

    private static MyApplication myApplication;

    String newCC = "";



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        initViews();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode","101160101");
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }

    private void initViews(){
        Intent intent = getIntent();//获取传来的intent对象
        String cityinfo = intent.getStringExtra("nowCity");//获取键值对的键名


        cityList = new ArrayList<City>(myApplication.getInstance().getCityList());//构建城市信息的一个列表
        data = new ArrayList<String>();//构建城市名的一个列表
        for(City s:cityList){
            data.add(s.getCity());//通过citylist获取数据
        }

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        mListView = (ListView)findViewById(R.id.list_city);
        mTitle = (TextView)findViewById(R.id.title_name);
        mSearchView = (SearchView)findViewById(R.id.search_view);
        mSearchView.setIconifiedByDefault(false);//设置左侧有放大镜，右侧无×
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }//开始搜索listener

            @Override
            public boolean onQueryTextChange(String newText) {//输入框内容变化listener
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        mTitle.setText("当前城市:"+cityinfo);
        Log.d(TAG,mListView.toString());
        mAdapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,data);
        mListView.setAdapter(mAdapter);//绑定
        mListView.setTextFilterEnabled(true);//开启过滤功能

        //listview点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //使用dialog 防止点击错误
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectCity.this);
                builder.setTitle("提醒");
                String nowProvince="";
                String nowCity = "";

                for(City s:cityList){
                    if(s.getCity() == mAdapter.getItem(position)){
                        nowProvince = s.getProvince();
                        nowCity = s.getCity();
                        newCC = s.getNumber();
                    }
                }
                builder.setMessage("你确定要将城市切换至"+nowProvince+"的"+nowCity+"吗？");//提示语句
                //点击提示框中的取消键
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        toast("取消");//直接消掉dialog
                    }
                });
                //点击提示框中的确定键
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(SelectCity.this,MainActivity.class).putExtra("cityCode",newCC);//将点击的城市代码传给主页面
                        setResult(10,i);
                        finish();

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



    }
}

