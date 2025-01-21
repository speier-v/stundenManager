package com.albsig.stundenmanager.common.viewmodel.admin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.Helpers;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.ShiftModel;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.VIModel;
import com.albsig.stundenmanager.domain.repository.AdminRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;
import com.google.firebase.Timestamp;

import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private MutableLiveData<Result<UserModel>> userModelResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<UserModel>>> userListResult = new MutableLiveData<>();

    private MutableLiveData<UserModel> userDetailModel = new MutableLiveData<>();
    private MutableLiveData<Result<List<VIModel>>> checkedVIList = new MutableLiveData<>();
    private MutableLiveData<Result<List<VIModel>>> viListToCheck = new MutableLiveData<>();

    public AdminViewModel(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    public LiveData<Result<UserModel>> getUserModel() {
        return userModelResult;
    }

    public LiveData<Result<List<UserModel>>> getUserList() {
        return userListResult;
    }

    public LiveData<Result<List<VIModel>>> getCheckedVIList() {
        return checkedVIList;
    }

    public void loginAdmin(JSONObject userData) {
        adminRepository.loginAdmin(userData, new ResultCallback<UserModel>() {
            @Override
            public void onSuccess(Result<UserModel> response) {
                Log.d(TAG, "Login successful");
                userModelResult.setValue(response);
            }

            @Override
            public void onError(Result<UserModel> error) {
                Log.d(TAG, "Login failed " + error.getError().toString());
                userModelResult.setValue(error);
            }
        });
    }

    public void signOutAdmin() {
        Log.d(TAG, "Logout");
        userRepository.signOutUser(new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                Log.d(TAG, "Logout successful");
                userModelResult = new MutableLiveData<>();
            }

            @Override
            public void onError(Result<Boolean> error) {
                Log.d(TAG, "Logout did not work. Try again later!");
            }
        });
    }

    public void getUsers() {
        adminRepository.getUsers(new ResultCallback<List<UserModel>>() {
            @Override
            public void onSuccess(Result<List<UserModel>> response) {
                userListResult.setValue(response);
            }

            @Override
            public void onError(Result<List<UserModel>> error) {

            }
        });
    }

    public void setUserModel(UserModel userModel) {
        userDetailModel.setValue(userModel);
    }

    public LiveData<UserModel> getUserDetailModel() {
        return userDetailModel;
    }

    public void clearUserDetailModel() {
        userDetailModel = new MutableLiveData<>();
    }

    public void getCheckedVIList(String uid) {
        adminRepository.getCheckedVIList(uid, new ResultCallback<List<VIModel>>() {

            @Override
            public void onSuccess(Result<List<VIModel>> response) {
                checkedVIList.setValue(response);
            }

            @Override
            public void onError(Result<List<VIModel>> error) {
                Log.d(TAG, "getCheckedVIList failed " + error.getError().toString());
            }
        });
    }

    public LiveData<Result<List<VIModel>>> getVIListToCheck() {
        return viListToCheck;
    }

    public void getVIListToCheck(String uid) {
        adminRepository.getVIListToCheck(uid, new ResultCallback<List<VIModel>>() {

            @Override
            public void onSuccess(Result<List<VIModel>> response) {
                viListToCheck.setValue(response);
            }

            @Override
            public void onError(Result<List<VIModel>> error) {
                Log.d(TAG, "getVIListToCheck failed " + error.getError().toString());
            }
        });
    }

    public void updateVIModel(String approvalType, String uid, String docId, ResultCallback<Boolean> callback) {
        adminRepository.updateVIModel(approvalType, uid, docId, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Result<Boolean> error) {
                callback.onError(error);
            }
        });
    }

    public void getFilteredShiftsForUser(String uid, int year, ResultCallback<List<ShiftModel>> callback) {
        String refUid = "/" + Constants.USERS_COLLECTION + "/" + uid;
        adminRepository.getShifts(new ResultCallback<List<ShiftModel>>() {
            @Override
            public void onSuccess(Result<List<ShiftModel>> response) {
                List<ShiftModel> filteredUserShifts = response.getValue();
                filteredUserShifts = filterForYearShifts(filteredUserShifts, year);
                filteredUserShifts = filterForUserShifts(filteredUserShifts, refUid);
                callback.onSuccess(Result.success(filteredUserShifts));
            }

            @Override
            public void onError(Result<List<ShiftModel>> error) {
                callback.onError(error);
            }
        });
    }

    private List<ShiftModel> filterForYearShifts(List<ShiftModel> filteredUserShifts, int year) {
        List<ShiftModel> res = new ArrayList<>();
        Timestamp startDate = Helpers.createCustomTimestamp(year, 1, 1, 0, 0);
        Timestamp endDate = Helpers.createCustomTimestamp(year, 12, 31, 23, 59);

        for (ShiftModel shift : filteredUserShifts) {
            long startDiff = shift.getStartDate().toDate().getTime() - startDate.toDate().getTime();
            long endDiff = endDate.toDate().getTime() - shift.getEndDate().toDate().getTime();

            if ( startDiff > 0  && endDiff > 0) {
                res.add(shift);
            }
        }

        return res;
    }

    private List<ShiftModel> filterForUserShifts(List<ShiftModel> filteredUserShifts, String refUid) {
        List<ShiftModel> res = new ArrayList<>();
        for (ShiftModel shift : filteredUserShifts) {
            if(shift.getMorningShift().contains(refUid)) {
                res.add(shift);
            } else if(shift.getAfternoonShift().contains(refUid)) {
                res.add(shift);
            } else if(shift.getEveningShift().contains(refUid)) {
                res.add(shift);
            }
        }

        return res;
    }

    public void getContents(LocalDateTime localDateTime, UserModel userModel,int year, ResultCallback<List<String>> callback) {
        getFilteredShiftsForUser(userModel.getUid(), year, new ResultCallback<List<ShiftModel>>() {
            @Override
            public void onSuccess(Result<List<ShiftModel>> response) {
                List<ShiftModel> filteredUserShifts = response.getValue();
                filteredUserShifts = filterForYearShifts(filteredUserShifts, year);
                filteredUserShifts = filterForUserShifts(filteredUserShifts, "/" + Constants.USERS_COLLECTION + "/" + userModel.getUid());
                Map<Month, Integer> monthlyCounts = getMonthlyCounts(filteredUserShifts, year);

                adminRepository.getCheckedVIList(userModel.getUid(), new ResultCallback<List<VIModel>>() {
                    @Override
                    public void onSuccess(Result<List<VIModel>> response) {
                        List<VIModel> viList = response.getValue();
                        Map<Month, Integer> monthlyVacation = getMonthlyVacations(viList, year);
                        Map<Month, Integer> monthlyIllness = getMonthlyIllness(viList, year);

                        List<String> lines = getContentLines(localDateTime, userModel, monthlyCounts, monthlyVacation, monthlyIllness);
                        callback.onSuccess(Result.success(lines));
                    }

                    @Override
                    public void onError(Result<List<VIModel>> error) {
                        Log.d(TAG, "getCheckedVIList failed " + error.getError().toString());
                    }
                });
            }

            @Override
            public void onError(Result<List<ShiftModel>> error) {

            }
        });
    }

    private Map<Month, Integer> getMonthlyVacations(List<VIModel> viList, int year) {
        List<VIModel> filteredVacationList = new ArrayList<>();
        filteredVacationList = filterForType(Constants.VACATION_COLLECTION, viList);
        filteredVacationList = filterForYearVI(filteredVacationList, year);

        Map<Month, Integer> monthlyVacation = new HashMap<>();

        for (VIModel viModel : filteredVacationList) {
            LocalDate viStartDate = viModel.getStartDate().toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate viEndDate = viModel.getEndDate().toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(viStartDate, viEndDate) + 1;
            long workdays = filterForWorkdays(viStartDate, viEndDate);

            int hoursBetween = (int) (workdays * 8);
            monthlyVacation.put(viStartDate.getMonth(), (int) (monthlyVacation.getOrDefault(viStartDate.getMonth(), 0) + hoursBetween));
        }
        return monthlyVacation;
    }

    private long filterForWorkdays(LocalDate viStartDate, LocalDate viEndDate) {
        long workdays = 0;
        for (LocalDate date = viStartDate; !date.isAfter(viEndDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workdays++;
            }
        }
        return workdays;
    }

    private List<VIModel> filterForYearVI(List<VIModel> filteredVIList, int year) {
        List<VIModel> res = new ArrayList<>();
        LocalDate relativeDate = LocalDate.of(year, 1, 1);

        for (VIModel viModel : filteredVIList) {
            LocalDate viStartDate = viModel.getStartDate().toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate viEndDate = viModel.getEndDate().toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            if(relativeDate.getYear() == viStartDate.getYear() || relativeDate.getYear() == viEndDate.getYear()) {
                res.add(viModel);
            }
        }

        return  res;
    }

    private List<VIModel> filterForType(String type, List<VIModel> viList) {
        List<VIModel> res = new ArrayList<>();
        for (VIModel viModel : viList) {
            if (viModel.getType().equals(type)) {
                res.add(viModel);
            }
        }

        return res;
    }

    private Map<Month, Integer> getMonthlyIllness(List<VIModel> viList, int year) {
        List<VIModel> filteredIllnessList = new ArrayList<>();
        filteredIllnessList = filterForType(Constants.ILLNESS_COLLECTION, viList);
        filteredIllnessList = filterForYearVI(filteredIllnessList, year);

        Map<Month, Integer> monthlyIllness = new HashMap<>();

        for (VIModel viModel : filteredIllnessList) {
            LocalDate viStartDate = viModel.getStartDate().toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            LocalDate viEndDate = viModel.getEndDate().toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(viStartDate, viEndDate) + 1;
            long workdays = filterForWorkdays(viStartDate, viEndDate);

            int hoursBetween = (int) (workdays * 8);
            monthlyIllness.put(viStartDate.getMonth(), (int) (monthlyIllness.getOrDefault(viStartDate.getMonth(), 0) + hoursBetween));
        }
        return monthlyIllness;
    }

    private List<String> getContentLines(LocalDateTime localData, UserModel userModel, Map<Month, Integer> monthlyCounts, Map<Month, Integer> monthlyVacation, Map<Month, Integer> monthlyIllness) {
        List<String> lines = new ArrayList<>();
        lines.add(Constants.APP_NAME);
        lines.add(addUnderlineBreak());
        lines.add("Austtelungsdatum: " + localData.getYear() + "-" + localData.getMonthValue() + "-" + localData.getDayOfMonth());
        lines.add("Name: " + userModel.getName());
        lines.add("Vorname: " + userModel.getSurname());
        lines.add(addUnderlineBreak());
        lines.add("Zu leistende Arbeitsstunden (pro Monat) : 160");
        lines.add("Zu leistende Arbeitsstunden (pro Woche) : 40");
        lines.add(addUnderlineBreak());
        for (Month month : Month.values()) {
            int valCounts = monthlyCounts.getOrDefault(month, 0);
            int valVacation = monthlyVacation.getOrDefault(month, 0);
            int valIllness = monthlyIllness.getOrDefault(month, 0);
            int valTotal = valCounts - valVacation - valIllness;
            lines.add(month.getDisplayName(TextStyle.FULL, Locale.GERMAN));
            lines.add("Geleistete Arbeitszeit (in Stunden): " + valCounts);
            lines.add("Urlaube (in Stunden): " + valVacation);
            lines.add("Krankheiten (in Stunden): " + valIllness);
            lines.add("Gesamt (in Stunden): " + valTotal);
            lines.add(addUnderlineBreak());
        }


        return lines;
    }

    private Map<Month, Integer> getMonthlyCounts(List<ShiftModel> shifts, int year) {
        Map<Month, Integer> monthlyCounts = new HashMap<>();
        for (Month month : Month.values()) {
            monthlyCounts.put(month, 0);
        }


        for(ShiftModel shiftModel : shifts) {
            LocalDate shiftStartDate = shiftModel.getStartDate().toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            shiftStartDate.getMonth();
            monthlyCounts.put(shiftStartDate.getMonth(), monthlyCounts.getOrDefault(shiftStartDate.getMonth(), 0) + 40);
        }

        return monthlyCounts;
    }

    private String addUnderlineBreak() {
        return "___________________________________";
    }
}
