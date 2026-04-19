package com.exploreMate.auth_service.routes;

public class AuthRoute {
    public static final String BASE = "/auth";
    public static final String SIGN_UP = BASE + "/signup";
    public static final String SIGN_UP_ALT = BASE + "/register"; // Alternate route for frontend compatibility
    public static final String SIGN_IN = BASE + "/login";
    public static final String ME = BASE + "/me";
    public static final String PROFILE_UPDATE = BASE + "/profile";
    public static final String FORGOT_PASSWORD = BASE + "/forgot-password";
    public static final String RESET_PASSWORD = BASE + "/reset-password";
    public static final String CHANGE_PASSWORD = BASE + "/profile/password";

    // Admin routes
    public static final String ADMIN_BASE = BASE + "/admin";
    public static final String ADMIN_SIGN_UP = ADMIN_BASE + "/signup";
    public static final String ADMIN_USERS = ADMIN_BASE + "/users";
    public static final String ADMIN_USER_BY_ID = ADMIN_BASE + "/users/{id}";
    public static final String ADMIN_STATS = ADMIN_BASE + "/stats";
}
