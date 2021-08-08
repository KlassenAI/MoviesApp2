package com.android.moviesapp.callback

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.moviesapp.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class SwipeCallback(
    val context: Context, dragDirs: Int, swipeDirs: Int
) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    val background = if (context.isDarkThemeOn())
        ContextCompat.getDrawable(context, R.drawable.card_background_dark)!!
    else ContextCompat.getDrawable(context, R.drawable.card_background_light)!!

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
        val itemView = viewHolder.itemView

        when {
            dX > 0 -> {
                background.setBounds(
                    itemView.left, itemView.top, itemView.left + 16 + dX.toInt(), itemView.bottom
                )
            }
            dX < 0 -> {
                background.setBounds(
                    itemView.right - 16 + dX.toInt(), itemView.top, itemView.right, itemView.bottom
                )
            }
            else -> {
                background.setBounds(0, 0, 0, 0)
            }
        }

        background.draw(c)

        RecyclerViewSwipeDecorator.Builder(
            c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
        ).run {
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