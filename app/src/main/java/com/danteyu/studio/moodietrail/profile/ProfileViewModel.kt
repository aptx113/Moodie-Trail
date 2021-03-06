package com.danteyu.studio.moodietrail.profile

import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.component.ProfileAvatarOutlineProvider
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.login.UserManager
import com.danteyu.studio.moodietrail.util.Logger


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [ProfileFragment].
 */

class ProfileViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    val userName = UserManager.name
    val userPic = UserManager.picture

    val outlineProvider = ProfileAvatarOutlineProvider()

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

    }
}