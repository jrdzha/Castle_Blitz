package com.jaredzhao.castleblitz.utils;

import com.badlogic.gdx.utils.Array;
import de.tomgrill.gdxfacebook.core.*;

public class FacebookAccessor {

    public GDXFacebook gdxFacebook;
    private Array<String> permissions;

    public void init(){
        GDXFacebookConfig config = new GDXFacebookConfig();
        config.APP_ID = "574160876305387"; // required
        gdxFacebook = GDXFacebookSystem.install(config);

        permissions = new Array<String>();
        permissions.add("email");
        permissions.add("public_profile");
        permissions.add("user_friends");
    }

    public void login(){

        gdxFacebook.signOut(false); //for debug

        gdxFacebook.signIn(SignInMode.READ, permissions, new GDXFacebookCallback<SignInResult>() {
            @Override
            public void onSuccess(SignInResult result) {
                if(gdxFacebook.getAccessToken().getToken() != null) {
                    System.out.println("POST SIGN IN: " + gdxFacebook.getAccessToken().getToken());
                }
            }

            @Override
            public void onError(GDXFacebookError error) {
                // Error handling
            }

            @Override
            public void onCancel() {
                // When the user cancels the login process
            }

            @Override
            public void onFail(Throwable t) {
                // When the login fails
            }
        });
    }

}
