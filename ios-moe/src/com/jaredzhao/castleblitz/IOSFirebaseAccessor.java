package com.jaredzhao.castleblitz;

import apple.foundation.NSError;
import com.google.com.firebaseauth.FIRAuth;
import com.google.com.firebaseauth.FIRUser;
import com.google.framework.firebasecore.FIRApp;
import com.jaredzhao.castleblitz.utils.FacebookAccessor;
import com.jaredzhao.castleblitz.utils.FirebaseAccessor;

public class IOSFirebaseAccessor implements FirebaseAccessor {

    public void init(){
        FIRApp.configure();
    }

    public void createUserWithEmailPasswordCompletion(String email, String password){
        FIRAuth.auth().createUserWithEmailPasswordCompletion(email, password, new EmailPasswordCompletion());
    }

    public class EmailPasswordCompletion implements FIRAuth.Block_createUserWithEmailPasswordCompletion {

        public void call_createUserWithEmailPasswordCompletion(FIRUser user, NSError error) {
            System.out.println("UID: " + user.uid());
        }
    }

    public boolean loginWithFacebook(FacebookAccessor facebookAccessor){
        facebookAccessor.login();
        return true;
    }
}
