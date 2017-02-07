package org.scalajs.testsuite.javalib.time

import java.time._
import java.time.temporal.{UnsupportedTemporalTypeException, ChronoUnit, ChronoField}

import org.junit.Test
import org.junit.Assert._
import org.scalajs.testsuite.utils.AssertThrows._

/** Created by alonsodomin on 26/12/2015. */
class InstantTest extends TemporalTest[Instant] {
  import DateTimeTestUtil._
  import ChronoField._
  import ChronoUnit._

  val somePositiveInstant = Instant.ofEpochMilli(928392983942L)
  val someNegativeInstant = Instant.ofEpochSecond(-83827873287L, 88936253)

  val samples = Seq(Instant.EPOCH, Instant.MIN, Instant.MAX,
      somePositiveInstant, someNegativeInstant)

  def isSupported(field: ChronoField): Boolean =
    field == INSTANT_SECONDS || field == NANO_OF_SECOND || field == MICRO_OF_SECOND ||
      field == MILLI_OF_SECOND

  def isSupported(unit: ChronoUnit): Boolean =
    unit.isTimeBased || unit == DAYS

  @Test def getLong(): Unit = {
    for (field <- ChronoField.values() if isSupported(field))
      assertEquals(0L, Instant.EPOCH.getLong(field))

    assertEquals(-31557014167219200L, Instant.MIN.getLong(INSTANT_SECONDS))
    assertEquals(0L, Instant.MIN.getLong(NANO_OF_SECOND))
    assertEquals(0L, Instant.MIN.getLong(MICRO_OF_SECOND))
    assertEquals(0L, Instant.MIN.getLong(MILLI_OF_SECOND))

    assertEquals(31556889864403199L, Instant.MAX.getLong(INSTANT_SECONDS))
    assertEquals(999999999L, Instant.MAX.getLong(NANO_OF_SECOND))
    assertEquals(999999L, Instant.MAX.getLong(MICRO_OF_SECOND))
    assertEquals(999L, Instant.MAX.getLong(MILLI_OF_SECOND))

    assertEquals(928392983L, somePositiveInstant.getLong(INSTANT_SECONDS))
    assertEquals(942000000L, somePositiveInstant.getLong(NANO_OF_SECOND))
    assertEquals(942000L, somePositiveInstant.getLong(MICRO_OF_SECOND))
    assertEquals(942L, somePositiveInstant.getLong(MILLI_OF_SECOND))

    assertEquals(-83827873287L, someNegativeInstant.getLong(INSTANT_SECONDS))
    assertEquals(88936253L, someNegativeInstant.getLong(NANO_OF_SECOND))
    assertEquals(88936L, someNegativeInstant.getLong(MICRO_OF_SECOND))
    assertEquals(88L, someNegativeInstant.getLong(MILLI_OF_SECOND))
  }

  @Test def getEpochSecond(): Unit = {
    assertEquals(0L, Instant.EPOCH.getEpochSecond)
    assertEquals(-31557014167219200L, Instant.MIN.getEpochSecond)
    assertEquals(31556889864403199L, Instant.MAX.getEpochSecond)
    assertEquals(928392983L, somePositiveInstant.getEpochSecond)
    assertEquals(-83827873287L, someNegativeInstant.getEpochSecond)
  }

  @Test def getNano(): Unit = {
    assertEquals(0, Instant.EPOCH.getNano)
    assertEquals(0, Instant.MIN.getNano)
    assertEquals(999999999, Instant.MAX.getNano)
    assertEquals(942000000, somePositiveInstant.getNano)
    assertEquals(88936253, someNegativeInstant.getNano)
  }

