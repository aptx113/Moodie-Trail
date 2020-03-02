package com.danteyu.studio.moodietrail

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.databinding.ActivityMainBinding
import com.danteyu.studio.moodietrail.dialog.MessageDialog
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.setTouchDelegate
import com.danteyu.studio.moodietrail.ext.showToast
import com.danteyu.studio.moodietrail.util.CurrentFragmentType
import com.danteyu.studio.moodietrail.util.Logger
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.launch
import java.util.*


/**
 * Created by George Yu in Jan. 2020.
 */
class MainActivity : BaseActivity() {

    /**
     * Lazily initialize our [MainViewModel].
     */
    private val viewModel by viewModels<MainViewModel> { getVmFactory() }

    private lateinit var binding: ActivityMainBinding
    private lateinit var messageDialog: MessageDialog

    private var alarmManager: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController(
                        R.id
                            .myNavHostFragment
                    ).navigate(NavigationDirections.navigateToHomeFragment())
                    if (viewModel.isFabOpen.value == true) {
                        viewModel.changeFabStatus()
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_statistic -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToStatisticFragment())
                    if (viewModel.isFabOpen.value == true) {
                        viewModel.changeFabStatus()
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_psy_test_record -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToPsyTestRecordFragment())
                    if (viewModel.isFabOpen.value == true) {
                        viewModel.changeFabStatus()
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToProfileFragment())
                    if (viewModel.isFabOpen.value == true) {
                        viewModel.changeFabStatus()
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

        binding.imageToolbarCall.setOnClickListener {
            showToast("Coming Soon")
        }

        messageDialog = MessageDialog()

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

        viewModel.navigateToLoginSuccess.observe(this, Observer {
            it?.let {
                findNavController(R.id.myNavHostFragment).navigate(
                    NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.LOGIN_SUCCESS)
                )

//                supportFragmentManager.let { fragmentManager ->
//                    if (!messageDialog.isInLayout) {
//                        messageDialog.show(fragmentManager, "Image Source Selector").MessageType.LOGIN_SUCCESS
//                    }
//                }

                viewModel.onLoginSuccessNavigated()
                viewModel.navigateToHome()

            }
        })

        viewModel.navigateToHome.observe(this, Observer {
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

        viewModel.navigateToPsyTestRecord.observe(this, Observer {
            it?.let {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToPsyTestRecordFragment())
                binding.bottomNavView.selectedItemId =
                    R.id.navigation_psy_test_record
                viewModel.onPsyTestRecordNavigated()
            }
        })

        setupToolbar()
        setupBottomNav()
        setupNavController()

        val notificationKey = "title"
        val notificationIntentValue = "activity_app"

        alarmManager =
            MoodieTrailApplication.instance.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent =
            Intent(MoodieTrailApplication.instance, AlarmReceiver::class.java).let { intent ->
                intent.putExtra(notificationKey, notificationIntentValue)
                PendingIntent.getBroadcast(MoodieTrailApplication.instance, 0, intent, 0)
            }

        // Set the alarm to start at 12:30 p.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 30)
        }

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 1 day
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 60 * 24,
            alarmIntent
        )

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
            fabStartTest.show()
            fabShadow.visibility = View.VISIBLE

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
            || viewModel.currentFragmentType.value == CurrentFragmentType.PSYTESTRATING
        ) {
            binding.fabRecordMood.visibility = View.GONE
            binding.fabStartTest.visibility = View.GONE
            binding.fabShadow.visibility = View.GONE
            binding.textFabRecordMood.alpha = 0.0f
            binding.textFabStartTest.alpha = 0.0f
        }
//        } else {
//            binding.textFabRecordMood.alpha = 1.0f
//            binding.textFabStartTest.alpha = 1.0f
//        }

        binding.apply {
            fabShadow.visibility = View.GONE
            fab.animate().rotation(0.0f)
            fabRecordMood.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))

            textFabRecordMood.animate().alpha(0.0f).duration = 300
            textFabStartTest.animate().alpha(0.0f).duration = 300

            fabStartTest.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))
            fabStartTest.animate().translationY(resources.getDimension(R.dimen.standard_0))
                .translationX(resources.getDimension(R.dimen.standard_0))
//                .setListener(object : Animator.AnimatorListener {
//                    override fun onAnimationStart(animator: Animator) {}
//                    override fun onAnimationEnd(animator: Animator) {
//                        if (!viewModel?.isFabOpen?.value!!) {
//                            binding.apply {
//
//                                fabRecordMood.visibility = View.GONE
//                                fabStartTest.hide()
//
//                            }
//                        }
//                    }
//
//                    override fun onAnimationCancel(animator: Animator) {}
//                    override fun onAnimationRepeat(animator: Animator) {}
//                })
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
