package eu.marcocattaneo.rememberhere.presentation.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class CoverImageView extends ImageView {
    public CoverImageView(Context context) {
        super(context);

        init();
    }

    public CoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public CoverImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CoverImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
    }

    public void setCover(final String url, final boolean fade) {

        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);

                Uri uri = Uri.parse(url);
                Picasso.with(getContext()).load(uri).resize(getMeasuredWidth(), getMeasuredHeight()).centerCrop().into(CoverImageView.this);

                return true;
            }

        });

    }

}
