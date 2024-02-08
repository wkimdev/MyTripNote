package com.wkimdev.mytripnote.config;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.kakao.sdk.common.KakaoSdk;
import com.wkimdev.mytripnote.R;

/**
 * 컴포넌트들 사이에서 공동으로 멤버들을 사용할 수 있게 해주는 공유 클래스.
 * 어디서든 context를 통해 접근 가능.
 * 객체들이 공동으로 접근가능하게 만드려면 Application을 상속 받아야 하며,
 * 매니페스트에 생성한 Application class를 추가 해야 한다.
 */
public class GlobalApplication extends Application {

    private static final String TAG = "GlobalApplication";
    private static GlobalApplication instance;
    public static String WEATHER_API_KEY = "";
    public static String GOOGLE_API_KEY = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //Log.e(TAG, "onCreate: ENTER!! ");
        WEATHER_API_KEY = getResources().getString(R.string.WEATHER_API_KEY);
        GOOGLE_API_KEY = getResources().getString(R.string.GOOGLE_API_KEY);

        // 카카오 SDK 초기화
        KakaoSdk.init(this, getString(R.string.app_key));

        // Enable disk persistence
        // - 파이어베이스 realtime database 생성시 최초에 한번만 실행되도록 한다
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
