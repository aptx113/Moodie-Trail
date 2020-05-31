package com.danteyu.studio.moodietrail.ext

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.danteyu.studio.moodietrail.R

/**
 * Created by George Yu on 2020/5/31.
 */
fun NavController.navigateForward(
    resId: Int,
    args: Bundle? = null,
    isSingleTop: Boolean = true,
    useDefaultAnim: Boolean = false
) {
    if (isSingleTop && this.currentDestination?.id == resId) return navigate(
        resId,
        args,
        if (useDefaultAnim) NavOptions.Builder().setEnterAnim(R.anim.slide_left_enter)
            .setExitAnim(R.anim.slide_left_exit).setPopEnterAnim(R.anim.slide_right_enter)
            .setPopExitAnim(R.anim.slide_right_exit).build() else null
    )
}