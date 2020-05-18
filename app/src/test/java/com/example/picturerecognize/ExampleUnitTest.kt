package com.example.picturerecognize

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        val s = "//dawd/dwdw.png"
        println(createHeadCropImagePath(s, 4))
    }


    fun createHeadCropImagePath(path: String, userId : Int): String? {
        val slashIndex = path.lastIndexOf('/')
        val pointIndex = path.lastIndexOf('.')
        val result = StringBuilder()
        return if (slashIndex != null && pointIndex != null) {
            result.append(path.substring(0, slashIndex + 1))
                .append(userId)
                .append(path.substring(pointIndex))
            result.toString()
        } else {
            null
        }
        return "${userId}.${path.substring(pointIndex)}"
    }

}
