package com.exploreMate.auth_service.routes;

public class AuthRoute {
    public static final String BASE = "/auth";
    public static final String SIGN_UP = BASE + "/signup";
    public static final String SIGN_UP_ALT = BASE + "/register";  // Alternate route for frontend compatibility
    public static final String SIGN_IN = BASE + "/login";
    public static final String ME = BASE + "/me";
}
