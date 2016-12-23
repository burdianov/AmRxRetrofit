package com.testography.am_mvp.ui.screens.showmore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.testography.am_mvp.R;

import java.util.List;

public class CommentsListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mDataSource;

    public CommentsListAdapter(Context context, List<String> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = mInflater.inflate(R.layout.item_comment, viewGroup, false);
        TextView textView = (TextView) rowView.findViewById(R.id.user_name_txt);
        String testItem = getItem(i).toString();
        textView.setText(testItem);

        ImageView avatar = (ImageView) rowView.findViewById(R.id
                .avatar_comments_img);
        Picasso.with(null)
                .load(R.drawable.user_avatar_round)
                .into(avatar);
        return rowView;
    }
}
