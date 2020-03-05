package com.danteyu.studio.moodietrail.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.MainViewModel
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentLoginBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.showToast
import com.danteyu.studio.moodietrail.login.LoginViewModel.Companion.LOGIN_WITH_PERMISSIONS_EMAIL
import com.danteyu.studio.moodietrail.login.LoginViewModel.Companion.LOGIN_WITH_PERMISSION_PROFILE
import com.danteyu.studio.moodietrail.login.LoginViewModel.Companion.RC_SIGN_IN
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getGoogleSignInClient
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class LoginFragment : Fragment() {

    val viewModel by viewModels<LoginViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val mainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)

        if (UserManager.userToken!!.isNotEmpty()) {
            findNavController().navigate(NavigationDirections.navigateToHomeFragment())

        } else {

            viewModel.loginGoogle.observe(viewLifecycleOwner, Observer {
                it?.let {

                    context?.let {
                        signInGoogle()
                    }
                    viewModel.onLoginGoogleCompleted()
                }
            })

            viewModel.loginFacebook.observe(viewLifecycleOwner, Observer {
                it?.let {
                    LoginManager.getInstance().logInWithReadPermissions(
                        this, listOf(
                            LOGIN_WITH_PERMISSIONS_EMAIL, LOGIN_WITH_PERMISSION_PROFILE
                        )
                    )
                    viewModel.onLoginFacebookCompleted()
                }
            })

            viewModel.user.observe(viewLifecycleOwner, Observer {
                it?.let {
                    mainViewModel.setupUser(it)
                }
            })

            viewModel.navigateToLoginSuccess.observe(viewLifecycleOwner, Observer {
                it?.let {
                    mainViewModel.navigateToLoginSuccess(it)

                }
            })

        }
        return binding.root
    }

    private fun signInGoogle() {

        val signInIntent = getGoogleSignInClient().signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                account?.let {
                    UserManager.userToken = it.idToken
                    UserManager.name = it.displayName
                    UserManager.picture = it.photoUrl.toString()
                    UserManager.email = it.email

                    viewModel.firebaseAuthWithGoogle(it)
                    Logger.i("ServerAuthCode =${it.serverAuthCode} account.id =${it.id}")
                }
            } catch (e: ApiException) {
                viewModel.cancelLoginGoogle()
                activity.showToast(getString(R.string.login_fail_toast))

                Logger.w("Google sign in failed, ApiException = $e")

            }
        } else {
            viewModel.fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

}