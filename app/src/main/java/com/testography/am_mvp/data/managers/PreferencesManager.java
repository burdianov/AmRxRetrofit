package com.testography.am_mvp.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.testography.am_mvp.data.storage.dto.ProductLocalInfo;
import com.testography.am_mvp.data.storage.dto.UserAddressDto;
import com.testography.am_mvp.utils.ConstantsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PreferencesManager {

    public static String PROFILE_FULL_NAME_KEY = "PROFILE_FULL_NAME_KEY";
    public static String PROFILE_AVATAR_KEY = "PROFILE_AVATAR_KEY";
    public static String PROFILE_PHONE_KEY = "PROFILE_PHONE_KEY";
    public static String NOTIFICATION_ORDER_KEY = "NOTIFICATION_ORDER_KEY";
    public static String NOTIFICATION_PROMO_KEY = "NOTIFICATION_PROMO_KEY";
    public static String PRODUCT_LAST_UPDATE_KEY = "PRODUCT_LAST_UPDATE_KEY";
    public static String USER_ADDRESSES_KEY = "USER_ADDRESSES_KEY";

    private final SharedPreferences mSharedPreferences;

    private Moshi mMoshi;
    private JsonAdapter<UserAddressDto> mJsonAdapter;

    public PreferencesManager(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        mMoshi = new Moshi.Builder().build();
        mJsonAdapter = mMoshi.adapter(UserAddressDto.class);
        if (mSharedPreferences.getString(PROFILE_FULL_NAME_KEY, null) == null) {
            generateMockData();
        }
    }

    public String getLastProductUpdate() {
        return mSharedPreferences.getString(PRODUCT_LAST_UPDATE_KEY,
                "Thu, 01 Jan 1970 00:00:00 GMT");
    }

    public void saveLastProductUpdate(String lastModified) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PRODUCT_LAST_UPDATE_KEY, lastModified);
        editor.apply();
    }

    public Map<String, Boolean> getUserSettings() {
        Map<String, Boolean> settings = new HashMap<>();
        settings.put(NOTIFICATION_ORDER_KEY, mSharedPreferences.getBoolean
                (NOTIFICATION_ORDER_KEY, false));
        settings.put(NOTIFICATION_PROMO_KEY, mSharedPreferences.getBoolean
                (NOTIFICATION_PROMO_KEY, false));
        return settings;
    }

    private void generateMockData() {
        UserAddressDto userAddressDto1 = new UserAddressDto(1, "Home",
                "Airport Road", "24", "56", 9, "Beware of crazy dogs");
        UserAddressDto userAddressDto2 = new UserAddressDto(2, "Work",
                "Central Park", "123", "67", 2, "In the middle of nowhere");
        String json1 = mJsonAdapter.toJson(userAddressDto1);
        String json2 = mJsonAdapter.toJson(userAddressDto2);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Set<String> setJson = new HashSet<>();
        setJson.add(json1);
        setJson.add(json2);
        editor.putStringSet(USER_ADDRESSES_KEY, setJson);

        editor.putString(PROFILE_FULL_NAME_KEY, "Hulk Hogan");
        editor.putString(PROFILE_AVATAR_KEY,
                "http://a1.files.biography.com/image/upload/c_fill,cs_srgb," +
                        "dpr_1.0,g_face,h_300,q_80,w_300/MTIwNjA4NjM0MDQyNzQ2Mzgw.jpg");
        editor.putString(PROFILE_PHONE_KEY, "+7(917)971-38-27");
        editor.apply();
    }

    public ArrayList<UserAddressDto> getUserAddress() {
        Set<String> setJson = mSharedPreferences
                .getStringSet(USER_ADDRESSES_KEY, new HashSet<>());
        ArrayList<String> listJson = new ArrayList<>(setJson);
        ArrayList<UserAddressDto> arrayAddress = new ArrayList<>();

        for (int i = 0; i < listJson.size(); i++) {
            try {
                arrayAddress.add(mJsonAdapter.fromJson(listJson.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return arrayAddress;
    }

    public Map<String, String> getUserProfileInfo() {
        Map<String, String> mapProfileInfo = new HashMap<>();
        mapProfileInfo.put(PROFILE_PHONE_KEY, mSharedPreferences.getString
                (PROFILE_PHONE_KEY, ""));
        mapProfileInfo.put(PROFILE_FULL_NAME_KEY, mSharedPreferences.getString
                (PROFILE_FULL_NAME_KEY, ""));
        mapProfileInfo.put(PROFILE_AVATAR_KEY, mSharedPreferences.getString
                (PROFILE_AVATAR_KEY, ""));
        return mapProfileInfo;
    }

    public void addUserAddress(UserAddressDto userAddressDto) {
        ArrayList<UserAddressDto> arrayList = getUserAddress();
        if (userAddressDto.getId() == 0) {
            userAddressDto.setId(arrayList.size() + 1);
        }

        arrayList.add(userAddressDto);
        // TODO: 15-Dec-16 complete the code!!!
    }

    public ProductLocalInfo getLocalInfo(int remoteId) {
        // TODO: 16-Dec-16 implement me
        return null;
    }

    public void saveAuthToken(String authToken) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantsManager.AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }

    public String getAuthToken() {
        return mSharedPreferences.getString(ConstantsManager.AUTH_TOKEN_KEY,
                ConstantsManager.INVALID_TOKEN);
    }

    public void clearAllData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    void saveAvatar(String avatar) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_AVATAR_KEY, avatar);
        editor.apply();
    }

    String checkAvatar() {
        String avatar;

        if (mSharedPreferences.contains(PROFILE_AVATAR_KEY)) {
            avatar = mSharedPreferences.getString(PROFILE_AVATAR_KEY, "null");
        } else {
            avatar = "null";
        }
        return avatar;
    }
}
