package com.tangem.tap.common.extensions

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import timber.log.Timber

/**
 * Created by Anton Zhilenkov on 08/09/2020.
 */
fun ViewGroup.inflate(viewToInflate: Int, rootView: ViewGroup?, parent: ViewGroup) {
    if (rootView == null) {
        val inflatedView = LayoutInflater.from(context).inflate(viewToInflate, rootView)
        parent.addView(inflatedView)
    }
}

fun ViewParent?.beginDelayedTransition(transition: Transition = AutoTransition()) {
    if (this == null) Timber.e("Can't invoke beginDelayedTransition, because parent is NULL")
    (this as? ViewGroup)?.beginDelayedTransition(transition)
}

fun ViewGroup.beginDelayedTransition(transition: Transition = AutoTransition()) {
    TransitionManager.beginDelayedTransition(this, transition)
}