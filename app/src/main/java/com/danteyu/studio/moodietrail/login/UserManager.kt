package com.danteyu.studio.moodietrail.login

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.data.User

/**
 * Created by George Yu on 2020/2/13.
 */

object UserManager {


    private const val USER_DATA = "user_data"
    private const val USER_TOKEN = "user_token"
    const val USER_NAME = "name"
    const val USER_ID = "uid"
    const val USER_PHOTO = "pic"
    const val USER_MAIL = "mail"


    private const val FB_DATA = "fb_data"
    private const val FB_TOKEN = "fb_token"

    var prefs: SharedPreferences =
        MoodieTrailApplication.instance.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE)

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    var userToken: String? = null
        get() = prefs.getString(USER_TOKEN, "")
        set(value) {
            field = when (value) {
                null -> {
                    prefs.edit()
                        .remove(USER_TOKEN)
                        .apply()
                    null
                }
                else -> {
                    prefs.edit()
                        .putString(USER_TOKEN, value)
                        .apply()
                    value
                }
            }
        }

    var name: String? = null
        get() = prefs.getString(USER_NAME, "")
        set(value) {
            field = when (value) {
                null -> {
                    prefs.edit()
                        .remove(USER_NAME)
                        .apply()
                    null
                }
                else -> {
                    prefs.edit()
                        .putString(USER_NAME, value)
                        .apply()
                    value
                }
            }
        }

    var picture : String? = null
        get() = prefs.getString(USER_PHOTO, "")
        set(value){
            field = when (value) {
                null -> {
                    prefs.edit()
                        .remove(USER_PHOTO)
                        .apply()
                    null
                }
                else -> {
                    prefs.edit()
                        .putString(USER_PHOTO, value)
                        .apply()
                    value
                }
            }
        }

    var id: String? = null
        get() = prefs.getString(USER_ID, "")
        set(value) {
            field = when (value) {
                null -> {
                    prefs.edit()
                        .remove(USER_ID)
                        .apply()
                    null
                }
                else -> {
                    prefs.edit()
                        .putString(USER_ID, value)
                        .apply()
                    value
                }
            }
        }

    var email: String? = null
        get() = prefs.getString(USER_MAIL, "")
        set(value) {
            field = when (value) {
                null -> {
                    prefs.edit()
                        .remove(USER_MAIL)
                        .apply()
                    null
                }
                else -> {
                    prefs.edit()
                        .putString(USER_MAIL,value )
                        .apply()
                    value
                }
            }
        }

    /**
     * It can be use to check login status directly
     */
    val isLoggedIn: Boolean
        get() = userToken != null

    /**
     * Clear the [userToken] and the [user]/[_user] data
     */
    fun clear() {
        userToken = null
        _user.value = null
    }


}