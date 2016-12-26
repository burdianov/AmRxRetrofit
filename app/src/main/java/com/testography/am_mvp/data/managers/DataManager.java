package com.testography.am_mvp.data.managers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.testography.am_mvp.App;
import com.testography.am_mvp.R;
import com.testography.am_mvp.data.network.RestService;
import com.testography.am_mvp.data.network.res.ProductRes;
import com.testography.am_mvp.data.network.res.RestCallTransformer;
import com.testography.am_mvp.data.storage.dto.CommentDto;
import com.testography.am_mvp.data.storage.dto.ProductDto;
import com.testography.am_mvp.data.storage.dto.ProductLocalInfo;
import com.testography.am_mvp.data.storage.dto.UserAddressDto;
import com.testography.am_mvp.data.storage.dto.UserDto;
import com.testography.am_mvp.di.DaggerService;
import com.testography.am_mvp.di.components.DaggerDataManagerComponent;
import com.testography.am_mvp.di.components.DataManagerComponent;
import com.testography.am_mvp.di.modules.LocalModule;
import com.testography.am_mvp.di.modules.NetworkModule;
import com.testography.am_mvp.utils.ConstantsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Retrofit;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.testography.am_mvp.data.managers.PreferencesManager.PROFILE_AVATAR_KEY;
import static com.testography.am_mvp.data.managers.PreferencesManager.PROFILE_FULL_NAME_KEY;
import static com.testography.am_mvp.data.managers.PreferencesManager.PROFILE_PHONE_KEY;

public class DataManager {

    private static DataManager sInstance = new DataManager();
    public static final String TAG = "DataManager";

    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    RestService mRestService;
    @Inject
    Retrofit mRetrofit;

    private Context mAppContext;

    private UserDto mUser;

    private Map<String, String> mUserProfileInfo;
    private List<UserAddressDto> mUserAddresses;
    private Map<String, Boolean> mUserSettings;

    public static DataManager getInstance() {
        return sInstance;
    }

    private DataManager() {

        // TODO: 04-Nov-16 the following line MUST BE REFACTORED AS PER DI
        mAppContext = App.getAppContext();

        DataManagerComponent component = DaggerService.getComponent
                (DataManagerComponent.class);
        if (component == null) {
            component = DaggerDataManagerComponent.builder()
                    .appComponent(App.getAppComponent())
                    .localModule(new LocalModule())
                    .networkModule(new NetworkModule())
                    .build();
            DaggerService.registerComponent(DataManagerComponent.class, component);
        }
        component.inject(this);

        generateMockData();
        initUserData();
    }

    private void initUserData() {
        mUserProfileInfo = new HashMap<>();
        mUserAddresses = new ArrayList<>();

        mUserProfileInfo = mPreferencesManager.getUserProfileInfo();
        if (mUserProfileInfo.get(PROFILE_FULL_NAME_KEY).equals("")) {
            mUserProfileInfo.put(PROFILE_FULL_NAME_KEY, "Hulk Hogan");
        }
        if (mUserProfileInfo.get(PROFILE_AVATAR_KEY).equals("")) {
            mUserProfileInfo.put(PROFILE_AVATAR_KEY,
                    "http://a1.files.biography.com/image/upload/c_fill,cs_srgb," +
                            "dpr_1.0,g_face,h_300,q_80,w_300/MTIwNjA4NjM0MDQyNzQ2Mzgw.jpg");
        }
        if (mUserProfileInfo.get(PROFILE_PHONE_KEY).equals("")) {
            mUserProfileInfo.put(PROFILE_PHONE_KEY, "+7(917)971-38-27");
        }
        List<UserAddressDto> userAddresses = getPreferencesManager().getUserAddresses();

        if (userAddresses == null) {
            UserAddressDto userAddress;
            userAddress = new UserAddressDto(1, "Home", "Airport Road", "24", "56",
                    9, "Beware of crazy dogs");
            mUserAddresses.add(userAddress);
            userAddress = new UserAddressDto(2, "Work", "Central Park", "123", "67",
                    2, "In the middle of nowhere");
            mUserAddresses.add(userAddress);
        } else {
            mUserAddresses = userAddresses;
        }

        mUserSettings = getPreferencesManager().getUserSettings();

        mUser = new UserDto(mUserProfileInfo, mUserAddresses, mUserSettings);
    }

    public Observable getProductsObsFromNetwork() {
        return mRestService.getProductResObs(mPreferencesManager.getLastProductUpdate())
                .compose(new RestCallTransformer<List<ProductRes>>())
                .doOnNext(productRes -> {
                    Log.e(TAG, "getProductsObsFromNetwork: " + Thread
                            .currentThread().getName());
                })
                .flatMap(Observable::from)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(productRes -> {
                    if (!productRes.isActive()) {
                        deleteFromDb(productRes);
                    }
                })
                .distinct(ProductRes::getRemoteId)
                .filter(ProductRes::isActive)
                .doOnNext(productRes -> {
                    saveOnDisk(productRes);
                })
                .doOnCompleted(() -> {
//                    generateMockData();
                });
    }

