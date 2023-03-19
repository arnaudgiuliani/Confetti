@file:OptIn(ExperimentalCoroutinesApi::class)
@file:Suppress("UnstableApiUsage")

package dev.johnoreilly.confetti.wear

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.view.View
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dev.johnoreilly.confetti.AppSettings
import dev.johnoreilly.confetti.wear.home.navigation.ConferenceHomeDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.GraphicsMode.Mode.NATIVE
import org.robolectric.shadows.ShadowDisplay
import org.robolectric.shadows.ShadowDisplayManager
import org.robolectric.shadows.ShadowView
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(application = KoinTestApp::class, sdk = [30])
@GraphicsMode(NATIVE)
class AppTest : KoinTest {
    @get:Rule
    val rule = createAndroidComposeRule(MainActivity::class.java)

    val appSettings: AppSettings by inject()

    @Before
    fun setUp() {
        val display = ShadowDisplay.getDefaultDisplay()
        ShadowDisplayManager.changeDisplay(display.displayId, "+round")
        shadowOf(display).apply {
            setXdpi(320f)
            setYdpi(320f)
            setHeight(454)
            setWidth(454)
        }
    }

    @Test
    fun launchHome() = runTest {
        val activity = rule.activity

        val navController = activity.navController

        assertEquals("conference_route/{conference}", navController.currentDestination?.route)

        assertEquals("", appSettings.getConference())
    }

    @Test
    fun offlineTest() = runTest {
        val activity = rule.activity

        val navController = activity.navController

        assertEquals("conference_route/{conference}", navController.currentDestination?.route)

        navController.navigate(ConferenceHomeDestination.createNavigationRoute("test"))
        appSettings.setConference("test")

        delay(2000)

        takeScreenshot()
    }

    private fun takeScreenshot() {
        assertTrue(ShadowView.useRealGraphics())

        val view = rule.activity.findViewById<View>(android.R.id.content)
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(Canvas(bitmap))
        bitmap.compress(CompressFormat.PNG, 100, FileOutputStream("test.png"))
    }
}