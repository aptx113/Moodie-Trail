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
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.setTouchDelegate
import com.danteyu.studio.moodietrail.util.CurrentFragmentType
import com.danteyu.studio.moodietrail.util.Logger
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
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_statistic -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToStatisticFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_psy_test_record -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToPsyTestRecordFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToProfileFragment())
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
                viewModel.onRecordMoodNavigated()
            }
        })

        viewModel.navigateToPsyTest.observe(this, Observer {
            it?.let {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToPsyTestFragment())
                viewModel.onPsyTestNavigated()
            }
        })

        setupToolbar()
        setupBottomNav()
        setupNavController()

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
                R.id.psyTestRecordFragment -> CurrentFragmentType.PSYTESTRECORD
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.recordMoodFragment -> CurrentFragmentType.RECORDMOOD
                R.id.recordDetailFragment -> CurrentFragmentType.RECORDDETAIL
                R.id.psyTestFragment -> CurrentFragmentType.PSYTEST
                R.id.psyTestBodyFragment -> CurrentFragmentType.PSYTESTBODY
                R.id.psyTestResultFragment -> CurrentFragmentType.PSYTESTRESULT
                R.id.psyTestRatingFragment -> CurrentFragmentType.PSYTESTRATING
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

            fabRecordMood.visibility = View.VISIBLE
            fabStartTest.visibility = View.VISIBLE

            fab.animate().rotation(135.0f)
            fabRecordMood.animate().translationY(-resources.getDimension(R.dimen.standard_70))
                .translationX(-resources.getDimension(R.dimen.standard_70))
            textFabRecordMood.animate().translationY(-resources.getDimension(R.dimen.standard_30))
                .translationX(-resources.getDimension(R.dimen.standard_70)).alpha(1.0f).duration =
                300
            fabStartTest.animate().translationY(-resources.getDimension(R.dimen.standard_70))
                .translationX(resources.getDimension(R.dimen.standard_70))
            textFabStartTest.animate().translationY(-resources.getDimension(R.dimen.standard_30))
                .translationX(resources.getDimension(R.dimen.standard_70)).alpha(1.0f).duration =
                300

        }

    }

    @SuppressLint("RestrictedApi")
    private fun closeFabMenu() {

        if (viewModel.currentFragmentType.value == CurrentFragmentType.RECORDMOOD
            || viewModel.currentFragmentType.value == CurrentFragmentType.RECORDDETAIL
            || viewModel.currentFragmentType.value == CurrentFragmentType.LOGIN
            || viewModel.currentFragmentType.value == CurrentFragmentType.PSYTEST
            || viewModel.currentFragmentType.value == CurrentFragmentType.PSYTESTBODY
            || viewModel.currentFragmentType.value == CurrentFragmentType.PSYTESTRESULT
            || viewModel.currentFragmentType.value == CurrentFragmentType.PSYTESTRATING ) {
            binding.fabRecordMood.visibility = View.GONE
            binding.fabStartTest.visibility = View.GONE
            binding.textFabRecordMood.alpha = 0.0f
            binding.textFabStartTest.alpha = 0.0f
        } else {
            binding.textFabRecordMood.alpha = 1.0f
            binding.textFabStartTest.alpha = 1.0f
        }

        binding.apply {

            fab.animate().rotation(0.0f)
            fabRecordMood.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))
            textFabRecordMood.animate().alpha(0.0f).duration = 300
//            textFabAddMood.animate().translationY(resources.getDimension(R.dimen.standard_0))
//                .translationX(resources.getDimension(R.dimen.standard_0)).duration = 1
            textFabStartTest.animate().alpha(0.0f).duration = 300
//            textFabAddTest.animate().translationY(resources.getDimension(R.dimen.standard_0))
//                .translationX(resources.getDimension(R.dimen.standard_0)).duration = 1
            fabStartTest.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))
            fabStartTest.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {}
                    override fun onAnimationEnd(animator: Animator) {
                        if (!viewModel?.isFabOpen?.value!!) {
                            binding.apply {

                                fabRecordMood.visibility = View.GONE
                                fabStartTest.visibility = View.GONE

                            }
                        }
                    }

                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                })
        }

    }

//    override fun onBackPressed() {
//        if (viewModel.isFabOpen.value!!) {
//            closeFabMenu()
//        } else {
//            super.onBackPressed()
//        }
//    }
}
