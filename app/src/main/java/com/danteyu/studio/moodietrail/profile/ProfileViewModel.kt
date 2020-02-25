package com.danteyu.studio.moodietrail.profile

import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.component.ProfileAvatarOutlineProvider
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.login.UserManager


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [ProfileFragment].
 */

class ProfileViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    val userName = UserManager.name
    val userPic = UserManager.picture

    val outlineProvider = ProfileAvatarOutlineProvider()
}