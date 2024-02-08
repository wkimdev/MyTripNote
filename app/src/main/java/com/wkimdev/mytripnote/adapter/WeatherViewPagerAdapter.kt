package com.wkimdev.mytripnote.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import com.wkimdev.mytripnote.MainActivity
import com.wkimdev.mytripnote.R

/**
 * 각 나라별 날씨 자동 스와이프 처리를 위한 뷰페이저 어댑터
 */
class WeatherViewPagerAdapter     // Context 를 전달받아 context 에 저장하는 생성자 추가
    (
    private val context: Context?, // 날씨 정도 6개
    private val weatherInfoArray: ArrayList<Array<String?>>?
) : PagerAdapter() {
    private val mainActivity = MainActivity()

    // 사용 가능한 뷰의 개수를 return 한다
    override fun getCount(): Int {
        return weatherInfoArray?.size ?: 0
    }

    // position 값을 받아 주어진 위치에 페이지를 생성한다
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // position 값을 받아 주어진 위치에 페이지를 생성한다
        var view: View? = null
        if (context != null) {
            // LayoutInflater 를 통해 "/res/layout/page.xml" 을 뷰로 생성.
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_weather, container, false)

            // view에 데이터를 바인딩
            // view선언
            val tv_city = view.findViewById<TextView>(R.id.tv_city)
            val tv_temp = view.findViewById<TextView>(R.id.tv_temp)
            val iv_weather_icon = view.findViewById<ImageView>(R.id.iv_weather_icon)
            val tv_temp_description = view.findViewById<TextView>(R.id.tv_temp_description)
            val tv_temp_humidity = view.findViewById<TextView>(R.id.tv_temp_humidity)
            val tv_temp_wind_speed = view.findViewById<TextView>(R.id.tv_temp_wind_speed)
            val tv_temp_cloud = view.findViewById<TextView>(R.id.tv_temp_cloud)
            val weatherInfo = weatherInfoArray!![position]
            tv_city.text = weatherInfo[2]
            tv_temp.text = weatherInfo[1] + "℃"
            tv_temp_description.text = weatherInfo[0]
            tv_temp_humidity.text = "습도: " + weatherInfo[3] + "%"
            tv_temp_wind_speed.text = "풍속: " + weatherInfo[4] + "m/s"
            tv_temp_cloud.text = "구름: " + weatherInfo[5] + "%"
            iv_weather_icon.setImageBitmap(MainActivity.Companion.getStringToBitmap(weatherInfo[6])) // 아이콘
            iv_weather_icon.setBackgroundColor(context.resources.getColor(R.color.white))


//            Bundle bun = new Bundle();
//            bun.putStringArray("weatherInfo", weatherInfoArray.get(position));
//
//            Message msg = mainActivity.weatherThreadHandler.obtainMessage();
//            msg.setData(bun);
//            mainActivity.weatherThreadHandler.sendMessage(msg);
        }

        // 뷰페이저에 추가
        container.addView(view)
        return view!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // position 값을 받아 주어진 위치의 페이지를 삭제한다
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        // 페이지 뷰가 생성된 페이지의 object key 와 같은지 확인한다
        // 해당 object key 는 instantiateItem 메소드에서 리턴시킨 오브젝트이다
        // 즉, 페이지의 뷰가 생성된 뷰인지 아닌지를 확인하는 것
        return view === `object`
    } // 외부 날씨 API 호출

    companion object {
        private const val TAG = "WeatherViewPagerAdapter"
    }
}