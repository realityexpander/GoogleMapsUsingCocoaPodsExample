package com.realityexpander

import getPlatformName
import kotlin.test.Test
import kotlin.test.assertTrue

class CommonGreetingTest2 {

    @Test
    fun testExample() {
        val result = getPlatformName()

        assertTrue(
            getPlatformName().contains("Android")
                    || getPlatformName().contains("iOS"),
            "Android or iOS was found"
        )
    }
}