package com.testography.am_mvp.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.testography.am_mvp.data.storage.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;

public class ProductRes {
    private int remoteId;
    private String productName;
    private String imageUrl;
    private String description;
    private int price;
    @SerializedName("raiting")
    @Expose
    private float rating;
    private boolean active;
    private List<CommentDto> comments = null;

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

    public float getRating() {
        return rating;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentDto> comments) {
        this.comments = comments;
    }
}