  @Test def `with`(): Unit = {
    for (i <- samples) {
      for (value <- Seq(0L, 999L, 999999L, 999999999L)) {
        assertEquals(value, i.`with`(NANO_OF_SECOND, value).getLong(NANO_OF_SECOND))
      }
      for (value <- Seq(0L, 999L, 999999L)) {
        assertEquals(value, i.`with`(MICRO_OF_SECOND, value).getLong(MICRO_OF_SECOND))
      }
      for (value <- Seq(0L, 500L, 999L)) {
        assertEquals(value, i.`with`(MILLI_OF_SECOND, value).getLong(MILLI_OF_SECOND))
      }
      for (value <- Seq(-31557014167219200L, -1L, 0L, 1L, 31557014167219200L))
        testDateTime(i.`with`(INSTANT_SECONDS, value).getLong(INSTANT_SECONDS))(value)

      for (n <- Seq(Long.MinValue, -1L, 1000000000L, Long.MaxValue))
        expectThrows(classOf[DateTimeException], i.`with`(NANO_OF_SECOND, n))
      for (n <- Seq(Long.MinValue, -1L, 1000000L, Long.MaxValue))
        expectThrows(classOf[DateTimeException], i.`with`(MICRO_OF_SECOND, n))
      for (n <- Seq(Long.MinValue, -1L, 1000L, Long.MaxValue))
        expectThrows(classOf[DateTimeException], i.`with`(MILLI_OF_SECOND, n))
      for (n <- Seq(Long.MinValue, -31557014167219201L, 31557014167219201L, Long.MaxValue))
        expectThrows(classOf[DateTimeException], i.`with`(INSTANT_SECONDS, n))
    }
  }

  @Test def truncatedTo(): Unit = {
    for (i <- samples)
      assertSame(i, i.truncatedTo(NANOS))

    assertEquals(Instant.EPOCH, Instant.EPOCH.truncatedTo(MICROS))
    assertEquals(Instant.EPOCH, Instant.EPOCH.truncatedTo(MILLIS))
    assertEquals(Instant.EPOCH, Instant.EPOCH.truncatedTo(SECONDS))
    assertEquals(Instant.EPOCH, Instant.EPOCH.truncatedTo(DAYS))

    assertEquals(Instant.MIN, Instant.MIN.truncatedTo(MICROS))
    assertEquals(Instant.MIN, Instant.MIN.truncatedTo(MILLIS))
    assertEquals(Instant.MIN, Instant.MIN.truncatedTo(SECONDS))
    assertEquals(Instant.MIN, Instant.MIN.truncatedTo(DAYS))

    assertEquals(Instant.ofEpochSecond(31556889864403199L, 999999000), Instant.MAX.truncatedTo(MICROS))
    assertEquals(Instant.ofEpochSecond(31556889864403199L, 999000000), Instant.MAX.truncatedTo(MILLIS))
    assertEquals(Instant.ofEpochSecond(31556889864403199L), Instant.MAX.truncatedTo(SECONDS))
    assertEquals(Instant.ofEpochSecond(31556889864316800L), Instant.MAX.truncatedTo(DAYS))

    assertEquals(somePositiveInstant, somePositiveInstant.truncatedTo(MICROS))
    assertEquals(somePositiveInstant, somePositiveInstant.truncatedTo(MILLIS))
    assertEquals(Instant.ofEpochSecond(928392983L), somePositiveInstant.truncatedTo(SECONDS))
    assertEquals(Instant.ofEpochSecond(928368000L), somePositiveInstant.truncatedTo(DAYS))

    assertEquals(Instant.ofEpochSecond(-83827873287L, 88937000), someNegativeInstant.truncatedTo(MICROS))
    assertEquals(Instant.ofEpochSecond(-83827873287L, 89000000), someNegativeInstant.truncatedTo(MILLIS))
    assertEquals(Instant.ofEpochSecond(-83827873286L), someNegativeInstant.truncatedTo(SECONDS))
    assertEquals(Instant.ofEpochSecond(-83827872000L), someNegativeInstant.truncatedTo(DAYS))

    for (i <- samples;u <- dateBasedUnits.filter(_ != DAYS))
      expectThrows(classOf[UnsupportedTemporalTypeException], i.truncatedTo(u))
  }

  @Test def plus(): Unit = {
    for {
      i <- samples
      u <- ChronoUnit.values() if isSupported(u)
    } {
      assertEquals(i, i.plus(0, u))

      for (n <- Seq(-1000, -1, 1, 1000)) {
        if (!(i == Instant.MIN && n < 0) && !(i == Instant.MAX && n > 0))
          assertEquals(i.plusNanos(u.getDuration.toNanos * n), i.plus(n, u))
        else if ((i == Instant.MIN && n < 0) && (i == Instant.MAX && n > 0))
          expectThrows(classOf[DateTimeException], i.plus(n, u))
      }
    }
  }

