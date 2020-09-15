package com.davinciapp.amindfullquit.utils

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes

fun Int.toStringThousandsSeparated() = String.format("%,d", this)

fun <T: View> Activity.bind(@IdRes res: Int) : Lazy<T> = lazy { findViewById<T>(res) }

const val millisInDay = 86_400_000L