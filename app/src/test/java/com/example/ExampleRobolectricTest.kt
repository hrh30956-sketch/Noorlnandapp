package com.example

import android.content.Context
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.NoorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("مدينة ألعاب نور المقدادية", appName)
  }

  @Test
  fun `instantiate noor view model`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = NoorViewModel(application)
    assertNotNull(viewModel)
  }
}