  @Test def plusSeconds(): Unit = {
    for (i <- samples)
      assertEquals(i, i.plusSeconds(0))

    assertEquals(Instant.ofEpochSecond(1), Instant.EPOCH.plusSeconds(1))
    assertEquals(Instant.ofEpochSecond(-1), Instant.EPOCH.plusSeconds(-1))

    assertEquals(Instant.ofEpochSecond(-31557014167219199L), Instant.MIN.plusSeconds(1))
    expectThrows(classOf[DateTimeException], Instant.MIN.plusSeconds(-1))
    assertEquals(Instant.EPOCH, Instant.MIN.plusSeconds(31557014167219200L))

    expectThrows(classOf[DateTimeException], Instant.MAX.plusSeconds(1))
    assertEquals(Instant.ofEpochSecond(31556889864403198L, 999999999), Instant.MAX.plusSeconds(-1))
    assertEquals(Instant.ofEpochSecond(0, 999999999), Instant.MAX.plusSeconds(-31556889864403199L))

    assertEquals(Instant.ofEpochSecond(928392984L, 942000000), somePositiveInstant.plusSeconds(1))
    assertEquals(Instant.ofEpochSecond(928392982L, 942000000), somePositiveInstant.plusSeconds(-1))

    assertEquals(Instant.ofEpochSecond(-83827873286L, 88936253), someNegativeInstant.plusSeconds(1))
    assertEquals(Instant.ofEpochSecond(-83827873288L, 88936253), someNegativeInstant.plusSeconds(-1))
  }

  @Test def plusMillis(): Unit = {
    for (i <- samples)
      assertEquals(i, i.plusMillis(0))

    assertEquals(Instant.ofEpochMilli(1), Instant.EPOCH.plusMillis(1))
    assertEquals(Instant.ofEpochMilli(-1), Instant.EPOCH.plusMillis(-1))

    assertEquals(Instant.ofEpochSecond(-31557014167219200L, 1000000),
        Instant.MIN.plusMillis(1))
    assertEquals(Instant.ofEpochSecond(-31557014167219199L, 0),
        Instant.MIN.plusMillis(1000))
    expectThrows(classOf[DateTimeException], Instant.MIN.plusMillis(-1))

    expectThrows(classOf[DateTimeException], Instant.MAX.plusMillis(1))
    assertEquals(Instant.ofEpochSecond(31556889864403199L, 998999999),
        Instant.MAX.plusMillis(-1))
    assertEquals(Instant.ofEpochSecond(31556889864403198L, 999999999),
        Instant.MAX.plusMillis(-1000))

    assertEquals(Instant.ofEpochSecond(928392983L, 943000000), somePositiveInstant.plusMillis(1))
    assertEquals(Instant.ofEpochSecond(928392983L, 941000000), somePositiveInstant.plusMillis(-1))

    assertEquals(Instant.ofEpochSecond(-83827873287L, 89936253), someNegativeInstant.plusMillis(1))
    assertEquals(Instant.ofEpochSecond(-83827873287L, 87936253), someNegativeInstant.plusMillis(-1))
  }

  @Test def plusNanos(): Unit = {
    for (i <- samples)
      assertEquals(i, i.plusNanos(0))

    assertEquals(Instant.ofEpochSecond(0, 1), Instant.EPOCH.plusNanos(1))
    assertEquals(Instant.ofEpochSecond(-1, 999999999), Instant.EPOCH.plusNanos(-1))

    assertEquals(Instant.ofEpochSecond(-31557014167219200L, 1),
        Instant.MIN.plusNanos(1))
    assertEquals(Instant.ofEpochSecond(-31557014167219199L, 0),
        Instant.MIN.plusNanos(1000000000))
    expectThrows(classOf[DateTimeException], Instant.MIN.plusNanos(-1))

    expectThrows(classOf[DateTimeException], Instant.MAX.plusMillis(1))
    assertEquals(Instant.ofEpochSecond(31556889864403199L, 999999998),
        Instant.MAX.plusNanos(-1))
    assertEquals(Instant.ofEpochSecond(31556889864403198L, 999999999),
        Instant.MAX.plusNanos(-1000000000))

    assertEquals(Instant.ofEpochSecond(928392983L, 942000001), somePositiveInstant.plusNanos(1))
    assertEquals(Instant.ofEpochSecond(928392983L, 941999999), somePositiveInstant.plusNanos(-1))

    assertEquals(Instant.ofEpochSecond(-83827873287L, 88936254), someNegativeInstant.plusNanos(1))
    assertEquals(Instant.ofEpochSecond(-83827873287L, 88936252), someNegativeInstant.plusNanos(-1))
  }

