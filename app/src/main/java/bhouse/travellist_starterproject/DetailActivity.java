package bhouse.travellist_starterproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.transition.Transition;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DetailActivity extends Activity implements View.OnClickListener {

  public static final String EXTRA_PARAM_ID = "place_id";
  private ListView mList;
  private ImageView mImageView;
  private TextView mTitle;
  private LinearLayout mTitleHolder;
  private ImageButton mAddButton;
  private LinearLayout mRevealView;
  private EditText mEditTextTodo;
  private boolean isEditTextVisible;
  private InputMethodManager mInputManager;
  private Place mPlace;
  private ArrayList<String> mTodoList;
  private ArrayAdapter mToDoAdapter;
  int defaultColor;

    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mList = (ListView) findViewById(R.id.list);

        try {
            SQLiteOpenHelper DatabaseHelper = new DatabaseHelper(this);
            db = DatabaseHelper.getReadableDatabase();
            cursor = db.query("DRINK",
                    new String[]{"_id", "NAME"},
                    null, null, null, null, null);


            CursorAdapter listAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"NAME"},
                    new int[]{android.R.id.text1},
                    0);

            mList.setAdapter(listAdapter);

            Toast.makeText(this, "Did not break", Toast.LENGTH_SHORT).show();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT).show();
        }

    mPlace = PlaceData.placeList().get(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));
    mImageView = (ImageView) findViewById(R.id.placeImage);
    mTitle = (TextView) findViewById(R.id.textView);
    mTitleHolder = (LinearLayout) findViewById(R.id.placeNameHolder);
    mAddButton = (ImageButton) findViewById(R.id.btn_add);
    mRevealView = (LinearLayout) findViewById(R.id.llEditTextHolder);
    mEditTextTodo = (EditText) findViewById(R.id.etTodo);
    mAddButton.setOnClickListener(this);
    defaultColor = getResources().getColor(R.color.primary_dark);
    mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    mRevealView.setVisibility(View.INVISIBLE);
    isEditTextVisible = false;

    loadPlace();
    windowTransition();
    getPhoto();
  }

  private void loadPlace() {
    mTitle.setText(mPlace.name);
    mImageView.setImageResource(mPlace.getImageResourceId(this));
  }

  private void windowTransition() {
      getWindow().getEnterTransition().addListener(new TransitionAdapter() {
          @Override
          public void onTransitionEnd(Transition transition) {
              mAddButton.animate().alpha(1.0f);
              getWindow().getEnterTransition().removeListener(this);
          }
      });
  }

  private void addToDo(String todo) {
    mTodoList.add(todo);
  }

  private void getPhoto() {
      Bitmap photo = BitmapFactory.decodeResource(getResources(), mPlace.getImageResourceId(this));
      colorize(photo);
  }

  private void colorize(Bitmap photo) {
      Palette mPalette = Palette.generate(photo);
      applyPalette(mPalette);
  }

    private void applyPalette(Palette mPalette) {
        getWindow().setBackgroundDrawable(new ColorDrawable(mPalette.getDarkMutedColor(defaultColor)));
        mTitleHolder.setBackgroundColor(mPalette.getMutedColor(defaultColor));
        mRevealView.setBackgroundColor(mPalette.getLightVibrantColor(defaultColor));
    }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_add:
          Animatable mAnimatable;
          if (!isEditTextVisible) {
              revealEditText(mRevealView);
              mEditTextTodo.requestFocus();
              mInputManager.showSoftInput(mEditTextTodo, InputMethodManager.SHOW_IMPLICIT);
              mAddButton.setImageResource(R.drawable.icn_morph);
              mAnimatable = (Animatable) (mAddButton).getDrawable();
              mAnimatable.start();
          } else {
              addToDo(mEditTextTodo.getText().toString());
              mToDoAdapter.notifyDataSetChanged();
              mInputManager.hideSoftInputFromWindow(mEditTextTodo.getWindowToken(), 0);
              hideEditText(mRevealView);
              mAddButton.setImageResource(R.drawable.icon_morph_reverse);
              mAnimatable = (Animatable) (mAddButton).getDrawable();
              mAnimatable.start();
        }
    }
  }

  private void revealEditText(LinearLayout view) {
    int cx = view.getRight() - 30;
    int cy = view.getBottom() - 60;
    int finalRadius = Math.max(view.getWidth(), view.getHeight());
    Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
    view.setVisibility(View.VISIBLE);
    isEditTextVisible = true;
    anim.start();
  }

  private void hideEditText(final LinearLayout view) {
      int cx = view.getRight() - 30;
      int cy = view.getBottom() - 60;
      int initialRadius = view.getWidth();
      Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
      anim.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
              super.onAnimationEnd(animation);
              view.setVisibility(View.INVISIBLE);
          }
      });
      isEditTextVisible = false;
      anim.start();
  }

  @Override
  public void onBackPressed() {
    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
    alphaAnimation.setDuration(100);
    mAddButton.startAnimation(alphaAnimation);
    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        mAddButton.setVisibility(View.GONE);
        finishAfterTransition();
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
  }
}
