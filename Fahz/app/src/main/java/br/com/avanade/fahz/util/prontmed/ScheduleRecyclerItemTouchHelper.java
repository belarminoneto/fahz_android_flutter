package br.com.avanade.fahz.util.prontmed;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import br.com.avanade.fahz.Adapter.prontmed.ProntMedScheduleAdapter;

public class ScheduleRecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener listener;

    public ScheduleRecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((ProntMedScheduleAdapter.ScheduleItemViewHolder) viewHolder).viewForeground;

            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        if (dX == 0 && dY == 0 && isCurrentlyActive == false) {
            final View viewBackground = ((ProntMedScheduleAdapter.ScheduleItemViewHolder) viewHolder).viewBackground;
            viewBackground.setVisibility(View.INVISIBLE);
        }
        final View foregroundView = ((ProntMedScheduleAdapter.ScheduleItemViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((ProntMedScheduleAdapter.ScheduleItemViewHolder) viewHolder).viewForeground;
        final View backgroundView = ((ProntMedScheduleAdapter.ScheduleItemViewHolder) viewHolder).viewBackground;
        if (backgroundView.getVisibility() == View.VISIBLE) {
            backgroundView.setVisibility(View.INVISIBLE);
        }

        getDefaultUIUtil().clearView(backgroundView);
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View viewBackground = ((ProntMedScheduleAdapter.ScheduleItemViewHolder) viewHolder).viewBackground;
        if (viewBackground.getVisibility() == View.INVISIBLE)
            viewBackground.setVisibility(View.VISIBLE);

        final View foregroundView = ((ProntMedScheduleAdapter.ScheduleItemViewHolder) viewHolder).viewForeground;

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (ItemTouchHelper.LEFT == direction) {
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
        else {
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