  @Test def minusSeconds(): Unit = {
    for (i <- samples)
      assertEquals(i, i.minusSeconds(0))

    assertEquals(Instant.ofEpochSecond(-1), Instant.EPOCH.minusSeconds(1))
    assertEquals(Instant.ofEpochSecond(1), Instant.EPOCH.minusSeconds(-1))

    expectThrows(classOf[DateTimeException], Instant.MIN.minusSeconds(1))
    assertEquals(Instant.ofEpochSecond(-31557014167219199L), Instant.MIN.minusSeconds(-1))
    assertEquals(Instant.EPOCH, Instant.MIN.minusSeconds(-31557014167219200L))

    assertEquals(Instant.ofEpochSecond(31556889864403198L, 999999999), Instant.MAX.minusSeconds(1))
    expectThrows(classOf[DateTimeException], Instant.MAX.minusSeconds(-1))
    assertEquals(Instant.ofEpochSecond(0, 999999999), Instant.MAX.minusSeconds(31556889864403199L))

    assertEquals(Instant.ofEpochSecond(928392982L, 942000000), somePositiveInstant.minusSeconds(1))
    assertEquals(Instant.ofEpochSecond(928392984L, 942000000), somePositiveInstant.minusSeconds(-1))

    assertEquals(Instant.ofEpochSecond(-83827873288L, 88936253), someNegativeInstant.minusSeconds(1))
    assertEquals(Instant.ofEpochSecond(-83827873286L, 88936253), someNegativeInstant.minusSeconds(-1))
  }

  @Test def minusMillis(): Unit = {
    for (i <- samples)
      assertEquals(i, i.minusMillis(0))

    assertEquals(Instant.ofEpochMilli(-1), Instant.EPOCH.minusMillis(1))
    assertEquals(Instant.ofEpochMilli(1), Instant.EPOCH.minusMillis(-1))

    expectThrows(classOf[DateTimeException], Instant.MIN.minusMillis(1))
    assertEquals(Instant.ofEpochSecond(-31557014167219200L, 1000000),
        Instant.MIN.minusMillis(-1))
    assertEquals(Instant.ofEpochSecond(-31557014167219199L, 0),
        Instant.MIN.minusMillis(-1000))

    assertEquals(Instant.ofEpochSecond(31556889864403199L, 998999999),
        Instant.MAX.minusMillis(1))
    assertEquals(Instant.ofEpochSecond(31556889864403198L, 999999999),
        Instant.MAX.minusMillis(1000))
    expectThrows(classOf[DateTimeException], Instant.MAX.minusMillis(-1))

    assertEquals(Instant.ofEpochSecond(928392983L, 941000000), somePositiveInstant.minusMillis(1))
    assertEquals(Instant.ofEpochSecond(928392983L, 943000000), somePositiveInstant.minusMillis(-1))

    assertEquals(Instant.ofEpochSecond(-83827873287L, 87936253), someNegativeInstant.minusMillis(1))
    assertEquals(Instant.ofEpochSecond(-83827873287L, 89936253), someNegativeInstant.minusMillis(-1))
  }

