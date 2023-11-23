package com.eaccid.musimpa

import com.eaccid.musimpa.dikoin.musimpaModule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class CheckModulesTest: KoinTest {
    @Test
    fun checkAllModules() {
        musimpaModule.verify()
    }
}