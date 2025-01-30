package com.kolown.porring.feature.camera.filter

import android.content.Context
import com.kolown.porring.feature.camera.R
import com.kolown.porring.feature.camera.filter.common.Filter
import com.kolown.porring.feature.camera.filter.util.GLUtil

class OriginalFilter(context:Context): Filter(context){
    private val innerProgram: Int = GLUtil.buildProgram(context, R.raw.vertext, R.raw.original)


    override fun onDraw(cameraTexId: Int, canvasWidth: Int, canvasHeight: Int) {
        setupShaderInputs(innerProgram,
            intArrayOf(canvasWidth,canvasHeight),
            intArrayOf(cameraTexId),
            arrayOf()
        )
    }
}
