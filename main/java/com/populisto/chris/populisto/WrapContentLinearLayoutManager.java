package com.populisto.chris.populisto;
//VERY IMPORTANT
//implemented to stop the app randomly crashing on searching
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class WrapContentLinearLayoutManager extends LinearLayoutManager {
  public WrapContentLinearLayoutManager(Context context) {
    super(context);
  }

  public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
    super(context, orientation, reverseLayout);
  }

  public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    try {
      super.onLayoutChildren(recycler, state);
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
  }
}