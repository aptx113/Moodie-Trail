package com.danteyu.studio.moodietrail.component

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.danteyu.studio.moodietrail.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.MaterialShapeDrawable.SHADOW_COMPAT_MODE_DEFAULT
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.ShapePathModel

/**
 * Created by George Yu in Jan. 2020.
 *
 * Custom BottomNavigationView for FAB
 */

class FabBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    private var topCurvedEdgeTreatment: TopCurvedEdgeTreatment
    private var materialShapeDrawable: MaterialShapeDrawable = MaterialShapeDrawable()
    private var fabSize = 0F
    var fabCradleMargin = 0F
    var fabCradleRoundedCornerRadius = 0F
    var cradleVerticalOffset = 0F

    init {
        val ta =
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.FabBottomNavigationView, 0, 0
            )
        fabSize = ta.getDimension(R.styleable.FabBottomNavigationView_fab_size, 0F)
        fabCradleMargin = ta.getDimension(R.styleable.FabBottomNavigationView_fab_cradle_margin, 0F)
        fabCradleRoundedCornerRadius = ta.getDimension(
            R.styleable.FabBottomNavigationView_fab_cradle_rounded_corner_radius,
            0F
        )
        cradleVerticalOffset =
            ta.getDimension(R.styleable.FabBottomNavigationView_cradle_vertical_offset, 0F)

        topCurvedEdgeTreatment =
            TopCurvedEdgeTreatment(
                fabCradleMargin,
                fabCradleRoundedCornerRadius,
                cradleVerticalOffset
            ).apply {
                fabDiameter = fabSize
            }

        val shapePathModel = ShapePathModel().apply {
            topEdge = topCurvedEdgeTreatment
        }


        val shapeAppearanceModel =
            materialShapeDrawable.shapeAppearanceModel.apply {
//                topEdge = topCurvedEdgeTreatment
            }
//        val shapeAppearanceModel = ShapeAppearanceModel().apply {
//            topEdge = topCurvedEdgeTreatment
//        }

        materialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
            setTint(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
            shadowRadius = 16
            elevation = 4f
            shadowCompatibilityMode = SHADOW_COMPAT_MODE_DEFAULT
            paintStyle = Paint.Style.FILL_AND_STROKE
        }

        background = materialShapeDrawable
    }
}
