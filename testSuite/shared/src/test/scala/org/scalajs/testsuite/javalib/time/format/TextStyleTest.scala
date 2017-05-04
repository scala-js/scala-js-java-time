package org.scalajs.testsuite.javalib.time.format

import java.time.format._

import org.junit.Test
import org.junit.Assert._
import org.scalajs.testsuite.utils.AssertThrows._

class TextStyleTest {
  import TextStyle._

  @Test def test_isStandalone(): Unit = {
    assertFalse(FULL.isStandalone())
    assertTrue(FULL_STANDALONE.isStandalone())
    assertFalse(SHORT.isStandalone())
    assertTrue(SHORT_STANDALONE.isStandalone())
    assertFalse(NARROW.isStandalone())
    assertTrue(NARROW_STANDALONE.isStandalone())
  }

  @Test def test_asStandalone(): Unit = {
    assertEquals(FULL_STANDALONE, FULL.asStandalone())
    assertEquals(FULL_STANDALONE, FULL_STANDALONE.asStandalone())
    assertEquals(SHORT_STANDALONE, SHORT.asStandalone())
    assertEquals(SHORT_STANDALONE, SHORT_STANDALONE.asStandalone())
    assertEquals(NARROW_STANDALONE, NARROW.asStandalone())
    assertEquals(NARROW_STANDALONE, NARROW_STANDALONE.asStandalone())
  }

  @Test def test_asNormal(): Unit = {
    assertEquals(FULL, FULL.asNormal())
    assertEquals(FULL, FULL_STANDALONE.asNormal())
    assertEquals(SHORT, SHORT.asNormal())
    assertEquals(SHORT, SHORT_STANDALONE.asNormal())
    assertEquals(NARROW, NARROW.asNormal())
    assertEquals(NARROW, NARROW_STANDALONE.asNormal())
  }

  @Test def test_compareTo(): Unit = {
    assertEquals(0, FULL.compareTo(FULL))
    assertTrue(FULL.compareTo(NARROW_STANDALONE) < 0)
    assertTrue(NARROW.compareTo(FULL_STANDALONE) > 0)
  }

  @Test def test_values(): Unit = {
    val styles: Array[AnyRef] =
      Array(FULL, FULL_STANDALONE, SHORT, SHORT_STANDALONE, NARROW, NARROW_STANDALONE)
    assertArrayEquals(styles, values.asInstanceOf[Array[AnyRef]])
  }

  @Test def test_valueOf(): Unit = {
    assertEquals(FULL, valueOf("FULL"))
    assertEquals(FULL_STANDALONE, valueOf("FULL_STANDALONE"))
    assertEquals(SHORT, valueOf("SHORT"))
    assertEquals(SHORT_STANDALONE, valueOf("SHORT_STANDALONE"))
    assertEquals(NARROW, valueOf("NARROW"))
    assertEquals(NARROW_STANDALONE, valueOf("NARROW_STANDALONE"))

    expectThrows(classOf[IllegalArgumentException], valueOf(""))
  }
}
