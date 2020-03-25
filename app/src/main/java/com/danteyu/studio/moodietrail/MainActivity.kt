package com.danteyu.studio.moodietrail

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.databinding.ActivityMainBinding
import com.danteyu.studio.moodietrail.dialog.MessageDialog
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.setTouchDelegate
import com.danteyu.studio.moodietrail.util.CurrentFragmentType
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.setupAlarmManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch


/**
 * Created by George Yu in Jan. 2020.
 */
class MainActivity : BaseActivity() {

    /**
     * Lazily initialize our [MainViewModel].
     */
    private val viewModel by viewModels<MainViewModel> { getVmFactory() }

    private lateinit var binding: ActivityMainBinding

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController(
                        R.id
                            .myNavHostFragment
                    ).navigate(NavigationDirections.navigateToHomeFragment())
                    if (viewModel.isFabOpen.value == true) {
                        viewModel.closeFabByBottomNav()
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_statistic -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToStatisticFragment())
                    if (viewModel.isFabOpen.value == true) {
                        viewModel.closeFabByBottomNav()
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_psy_test_record -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToPsyTestRecordFragment())
                    if (viewModel.isFabOpen.value == true) {
                        viewModel.closeFabByBottomNav()
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToProfileFragment())
                    if (viewModel.isFabOpen.value == true) {
                        viewModel.closeFabByBottomNav()
                    }
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    // get the height of status bar from system
    private val statusBarHeight: Int
        get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return when {
                resourceId > 0 -> resources.getDimensionPixelSize(resourceId)
                else -> 0
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.fabRecordMood.setTouchDelegate()
        binding.fabStartTest.setTouchDelegate()
        binding.buttonTestBodyBack.setTouchDelegate()
        binding.buttonTestResultBack.setTouchDelegate()
        binding.imageToolbarCall.setTouchDelegate()

        // observe current fragment change, only for show info
        viewModel.currentFragmentType.observe(this, Observer {
            Logger.i("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            Logger.i("[${viewModel.currentFragmentType.value}]")
            Logger.i("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
        })

        viewModel.isFabOpen.observe(this, Observer {
            it?.let {
                if (it) openFabMenu()
                else closeFabMenu()
            }
        })

        viewModel.navigateToRecordMood.observe(this, Observer {
            it?.let {
                findNavController(R.id.myNavHostFragment).navigate(
                    NavigationDirections.navigateToRecordMoodFragment(
                        Note()
                    )
                )
                closeFabMenu()
                viewModel.onRecordMoodNavigated()
            }
        })

        viewModel.navigateToPsyTest.observe(this, Observer {
            it?.let {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToPsyTestFragment())
                closeFabMenu()
                viewModel.onPsyTestNavigated()
            }
        })

        viewModel.navigateToLoginSuccess.observe(this, Observer {
            it?.let {
                findNavController(R.id.myNavHostFragment).navigate(
                    NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.LOGIN_SUCCESS)
                )
                viewModel.onLoginSuccessNavigated()
                viewModel.navigateToHomeByBottomNav()
            }
        })

        viewModel.navigateToHomeByBottomNav.observe(this, Observer {
            it?.let {
                binding.bottomNavView.selectedItemId = R.id.navigation_home
                viewModel.onHomeNavigated()
            }
        })

        viewModel.backToPsyTest.observe(this, Observer {
            it?.let {
                findNavController(R.id.myNavHostFragment).navigateUp()
                viewModel.onPsyTestBacked()
            }
        })

        viewModel.navigateToPsyTestRecordByBottomNav.observe(this, Observer {
            it?.let {
                binding.bottomNavView.selectedItemId =
                    R.id.navigation_psy_test_record
                viewModel.onPsyTestRecordNavigated()
            }
        })

        viewModel.navigateToConsultationCall.observe(this, Observer {
            it?.let {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToConsultationCallFragment())
                viewModel.onConsultationCallNavigated()
            }
        })

