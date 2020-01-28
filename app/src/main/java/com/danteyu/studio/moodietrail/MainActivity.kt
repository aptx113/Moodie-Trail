package com.danteyu.studio.moodietrail

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.danteyu.studio.moodietrail.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isFABOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.fab.setOnClickListener {
            if (!isFABOpen) {
                openFABMenu()
            } else {
                closeFABMenu()
            }
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
                .translationX(-resources.getDimension(R.dimen.standard_70)).alpha(1.0f).duration = 300
            fabAddTest.animate().translationY(-resources.getDimension(R.dimen.standard_70))
                .translationX(resources.getDimension(R.dimen.standard_70))
            textFabAddTest.animate().translationY(-resources.getDimension(R.dimen.standard_30))
                .translationX(resources.getDimension(R.dimen.standard_70)).alpha(1.0f).duration = 300

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
}
