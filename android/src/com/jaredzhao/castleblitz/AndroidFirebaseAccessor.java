package com.jaredzhao.castleblitz;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jaredzhao.castleblitz.utils.FacebookAccessor;
import com.jaredzhao.castleblitz.utils.FirebaseAccessor;

public class AndroidFirebaseAccessor implements FirebaseAccessor {

    private FirebaseAuth mAuth;

    public void init(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser(); //check if logged in
    }

    public void createUserWithEmailPasswordCompletion(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    System.out.println("UID: " + user.getUid());
                    //updateUI(user);
                } else {
                    //updateUI(null);
                }
            }
        });
    }

    public boolean loginWithFacebook(FacebookAccessor facebookAccessor){
        facebookAccessor.login();
        return true;
    }

}
