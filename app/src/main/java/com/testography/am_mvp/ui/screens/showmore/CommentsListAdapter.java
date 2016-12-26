package com.testography.am_mvp.ui.screens.showmore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.testography.am_mvp.R;
import com.testography.am_mvp.data.storage.dto.CommentDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentsListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<CommentDto> mComments;

    public CommentsListAdapter(Context context, List<CommentDto> comments) {
        mContext = context;
        mComments = comments;
        if (mComments.size() == 0) {
            mComments.add(new CommentDto());
        }
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Object getItem(int i) {
        return mComments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = mInflater.inflate(R.layout.item_comment, viewGroup, false);
        ImageView avatar = (ImageView) rowView.findViewById(R.id
                .avatar_comments_img);
        TextView userName = (TextView) rowView.findViewById(R.id.user_name_txt);
        RatingBar rating = (RatingBar) rowView.findViewById(R.id
                .rating_bar_comments);
        TextView timeElapsedTxt = (TextView) rowView.findViewById(R.id.time_txt);
        TextView comments = (TextView) rowView.findViewById(R.id.comment_text_txt);

        userName.setText("John Doe");
        rating.setRating(mComments.get(i).getRaiting());

        SimpleDateFormat timeFormat = new SimpleDateFormat
                ("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String dateString = mComments.get(i).getCommentDate();
        try {
            Date dateComment = timeFormat.parse(dateString);
            long timeCurrent = dateComment.getTime();

            Date dateNow = new Date();
            long timeNow = dateNow.getTime();

            long agoMinutes = (timeNow - timeCurrent) / 1000 / 60;
            long agoHours = agoMinutes / 60;
            long agoDays = agoHours / 60;

            String timeElapsed;
            if (agoHours > 24) {
                timeElapsed = (String.valueOf(agoDays) + " days ago");
            } else if (agoHours > 1) {
                timeElapsed = String.valueOf(agoHours) + " hours ago";
            } else {
                timeElapsed = String.valueOf(agoMinutes) + " minutes ago";
            }
            timeElapsedTxt.setText(timeElapsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        comments.setText(mComments.get(i).getComment());

        String avatarUri = mComments.get(i).getAvatar();

        Picasso.with(null)
                .load(R.drawable.user_avatar_round)
                .into(avatar);
        return rowView;

    }
}
