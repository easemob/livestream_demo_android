package com.easemob.livedemo.ui.other.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.easemob.livedemo.common.livedata.SingleSourceLiveData;
import com.easemob.livedemo.common.repository.ClientRepository;
import com.easemob.livedemo.common.repository.Resource;
import com.easemob.livedemo.data.model.LoginResult;

public class LoginViewModel extends AndroidViewModel {
    private final ClientRepository repository;
    private SingleSourceLiveData<Resource<Boolean>> verificationCodeObservable;
    private SingleSourceLiveData<Resource<LoginResult>> loginFromAppServeObservable;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new ClientRepository();
        verificationCodeObservable =  new SingleSourceLiveData<>();
        loginFromAppServeObservable = new SingleSourceLiveData<>();
    }

    public LiveData<Resource<Boolean>> getVerificationCodeObservable(){
        return verificationCodeObservable;
    }

    public LiveData<Resource<LoginResult>> getLoginFromAppServeObservable(){
        return loginFromAppServeObservable;
    }

    /**
     * 获取短信验证码
     */
    public void postVerificationCode(String phoneNumber){
        verificationCodeObservable.setSource(repository.getVerificationCode(phoneNumber));
    }

    /**
     * 通过AppServe授权登录
     * @param userName
     * @param userPassword
     */
    public void loginFromAppServe(String userName,String userPassword){
        loginFromAppServeObservable.setSource(repository.loginFromServe(userName,userPassword));
    }

}
