package com.danteyu.studio.moodietrail.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.MainViewModel
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.FragmentLoginBinding
import com.danteyu.studio.moodietrail.dialog.MessageDialog
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.showToast
import com.danteyu.studio.moodietrail.login.LoginViewModel.Companion.RC_SIGN_IN
import com.danteyu.studio.moodietrail.util.Logger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginFragment : Fragment() {

    val viewModel by viewModels<LoginViewModel> { getVmFactory() }

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

//    override fun onStart() {
//        super.onStart()
////        (activity as MainActivity).bottom_nav_view.visibility = View.GONE
//        // Configure Google Sign In
//        currentUser = auth.currentUser!!
//    }

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



        viewModel.loginGoogle.observe(this, Observer {
            it?.let {

                context?.let {
                    viewModel.googleSignInClient =
                        GoogleSignIn.getClient(MoodieTrailApplication.instance, viewModel.gso)
                    val signInIntent = viewModel.googleSignInClient?.signInIntent
                    startActivityForResult(signInIntent!!, RC_SIGN_IN)
                }
                viewModel.onLoginGoogleCompleted()
                findNavController().navigate(
                    NavigationDirections.navigateToMessageDialog(
                        MessageDialog.MessageType.LOGIN_SUCCESS
                    )
                )
            }
        })

//        viewModel.user.observe(this, Observer {
//            if (it != null) {
//                findNavController().navigate(
//                    NavigationDirections.navigateToMessageDialog(
//                        MessageDialog.MessageType.LOGIN_SUCCESS
//                    )
//                )
//            }
//
//        })

//        viewModel.navigateToLoginSuccess.observe(this, Observer {
//            it?.let {
//                viewModel.navigateToLoginSuccess(it)
//                findNavController().navigate(
//                    NavigationDirections.navigateToMessageDialog(
//                        MessageDialog.MessageType.LOGIN_SUCCESS
//                    )
//                )
//                viewModel.onLoginSuccessNavigated()
//            }
//        })


        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
                Logger.i("ServerAuthCode =${account.serverAuthCode} account.id =${account.id}")

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Logger.w("Google sign in failed")
                // ...
            }
        } else {
            viewModel.fbCallbackManager?.onActivityResult(requestCode, resultCode, data)

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

                } else {
                    activity.showToast(getString(R.string.login_fail_toast))

                    Logger.w("signInWithCredential:failure ${task.exception} error_code =${task.exception}")
                }
            }
    }


}