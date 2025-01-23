package com.kolown.rule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

//TODO 테스트 모듈 생성시 이동
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTestRule : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}