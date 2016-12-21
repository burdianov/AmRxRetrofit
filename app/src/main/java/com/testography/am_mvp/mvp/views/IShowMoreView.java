package com.testography.am_mvp.mvp.views;

import com.testography.am_mvp.data.storage.dto.ProductDto;

public interface IShowMoreView extends IView {
    void initView(ProductDto productDto);

    void setFavoriteProduct();

    void addProductComment();
}
