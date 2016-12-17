package com.testography.am_mvp.ui.screens.account;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import com.testography.am_mvp.R;
import com.testography.am_mvp.data.storage.dto.ActivityResultDto;
import com.testography.am_mvp.data.storage.dto.UserAddressDto;
import com.testography.am_mvp.data.storage.dto.UserInfoDto;
import com.testography.am_mvp.data.storage.dto.UserSettingsDto;
import com.testography.am_mvp.di.DaggerService;
import com.testography.am_mvp.di.scopes.AccountScope;
import com.testography.am_mvp.flow.AbstractScreen;
import com.testography.am_mvp.flow.Screen;
import com.testography.am_mvp.mvp.models.AccountModel;
import com.testography.am_mvp.mvp.presenters.IAccountPresenter;
import com.testography.am_mvp.mvp.presenters.RootPresenter;
import com.testography.am_mvp.mvp.presenters.SubscribePresenter;
import com.testography.am_mvp.mvp.views.IRootView;
import com.testography.am_mvp.ui.activities.RootActivity;
import com.testography.am_mvp.ui.screens.address.AddressScreen;
import com.testography.am_mvp.utils.ConstantsManager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import mortar.MortarScope;
import rx.Observable;
import rx.Subscription;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.DIRECTORY_PICTURES;
import static java.text.DateFormat.MEDIUM;

@Screen(R.layout.screen_account)
public class AccountScreen extends AbstractScreen<RootActivity.RootComponent> {

    private int mCustomState = 1;

    public int getCustomState() {
        return mCustomState;
    }

    public void setCustomState(int customState) {
        mCustomState = customState;
    }

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerAccountScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //region ==================== DI ===================

    @dagger.Module
    public class Module {

        @Provides
        @AccountScope
        AccountPresenter provideAccountPresenter() {
            return new AccountPresenter();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules =
            Module.class)
    @AccountScope
    public interface Component {
        void inject(AccountPresenter presenter);

        void inject(AccountView view);

        RootPresenter getRootPresenter();

        AccountModel getAccountModel();
    }

    //endregion

    //region ==================== Presenter ===================

    public class AccountPresenter extends SubscribePresenter<AccountView> implements
            IAccountPresenter {

        public static final String TAG = "AccountPresenter";

        @Inject
        RootPresenter mRootPresenter;
        @Inject
        AccountModel mAccountModel;

        private Subscription mAddressSub;
        private Subscription mSettingsSub;
        private File mPhotoFile;
        private Subscription mActivityResultSub;
        private Subscription mUserInfoSub;

        //region ==================== Lifecycle ===================

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
            subscribeOnActivityResult();
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if (getView() != null) {
                getView().initView();
            }
            subscribeOnAddressesObs();
            subscribeOnSettingsObs();
            subscribeOnUserInfoObs();
        }

        @Override
        protected void onSave(Bundle outState) {
            super.onSave(outState);
            mAddressSub.unsubscribe();
            mSettingsSub.unsubscribe();
            mUserInfoSub.unsubscribe();
        }

        @Override
        protected void onExitScope() {
            mActivityResultSub.unsubscribe();
            super.onExitScope();
        }

        //endregion

        //region ==================== Subscription ===================

        private void subscribeOnAddressesObs() {

            mAddressSub = subscribe(mAccountModel.getAddressObs(), new
                    ViewSubscriber<UserAddressDto>() {
                        @Override
                        public void onNext(UserAddressDto addressDto) {
                            if (getView() != null) {
                                getView().getAdapter().addItem(addressDto);
                            }
                        }
                    });
        }

        private void subscribeOnActivityResult() {
            Observable<ActivityResultDto> activityResultObs = mRootPresenter.getActivityResultDtoObs()
                    .filter(activityResultDto -> activityResultDto.getResultCode() == Activity
                            .RESULT_OK);

            mActivityResultSub = subscribe(activityResultObs, new ViewSubscriber<ActivityResultDto>() {
                @Override
                public void onNext(ActivityResultDto activityResultDto) {
                    handleActivityResult(activityResultDto);
                }
            });
        }

        private void handleActivityResult(ActivityResultDto activityResultDto) {

            if (getView() != null) {
                Observable.just(activityResultDto)
                        .filter(dto -> dto.getRequestCode() == ConstantsManager.REQUEST_PROFILE_PHOTO_PICKER)
                        .filter(dto -> dto.getIntent() != null)
                        .subscribe(dto -> getView().updateAvatarPhoto(Uri.parse(dto
                                .getIntent().getData().toString())));

                if (mPhotoFile != null) {
                    getView().updateAvatarPhoto(Uri.fromFile(mPhotoFile));
                }
            }
        }