  @Test def minusNanos(): Unit = {
    for (i <- samples)
      assertEquals(i, i.minusNanos(0))

    assertEquals(Instant.ofEpochSecond(-1, 999999999), Instant.EPOCH.minusNanos(1))
    assertEquals(Instant.ofEpochSecond(0, 1), Instant.EPOCH.minusNanos(-1))

    expectThrows(classOf[DateTimeException], Instant.MIN.minusNanos(1))
    assertEquals(Instant.ofEpochSecond(-31557014167219200L, 1),
        Instant.MIN.minusNanos(-1))
    assertEquals(Instant.ofEpochSecond(-31557014167219199L, 0),
        Instant.MIN.minusNanos(-1000000000))

    assertEquals(Instant.ofEpochSecond(31556889864403199L, 999999998),
        Instant.MAX.minusNanos(1))
    assertEquals(Instant.ofEpochSecond(31556889864403198L, 999999999),
        Instant.MAX.minusNanos(1000000000))
    expectThrows(classOf[DateTimeException], Instant.MAX.minusNanos(-1))

    assertEquals(Instant.ofEpochSecond(928392983L, 941999999), somePositiveInstant.minusNanos(1))
    assertEquals(Instant.ofEpochSecond(928392983L, 942000001), somePositiveInstant.minusNanos(-1))

    assertEquals(Instant.ofEpochSecond(-83827873287L, 88936252), someNegativeInstant.minusNanos(1))
    assertEquals(Instant.ofEpochSecond(-83827873287L, 88936254), someNegativeInstant.minusNanos(-1))
  }

  @Test def adjustInto(): Unit = {
    for {
      i1 <- samples
      i2 <- samples
    } {
      testDateTime(i1.adjustInto(i2))(i1)
    }

    for {
      i <- samples
      date <- Seq(LocalDate.MIN, LocalDate.MAX)
    } {
      expectThrows(classOf[DateTimeException], i.adjustInto(date))
    }
  }

  @Test def until(): Unit = {
    expectThrows(classOf[ArithmeticException], Instant.MIN.until(Instant.MAX, NANOS))
    expectThrows(classOf[ArithmeticException], Instant.MIN.until(Instant.MAX, MICROS))
    expectThrows(classOf[ArithmeticException], Instant.MIN.until(Instant.MAX, MILLIS))

    assertEquals(31557014167219200L, Instant.MIN.until(Instant.EPOCH, SECONDS))
    assertEquals(525950236120320L, Instant.MIN.until(Instant.EPOCH, MINUTES))
    assertEquals(8765837268672L, Instant.MIN.until(Instant.EPOCH, HOURS))
    assertEquals(730486439056L, Instant.MIN.until(Instant.EPOCH, HALF_DAYS))
    assertEquals(365243219528L, Instant.MIN.until(Instant.EPOCH, DAYS))

    assertEquals(-31556889864403199L, Instant.MAX.until(Instant.EPOCH, SECONDS))
    assertEquals(-525948164406719L, Instant.MAX.until(Instant.EPOCH, MINUTES))
    assertEquals(-8765802740111L, Instant.MAX.until(Instant.EPOCH, HOURS))
    assertEquals(-730483561675L, Instant.MAX.until(Instant.EPOCH, HALF_DAYS))
    assertEquals(-365241780837L, Instant.MAX.until(Instant.EPOCH, DAYS))

    assertEquals(63113904031622399L, Instant.MIN.until(Instant.MAX, SECONDS))
    assertEquals(1051898400527039L, Instant.MIN.until(Instant.MAX, MINUTES))
    assertEquals(17531640008783L, Instant.MIN.until(Instant.MAX, HOURS))
    assertEquals(1460970000731L, Instant.MIN.until(Instant.MAX, HALF_DAYS))
    assertEquals(730485000365L, Instant.MIN.until(Instant.MAX, DAYS))

    assertEquals(84756266270854L, someNegativeInstant.until(somePositiveInstant, MILLIS))
    assertEquals(84756266270L, someNegativeInstant.until(somePositiveInstant, SECONDS))
    assertEquals(1412604437L, someNegativeInstant.until(somePositiveInstant, MINUTES))
    assertEquals(23543407L, someNegativeInstant.until(somePositiveInstant, HOURS))
    assertEquals(1961950L, someNegativeInstant.until(somePositiveInstant, HALF_DAYS))
    assertEquals(980975L, someNegativeInstant.until(somePositiveInstant, DAYS))
  }

