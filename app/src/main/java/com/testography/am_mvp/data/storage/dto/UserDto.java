package com.testography.am_mvp.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.testography.am_mvp.data.managers.PreferencesManager;

import java.util.ArrayList;
import java.util.Map;

public class UserDto implements Parcelable {
    private int id;
    private String fullname;
    private String avatar;
    private String phone;
    private boolean orderNotification;
    private boolean promoNotification;
    private ArrayList<UserAddressDto> userAddresses;

    protected UserDto(Parcel in) {
        id = in.readInt();
        fullname = in.readString();
        avatar = in.readString();
        phone = in.readString();
        orderNotification = in.readByte() != 0;
        promoNotification = in.readByte() != 0;
        userAddresses = in.createTypedArrayList(UserAddressDto.CREATOR);
    }

    public UserDto(Map<String, String> userProfileInfo,
                   ArrayList<UserAddressDto> userAddresses, Map<String, Boolean>
                           userSettings) {
        this.fullname = userProfileInfo.get(PreferencesManager
                .PROFILE_FULL_NAME_KEY);
        this.avatar = userProfileInfo.get(PreferencesManager
                .PROFILE_AVATAR_KEY);
        this.phone = userProfileInfo.get(PreferencesManager
                .PROFILE_PHONE_KEY);
        this.orderNotification = userSettings.get(PreferencesManager
                .NOTIFICATION_ORDER_KEY);
        this.promoNotification = userSettings.get(PreferencesManager
                .NOTIFICATION_PROMO_KEY);
        this.userAddresses = userAddresses;
    }

    public static final Creator<UserDto> CREATOR = new Creator<UserDto>() {
        @Override
        public UserDto createFromParcel(Parcel in) {
            return new UserDto(in);
        }

        @Override
        public UserDto[] newArray(int size) {
            return new UserDto[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isOrderNotification() {
        return orderNotification;
    }

    public void setOrderNotification(boolean orderNotification) {
        this.orderNotification = orderNotification;
    }

    public boolean isPromoNotification() {
        return promoNotification;
    }

    public void setPromoNotification(boolean promoNotification) {
        this.promoNotification = promoNotification;
    }

    public ArrayList<UserAddressDto> getUserAddresses() {
        return userAddresses;
    }

    public void setUserAddresses(ArrayList<UserAddressDto> userAddresses) {
        this.userAddresses = userAddresses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(fullname);
        parcel.writeString(avatar);
        parcel.writeString(phone);
        parcel.writeByte((byte) (orderNotification ? 1 : 0));
        parcel.writeByte((byte) (promoNotification ? 1 : 0));
        parcel.writeTypedList(userAddresses);
    }
}
