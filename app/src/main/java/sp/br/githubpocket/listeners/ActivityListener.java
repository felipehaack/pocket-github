package sp.br.githubpocket.listeners;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import sp.br.githubpocket.interfaces.ActivityInterface;
import sp.br.githubpocket.R;
import sp.br.githubpocket.utils.Utils;

public abstract class ActivityListener implements ActivityInterface {

    Activity activity;
    Utils utils = new Utils();

    private Resources resources;
    private RecyclerView recyclerView;
    private ImageView imageViewLoader;
    private TextView textViewInformation;

    public Boolean direction = false;
    private Boolean scrollEndReached = false;
    private Boolean detectingTranslateX = true;
    public Boolean enableTranslateX = false;
    private Boolean animationStill = false;
    private Boolean contentSeeMoreVisibility = false;

    float limitX = 17f;
    float limitY = 7f;

    private float startX;
    private float startY;

    private float moveX = 0f;
    private float moveY = 0f;

    public float translateAcumX = 0f;
    private float translateAcumY = 0f;

    public float translateMaxX = 0f;
    private float translateMaxY = 0f;

    int animationDuration = 500;

    private Runnable animationEndX = new Runnable() {
        @Override
        public void run() {

            animationStill = false;
            enableTranslateX = false;

            recyclerView.setTranslationX(translateAcumX);
        }
    };

    private Runnable animationEndY = new Runnable() {
        @Override
        public void run() {

            translateAcumY = 0f;
            contentSeeMoreVisibility = animationStill = scrollEndReached = false;

            recyclerView.setTranslationY(translateAcumY);
            textViewInformation.setText(resources.getString(R.string.main_see_more_text_default));

            activity.findViewById(R.id.content_see_more).setVisibility(View.GONE);
        }
    };

    public ActivityListener(Activity activity, RecyclerView recyclerView, ImageView imageViewLoader, TextView textViewInformation) {

        this.activity = activity;
        this.recyclerView = recyclerView;
        this.imageViewLoader = imageViewLoader;
        this.textViewInformation = textViewInformation;
        this.resources = recyclerView.getResources();

        this.translateMaxY = utils.getConvertDpToPixel(activity, 120);
        this.translateMaxX = utils.getScreenWidth(activity);

        initialize();
    }

    public void initialize(){

        executeEndAnimationX(translateMaxX);
    }

    public void clearSpinnerWithFail() {

        imageViewLoader.clearAnimation();
        textViewInformation.setText(resources.getString(R.string.main_see_more_text_fail));

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {

                executeEndAnimation();
            }
        }, 1000);
    }

    public void clearSpinnerAnimation() {

        executeEndAnimation();

        imageViewLoader.clearAnimation();
        textViewInformation.setText(resources.getString(R.string.main_see_more_text_end));
    }

    public void executeStartAnimation() {

        animationStill = true;

        imageViewLoader.startAnimation(AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.main_spinner_rotate));
        textViewInformation.setText(resources.getString(R.string.main_see_more_text_start));

        infiniteScrollDetected();
    }

    private void executeEndAnimation() {

        animationStill = true;

        recyclerView.animate().y(0f).setDuration(animationDuration).withEndAction(animationEndY);
    }

    public void executeEndAnimationX(float translateX) {

        recyclerView.animate().x(translateX).setDuration(animationDuration).withEndAction(animationEndX);

        translateAcumX = translateX;
    }

    public Boolean motionEventMove() {

        if (!animationStill) {

            if (!scrollEndReached && detectingTranslateX) {

                float distX = startX > moveX ? startX - moveX : moveX - startX;
                float distY = startY > moveY ? startY - moveY : moveY - startY;

                if (distX > limitX) {

                    enableTranslateX = true;
                    detectingTranslateX = false;
                }

                if (distY > limitY) {

                    detectingTranslateX = false;
                }
            }

            if (enableTranslateX) {

                float aux = translateAcumX + (moveX - startX);

                if (aux > 0 && aux < translateMaxX) {

                    translateAcumX = aux;

                    recyclerView.setTranslationX(translateAcumX);
                }

                if (startX > moveX)
                    direction = true;
                else
                    direction = false;

                startX = moveX;

                return true;
            }

            if (scrollEndReached) {

                if(!contentSeeMoreVisibility) {

                    activity.findViewById(R.id.content_see_more).setVisibility(View.VISIBLE);

                    contentSeeMoreVisibility = true;
                }

                if (moveY <= startY) {

                    if (translateAcumY <= translateMaxY) {

                        translateAcumY += (startY - moveY);

                        recyclerView.setTranslationY(-translateAcumY);

                        startY = moveY;

                        return true;
                    } else {

                        executeStartAnimation();
                    }
                } else {

                    if (translateAcumY > 0f)
                        executeEndAnimation();
                }
            }
        }

        return false;
    }

    public void motionEventEnd() {

        if (scrollEndReached && !animationStill)
            executeEndAnimation();

        if (enableTranslateX && translateAcumX > 0f && translateAcumX < translateMaxX) {

            animationStill = true;

            if (direction)
                executeEndAnimationX(0f);
            else
                executeEndAnimationX(translateMaxX);
        }
    }

    public Boolean motionEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: {

                if (event.getPointerCount() == 1) {

                    detectingTranslateX = true;
                    enableTranslateX = false;
                }

                startX = event.getX(0);
                startY = event.getY(0);

                break;
            }

            case MotionEvent.ACTION_MOVE: {

                moveX = event.getX(0);
                moveY = event.getY(0);

                return motionEventMove();
            }

            case MotionEvent.ACTION_UP: {

            }

            case MotionEvent.ACTION_CANCEL: {

                motionEventEnd();

                break;
            }
        }

        return false;
    }

    public void onConfigurationChanged(){

        int newWidth = utils.getScreenWidth(activity);

        if(newWidth != translateMaxX){

            translateMaxX = newWidth;

            if(translateAcumX > 0f)
                executeEndAnimationX(translateMaxX);
        }
    }

    public void setStartDetect(Boolean status) {

        this.startY = moveY;
        this.scrollEndReached = status;
    }
}
