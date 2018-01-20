package com.jaredzhao.castleblitz.utils;

import com.badlogic.gdx.utils.Array;
//import de.tomgrill.gdxfacebook.core.*;

/**
 * Utility for accessing Facebook login
 */
public class FacebookAccessor {

    /*

    public GDXFacebook gdxFacebook;
    private Array<String> permissions;

    */

    /**
     * Initialize FacebookAccessor
     */

    /*
    public void init(){
        GDXFacebookConfig config = new GDXFacebookConfig();
        config.APP_ID = "574160876305387"; // required
        gdxFacebook = GDXFacebookSystem.install(config);

        permissions = new Array<String>();
        permissions.add("email");
        permissions.add("public_profile");
        permissions.add("user_friends");
    }

    */

    /**
     * Attempt login
     */

    /*
    public void login(){

        gdxFacebook.signOut(false); //for debug

        gdxFacebook.signIn(SignInMode.READ, permissions, new GDXFacebookCallback<SignInResult>() {
            /**
             * Called upon successful login
             *
             * @param result
             */

            /*
            @Override
            public void onSuccess(SignInResult result) {
                if(gdxFacebook.getAccessToken().getToken() != null) {
                    DisposableEntitySystem.out.println("POST SIGN IN: " + gdxFacebook.getAccessToken().getToken());
                }
            }

            /**
             * Called upon failed login
             *
             * @param error
             */

            /*
            @Override
            public void onError(GDXFacebookError error) {
                // Error handling
            }

            /**
             * Called upon cancelled login
             */

            /*
            @Override
            public void onCancel() {
                // When the user cancels the login process
            }

            /**
             * Called upon failed login
             *
             * @param t
             */

            /*
            @Override
            public void onFail(Throwable t) {
                // When the login fails
            }
        });
    }

    */

}
