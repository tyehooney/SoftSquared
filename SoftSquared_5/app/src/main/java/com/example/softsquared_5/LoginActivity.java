package com.example.softsquared_5;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

public class LoginActivity extends AppCompatActivity {

    private SessionCallback sessionCallback;

    private LoginButton btnLogin;

    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.button_kakao_login);

        synchronized (this){
            try {
                wait(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        if(!Session.getCurrentSession().checkAndImplicitOpen()){
            btnLogin.setVisibility(View.VISIBLE);
        }//자동 로그인
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면\n앱이 종료됩니다."
                    , Toast.LENGTH_SHORT).show();
        }else{
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"로그인 도중 오류가 발생했습니다: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(),"세션이 닫혔습니다. 다시 시도해 주세요: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    Log.i("KAKAO_API", "사용자 아이디: " + result.getId());

                    UserAccount kakaoAccount = result.getKakaoAccount();
                    if (kakaoAccount != null) {

                        // 이메일
                        String email = kakaoAccount.getEmail();

                        if (email != null) {
                            Log.i("KAKAO_API", "email: " + email);

                        } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                            // 동의 요청 후 이메일 획득 가능
                            // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.

                        } else {
                            // 이메일 획득 불가
                        }

                        // 프로필
                        Profile profile = kakaoAccount.getProfile();

                        if (profile != null) {
                            Log.d("KAKAO_API", "nickname: " + profile.getNickname());
                            Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("nicklname", profile.getNickname());
                            intent.putExtra("profileImg", profile.getThumbnailImageUrl());
                            startActivity(intent);
                            finish();

                        } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                            // 동의 요청 후 프로필 정보 획득 가능

                        } else {
                            // 프로필 획득 불가
                        }
                    }
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}