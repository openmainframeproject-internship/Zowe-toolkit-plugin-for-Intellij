package customTestCase

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import eu.ibagroup.formainframe.analytics.AnalyticsService
import eu.ibagroup.formainframe.analytics.AnalyticsStartupActivity
import io.mockk.every
import io.mockk.mockkConstructor

/**
 * A custom test case for API tests.
 * This test case modifies the setUp method of BasePlatformTestCase and mocks the license agreement.
 * The class also overrides the getBasePath and getTestDataPath methods.
 */
abstract class PluginTestCase : BasePlatformTestCase() {

    override fun setUp() {
        mockkConstructor(AnalyticsStartupActivity::class)
        every { AnalyticsStartupActivity().runActivity(any()) } returns Unit

        super.setUp()

        val analyticsService = service<AnalyticsService>()
        analyticsService.isAnalyticsEnabled = true
        analyticsService.isUserAcknowledged = true
    }

    override fun getBasePath() = "/testData/"

    override fun getTestDataPath() = System.getProperty("user.dir") + basePath
}