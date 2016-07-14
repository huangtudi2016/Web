package com.huangzan.web.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huangzan.web.R;
import com.huangzan.web.utils.CollectionUtil;
import com.huangzan.web.utils.XmlUtil;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by huangzan on 16/7/13.
 */
public class WeatherActivity extends AppCompatActivity {
    private List<String> list_city,list_pro,list_data;
    private Spinner pro,city;
    private TextView show;
    private Context context = getBaseContext();
    private String cityName,proName,url_city,url_city_data;
    private LinearLayout llWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        llWeather = (LinearLayout) findViewById(R.id.ll_weather);
        pro = (Spinner)findViewById(R.id.pro);
        city = (Spinner)findViewById(R.id.city);
        show = (TextView)findViewById(R.id.show);
        //获取省份的地址
        String url_pro = "http://www.webxml.com.cn/webservices/weatherwebservice.asmx/getSupportProvince";
        //执行获取省份的异步操作
        new MyAsync().execute(url_pro);


    }

    //获取省份。   城市和天气的获取与解析都是一样的思路
    public class MyAsync extends AsyncTask<String,Void,List> {

        @Override
        protected List doInBackground(String... params) {
            //调用getXml（）方法，（该方法在本类底部位置）
            list_pro = getXml(params[0]);
            return list_pro;
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            if (list==null || list.size()==0){
                return;
            }
//            Toast.makeText(getBaseContext(),""+list.size(),Toast.LENGTH_SHORT).show();
            ArrayAdapter arrayAdapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_spinner_dropdown_item,list_pro);
            arrayAdapter.setDropDownViewResource(R.layout.item_spinner_weather);
            pro.setAdapter(arrayAdapter);
            pro.setDropDownVerticalOffset(llWeather.getHeight());
            pro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    proName = pro.getSelectedItem().toString().trim();
                    //Toast.makeText(getBaseContext(),proName,Toast.LENGTH_SHORT).show();
                    url_city = "http://www.webxml.com.cn/webservices/weatherwebservice.asmx/getSupportCity?byProvinceName="+proName;
                    new MyAsyncCity().execute(url_city);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }
    //获取城市
    public class MyAsyncCity extends AsyncTask<String,Void,List>{

        @Override
        protected List doInBackground(String... params) {
            list_city = getXml(params[0]);
            return list_city;
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            if (list==null || list.size()==0){
                return;
            }
            //Toast.makeText(getBaseContext(),""+list.size(),Toast.LENGTH_SHORT).show();
            ArrayAdapter arrayAdapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_spinner_dropdown_item,list_city);
            arrayAdapter.setDropDownViewResource(R.layout.item_spinner_weather);
            city.setAdapter(arrayAdapter);
            city.setDropDownVerticalOffset(llWeather.getHeight());
            arrayAdapter.notifyDataSetChanged();
            city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cityName = city.getSelectedItem().toString().trim();
                    cityName = cityName.split(" ")[0];
                    if (!"".equals(cityName.trim())){
                        url_city_data = "http://www.webxml.com.cn/webservices/weatherwebservice.asmx/getWeatherbyCityName?theCityName="+cityName;
                        Toast.makeText(getBaseContext(),cityName,Toast.LENGTH_SHORT).show();
                        new MyWeather().execute(url_city_data);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public class MyWeather extends AsyncTask<String,Void,List>{

        @Override
        protected List doInBackground(String... params) {
            list_data = getXml(params[0]);
            return list_data;
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            StringBuilder sb = new StringBuilder();
            if (list_data.size()!=0 && list_data!=null){
                for (int i=0;i<list_data.size();i++){
                    String str = list.get(i).toString();
                    sb.append(str+ System.getProperty("line.separator"));
                }
                show.setText(sb.toString());
            }


        }
    }
    //getXml方法，解析获得流，并调用XMLUtil类解析流
    public List getXml(String urlString){
        List list = null;
        InputStream is = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            if (httpURLConnection.getResponseCode()==200){
                //获取返回的数据流
                is = httpURLConnection.getInputStream();
                //传入Xml解析工具类进行解析
                XmlUtil xmlUtil = new XmlUtil(is);
                try {
                    list = xmlUtil.getAllElement();
                    list = CollectionUtil.duplicate(list);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(context,"网络异常", Toast.LENGTH_SHORT).show();
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is!=null){
                    is.close();
                }

                if (httpURLConnection!=null){
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return list;
    }
}
