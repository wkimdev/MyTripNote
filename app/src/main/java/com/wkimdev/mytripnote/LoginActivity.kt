package com.wkimdev.mytripnote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.wkimdev.mytripnote.config.PreferenceManager


/**
 * 앱 시작전 로그인 화면
 * 로그아웃 이후 다시 돌아오는 화면
 */
class LoginActivity : AppCompatActivity() {
    // 구글 로그인 버튼
    private var btn_google: SignInButton? = null

    // 카카오 로그인 버튼
    private var btn_kakao: ImageView? = null

    // 구글 API 클라이언트 객체
    private var mGoogleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btn_google = findViewById(R.id.btn_google)
        btn_kakao = findViewById(R.id.btn_kakao)

        // 구글 로그인을 위한 사용자의 아이디, 이메일 주소, 기본 정보를 요청하기 위한 설정
        // ID와 기본 프로필은 DEFAULT_SIGN_IN에 포함되어 있음
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // GoogleSignInOptions에 지정된 옵션으로, GoogleSignInClient 객체 구성
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // 기존 로그인 사용자 확인 코드
        val account = GoogleSignIn.getLastSignedInAccount(this)


        // 구글 로그인 시도
        /*textView ?: return
        textView.text = "$text$suffix"*/
        //val nicknameTextView: TextView = tv_nickName ?: return // if null, return early

        val googleBtn: SignInButton = btn_google ?: return
        googleBtn.setOnClickListener(View.OnClickListener { signIn() })

        // 카카오 로그인 시도
        val kakaoBtn: ImageView = btn_kakao ?: return
        kakaoBtn.setOnClickListener(View.OnClickListener { // 카카오 로그인
            UserApiClient.instance
                .loginWithKakaoAccount(this@LoginActivity) { oAuthToken: OAuthToken?, error: Throwable? ->
                    if (error != null) {
                        Log.e(TAG, "로그인 실패", error)

                    } else if (oAuthToken != null) {
                        PreferenceManager.setBoolean(this@LoginActivity, "isLogin", true)

                        //사용자 정보 요청
                        // 사용자 정보 요청 (기본)
                        UserApiClient.instance.me { user, error ->
                            if (error != null) {
                                Log.e(TAG, "사용자 정보 요청 실패", error)
                            }
                            else if (user != null) {
                                Log.i(TAG, "사용자 정보 요청 성공" +
                                        "\n회원번호: ${user.id}" +
                                        "\n이메일: ${user.kakaoAccount?.email}" +
                                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")

                                val intent =
                                    Intent(this@LoginActivity, MainActivity_bakk::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                intent.putExtra("kakaoNickName", user.kakaoAccount?.profile?.nickname)
                                intent.putExtra(
                                    "kakaoPhoto",
                                    user.kakaoAccount?.profile?.thumbnailImageUrl
                                )
                                intent.putExtra("kakaoEmail", user.kakaoAccount?.email)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    null
                }
        })
    }

    // 구글 로그인 처리 메소드
    private fun signIn() {
        // 인텐트를 시작하면 로그인할 Google 계정을 선택하라는 메시지가 사용자에게 표시
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, REQ_SIGN_GOOGLE)
    }

    // 사용자가 구글 계정으로 로그인 한 뒤, 결과값을 되돌려 받는 곳
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQ_SIGN_GOOGLE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            PreferenceManager.setBoolean(this@LoginActivity, "isLogin", true)
        }
    }

    // 로그인한 사용자의 프로필 정보를 얻는 곳
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            if (account != null) {
                val personName = account.displayName
                val personEmail = account.email
                val personPhoto = account.photoUrl
                val intent = Intent(this@LoginActivity, MainActivity_bakk::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("nickName", personName)
                intent.putExtra("photoUrl", personPhoto.toString())
                intent.putExtra("googleEmail", personEmail)
                startActivity(intent)
                finish()
            }

            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    companion object {
        private const val TAG = "LoginActivity"

        // 구글 로그인 결과 코드
        private const val REQ_SIGN_GOOGLE = 100
    }
}