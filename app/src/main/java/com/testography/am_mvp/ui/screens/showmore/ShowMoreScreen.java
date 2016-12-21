package com.testography.am_mvp.ui.screens.showmore;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.testography.am_mvp.R;
import com.testography.am_mvp.data.storage.dto.ProductDto;
import com.testography.am_mvp.di.DaggerService;
import com.testography.am_mvp.di.scopes.ShowMoreScope;
import com.testography.am_mvp.flow.AbstractScreen;
import com.testography.am_mvp.flow.Screen;
import com.testography.am_mvp.mvp.presenters.IShowMorePresenter;
import com.testography.am_mvp.ui.activities.RootActivity;

import dagger.Provides;
import flow.TreeKey;
import mortar.MortarScope;
import mortar.ViewPresenter;

// TODO: 20-Dec-16 Manage properly the scopes
// RootActivity.RootComponent = ProductScreen.Component ???

@Screen(R.layout.screen_show_more)
public class ShowMoreScreen extends AbstractScreen<RootActivity.RootComponent>
        implements TreeKey {

    @Nullable
    private ProductDto mProductDto;

    public ShowMoreScreen(@Nullable ProductDto productDto) {
        mProductDto = productDto;
    }

    @NonNull
    @Override
    public Object getParentKey() {
        return new RootActivity();
    }

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerShowMoreScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    @Override
    public boolean equals(Object o) {

        if (mProductDto != null) {
            return o instanceof ShowMoreScreen && mProductDto.equals((
                    (ShowMoreScreen) o).mProductDto);
        } else {
            return super.equals(o);
        }
    }

    @Override
    public int hashCode() {
        return mProductDto != null ? mProductDto.hashCode() : super.hashCode();
    }

    //region ==================== DI ===================

    @dagger.Module
    public class Module {
        @Provides
        @ShowMoreScope
        ShowMoreScreen.ShowMorePresenter provideShowMorePresenter() {
            return new ShowMoreScreen.ShowMorePresenter();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules =
            ShowMoreScreen.Module.class)
    @ShowMoreScope
    public interface Component {
        void inject(ShowMoreScreen.ShowMorePresenter presenter);

        void inject(ShowMoreView view);
    }

    //endregion

    //region ==================== Presenter ===================

    public class ShowMorePresenter extends ViewPresenter<ShowMoreView> implements
            IShowMorePresenter {

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((ShowMoreScreen.Component) scope.getService(DaggerService
                    .SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            getView().initView(mProductDto);
        }

        @Override
        public void clickOnFabFavorite() {
            getView().setFavoriteProduct();
        }

        @Override
        public void clickOnFabAddComment() {
            getView().addProductComment();
        }
    }

    //endregion
}