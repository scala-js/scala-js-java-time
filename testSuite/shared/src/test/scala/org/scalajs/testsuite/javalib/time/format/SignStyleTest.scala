package org.scalajs.testsuite.javalib.time.format

import java.time.format._

import org.junit.Test
import org.junit.Assert._
import org.scalajs.testsuite.utils.AssertThrows._

class SignStyleTest {
  import SignStyle._

  @Test def test_compareTo(): Unit = {
    assertEquals(0, NORMAL.compareTo(NORMAL))
    assertTrue(ALWAYS.compareTo(EXCEEDS_PAD) < 0)
    assertTrue(NOT_NEGATIVE.compareTo(NEVER) > 0)
  }

  @Test def test_values(): Unit = {
    val styles: Array[AnyRef] =
      Array(NORMAL, ALWAYS, NEVER, NOT_NEGATIVE, EXCEEDS_PAD)
    assertArrayEquals(styles, values.asInstanceOf[Array[AnyRef]])
  }

  @Test def test_valueOf(): Unit = {
    assertEquals(NORMAL, valueOf("NORMAL"))
    assertEquals(ALWAYS, valueOf("ALWAYS"))
    assertEquals(NEVER, valueOf("NEVER"))
    assertEquals(NOT_NEGATIVE, valueOf("NOT_NEGATIVE"))
    assertEquals(EXCEEDS_PAD, valueOf("EXCEEDS_PAD"))

    expectThrows(classOf[IllegalArgumentException], valueOf(""))
  }
}