    private void deleteFromDb(ProductRes productRes) {
        mPreferencesManager.deleteProduct(productRes);
    }

    @Nullable
    public List<ProductDto> fromDisk() {
        List<ProductDto> productDtoList = mPreferencesManager.getProductList();

        if (productDtoList == null) {
            productDtoList = generateMockData();
            mPreferencesManager.generateProductsMockData(productDtoList);
        }
        return productDtoList;
    }

    private void saveOnDisk(ProductRes productRes) {
        mPreferencesManager.updateOrInsertProduct(productRes);
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Context getAppContext() {
        return mAppContext;
    }

    public boolean isAuthUser() {
        return !mPreferencesManager.getAuthToken().equals(ConstantsManager
                .INVALID_TOKEN);
    }

    public void loginUser(String email, String password) {
        // TODO: 23-Oct-16 implement user authentication
    }

    public void updateProduct(ProductDto product) {
        // TODO: 28-Oct-16 update product count or other property and save to DB
    }

    private String getResVal(int resourceId) {
        return getAppContext().getString(resourceId);
    }

    public Map<String, String> getUserProfileInfo() {
        return mUserProfileInfo;
    }

    public List<UserAddressDto> getUserAddresses() {
        return mUserAddresses;
    }

    public Map<String, Boolean> getUserSettings() {
        return mUserSettings;
    }

    public void saveProfileInfo(String name, String phone, String avatar) {
        mUserProfileInfo.put(PROFILE_FULL_NAME_KEY, name);
        mUserProfileInfo.put(PROFILE_AVATAR_KEY, avatar);
        mUserProfileInfo.put(PROFILE_PHONE_KEY, phone);
        mPreferencesManager.saveProfileInfo(mUserProfileInfo);
    }

    public void saveSetting(String notificationKey, boolean isChecked) {
        mPreferencesManager.saveSetting(notificationKey, isChecked);
        mUserSettings.put(notificationKey, isChecked);
    }

    public void updateOrInsertAddress(UserAddressDto addressDto) {
        if (mUserAddresses.contains(addressDto)) {
            mUserAddresses.set(mUserAddresses.indexOf(addressDto), addressDto);
        } else {
            mUserAddresses.add(0, addressDto);
        }
        mPreferencesManager.saveUserAddresses(mUserAddresses);
    }

    public void removeAddress(UserAddressDto addressDto) {
        if (mUserAddresses.contains(addressDto)) {
            mUserAddresses.remove(mUserAddresses.indexOf(addressDto));
            getPreferencesManager().saveUserAddresses(mUserAddresses);
        }
    }

    private List<ProductDto> generateMockData() {
        List<ProductDto> productDtoList = getPreferencesManager().getProductList();
        List<CommentDto> commentList = new ArrayList<>();
        commentList.add(new CommentDto());

        if (productDtoList == null) {
            productDtoList = new ArrayList<>();
            productDtoList.add(new ProductDto(1, "disk " +
                    getResVal(R.string.product_name_1),
                    getResVal(R.string.product_url_1),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(2, "disk " +
                    getResVal(R.string.product_name_2),
                    getResVal(R.string.product_url_2),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(3, "disk " +
                    getResVal(R.string.product_name_3),
                    getResVal(R.string.product_url_3),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(4, "disk " +
                    getResVal(R.string.product_name_4),
                    getResVal(R.string.product_url_4),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(5, "disk " +
                    getResVal(R.string.product_name_5),
                    getResVal(R.string.product_url_5),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(6, "disk " +
                    getResVal(R.string.product_name_6),
                    getResVal(R.string.product_url_6),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(7, "disk " +
                    getResVal(R.string.product_name_7),
                    getResVal(R.string.product_url_7),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(8, "disk " +
                    getResVal(R.string.product_name_8),
                    getResVal(R.string.product_url_8),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(9, "disk " +
                    getResVal(R.string.product_name_9),
                    getResVal(R.string.product_url_9),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
            productDtoList.add(new ProductDto(10, "disk " +
                    getResVal(R.string.product_name_10),
                    getResVal(R.string.product_url_10),
                    getResVal
                            (R.string.lorem_ipsum), 100, 1, false, commentList));
        }
        return productDtoList;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public Observable<ProductLocalInfo> getProductLocalInfoObs(ProductRes productRes) {
        return Observable.just(getPreferencesManager().getLocalInfo(productRes
                .getRemoteId()))
                .flatMap(productLocalInfo ->
                        productLocalInfo == null ?
                                Observable.just(new ProductLocalInfo()) :
                                Observable.just(productLocalInfo)
                );
    }
}
