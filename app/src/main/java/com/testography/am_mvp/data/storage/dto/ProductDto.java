package com.testography.am_mvp.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.testography.am_mvp.data.network.res.ProductRes;

public class ProductDto implements Parcelable {
    private int id;
    private String productName;
    private String imageUrl;
    private String description;
    private int price;
    private int count;
    private boolean favorite;

    public ProductDto(int id, String productName, String imageUrl,
                      String description, int price, int count, boolean favorite) {
        this.id = id;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
        this.count = count;
        this.favorite = favorite;
    }

    //region ==================== Parcelable ===================

    protected ProductDto(Parcel in) {
        id = in.readInt();
        productName = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        price = in.readInt();
        count = in.readInt();
        favorite = in.readByte() != 0;
    }

    public static final Creator<ProductDto> CREATOR = new Creator<ProductDto>() {
        @Override
        public ProductDto createFromParcel(Parcel in) {
            return new ProductDto(in);
        }

        @Override
        public ProductDto[] newArray(int size) {
            return new ProductDto[size];
        }
    };

    public ProductDto(ProductRes productRes, ProductLocalInfo productLocalInfo) {
        id = productRes.getRemoteId();
        productName = productRes.getProductName();
        imageUrl = productRes.getImageUrl();
        description = productRes.getDescription();
        price = productRes.getPrice();
        count = productLocalInfo.getCount();
        favorite = productLocalInfo.isFavorite();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(productName);
        parcel.writeString(imageUrl);
        parcel.writeString(description);
        parcel.writeInt(price);
        parcel.writeInt(count);
    }
    //endregion

    //region ==================== Setters ===================

    public void setCount(int count) {
        this.count = count;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    //endregion


    //region ==================== Getters ===================
    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public void deleteProduct() {
        count--;
    }

    public void addProduct() {
        count++;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    //endregion
}
