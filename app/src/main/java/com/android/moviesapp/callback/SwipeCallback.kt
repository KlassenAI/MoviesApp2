package com.android.moviesapp.callback

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.moviesapp.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class SwipeCallback(
    val context: Context, dragDirs: Int, swipeDirs: Int
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private val background = getCardBackground(context)

    private fun getCardBackground(context: Context) =
        if (context.isDarkThemeOn()) {
            ContextCompat.getDrawable(context, R.drawable.card_background_dark)!!
        } else {
            ContextCompat.getDrawable(context, R.drawable.card_background_light)!!
        }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        with(background) {
            setBounds(dX, viewHolder.itemView)
            draw(c)
        }

        createSwipeDecorator(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun Drawable.setBounds(dX: Float, itemView: View) = when {
        dX > 0 -> with(itemView) {
            this@setBounds.setBounds(left, top, left + 16 + dX.toInt(), bottom)
        }
        dX < 0 -> with(itemView) {
            this@setBounds.setBounds(right - 16 + dX.toInt(), top, right, bottom)
        }
        else -> {
            this.setBounds(0, 0, 0, 0)
        }
    }

    private fun createSwipeDecorator(
        c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(
            c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
        ).apply {
            addSwipeRightActionIcon(R.drawable.ic_heart_fill_24)
            addSwipeLeftActionIcon(R.drawable.ic_delete)
            create().decorate()
        }
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    private fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }
}
