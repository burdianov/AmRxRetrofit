package com.testography.am_mvp.data.network.res;

public class ProductRes {
    private int remoteId;
    private String productName;
    private String imageUrl;
    private String description;
    private int price;
    private float raiting;
    private boolean active;

    public int getRemoteId() {
        return remoteId;
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

    public float getRaiting() {
        return raiting;
    }

    public boolean isActive() {
        return active;
    }
}
