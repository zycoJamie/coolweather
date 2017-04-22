package com.android.zycojamie.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zycojamie.coolweather.db.city;
import com.android.zycojamie.coolweather.db.county;
import com.android.zycojamie.coolweather.db.province;
import com.android.zycojamie.coolweather.util.HttpUtil;
import com.android.zycojamie.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zckya on 2017/4/19.
 */

public class ChooseAreaFragment extends Fragment{
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();
    private List<province> provinceList=new ArrayList<>();
    private List<city> cityList=new ArrayList<>();
    private List<county> countyList=new ArrayList<>();
    private province selectedProvince;
    private city selectedCity;
    private int currentLevel;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=(TextView)view.findViewById(R.id.title_text);
        backButton=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?>parent,View view,int position,long id){
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    String weatherId=countyList.get(position).getWeatherId();
                    Intent intent=new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }

            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }else if(currentLevel==LEVEL_COUNTY){
                    queryCities();
                }
            }
        });
        queryProvinces();
    }
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for(province province1:provinceList){
                dataList.add(province1.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else{
            String address="http://guolin.tech/api/china";
            queryFromService(address,"province");
        }
    }
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceCode=?",String.valueOf(selectedProvince.getProvinceCode())).find(city.class);
        if(cityList.size()>0){
            dataList.clear();
            for(city city1:cityList){
                dataList.add(city1.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            String address="http://guolin.tech/api/china"+"/"+selectedProvince.getProvinceCode();
            queryFromService(address,"city");
        }
    }
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityId=?",String.valueOf(selectedCity.getCityCode())).find(county.class);
        if(countyList.size()>0){
            dataList.clear();
            for(county county1:countyList){
                dataList.add(county1.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            String address="http://guolin.tech/api/china"+"/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            queryFromService(address,"county");
        }
    }
    private void queryFromService(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address,new okhttp3.Callback(){
            public void onResponse(Call call, Response response)throws IOException{
                String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectedProvince.getProvinceCode());
                }else if("county".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedCity.getCityCode());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable(){
                        public void run(){
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }else{
                    getActivity().runOnUiThread(new Runnable(){
                        public void run(){
                            closeProgressDialog();
                            Toast.makeText(getActivity(),"没有数据",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            public void onFailure(Call call,IOException e){
                getActivity().runOnUiThread(new Runnable(){
                    public void run(){
                        closeProgressDialog();
                        Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}
