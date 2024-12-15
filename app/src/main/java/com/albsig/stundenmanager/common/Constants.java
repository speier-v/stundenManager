package com.albsig.stundenmanager.common;

public class Constants {
    public static final String TAG_LOGIN = "Login";
    public static final String TAG_DASHBOARD = "Dashboard";
    public static final String TAG_DETAIL_TIME = "DetailTime";
    public static final String TAG_DETAIL_BREAK = "DetailBreak";
    public static final String TAG_REGISTRATION = "Registration";
    public static final String TAG_ADMIN_LOGIN = "AdminLogin";
    public static String TAG_ADMIN_DASHBOARD = "AdminDashboard";

    public static final String USER_MODEL_ID = "uid";
    public static final String USER_MODEL_EMAIL = "email";
    public static final String USER_MODEL_PASSWORD = "password";
    public static final String USER_MODEL_BIRTHDAY = "birthday";
    public static final String USER_MODEL_STREET = "street";
    public static final String USER_MODEL_ZIP_CODE = "zipCode";
    public static final String USER_MODEL_CITY = "city";
    public static final String USER_MODEL_NAME = "name";
    public static final String USER_MODEL_SURNAME = "surname";
    public static final String USER_MODEL_ROLE = "role";
    public static final String USERS_COLLECTION = "users";
    public static final String SESSIONS_COLLECTION = "sessions";
    public static final String ASSIGNED_SESSIONS_COLLECTION = "shifts";

    public static final String HTTP_CALLABLE_REF_CREATE_USER = "createUser";

    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String SESSION_FIELD_START_TIME = "startTime";
    public static final String SESSION_FIELD_END_TIME = "endTime";
    public static final String SESSION_FIELD_BREAKS = "breaks";
    public static final String BREAK_FIELD_START_TIME = "breakStart";
    public static final String BREAK_FIELD_END_TIME = "breakEnd";
    public static final String HTTP_CALLABLE_REF_CREATE_SESSION = "createSession";
    public static final String HTTP_CALLABLE_REF_CREATE_BREAK = "createBreak";
    public static final String FORMATTED_DATE_PATTERN = "dd.MM.yyyy HH:mm";

    public static final String HTTP_CALLABLE_REF_LOGIN_ADMIN = "loginAdmin";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
}
