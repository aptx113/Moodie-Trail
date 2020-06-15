package com.danteyu.studio.moodietrail.component

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R

/**
 * Created by George Yu on 2020/2/25.
 */
class ProfileAvatarOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        view.clipToOutline = true
        val radius = MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.radius_profile_avatar)
        outline.setOval(0, 0, radius, radius)
    }
}