        setupToolbar()
        setupBottomNav()
        setupNavController()
        setupAlarmManager()

    }

    /**
     * Set up [BottomNavigationView]
     */
    private fun setupBottomNav() {
        binding.bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    /**
     * Set up [NavController.addOnDestinationChangedListener] to record the current fragment.
     */
    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.loginFragment -> CurrentFragmentType.LOGIN
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.statisticFragment -> CurrentFragmentType.STATISTIC
                R.id.psyTestRecordFragment -> CurrentFragmentType.PSY_TEST_RECORD
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.recordMoodFragment -> CurrentFragmentType.RECORD_MOOD
                R.id.recordDetailFragment -> CurrentFragmentType.RECORD_DETAIL
                R.id.psyTestFragment -> CurrentFragmentType.PSY_TEST
                R.id.psyTestBodyFragment -> CurrentFragmentType.PSY_TEST_BODY
                R.id.psyTestResultFragment -> CurrentFragmentType.PSY_TEST_RESULT
                R.id.psyTestRatingFragment -> CurrentFragmentType.PSY_TEST_RATING
                R.id.consultationCallFragment -> CurrentFragmentType.CONSULTATION_CALL
                R.id.mentalHealthResFragment -> CurrentFragmentType.MENTAL_HEALTH_RES
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    /**
     * Set up the layout of [Toolbar], according to whether it has cutout
     */
    private fun setupToolbar() {

        binding.toolbar.setPadding(0, statusBarHeight, 0, 0)

        launch {

            val dpi = resources.displayMetrics.densityDpi.toFloat()
            val dpiMultiple = dpi / DisplayMetrics.DENSITY_DEFAULT

            val cutoutHeight = getCutoutHeight()

            Logger.i("====== ${Build.MODEL} ======")
            Logger.i("$dpi dpi (${dpiMultiple}x)")
            Logger.i("statusBarHeight: ${statusBarHeight}px/${statusBarHeight / dpiMultiple}dp")

            when {
                cutoutHeight > 0 -> {
                    Logger.i("cutoutHeight: ${cutoutHeight}px/${cutoutHeight / dpiMultiple}dp")

                    val oriStatusBarHeight =
                        resources.getDimensionPixelSize(R.dimen.height_status_bar_origin)

                    binding.toolbar.setPadding(0, oriStatusBarHeight, 0, 0)
                    val layoutParams = Toolbar.LayoutParams(
                        Toolbar.LayoutParams.WRAP_CONTENT,
                        Toolbar.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.gravity = Gravity.CENTER
                    layoutParams.topMargin = statusBarHeight - oriStatusBarHeight
                    binding.textToolbarTitle.layoutParams = layoutParams
                }
            }
            Logger.i("====== ${Build.MODEL} ======")
        }
    }

    @SuppressLint("RestrictedApi")
    private fun openFabMenu() {

        binding.apply {
            fabRecordMood.show()
            fabStartTest.show()
            fabShadow.visibility = View.VISIBLE

            fab.animate().rotation(ROTATION_DEGREE_FOR_OPEN_FAB)

            fabRecordMood.animate().translationY(-resources.getDimension(R.dimen.standard_70))
                .translationX(-resources.getDimension(R.dimen.standard_70))

            textFabRecordMood.animate().translationY(-resources.getDimension(R.dimen.standard_30))
                .translationX(-resources.getDimension(R.dimen.standard_70)).alpha(1.0f).duration =
                DURATION_FOR_FAB_ANIMATION

            fabStartTest.animate().translationY(-resources.getDimension(R.dimen.standard_70))
                .translationX(resources.getDimension(R.dimen.standard_70))

            textFabStartTest.animate().translationY(-resources.getDimension(R.dimen.standard_30))
                .translationX(resources.getDimension(R.dimen.standard_70)).alpha(1.0f).duration =
                DURATION_FOR_FAB_ANIMATION
        }
    }

    @SuppressLint("RestrictedApi")
    private fun closeFabMenu() {

        if (viewModel.currentFragmentType.value == CurrentFragmentType.RECORD_MOOD
            || viewModel.currentFragmentType.value == CurrentFragmentType.LOGIN
            || viewModel.currentFragmentType.value == CurrentFragmentType.PSY_TEST
        ) {
            binding.fabRecordMood.visibility = View.GONE
            binding.fabStartTest.visibility = View.INVISIBLE
            binding.fabShadow.visibility = View.GONE
            binding.textFabRecordMood.alpha = 0.0f
            binding.textFabStartTest.alpha = 0.0f
        }

        binding.apply {
            fabShadow.visibility = View.GONE

            fab.animate().rotation(0.0f)

            fabRecordMood.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))

            fabStartTest.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))

            textFabRecordMood.animate().alpha(0.0f).duration = DURATION_FOR_FAB_ANIMATION
            textFabStartTest.animate().alpha(0.0f).duration = DURATION_FOR_FAB_ANIMATION

            // When animation end, do something
            textFabStartTest.animate().setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    if (!viewModel?.isFabOpen?.value!!) {
                        binding.apply {

                            fabRecordMood.hide()
                            fabStartTest.hide()
                        }
                    }
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
        }
    }

    companion object {
        private const val DURATION_FOR_FAB_ANIMATION = 300L
        private const val ROTATION_DEGREE_FOR_OPEN_FAB = 135.0f
    }
}
