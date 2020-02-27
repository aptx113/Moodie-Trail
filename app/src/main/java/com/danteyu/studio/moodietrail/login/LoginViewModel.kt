package com.danteyu.studio.moodietrail.login

import android.os.Bundle
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
import com.danteyu.studio.moodietrail.util.Util.getAuth
import com.danteyu.studio.moodietrail.util.Util.getString
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.IOException

/**
 * Created by George Yu on 2020/2/13.
 */

class LoginViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    lateinit var fbCallbackManager: CallbackManager

    lateinit var accessToken: AccessToken

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

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

    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _statusForGoogle = MutableLiveData<LoadApiStatus>()

    val statusForGoogle: LiveData<LoadApiStatus>
        get() = _statusForGoogle

    private val _statusForFb = MutableLiveData<LoadApiStatus>()

    val statusForFb: LiveData<LoadApiStatus>
        get() = _statusForFb

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

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
                        _navigateToLoginSuccess.value = user.value
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
                    _navigateToLoginSuccess.value = user.value
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

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        coroutineScope.launch {

            _statusForGoogle.value = LoadApiStatus.LOADING

            when (val result = moodieTrailRepository.firebaseAuthWithGoogle(acct)) {
                is Result.Success -> {
                    _error.value = null
                    _statusForGoogle.value = LoadApiStatus.DONE
                    val user = getAuth().currentUser
                    user?.let {
                        UserManager.id = it.uid
                        checkUser(it.uid)
                    }
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _statusForGoogle.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _statusForGoogle.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    _statusForGoogle.value = LoadApiStatus.ERROR
                }
            }
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        coroutineScope.launch {

            _statusForFb.value = LoadApiStatus.LOADING

            when (val result = moodieTrailRepository.handleFacebookAccessToken(token)) {
                is Result.Success -> {
                    _error.value = null
                    _statusForFb.value = LoadApiStatus.DONE
                    val user = getAuth().currentUser
                    user?.let {
                        UserManager.id = it.uid
                        checkUser(it.uid)
                    }
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _statusForFb.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _statusForFb.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value = getString(R.string.you_know_nothing)
                    _statusForFb.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    /**
     * Login  by Facebook: Step 1. Register FB Login Callback
     */
    fun login() {
        _statusForFb.value = LoadApiStatus.LOADING

        fbCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(fbCallbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                accessToken = loginResult.accessToken //save fbToken

                handleFacebookAccessToken(loginResult.accessToken)
                val graphRequest = GraphRequest.newMeRequest(
                    loginResult.accessToken
                ) { `object`, response ->
                    try {
                        if (response.connection.responseCode == 200) {
//                            handleFacebookAccessToken(loginResult.accessToken)
                            UserManager.userToken = `object`.getLong("id").toString()
                            UserManager.name = `object`.getString("name")
                            UserManager.mail = `object`.getString("email")
                            Profile.getCurrentProfile()?.let {
                                UserManager.picture = it.getProfilePictureUri(300, 300).toString()
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Logger.w("$e")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Logger.w("$e")
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email")
                graphRequest.parameters = parameters
                graphRequest.executeAsync()

            }

            override fun onCancel() {
                _statusForFb.value = LoadApiStatus.ERROR
            }

            override fun onError(exception: FacebookException) {
                Logger.w("[${this::class.simpleName}] exception=${exception.message}")

                exception.message?.let {
                    _error.value = if (it.contains("ERR_INTERNET_DISCONNECTED")) {
                        getString(R.string.internet_not_connected)
                    } else {
                        it
                    }
                }
                _statusForFb.value = LoadApiStatus.ERROR
            }
        })
        loginFacebook()
    }

    fun cancelLoginGoogle() {
        _statusForGoogle.value = LoadApiStatus.ERROR
    }

    fun loginGoogle() {
        _loginGoogle.value = true
        _statusForGoogle.value = LoadApiStatus.LOADING
    }

    fun onLoginGoogleCompleted() {
        _loginGoogle.value = null
    }

    private fun loginFacebook() {
        _loginFacebook.value = true
    }

    fun onLoginFacebookCompleted() {
        _loginFacebook.value = null
    }


    companion object {
        private val logInWithPermissionsEmail = "email"
        private val loginWithPermissionProfile = "public_profile"
        const val RC_SIGN_IN = 0x01
    }
}