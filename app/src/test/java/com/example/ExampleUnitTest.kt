package com.example

import com.example.firebase.FirebaseConfig
import com.example.firebase.UserAccount
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun firebaseConfig_hasValidProjectSettings() {
    assertEquals("ruhul-mesia", FirebaseConfig.PROJECT_ID)
    assertEquals("AIzaSyDcx1EzZ6S2V-ooN-rw1t25wLwmur_1ijI", FirebaseConfig.API_KEY)
    assertEquals("ruhul-mesia.firebaseapp.com", FirebaseConfig.AUTH_DOMAIN)
    assertEquals("105920612724", FirebaseConfig.MESSAGING_SENDER_ID)
  }

  @Test
  fun userAccount_initialization() {
    val user = UserAccount(
      uid = "test_uid",
      email = "test@example.com",
      displayName = "Test User"
    )
    assertEquals("test_uid", user.uid)
    assertEquals("test@example.com", user.email)
    assertEquals("Test User", user.displayName)
  }
}
