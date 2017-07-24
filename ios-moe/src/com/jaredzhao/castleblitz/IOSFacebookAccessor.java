package com.jaredzhao.castleblitz;

import com.facebook.fbsdkloginkit.FBSDKLoginButton;
import com.jaredzhao.castleblitz.utils.FacebookAccessor;

public class IOSFacebookAccessor implements FacebookAccessor {

    private FBSDKLoginButton loginButton;

    public void init(){
        loginButton = FBSDKLoginButton.alloc().init();
    }

    public void login(){
        System.out.println("Login with Facebook on IOS");
    }
}
