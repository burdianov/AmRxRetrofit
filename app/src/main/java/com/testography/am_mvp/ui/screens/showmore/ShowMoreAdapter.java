package com.testography.am_mvp.ui.screens.showmore;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.testography.am_mvp.R;

import java.util.ArrayList;
import java.util.List;

import static com.testography.am_mvp.App.getContext;

public class ShowMoreAdapter extends PagerAdapter {

    public static final int TABS_NUMBER = 2;

    private Context mContext;

    public ShowMoreAdapter(Context context) {
        mContext = context;
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
