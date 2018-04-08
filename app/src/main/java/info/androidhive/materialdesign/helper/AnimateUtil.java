package info.androidhive.materialdesign.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.androidhive.materialdesign.adapter.NotifyAdapter;

/**
 * Created by HP ENVY on 5/25/2017.
 */

public class AnimateUtil {
    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = toVisibility == View.VISIBLE;
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }

    public static void animateTextViewHeight(final NotifyAdapter Na, final TextView view, final int newHeight, float toAlpha, int duration) {
        boolean show = newHeight == view.getHeight();
//        if (show) {
            view.setAlpha(0);
//        }
        view.setHeight(newHeight);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewGroup.LayoutParams params = view.getLayoutParams();

                        if(params.height == ViewGroup.LayoutParams.WRAP_CONTENT){
//                            params.height = 70;
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        }else{
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        }
//                }
                view.setLayoutParams(params);
//                        view.setHeight(newHeight);
                        Na.notifyDataSetChanged();
                    }
                });
    }


}
