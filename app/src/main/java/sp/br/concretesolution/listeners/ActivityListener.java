package sp.br.concretesolution.listeners;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import sp.br.concretesolution.interfaces.ActivityInterface;
import sp.br.concretesolution.R;

public abstract class ActivityListener implements ActivityInterface {

    private Resources resources;
    private RecyclerView recyclerView;
    private ImageView imageViewLoader;
    private TextView textViewInformation;

    private Boolean startDetect = false;

    private float translateY = 0f;
    private float translateAcum = 0f;
    private float translateMax = 0f;

    int animationDuration = 500;
    private Boolean animationStill = false;
    private Runnable animationEnd = new Runnable() {
        @Override
        public void run() {

            animationStill = startDetect = false;
            translateAcum = 0f;

            recyclerView.setTranslationY(0f);
            textViewInformation.setText(resources.getString(R.string.main_see_more_text_default));
        }
    };

    public ActivityListener(RecyclerView recyclerView, ImageView imageViewLoader, TextView textViewInformation) {

        this.recyclerView = recyclerView;
        this.imageViewLoader = imageViewLoader;
        this.textViewInformation = textViewInformation;
        this.resources = recyclerView.getResources();

        this.translateMax = convertDpToPixel(120);
    }

    private float convertDpToPixel(int dp) {

        DisplayMetrics displayMetrics = recyclerView.getContext().getResources().getDisplayMetrics();
        return (float) ((dp * displayMetrics.density) + 0.5);
    }

    public void clearSpinnerAnimationWithFail() {

        imageViewLoader.clearAnimation();
        textViewInformation.setText(resources.getString(R.string.main_see_more_text_fail));

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {

                executeAnimation();
            }
        }, 1000);
    }

    public void clearSpinnerAnimation() {

        executeAnimation();

        imageViewLoader.clearAnimation();
        textViewInformation.setText(resources.getString(R.string.main_see_more_text_end));
    }

    public void executeSpinnerAnimation() {

        animationStill = true;

        imageViewLoader.startAnimation(AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.main_spinner_rotate));
        textViewInformation.setText(resources.getString(R.string.main_see_more_text_start));

        infiniteScrollDetected();
    }

    private void executeAnimation() {

        animationStill = true;

        recyclerView.animate().y(0f).setDuration(animationDuration).withEndAction(animationEnd);
    }

    public Boolean detectInfiniteScroll(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: {

                translateY = event.getY();

                break;
            }

            case MotionEvent.ACTION_MOVE: {

                if (startDetect && !animationStill) {

                    float currentY = event.getY();

                    if (currentY <= translateY) {

                        if (translateAcum <= translateMax) {

                            translateAcum += (translateY - currentY);

                            recyclerView.setTranslationY(-translateAcum);

                            translateY = currentY;
                        } else {

                            executeSpinnerAnimation();
                        }

                        return true;
                    } else {

                        if (translateAcum > 0f)
                            executeAnimation();

                        return false;
                    }
                } else {

                    translateY = event.getY();
                }

                break;
            }

            case MotionEvent.ACTION_UP: {

            }

            case MotionEvent.ACTION_CANCEL: {

                if (startDetect && !animationStill)
                    executeAnimation();

                break;
            }
        }

        return false;
    }

    public void setStartDetect(Boolean status) {

        this.startDetect = status;
    }
}
