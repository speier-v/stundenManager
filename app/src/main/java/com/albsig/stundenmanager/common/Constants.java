package com.albsig.stundenmanager.common;

public class Constants {
    public static final String TAG_LOGIN = "Login";
    public static final String TAG_DASHBOARD = "Dashboard";
    public static final String TAG_DETAIL_TIME = "DetailTime";
    public static final String TAG_DETAIL_BREAK = "DetailBreak";
    public static final String TAG_REGISTRATION = "Registration";
    public static final String TAG_ADMIN_LOGIN = "AdminLogin";
    public static final String TAG_SHIFT_PLANNER = "ShiftPlanner";
    public static final String TAG_VACATION_ILLNESS = "VacationIllness";
    public static final String HTTP_CALLABLE_REF_CREATE_VACATION = "createVacation";
    public static final String HTTP_CALLABLE_REF_CREATE_ILLNESS = "createIllness";
    public static final String TAG_WORKER_ADMINISTRATION = "WorkerAdministration";
    public static final String TAG_USER_DETAILS = "UserDetails";
    public static final String FIELD_APPROVAL = "approval";
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
    public static final String VACATION_COLLECTION = "vacation";
    public static final String ILLNESS_COLLECTION = "illness";
    public static final String SHIFTS_COLLECTION = "shifts";
    public static final String ASSIGNED_SESSIONS_COLLECTION = "shifts";

    public static final String HTTP_CALLABLE_REF_CREATE_USER = "createUser";
    public static final String HTTP_CALLABLE_REF_CREATE_SHIFT = "createShift" ;

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
    public static final int MORNING_SHIFT = 0;
    public static final int LATE_SHIFT = 1;
    public static final int NIGHT_SHIFT = 2;
    public static final int NO_SHIFT_SELECTED = -1;

    public static final String APPROVAL_TYPE = "Vacation";
    public static final String APPROVAL_TYPE_ILLNESS = "Illness";
    public static final String APPROVAL_STATUS_CHECK = "check";
    public static final String APPROVAL_STATUS_ACCEPTED = "accepted";
    public static final String APPROVAL_STATUS_DENIED = "denied";
}
