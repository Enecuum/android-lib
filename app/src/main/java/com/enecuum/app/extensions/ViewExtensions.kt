package com.enecuum.app.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.enecuum.app.R
import com.enecuum.app.utils.SafeClickListener

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.copyToClipboard(text: String) {
    val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText("text", text)
    clipboard?.setPrimaryClip(clip)
}

fun View.showToast(resId: Int) = Toast.makeText(context, resId, Toast.LENGTH_LONG).show()

fun View.setOnSafeClickListener(interval: Int = 1000, onSafeClick: (View) -> Unit) {
    setOnClickListener(SafeClickListener(interval) { v -> onSafeClick(v) })
}

//awkward workaround for double-tap crashes
fun View.safeNavigate(actionId: Int) {
    val navController: NavController = Navigation.findNavController(this)
    val action = navController.currentDestination?.getAction(actionId)
    if (action != null) navController.navigate(actionId)
}

fun ConstraintLayout.addDropShadow(view: View) {
    view.post {
        val shadow = ImageView(context)
        shadow.id = View.generateViewId()
        shadow.layoutParams = ConstraintLayout.LayoutParams(
            (view.width.toFloat() * 1.3).toInt(),
            (view.height.toFloat() * 3.5).toInt()
        )
        shadow.setImageResource(R.drawable.button_shadow)
        addView(shadow, 0)

        val set = ConstraintSet()
        set.clone(this)
        set.connect(
            shadow.id,
            ConstraintSet.TOP,
            view.id,
            ConstraintSet.TOP,
            resources.getDimension(R.dimen.button_shadow_margin).toInt()
        )
        set.connect(shadow.id, ConstraintSet.BOTTOM, view.id, ConstraintSet.BOTTOM, 0)
        set.connect(shadow.id, ConstraintSet.START, view.id, ConstraintSet.START, 0)
        set.connect(shadow.id, ConstraintSet.END, view.id, ConstraintSet.END, 0)
        set.applyTo(this)
    }
}

fun ViewGroup.showProgress(fade: Boolean = true) {
    val layout = if (fade) {
        R.layout.view_progress_overlay
    } else {
        R.layout.view_progress_overlay_transparent
    }
    val progress = LayoutInflater.from(context).inflate(layout, null) ?: return
    val params: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    progress.tag = PROGRESS_OVERLAY_TAG
    addView(progress, childCount, params)
}

fun ViewGroup.hideProgress() {
    findViewWithTag<View>(PROGRESS_OVERLAY_TAG)?.let {
        removeViewInLayout(it)
    }
}

fun ViewGroup.progressIsShown(): Boolean =
    findViewWithTag<View>(PROGRESS_OVERLAY_TAG)?.isVisible ?: false

const val PROGRESS_OVERLAY_TAG = "888"