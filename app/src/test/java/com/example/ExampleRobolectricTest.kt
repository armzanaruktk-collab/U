package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
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
    assertEquals("VPN Settings", appName)
  }

  @Test
  fun `test vpn config ini serialization and payload formatting`() {
    val defaultCfg = com.example.model.VpnConfig.createDefault()
    val ini = defaultCfg.toIniString()
    org.junit.Assert.assertTrue(ini.contains("[mode]"))
    org.junit.Assert.assertTrue(ini.contains("connection_mode = 1"))
    org.junit.Assert.assertTrue(ini.contains("[Payload]"))
    org.junit.Assert.assertTrue(ini.contains("[ssh]"))

    val parsed = com.example.model.VpnConfig.parseIni(ini, "Test Profile")
    assertEquals(1, parsed.connectionMode)
    assertEquals(true, parsed.autoReplace)
    assertEquals("sg-server.net", parsed.sshHost)

    val formatted = parsed.getFormattedPayload()
    org.junit.Assert.assertTrue(formatted.contains("Dalvik/2.1.0"))
    org.junit.Assert.assertTrue(formatted.contains("sg-server.net"))
  }
}
