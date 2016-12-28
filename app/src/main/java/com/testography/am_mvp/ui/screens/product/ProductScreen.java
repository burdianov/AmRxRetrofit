package com.testography.am_mvp.ui.screens.product;

import android.os.Bundle;

import com.testography.am_mvp.R;
import com.testography.am_mvp.data.storage.dto.ProductDto;
import com.testography.am_mvp.data.storage.dto.ProductLocalInfo;
import com.testography.am_mvp.di.DaggerService;
import com.testography.am_mvp.di.scopes.ProductScope;
import com.testography.am_mvp.flow.AbstractScreen;
import com.testography.am_mvp.flow.Screen;
import com.testography.am_mvp.mvp.models.CatalogModel;
import com.testography.am_mvp.mvp.presenters.IProductPresenter;
import com.testography.am_mvp.ui.screens.catalog.CatalogScreen;
import com.testography.am_mvp.ui.screens.showmore.ShowMoreScreen;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Screen(R.layout.screen_product)
public class ProductScreen extends AbstractScreen<CatalogScreen.Component> {

    private ProductDto mProductDto;

    public ProductScreen(ProductDto product) {
        mProductDto = product;
    }

    //region ==================== Flow & Mortar ===================

    @Override
    public boolean equals(Object o) {
        return o instanceof ProductScreen && mProductDto.equals(((ProductScreen)
                o).mProductDto);
    }

    @Override
    public int hashCode() {
        return mProductDto.hashCode();
    }

    @Override
    public Object createScreenComponent(CatalogScreen.Component parentComponent) {
        return DaggerProductScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    //endregion

    //region ==================== DI ===================

    @dagger.Module
    public class Module {
        @Provides
        @ProductScope
        ProductPresenter provideProductPresenter() {
            return new ProductPresenter(mProductDto);
        }
    }

    @dagger.Component(dependencies = {CatalogScreen.Component.class}, modules = Module.class)
    @ProductScope
    public interface Component {
        void inject(ProductPresenter presenter);

        void inject(ProductView view);
    }

    //endregion

    //region ==================== Presenter ===================

    public class ProductPresenter extends ViewPresenter<ProductView> implements IProductPresenter {

        @Inject
        CatalogModel mCatalogModel;

        private ProductDto mProduct;

        public ProductPresenter(ProductDto productDto) {
            mProduct = productDto;
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if (getView() != null) {
                getView().showProductView(mCatalogModel.getProductById(mProduct
                        .getId()));
            }
        }

        @Override
        public void clickOnPlus() {
            if (getView() != null) {
                ProductLocalInfo pli = getView().getProductLocalInfo();
                pli.setRemoteId(mProduct.getId());
                pli.addCount();
                mCatalogModel.updateProductLocalInfo(pli);
                getView().updateProductCountView(mCatalogModel.getProductById
                        (mProduct.getId()));
                // TODO: 19-Dec-16 fix me with realm
            }
        }

        @Override
        public void clickOnMinus() {
            if (getView() != null) {
                ProductLocalInfo pli = getView().getProductLocalInfo();
                if (pli.getCount() > 0) {
                    pli.deleteCount();
                    pli.setRemoteId(mProduct.getId());
                    mCatalogModel.updateProductLocalInfo(pli);
                    getView().updateProductCountView(mCatalogModel.getProductById
                            (mProduct.getId()));
                    // TODO: 19-Dec-16 fix me with realm
                }
            }
        }

        public void clickFavorite() {
            if (getView() != null) {
                ProductLocalInfo pli = getView().getProductLocalInfo();
                pli.setRemoteId(mProduct.getId());
                mCatalogModel.updateProductLocalInfo(pli);
            }
        }

        public void clickShowMore() {
            if (getView() != null) {
                Flow.get(getView()).set(new ShowMoreScreen(mProduct));
            }
        }
    }

    //endregion
}