  @Test def compareTo(): Unit = {
    for (i <- samples)
      assertEquals(0, i.compareTo(i))

    assert(Instant.MIN.compareTo(Instant.MAX) < 0)
    assert(Instant.MIN.compareTo(Instant.EPOCH) < 0)
    assert(Instant.MAX.compareTo(Instant.MIN) > 0)
    assert(Instant.MAX.compareTo(Instant.EPOCH) > 0)
  }

  @Test def isAfter(): Unit = {
    assertFalse(Instant.MIN.isAfter(Instant.MIN))
    assertFalse(Instant.MIN.isAfter(Instant.MAX))
    assertFalse(Instant.MIN.isAfter(Instant.EPOCH))
    assertTrue(Instant.MAX.isAfter(Instant.MIN))
    assertFalse(Instant.MAX.isAfter(Instant.MAX))
    assertTrue(Instant.MAX.isAfter(Instant.EPOCH))
  }

  @Test def isBefore(): Unit = {
    assertFalse(Instant.MIN.isBefore(Instant.MIN))
    assertTrue(Instant.MIN.isBefore(Instant.MAX))
    assertTrue(Instant.MIN.isBefore(Instant.EPOCH))
    assertFalse(Instant.MAX.isBefore(Instant.MIN))
    assertFalse(Instant.MAX.isBefore(Instant.MAX))
    assertFalse(Instant.MAX.isBefore(Instant.EPOCH))
  }

  @Test def toEpochMilli(): Unit = {
    assertEquals(0L, Instant.EPOCH.toEpochMilli)
    assertEquals(928392983942L, somePositiveInstant.toEpochMilli)
    assertEquals(-83827873286912L, someNegativeInstant.toEpochMilli)
  }

  @Test def toStringOutput(): Unit = {
    assertEquals("1970-01-01T00:00:00Z", Instant.EPOCH.toString)
    assertEquals("-1000000000-01-01T00:00:00Z", Instant.MIN.toString)

    // https://github.com/scala-js/scala-js-java-time/issues/23
    assertEquals("1970-01-01T00:10:00.100Z", Instant.EPOCH.plus(10, MINUTES).plusMillis(100).toString)

    assertEquals("+1000000000-12-31T23:59:59.999999999Z", Instant.MAX.toString)
    assertEquals("1999-06-03T06:56:23.942Z", somePositiveInstant.toString)
    assertEquals("-0687-08-07T23:38:33.088936253Z", someNegativeInstant.toString)
  }

  @Test def now(): Unit = {
    assertNotNull(Instant.now)
  }

  @Test def ofEpochSecond(): Unit = {
    assertEquals(Instant.EPOCH, Instant.ofEpochSecond(0))
    assertEquals(Instant.EPOCH, Instant.ofEpochSecond(0, 0))

    assertEquals(Instant.MIN, Instant.ofEpochSecond(-31557014167219200L))
    assertEquals(Instant.MIN, Instant.ofEpochSecond(-31557014167219200L, 0))
    assertEquals(Instant.MAX, Instant.ofEpochSecond(31556889864403199L, 999999999))

    expectThrows(classOf[DateTimeException], Instant.ofEpochSecond(-31557014167219200L, Long.MinValue))

    val limits = Seq(-31557014167219200L, 31557014167219200L)
    val invalidNanos = Seq(Long.MinValue, -1L, 1000000000L, Long.MaxValue)

    val invalidPairs = limits.flatMap(l => invalidNanos.map(n => (l, n))).filter {
      case (a, b) => ((a < 0) && (b < 0)) || ((a > 0) && (b > 0))
    }
    for ((s, n) <- invalidPairs)
      expectThrows(classOf[DateTimeException], Instant.ofEpochSecond(s, n))
  }

  @Test def ofEpochMilli(): Unit = {
    assertEquals(Instant.EPOCH, Instant.ofEpochMilli(0))
  }

  @Test def from(): Unit = {
    for (i <- samples)
      assertSame(i, Instant.from(i))

    val aTime = LocalTime.ofNanoOfDay(98392983293L)
    expectThrows(classOf[DateTimeException], Instant.from(aTime))

    val aDate = LocalDate.ofEpochDay(392889321L)
    expectThrows(classOf[DateTimeException], Instant.from(aDate))

    val aYear = Year.of(329)
    expectThrows(classOf[DateTimeException], Instant.from(aYear))
  }

}
