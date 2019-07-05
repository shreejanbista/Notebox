package in.cipherhub.notebox.Animation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import in.cipherhub.notebox.Utils.ViewUtils;
import in.cipherhub.notebox.R;

/**
 * Created by ArmanSo on 4/16/17.
 */

public class RoundLinearLayoutNormal extends LinearLayout {
    public RoundLinearLayoutNormal(Context context) {
        super(context);
        initBackground();
    }

    public RoundLinearLayoutNormal(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBackground();
    }

    public RoundLinearLayoutNormal(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBackground();
    }

    private void initBackground() {
//        white, radius_corner, shadowColor, elevation, Gravity
        setBackground(ViewUtils.generateBackgroundWithShadow(this, R.color.colorWhite_FFFFFF,
                R.dimen.small_radius_10,R.color.md_blue_grey_100,R.dimen.small_radius_4, Gravity.TOP));
    }
}
