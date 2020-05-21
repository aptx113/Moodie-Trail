package com.danteyu.studio.moodietrail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.data.model.User
import com.danteyu.studio.moodietrail.repository.MoodieTrailRepository
import com.danteyu.studio.moodietrail.ui.login.UserManager
import com.danteyu.studio.moodietrail.util.CurrentFragmentType
import com.danteyu.studio.moodietrail.util.Logger


/**
 * Created by George Yu in Jan. 2020.
 *
 * The [ViewModel] that is attached to the [MainActivity].
 */
class MainViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    // Handle navigation to login success
    private val _navigateToLoginSuccess = MutableLiveData<User>()

    val navigateToLoginSuccess: LiveData<User>
        get() = _navigateToLoginSuccess

    private val _navigateToHomeByBottomNav = MutableLiveData<Boolean>()

    val navigateToHomeByBottomNav: LiveData<Boolean>
        get() = _navigateToHomeByBottomNav

    //Handle Fab open and close
    private val _isFabOpen = MutableLiveData<Boolean>()

    val isFabOpen: LiveData<Boolean>
        get() = _isFabOpen

    // check user login status
    val isLoggedIn
        get() = UserManager.isLoggedIn

    // Handle navigation to record mood
    private val _navigateToRecordMood = MutableLiveData<Boolean>()

    val navigateToRecordMood: LiveData<Boolean>
        get() = _navigateToRecordMood

    private val _navigateToPsyTest = MutableLiveData<Boolean>()

    val navigateToPsyTest: LiveData<Boolean>
        get() = _navigateToPsyTest

    private val _backToPsyTest = MutableLiveData<Boolean>()

    val backToPsyTest: LiveData<Boolean>
        get() = _backToPsyTest

    private val _navigateToPsyTestRecordByBottomNav = MutableLiveData<Boolean>()

    val navigateToPsyTestRecordByBottomNav: LiveData<Boolean>
        get() = _navigateToPsyTestRecordByBottomNav

    private val _navigateToConsultationCall = MutableLiveData<Boolean>()

    val navigateToConsultationCall: LiveData<Boolean>
        get() = _navigateToConsultationCall

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

    }

    fun setupUser(user: User) {

        _user.value = user
        Logger.i("=============")
        Logger.i("| setupUser |")
        Logger.i("user=$user")
        Logger.i("MainViewModel=${this}")
        Logger.i("=============")
    }

    //Change Fab status when is pressed
    fun onFabPressed() {
        _isFabOpen.value = !(_isFabOpen.value ?: false)
    }

    fun closeFabByBottomNav() {
        _isFabOpen.value = false
    }

    fun navigateToRecordMood() {
        _navigateToRecordMood.value = true
        _isFabOpen.value = false
    }

    fun onRecordMoodNavigated() {
        _navigateToRecordMood.value = null
    }

    fun navigateToPsyTest() {
        _navigateToPsyTest.value = true
        _isFabOpen.value = false
    }

    fun onPsyTestNavigated() {
        _navigateToPsyTest.value = null
    }

    fun navigateToLoginSuccess(user: User) {
        _navigateToLoginSuccess.value = user
    }

    fun onLoginSuccessNavigated() {
        _navigateToLoginSuccess.value = null
    }

    fun navigateToHomeByBottomNav() {
        _navigateToHomeByBottomNav.value = true
    }

    fun onHomeNavigated() {
        _navigateToHomeByBottomNav.value = null
    }

    fun backToPsyTest() {
        _backToPsyTest.value = true
    }

    fun onPsyTestBacked() {
        _backToPsyTest.value = null
    }

    fun navigateToPsyTestRecordByBottomNav() {
        _navigateToPsyTestRecordByBottomNav.value = true
    }

    fun onPsyTestRecordNavigated() {
        _navigateToPsyTestRecordByBottomNav.value = null
    }

    fun navigateToConsultationCall() {
        _navigateToConsultationCall.value = true
    }

    fun onConsultationCallNavigated() {
        _navigateToConsultationCall.value = null
    }

}