        private void subscribeOnUserInfoObs() {
            mUserInfoSub = subscribe(mAccountModel.getUserInfoObs(), new ViewSubscriber<UserInfoDto>() {
                @Override
                public void onNext(UserInfoDto userInfoDto) {
                    if (getView() != null) {
                        getView().updateProfileInfo(userInfoDto);
                    }
                }
            });
        }

        public void updateListView() {
            getView().getAdapter().reloadAdapter();
            subscribeOnAddressesObs();
        }

        private void subscribeOnSettingsObs() {
            mSettingsSub = subscribe(mAccountModel.getUserSettingsObs(), new ViewSubscriber<UserSettingsDto>() {
                @Override
                public void onNext(UserSettingsDto userSettingsDto) {
                    if (getView() != null) {
                        getView().initSettings(userSettingsDto);
                    }
                }
            });
        }

        //endregion

        @Override
        public void clickOnAddress() {
            Flow.get(getView()).set(new AddressScreen(null));
        }

        @Override
        public void switchViewState() {
            if (getCustomState() == AccountView.EDIT_STATE && getView() != null)
                mAccountModel.saveProfileInfo(getView().getUserProfileInfo());
            if (getView() != null) {
                getView().changeState();
            }
        }

        @Override
        public void takePhoto() {
            if (getView() != null) {
                getView().showPhotoSourceDialog();
            }
        }

        //region ==================== CAMERA ===================

        @Override
        public void chooseCamera() {
            if (getView() != null) {
                String[] permissions = new String[]{CAMERA,
                        WRITE_EXTERNAL_STORAGE};
                if (mRootPresenter.checkPermissionsAndRequestIfNotGranted
                        (permissions, ConstantsManager.REQUEST_PERMISSION_CAMERA)) {
                    mPhotoFile = createFileForPhoto();
                    if (mPhotoFile == null && getRootView() != null) {
                        getRootView().showMessage("The picture cannot be created");
                        return;
                    }
                    takePhotoFromCamera();
                }
            }
        }

        private void takePhotoFromCamera() {
            Uri uriForFile = FileProvider.getUriForFile(((RootActivity)
                    getRootView()), ConstantsManager.FILE_PROVIDER_AUTHORITY, mPhotoFile);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
            if (getRootView() != null) {
                ((RootActivity) getRootView()).startActivityForResult
                        (takePictureIntent, ConstantsManager
                                .REQUEST_PROFILE_PHOTO_CAMERA);
            }
        }

        private File createFileForPhoto() {
            DateFormat dateTimeInstance = SimpleDateFormat.getTimeInstance(MEDIUM);
            String timeStamp = dateTimeInstance.format(new Date());
            String imageFileName = ConstantsManager.PHOTO_FILE_PREFIX + timeStamp
                    .replace(" ", "_");
            File storageDir = getView().getContext().getExternalFilesDir(DIRECTORY_PICTURES);
            File fileImage;
            try {
                fileImage = File.createTempFile(imageFileName, ".jpg", storageDir);
            } catch (IOException e) {
                return null;
            }
            return fileImage;
        }

        //endregion

        //region ==================== GALLERY ===================

        @Override
        public void chooseGallery() {
            if (getView() != null) {
                String[] permissions = new String[]{READ_EXTERNAL_STORAGE};
                if (mRootPresenter.checkPermissionsAndRequestIfNotGranted
                        (permissions, ConstantsManager
                                .REQUEST_PERMISSION_READ_WRITE_STORAGE)) {
                    takePhotoFromGallery();
                }
            }
        }

        private void takePhotoFromGallery() {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT < 19) {
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
            } else {
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            }
            if (getRootView() != null) {
                ((RootActivity) getRootView()).startActivityForResult(intent,
                        ConstantsManager.REQUEST_PROFILE_PHOTO_PICKER);
            }
        }

        //endregion

        @Override
        public void removeAddress(int position) {
            mAccountModel.removeAddress(mAccountModel.getAddressFromPosition(position));
            updateListView();
        }

        @Override
        public void editAddress(int position) {
            Flow.get(getView()).set(new AddressScreen(mAccountModel
                    .getAddressFromPosition(position)));
        }

        @Nullable
        @Override
        protected IRootView getRootView() {
            return mRootPresenter.getView();
        }

        public void switchSettings() {
            if (getView() != null) {
                mAccountModel.saveSettings(getView().getSettings());
            }
        }
    }

    //endregion
}
