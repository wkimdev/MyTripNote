package com.wkimdev.mytripnote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.wkimdev.mytripnote.MainActivity_bakk;
import com.wkimdev.mytripnote.R;

import java.util.ArrayList;

/**
 * 각 나라별 날씨 자동 스와이프 처리를 위한 뷰페이저 어댑터
 */
public class WeatherViewPagerAdapter extends PagerAdapter  {

    private static final String TAG = "WeatherViewPagerAdapter";
    private Context context;

    // 날씨 정도 6개
    private ArrayList<String[]> weatherInfoArray;
    private MainActivity_bakk mainActivity = new MainActivity_bakk();


    // Context 를 전달받아 context 에 저장하는 생성자 추가
    public WeatherViewPagerAdapter(Context context, ArrayList<String[]> weatherInfoArray) {
        this.context = context;
        this.weatherInfoArray = weatherInfoArray;
    }


    // 사용 가능한 뷰의 개수를 return 한다
    @Override
    public int getCount() {
        return ( null != weatherInfoArray ? weatherInfoArray.size() : 0 );
    }

    // position 값을 받아 주어진 위치에 페이지를 생성한다
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // position 값을 받아 주어진 위치에 페이지를 생성한다
        View view = null;

        if(context != null) {
            // LayoutInflater 를 통해 "/res/layout/page.xml" 을 뷰로 생성.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_weather, container, false);

            // view에 데이터를 바인딩
            // view선언
            TextView tv_city = view.findViewById(R.id.tv_city);
            TextView tv_temp = view.findViewById(R.id.tv_temp);
            ImageView iv_weather_icon = view.findViewById(R.id.iv_weather_icon);
            TextView tv_temp_description = view.findViewById(R.id.tv_temp_description);
            TextView tv_temp_humidity = view.findViewById(R.id.tv_temp_humidity);
            TextView tv_temp_wind_speed = view.findViewById(R.id.tv_temp_wind_speed);
            TextView tv_temp_cloud = view.findViewById(R.id.tv_temp_cloud);

            String[] weatherInfo = weatherInfoArray.get(position);
            tv_city.setText(weatherInfo[2]);
            tv_temp.setText(weatherInfo[1] + "℃");
            tv_temp_description.setText(weatherInfo[0]);
            tv_temp_humidity.setText("습도: " + weatherInfo[3] + "%");
            tv_temp_wind_speed.setText("풍속: " + weatherInfo[4] + "m/s");
            tv_temp_cloud.setText("구름: " + weatherInfo[5]+ "%");
            iv_weather_icon.setImageBitmap(MainActivity_bakk.getStringToBitmap(weatherInfo[6]));// 아이콘
            iv_weather_icon.setBackgroundColor(context.getResources().getColor(R.color.white));


//            Bundle bun = new Bundle();
//            bun.putStringArray("weatherInfo", weatherInfoArray.get(position));
//
//            Message msg = mainActivity.weatherThreadHandler.obtainMessage();
//            msg.setData(bun);
//            mainActivity.weatherThreadHandler.sendMessage(msg);

        }

        // 뷰페이저에 추가
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // position 값을 받아 주어진 위치의 페이지를 삭제한다
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        // 페이지 뷰가 생성된 페이지의 object key 와 같은지 확인한다
        // 해당 object key 는 instantiateItem 메소드에서 리턴시킨 오브젝트이다
        // 즉, 페이지의 뷰가 생성된 뷰인지 아닌지를 확인하는 것
        return view == object;
    }

    // 외부 날씨 API 호출



}
