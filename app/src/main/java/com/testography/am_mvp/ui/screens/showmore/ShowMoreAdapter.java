package com.testography.am_mvp.ui.screens.showmore;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.testography.am_mvp.R;
import com.testography.am_mvp.data.storage.dto.ProductDto;

import java.util.ArrayList;
import java.util.List;

import static com.testography.am_mvp.App.getContext;

public class ShowMoreAdapter extends PagerAdapter {

    public static final int TABS_NUMBER = 2;

    private Context mContext;
    private ProductDto mProductDto;

    public ShowMoreAdapter(Context context, ProductDto productDto) {
        mContext = context;
        mProductDto = productDto;
    }

    @Override
    public int getCount() {
        return TABS_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.tab_label_description);
            case 1:
                return mContext.getString(R.string.tab_label_comments);
            default:
                return super.getPageTitle(position);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View newView;

        switch (position) {
            case 0:
                newView = LayoutInflater.from(mContext).inflate
                        (R.layout.screen_show_more_description, container, false);

                TextView name = (TextView) newView.findViewById(R.id
                        .product_name_txt);
                TextView description = (TextView) newView.findViewById(R.id
                        .product_description_txt);
                TextView price = (TextView) newView.findViewById(R.id
                        .product_price_txt);
                TextView count = (TextView) newView.findViewById(R.id.product_count_txt);

                name.setText(mProductDto.getProductName());
                description.setText(mProductDto.getDescription());
                count.setText(Integer.toString(mProductDto.getCount()));
                price.setText(Integer.toString(mProductDto.getPrice()));

                break;
            case 1:
                newView = LayoutInflater.from(mContext).inflate
                        (R.layout.screen_show_more_comments, container, false);

                ListView listView = (ListView) newView.findViewById(R.id.list_view);

                final List<String> testItems = new ArrayList<>();
                testItems.add("Item 1");
                testItems.add("Item 2");
                testItems.add("Item 3");

                CommentsListAdapter adapter = new CommentsListAdapter(getContext
                        (), testItems);
                listView.setAdapter(adapter);

                break;
            default:
                newView = null;
                break;
        }
        container.addView(newView);

        return newView;
    }
}
