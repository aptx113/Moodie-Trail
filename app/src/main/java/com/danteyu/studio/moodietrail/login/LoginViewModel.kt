package com.danteyu.studio.moodietrail.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.data.User
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getString
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by George Yu on 2020/2/13.
 */

class LoginViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    var fbCallbackManager: CallbackManager? = null
    var loginManager: LoginManager? = null

    // Handle navigation to login success
    private val _navigateToLoginSuccess = MutableLiveData<User>()

    val navigateToLoginSuccess: LiveData<User>
        get() = _navigateToLoginSuccess

    // Handle leave login
    private val _loginFacebook = MutableLiveData<Boolean>()

    val loginFacebook: LiveData<Boolean>
        get() = _loginFacebook

    private val _loginGoogle = MutableLiveData<Boolean>()

    val loginGoogle: LiveData<Boolean>
        get() = _loginGoogle

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    var googleSignInClient: GoogleSignInClient? = null


    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")
    }

    fun checkUser(uid: String) {
        getUserProfile(uid)

    }

// Check whether user had registered, if not than call sign up
    private fun getUserProfile(uid: String) {
        _status.value = LoadApiStatus.LOADING

        coroutineScope.launch {

            when (val result = moodieTrailRepository.getUserProfile(uid)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    if (result.data.id == uid) {
                        UserManager.id = uid
                        _user.value = result.data
                    }
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    signUpUserProfile(
                        User(
                            name = UserManager.name!!,
                            picture = UserManager.picture!!,
                            email = UserManager.mail!!
                        ), uid
                    )
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR

                }
                else -> {
                    _error.value =
                        MoodieTrailApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR

                }
            }
        }
    }

    private fun signUpUserProfile(userData: User, id: String) {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {

            when (val result = moodieTrailRepository.signUpUser(userData, id)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    _user.value = userData
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR

                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR

                }
                else -> {
                    _error.value =
                        MoodieTrailApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR

                }
            }
        }
    }

    fun navigateToLoginSuccess(user: User) {
        _navigateToLoginSuccess.value = user
    }

    fun onLoginSuccessNavigated() {
        _navigateToLoginSuccess.value = null
    }

    fun loginGoogle() {
        _loginGoogle.value = true
    }

    fun onLoginGoogleCompleted() {
        _loginGoogle.value = null
    }


    companion object {
        private lateinit var auth: FirebaseAuth
        private var currentUser: FirebaseUser? = null
        private val logInWithPermissionsEmail = "email"
        private val loginWithPermissionProfile = "public_profile"
        const val RC_SIGN_IN = 0x01
    }
}