package com.testography.am_mvp.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.testography.am_mvp.data.network.res.ProductRes;
import com.testography.am_mvp.data.storage.dto.ProductDto;
import com.testography.am_mvp.data.storage.dto.ProductLocalInfo;
import com.testography.am_mvp.data.storage.dto.UserAddressDto;
import com.testography.am_mvp.utils.ConstantsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PreferencesManager {

    public static String PROFILE_FULL_NAME_KEY = "PROFILE_FULL_NAME_KEY";
    public static String PROFILE_AVATAR_KEY = "PROFILE_AVATAR_KEY";
    public static String PROFILE_PHONE_KEY = "PROFILE_PHONE_KEY";

    public static String NOTIFICATION_ORDER_KEY = "NOTIFICATION_ORDER_KEY";
    public static String NOTIFICATION_PROMO_KEY = "NOTIFICATION_PROMO_KEY";

    public static String PRODUCT_LAST_UPDATE_KEY = "PRODUCT_LAST_UPDATE_KEY";
    public static String USER_ADDRESSES_KEY = "USER_ADDRESSES_KEY";
    public static String MOCK_PRODUCT_LIST = "MOCK_PRODUCT_LIST";

    private final SharedPreferences mSharedPreferences;

    private List<ProductDto> mProductDtoList = new ArrayList<>();

    public PreferencesManager(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        initProductsMockData();
    }

    //region ==================== User Settings ===================

    public Map<String, Boolean> getUserSettings() {
        Map<String, Boolean> settings = new HashMap<>();
        settings.put(NOTIFICATION_ORDER_KEY, mSharedPreferences.getBoolean
                (NOTIFICATION_ORDER_KEY, false));
        settings.put(NOTIFICATION_PROMO_KEY, mSharedPreferences.getBoolean
                (NOTIFICATION_PROMO_KEY, false));
        return settings;
    }

    public void saveSetting(String notificationKey, boolean isChecked) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(notificationKey, isChecked);
        editor.apply();
    }

    //endregion

    //region ==================== User Addresses ===================

    public ArrayList<UserAddressDto> getUserAddresses() {
        String addresses = mSharedPreferences.getString(USER_ADDRESSES_KEY, null);
        if (addresses != null) {
            Gson gson = new Gson();
            UserAddressDto[] addressList =
                    gson.fromJson(addresses, UserAddressDto[].class);
            return new ArrayList<>(Arrays.asList(addressList));
        }
        return null;
    }

    public void saveUserAddresses(List<UserAddressDto> userAddresses) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String addresses = gson.toJson(userAddresses);
        editor.putString(USER_ADDRESSES_KEY, addresses);
        editor.apply();
    }

    //endregion

    //region ==================== User Profile Info ===================

    public void saveProfileInfo(Map<String, String> userProfileInfo) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_FULL_NAME_KEY, userProfileInfo.get(PROFILE_FULL_NAME_KEY));
        editor.putString(PROFILE_AVATAR_KEY, userProfileInfo.get(PROFILE_AVATAR_KEY));
        editor.putString(PROFILE_PHONE_KEY, userProfileInfo.get(PROFILE_PHONE_KEY));
        editor.apply();
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

    //endregion

    //region ==================== Products ===================

    public String getLastProductUpdate() {
        return mSharedPreferences.getString(PRODUCT_LAST_UPDATE_KEY,
                "Thu, 01 Jan 1970 00:00:00 GMT");
    }

    public void saveLastProductUpdate(String lastModified) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PRODUCT_LAST_UPDATE_KEY, lastModified);
        editor.apply();
    }

    public ProductLocalInfo getLocalInfo(int remoteId) {
        // TODO: 16-Dec-16 implement me
        return null;
    }

    public void generateProductsMockData(List<ProductDto> mockProductList) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String products = gson.toJson(mockProductList);
        if (mSharedPreferences.getString(MOCK_PRODUCT_LIST, null) == null) {
            editor.putString(MOCK_PRODUCT_LIST, products);
            editor.apply();
        }
    }

    private void initProductsMockData() {
        String products = mSharedPreferences.getString(MOCK_PRODUCT_LIST, null);
        if (products != null) {
            Gson gson = new Gson();
            ProductDto[] productList = gson.fromJson(products, ProductDto[].class);
            List<ProductDto> productDtoList = Arrays.asList(productList);
            mProductDtoList = new ArrayList<>(productDtoList);
        }
    }

    public List<ProductDto> getProductList() {
        return mProductDtoList;
    }

    public void updateOrInsertProduct(ProductRes productRes) {
        ProductDto productDto;

        Iterator<ProductDto> iterator = mProductDtoList.iterator();

        while (iterator.hasNext()) {
            productDto = iterator.next();
            if (productDto.getId() == productRes.getRemoteId()) {
                productDto.setId(productRes.getRemoteId());
                productDto.setProductName(productRes.getProductName());
                productDto.setImageUrl(productRes.getImageUrl());
                productDto.setDescription(productRes.getDescription());
                productDto.setPrice(productRes.getPrice());
            }
        }
        productDto = new ProductDto(
                productRes.getRemoteId(),
                productRes.getProductName(),
                productRes.getImageUrl(),
                productRes.getDescription(),
                productRes.getPrice(),
                0, false);
        mProductDtoList.add(productDto);

        updateMockProductList(mProductDtoList);
    }

    public void updateOrInsertLocalInfo(ProductLocalInfo pli) {
        ProductDto productDto;

        Iterator<ProductDto> iterator = mProductDtoList.iterator();

        while (iterator.hasNext()) {
            productDto = iterator.next();
            if (productDto.getId() == pli.getRemoteId()) {
                productDto.setCount(pli.getCount());
                productDto.setFavorite(pli.isFavorite());
            }
        }
        updateMockProductList(mProductDtoList);
    }

    public void deleteProduct(ProductRes productRes) {

        // TODO: 21-Dec-16 shall be fixed with Realm
//        Iterator<ProductDto> iterator = mProductDtoList.iterator();
//        ProductDto productDto;
//        while (iterator.hasNext()) {
//            productDto = iterator.next();
//            if (productDto.getId() == productRes.getRemoteId()) {
//                iterator.remove();
//            }
//        }
    }

    private void updateMockProductList(List<ProductDto> productDtoList) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String products = gson.toJson(new ArrayList<>(productDtoList));
        editor.putString(MOCK_PRODUCT_LIST, products);
        editor.apply();
    }

    //endregion

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
}