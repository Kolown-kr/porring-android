package com.kolown.porring.feature.camera.filter

import android.content.Context
import android.opengl.GLES20
import com.kolown.porring.feature.camera.R
import com.kolown.porring.feature.camera.filter.common.Filter
import com.kolown.porring.feature.camera.filter.util.GLUtil


class BlackWhiteFilter(context: Context) : Filter(context) {
    // Build shaders
    private val innerProgram: Int = GLUtil.buildProgram(context, R.raw.vertext, R.raw.black_white)



    override fun onDraw(cameraTexId: Int, canvasWidth: Int, canvasHeight: Int) {
        setupShaderInputs(
            innerProgram,
            intArrayOf(canvasWidth, canvasHeight),
            intArrayOf(cameraTexId),
            arrayOf()
        )
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }
}
