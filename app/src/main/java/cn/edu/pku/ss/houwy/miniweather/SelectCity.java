package cn.edu.pku.ss.houwy.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

    private ArrayList<City> cityList;
    private ArrayList<String> data;

    private static MyApplication myApplication;



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
        cityList = new ArrayList<City>(myApplication.getInstance().getCityList());
        data = new ArrayList<String>();
        for(City s:cityList){
            data.add(s.getCity());
        }

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        mListView = (ListView)findViewById(R.id.list_city);
        Log.d(TAG,mListView.toString());
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,data);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCity.this,"你点击了"+position,Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SelectCity.this,MainActivity.class).putExtra("cityCode",cityList.get(position).getNumber());
                setResult(10,i);
                finish();
            }
        });



    }
}

