package com.testography.am_mvp.ui.screens.showmore;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.testography.am_mvp.R;
import com.testography.am_mvp.data.storage.dto.ProductDto;
import com.testography.am_mvp.di.DaggerService;
import com.testography.am_mvp.mvp.views.IShowMoreView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowMoreView extends LinearLayout implements IShowMoreView {

    @BindView(R.id.show_more_pager)
    ViewPager mShowMorePager;
    @BindView(R.id.show_more_tabs)
    TabLayout mShowMoreTabs;

    @Inject
    ShowMoreScreen.ShowMorePresenter mPresenter;

    public ShowMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            DaggerService.<ShowMoreScreen.Component>getDaggerComponent(context)
                    .inject(this);
        }
    }

    //region ==================== flow view lifecycle callbacks ===================

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mPresenter.takeView(this);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            mPresenter.dropView(this);
        }
    }

    //endregion

    @Override
    public void initView(ProductDto productDto) {
        mShowMorePager.setAdapter(new ShowMoreAdapter(getContext(), productDto));
        mShowMoreTabs.setupWithViewPager(mShowMorePager);
    }

    @Override
    public void setFavoriteProduct() {
        Toast.makeText(getContext(), "Set Favorite Product", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void addProductComment() {
        Toast.makeText(getContext(), "addProductComment", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }
}
