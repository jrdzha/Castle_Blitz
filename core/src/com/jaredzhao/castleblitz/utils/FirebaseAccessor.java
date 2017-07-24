package com.jaredzhao.castleblitz.utils;

public interface FirebaseAccessor {

    public void init();
    public void createUserWithEmailPasswordCompletion(String email, String password);
    public boolean loginWithFacebook(FacebookAccessor facebookAccessor);

}
