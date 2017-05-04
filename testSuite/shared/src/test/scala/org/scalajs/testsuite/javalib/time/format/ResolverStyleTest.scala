package org.scalajs.testsuite.javalib.time.format

import java.time.format._

import org.junit.Test
import org.junit.Assert._
import org.scalajs.testsuite.utils.AssertThrows._

class ResolverStyleTest {
  import ResolverStyle._

  @Test def test_compareTo(): Unit = {
    assertEquals(0, STRICT.compareTo(STRICT))
    assertTrue(STRICT.compareTo(LENIENT) < 0)
    assertTrue(SMART.compareTo(STRICT) > 0)
  }

  @Test def test_values(): Unit = {
    val styles: Array[AnyRef] =
      Array(STRICT, SMART, LENIENT)
    assertArrayEquals(styles, values.asInstanceOf[Array[AnyRef]])
  }

  @Test def test_valueOf(): Unit = {
    assertEquals(STRICT, valueOf("STRICT"))
    assertEquals(SMART, valueOf("SMART"))
    assertEquals(LENIENT, valueOf("LENIENT"))

    expectThrows(classOf[IllegalArgumentException], valueOf(""))
  }
}
