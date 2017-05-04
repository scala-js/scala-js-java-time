package org.scalajs.testsuite.javalib.time.format

import java.time.format._

import org.junit.Test
import org.junit.Assert._
import org.scalajs.testsuite.utils.AssertThrows._

class DecimalStyleTest {
  import DecimalStyle._

  @Test def test_get(): Unit = {
    assertEquals('.', STANDARD.getDecimalSeparator())
    assertEquals('-', STANDARD.getNegativeSign())
    assertEquals('+', STANDARD.getPositiveSign())
    assertEquals('0', STANDARD.getZeroDigit())
  }

  @Test def test_with(): Unit = {
    assertEquals('X', STANDARD.withDecimalSeparator('X').getDecimalSeparator())
    assertEquals('X', STANDARD.withNegativeSign('X').getNegativeSign())
    assertEquals('X', STANDARD.withPositiveSign('X').getPositiveSign())
    assertEquals('X', STANDARD.withZeroDigit('X').getZeroDigit())
  }

  @Test def test_equals(): Unit = {
    val ds1 = STANDARD.withZeroDigit('0')
    val ds2 = STANDARD.withZeroDigit('1')
    assertEquals(ds1, STANDARD)
    assertNotEquals(ds1, ds2)
  }
}
