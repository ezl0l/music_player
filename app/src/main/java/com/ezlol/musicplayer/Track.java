package com.ezlol.musicplayer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Track {
    private long id;
    private float duration;
    private String title, author, path;

    public Track(int id, float duration, String title, String author, String path) {
        this.id = id;
        this.duration = duration;
        this.title = title;
        this.author = author;
        this.path = path;
    }

    public View getView(Context c) {
        LinearLayout layout = new LinearLayout(c);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding((int) c.getResources().getDimension(R.dimen.trackLayoutLeftPadding),
                (int) c.getResources().getDimension(R.dimen.trackLayoutTopPadding),
                (int) c.getResources().getDimension(R.dimen.trackLayoutRightPadding),
                (int) c.getResources().getDimension(R.dimen.trackLayoutBottomPadding));

        ImageView image = new ImageView(c);
        image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        image.getLayoutParams().height = (int) c.getResources().getDimension(R.dimen.trackImageHeight);
        image.getLayoutParams().width = (int) c.getResources().getDimension(R.dimen.trackImageWidth);
        image.setImageResource(R.drawable.ic_default_track_album);

        LinearLayout trackNamesLayout = new LinearLayout(c);
        trackNamesLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        trackNamesLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams trackNameAndAuthorParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        trackNameAndAuthorParams.setMargins((int) c.getResources().getDimension(R.dimen.trackNameAndAuthorLeftMargin),
                (int) c.getResources().getDimension(R.dimen.trackNameAndAuthorTopMargin),
                (int) c.getResources().getDimension(R.dimen.trackNameAndAuthorRightMargin),
                (int) c.getResources().getDimension(R.dimen.trackNameAndAuthorBottomMargin));

        TextView trackName = new TextView(c);
        trackName.setLayoutParams(trackNameAndAuthorParams);
        trackName.setTypeface(Typeface.DEFAULT_BOLD);
        trackName.setText(title);

        TextView trackAuthor = new TextView(c);
        trackAuthor.setLayoutParams(trackNameAndAuthorParams);
        trackAuthor.setText(author);

        trackNamesLayout.addView(trackName);
        trackNamesLayout.addView(trackAuthor);

        LinearLayout trackDurationLayout = new LinearLayout(c);
        trackDurationLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        trackDurationLayout.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout trackDurationRelativeLayout = new RelativeLayout(c);
        trackDurationRelativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        TextView trackDuration = new TextView(c);
        LinearLayout.LayoutParams trackDurationLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        trackDurationLayoutParams.setMargins(0, 0, (int) c.getResources().getDimension(R.dimen.trackDurationRightMargin), 0);
        trackDuration.setLayoutParams(trackDurationLayoutParams);
        trackDuration.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        trackDuration.setText(Utils.beautifyTime(Math.round(duration)));

        ImageView cachedTrackImage = new ImageView(c);
        RelativeLayout.LayoutParams cachedTrackImageLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        cachedTrackImageLayoutParams.width = (int) c.getResources().getDimension(R.dimen.cachedTrackImageWidth);
        cachedTrackImageLayoutParams.height = (int) c.getResources().getDimension(R.dimen.cachedTrackImageHeight);
        cachedTrackImageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        cachedTrackImage.setLayoutParams(cachedTrackImageLayoutParams);
        //cachedTrackImage.setImageResource(R.drawable.ic_save_icon);

        trackDurationRelativeLayout.addView(trackDuration);
        trackDurationRelativeLayout.addView(cachedTrackImage);

        trackDurationLayout.addView(trackDurationRelativeLayout);

        layout.addView(image);
        layout.addView(trackNamesLayout);
        layout.addView(trackDurationLayout);

        return layout;
    }

    public long getId() {
        return id;
    }

    public float getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPath() {
        return path;
    }
}
