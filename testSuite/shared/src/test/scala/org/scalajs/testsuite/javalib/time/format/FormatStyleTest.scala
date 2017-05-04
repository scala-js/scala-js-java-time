package org.scalajs.testsuite.javalib.time.format

import java.time.format._

import org.junit.Test
import org.junit.Assert._
import org.scalajs.testsuite.utils.AssertThrows._

class FormatStyleTest {
  import FormatStyle._

  @Test def test_compareTo(): Unit = {
    assertEquals(0, FULL.compareTo(FULL))
    assertTrue(FULL.compareTo(SHORT) < 0)
    assertTrue(MEDIUM.compareTo(LONG) > 0)
  }

  @Test def test_values(): Unit = {
    val styles: Array[AnyRef] =
      Array(FULL, LONG, MEDIUM, SHORT)
    assertArrayEquals(styles, values.asInstanceOf[Array[AnyRef]])
  }

  @Test def test_valueOf(): Unit = {
    assertEquals(FULL, valueOf("FULL"))
    assertEquals(LONG, valueOf("LONG"))
    assertEquals(MEDIUM, valueOf("MEDIUM"))
    assertEquals(SHORT, valueOf("SHORT"))

    expectThrows(classOf[IllegalArgumentException], valueOf(""))
  }
}
