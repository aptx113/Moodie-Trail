package com.danteyu.studio.moodietrail

import android.animation.Animator
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewConfiguration
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.danteyu.studio.moodietrail.databinding.ActivityMainBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
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
    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var isFABOpen: Boolean = false

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_diary -> {
                    findNavController(
                        R.id
                            .myNavHostFragment
                    ).navigate(NavigationDirections.navigateToDiaryFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_statistic -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToStatisticFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_test_result -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToTestResultFragment())
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

        binding.fab.setOnClickListener {
            if (!isFABOpen) {
                openFABMenu()
            } else {
                closeFABMenu()
            }
        }
        binding.fabShadow.setOnClickListener { closeFABMenu() }

        // observe current fragment change, only for show info
        viewModel.currentFragmentType.observe(this, Observer {
            Logger.i("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            Logger.i("[${viewModel.currentFragmentType.value}]")
            Logger.i("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
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
                R.id.diaryFragment -> CurrentFragmentType.DIARY
                R.id.statisticFragment -> CurrentFragmentType.STATISTIC
                R.id.testResultFragment -> CurrentFragmentType.TESTRESULT
                R.id.profileFragment -> CurrentFragmentType.PROFILE
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

                    val oriStatusBarHeight = resources.getDimensionPixelSize(R.dimen.height_status_bar_origin)

                    binding.toolbar.setPadding(0, oriStatusBarHeight, 0, 0)
                    val layoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
                    layoutParams.gravity = Gravity.CENTER
                    layoutParams.topMargin = statusBarHeight - oriStatusBarHeight
                    binding.textToolbarTitle.layoutParams = layoutParams
                }
            }
            Logger.i("====== ${Build.MODEL} ======")
        }
    }

    private fun openFABMenu() {
        isFABOpen = true

        binding.apply {
            fabAddMood.visibility = View.VISIBLE
//            textFabAddMood.visibility = View.VISIBLE
            fabAddTest.visibility = View.VISIBLE
//            textFabAddTest.visibility = View.VISIBLE
            fabShadow.visibility = View.VISIBLE

            fab.animate().rotation(135.0f)
            fabAddMood.animate().translationY(-resources.getDimension(R.dimen.standard_70))
                .translationX(-resources.getDimension(R.dimen.standard_70))
            textFabAddMood.animate().translationY(-resources.getDimension(R.dimen.standard_30))
                .translationX(-resources.getDimension(R.dimen.standard_70)).alpha(1.0f).duration =
                300
            fabAddTest.animate().translationY(-resources.getDimension(R.dimen.standard_70))
                .translationX(resources.getDimension(R.dimen.standard_70))
            textFabAddTest.animate().translationY(-resources.getDimension(R.dimen.standard_30))
                .translationX(resources.getDimension(R.dimen.standard_70)).alpha(1.0f).duration =
                300

        }

    }

    private fun closeFABMenu() {
        isFABOpen = false

        binding.apply {
            fabShadow.visibility = View.GONE

            fab.animate().rotation(0.0f)
            fabAddMood.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))
            textFabAddMood.animate().alpha(0.0f).duration = 300
//            textFabAddMood.animate().translationY(resources.getDimension(R.dimen.standard_0))
//                .translationX(resources.getDimension(R.dimen.standard_0)).duration = 1
            textFabAddTest.animate().alpha(0.0f).duration = 300
//            textFabAddTest.animate().translationY(resources.getDimension(R.dimen.standard_0))
//                .translationX(resources.getDimension(R.dimen.standard_0)).duration = 1
            fabAddTest.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))
            fabAddTest.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animator: Animator) {}
                    override fun onAnimationEnd(animator: Animator) {
                        if (!isFABOpen) {
                            binding.apply {
                                fabAddMood.visibility = View.GONE
//                                textFabAddMood.visibility = View.GONE
                                fabAddTest.visibility = View.GONE
//                                textFabAddTest.visibility = View.GONE
                            }
                        }
                    }

                    override fun onAnimationCancel(animator: Animator) {}
                    override fun onAnimationRepeat(animator: Animator) {}
                })
        }

    }

    override fun onBackPressed() {
        if (isFABOpen) {
            closeFABMenu()
        } else {
            super.onBackPressed()
        }
    }
}
