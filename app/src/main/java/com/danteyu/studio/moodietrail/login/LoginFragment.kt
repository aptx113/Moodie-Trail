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
import com.danteyu.studio.moodietrail.dialog.MessageDialog
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.showToast
import com.danteyu.studio.moodietrail.login.LoginViewModel.Companion.RC_SIGN_IN
import com.danteyu.studio.moodietrail.util.Logger
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    val viewModel by viewModels<LoginViewModel> { getVmFactory() }

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
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
                            "email", "public_profile"
                        )
                    )
                    viewModel.onLoginFacebookCompleted()
                }
            })

            viewModel.user.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    findNavController().navigate(
                        NavigationDirections.navigateToMessageDialog(
                            MessageDialog.MessageType.LOGIN_SUCCESS
                        )
                    )
                }

            })

//            viewModel.navigateToLoginSuccess.observe(this, Observer {
//                it?.let {
//                    viewModel.navigateToLoginSuccess(it)
//                    findNavController().navigate(
//                        NavigationDirections.navigateToMessageDialog(
//                            MessageDialog.MessageType.LOGIN_SUCCESS
//                        )
//                    )
//                    viewModel.onLoginSuccessNavigated()
//                }
//            })

        }
        return binding.root
    }

    private fun signInGoogle() {

        val signInIntent = viewModel.getGoogleSignInClient().signInIntent
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

                UserManager.userToken = account!!.idToken
                UserManager.name = account.displayName
                UserManager.picture = account.photoUrl.toString()
                UserManager.mail = account.email

                firebaseAuthWithGoogle(account)
                Logger.i("ServerAuthCode =${account.serverAuthCode} account.id =${account.id}")

            } catch (e: ApiException) {

                Logger.w("Google sign in failed, ApiException = $e")

            }
        } else {
            viewModel.fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Logger.d("firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Logger.d("signInWithCredential:success")
                    val user = auth.currentUser
                    user?.let {
                        UserManager.id = it.uid
                        viewModel.checkUser(it.uid)
                    }

                } else {
                    activity.showToast(getString(R.string.login_fail_toast))

                    Logger.w("signInWithCredential:failure ${task.exception} error_code =${task.exception}")
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Logger.d("handleFacebookAccessToken:${token.token}")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    user?.let {
                        UserManager.id = it.uid
                        viewModel.checkUser(it.uid)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    activity.showToast(getString(R.string.login_fail_toast))
                }
            }
    }


}