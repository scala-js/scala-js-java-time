package java.time

import scala.scalajs.js

import java.time.Preconditions.requireDateTimeParse
import java.time.chrono.IsoChronology
import java.time.format.DateTimeParseException
import java.time.temporal._

import scala.util.control.NonFatal

/** Created by alonsodomin on 26/12/2015. */
final class Instant private (private val seconds: Long, private val nanos: Int)
    extends TemporalAccessor with Temporal with TemporalAdjuster
    with Comparable[Instant] with java.io.Serializable {

  import Preconditions._
  import Constants._
  import Instant._
  import ChronoField._
  import ChronoUnit._

  requireDateTime(seconds >= MinSecond && seconds <= MaxSecond,
      s"Invalid seconds: $seconds")
  requireDateTime(nanos >= 0 && nanos <= MaxNanosInSecond,
      s"Invalid nanos: $nanos")

  def isSupported(field: TemporalField): Boolean = field match {
    case _: ChronoField =>
      field == INSTANT_SECONDS || field == NANO_OF_SECOND || field == MICRO_OF_SECOND ||
        field == MILLI_OF_SECOND

    case null => false
    case _    => field.isSupportedBy(this)
  }

  def isSupported(unit: TemporalUnit): Boolean = unit match {
    case _: ChronoUnit => unit.isTimeBased || unit == DAYS
    case null          => false
    case _             => unit.isSupportedBy(this)
  }

  // Implemented by TemporalAccessor
  // def range(field: TemporalField): ValueRange

  // Implemented by TemporalAccessor
  // def get(field: TemporalField): Int

  def getLong(field: TemporalField): Long = field match {
    case INSTANT_SECONDS => seconds
    case NANO_OF_SECOND  => nanos
    case MICRO_OF_SECOND => nanos / NANOS_IN_MICRO
    case MILLI_OF_SECOND => nanos / NANOS_IN_MILLI

    case _: ChronoField =>
      throw new UnsupportedTemporalTypeException(s"Field not supported: $field")

    case _ => field.getFrom(this)
  }

  def getEpochSecond(): Long = seconds

  def getNano(): Int = nanos

  override def `with`(adjuster: TemporalAdjuster): Instant =
    adjuster.adjustInto(this).asInstanceOf[Instant]

  def `with`(field: TemporalField, value: Long): Instant = {
    val msg = s"Invalid value for field $field: $value"
    field match {
      case INSTANT_SECONDS =>
        requireDateTime(value >= MinSecond && value <= MaxSecond, msg)
        if (value == seconds) this
        else ofEpochSecond(value, nanos)

      case NANO_OF_SECOND =>
        requireDateTime(value >= 0 && value <= MaxNanosInSecond, msg)
        if (value == nanos) this
        else ofEpochSecond(seconds, value)

      case MICRO_OF_SECOND =>
        requireDateTime(value >= 0 && value <= MaxNanosInSecond / NANOS_IN_MICRO, msg)
        val newNanos = value * NANOS_IN_MICRO
        if (newNanos == nanos) this
        else ofEpochSecond(seconds, newNanos)

      case MILLI_OF_SECOND =>
        requireDateTime(value >= 0 && value <= MaxNanosInSecond / NANOS_IN_MILLI, msg)
        val newNanos = value * NANOS_IN_MILLI
        if (newNanos == nanos) this
        else ofEpochSecond(seconds, newNanos)

      case _: ChronoField =>
        throw new UnsupportedTemporalTypeException(s"Field not supported: $field")

      case _ => field.adjustInto(this, value)
    }
  }

  def truncatedTo(unit: TemporalUnit): Instant = {
    if (unit == NANOS) {
      this
    } else {
      val duration = unit.getDuration
      if (duration.getSeconds > SECONDS_IN_DAY)
        throw new UnsupportedTemporalTypeException("Unit too large")

      val unitNanos = duration.toNanos
      if ((NANOS_IN_DAY % unitNanos) != 0)
        throw new UnsupportedTemporalTypeException("Unit must be a multiple of a standard day")

      val extraNanos = (seconds % SECONDS_IN_DAY) * NANOS_IN_SECOND + nanos
      val extraNanosPerUnit = (extraNanos / unitNanos) * unitNanos
      plusNanos(extraNanosPerUnit - extraNanos)
    }
  }

  def plus(amount: Long, unit: TemporalUnit): Instant = unit match {
    case NANOS     => plusNanos(amount)
    case MICROS    => plusNanos(Math.multiplyExact(amount, NANOS_IN_MICRO))
    case MILLIS    => plusMillis(amount)
    case SECONDS   => plusSeconds(amount)
    case MINUTES   => plusSeconds(Math.multiplyExact(amount, SECONDS_IN_MINUTE))
    case HOURS     => plusSeconds(Math.multiplyExact(amount, SECONDS_IN_HOUR))
    case HALF_DAYS => plusSeconds(Math.multiplyExact(amount, SECONDS_IN_DAY) / 2)
    case DAYS      => plusSeconds(Math.multiplyExact(amount, SECONDS_IN_DAY))

    case _: ChronoUnit =>
      throw new UnsupportedTemporalTypeException(s"Unit not supported: $unit")

    case _ => unit.addTo(this, amount)
  }

  def plusSeconds(secs: Long): Instant = plus(secs, 0)

  def plusMillis(millis: Long): Instant =
    plusNanos(Math.multiplyExact(millis, NANOS_IN_MILLI))

  def plusNanos(nans: Long): Instant = plus(0, nans)

  private def plus(secs: Long, nans: Long): Instant = {
    if (secs == 0 && nans == 0) {
      this
    } else {
      val secondsFromNanos = Math.floorDiv(nans, NANOS_IN_SECOND)
      val remainingNanos = Math.floorMod(nans, NANOS_IN_SECOND)
      val additionalSecs = Math.addExact(secs, secondsFromNanos)
      ofEpochSecond(
          Math.addExact(seconds, additionalSecs),
          Math.addExact(nanos, remainingNanos)
      )
    }
  }

  override def minus(amount: TemporalAmount): Instant =
    amount.subtractFrom(this).asInstanceOf[Instant]

  override def minus(amount: Long, unit: TemporalUnit): Instant = {
    if (amount == Long.MinValue) plus(Long.MaxValue, unit).plus(1, unit)
    else plus(-amount, unit)
  }

  def minusSeconds(secs: Long): Instant = minus(secs, SECONDS)

  def minusMillis(millis: Long): Instant = minus(millis, MILLIS)

  def minusNanos(nans: Long): Instant = minus(nans, NANOS)

  // Not implemented
  // def query[R](query: TemporalQuery[R]): R

  def adjustInto(temporal: Temporal): Temporal =
    temporal.`with`(INSTANT_SECONDS, seconds).`with`(NANO_OF_SECOND, nanos)

  def until(end: Temporal, unit: TemporalUnit): Long = {
    val endInstant = from(end)

    def nanosUntil: Long = {
      val secsDiff: Long = Math.subtractExact(endInstant.seconds, seconds)
      val nanosBase: Long = Math.multiplyExact(secsDiff, NANOS_IN_SECOND)
      Math.addExact(nanosBase, endInstant.nanos - nanos)
    }

    def secondsUntil: Long = {
      val secsDiff: Long = Math.subtractExact(endInstant.seconds, seconds)
      val nanosDiff: Int = endInstant.nanos - nanos

      // correct "off by one" in the seconds difference
      if (secsDiff > 0 && nanosDiff < 0) {
        secsDiff - 1
      } else if (secsDiff < 0 && nanosDiff > 0) {
        secsDiff + 1
      } else {
        secsDiff
      }
    }

    unit match {
      case NANOS     => nanosUntil
      case MICROS    => nanosUntil / NANOS_IN_MICRO
      case MILLIS    => Math.subtractExact(endInstant.toEpochMilli(), toEpochMilli())
      case SECONDS   => secondsUntil
      case MINUTES   => secondsUntil / SECONDS_IN_MINUTE
      case HOURS     => secondsUntil / SECONDS_IN_HOUR
      case HALF_DAYS => secondsUntil / (SECONDS_IN_HOUR * 12)
      case DAYS      => secondsUntil / SECONDS_IN_DAY

      case _: ChronoUnit =>
        throw new UnsupportedTemporalTypeException(s"Unit not supported: $unit")

      case _ => unit.between(this, end)
    }
  }

  // Not implemented
  // def atOffset(offset: ZoneOffset): OffsetDateTime

  // Not implemented
  // def atZone(zone: ZoneId): ZonedDateTime

  def toEpochMilli(): Long = {
    val millis: Long = Math.multiplyExact(seconds, MILLIS_IN_SECOND.toLong)
    millis + nanos / NANOS_IN_MILLI
  }

  def compareTo(that: Instant): Int = {
    val cmp = seconds compareTo that.seconds
    if (cmp != 0) {
      cmp
    } else {
      nanos compareTo that.nanos
    }
  }

  def isAfter(that: Instant): Boolean = compareTo(that) > 0

  def isBefore(that: Instant): Boolean = compareTo(that) < 0

  override def equals(other: Any): Boolean = other match {
    case that: Instant => seconds == that.seconds && nanos == that.nanos
    case _             => false
  }

  override def hashCode(): Int = (seconds + 51 * nanos).hashCode

  override def toString: String = {
    def tenThousandPartsAndRemainder: (Long, Long) = {
      if (seconds < -secondsFromZeroToEpoch) {
        val quot = seconds / secondsInTenThousandYears
        val rem = seconds % secondsInTenThousandYears
        (quot, rem)
      } else {
        val quot = Math.floorDiv(seconds, secondsInTenThousandYears)
        val rem = Math.floorMod(seconds, secondsInTenThousandYears)
        (quot, rem)
      }
    }

    def dateTime(epochSecond: Long): (LocalDate, LocalTime) = {
      val epochDay = Math.floorDiv(epochSecond, SECONDS_IN_DAY)
      val secondsOfDay = Math.floorMod(epochSecond, SECONDS_IN_DAY).toInt
      (LocalDate.ofEpochDay(epochDay), LocalTime.ofSecondOfDay(secondsOfDay).withNano(nanos))
    }

    val (hi, lo) = tenThousandPartsAndRemainder
    val epochSecond = lo
    val (date, time) = dateTime(epochSecond)

    val years = hi * 10000 + date.getYear

    val yearSegment = {
      if (years > 9999) s"+$years"
      else if (years < 0 && years > -1000) "-%04d".format(Math.abs(years))
      else years.toString
    }

    val monthSegement = "%02d".format(date.getMonthValue)
    val daySegment = "%02d".format(date.getDayOfMonth)

    val timePart = {
      val timeStr = time.toString
      if (time.getSecond == 0 && time.getNano == 0) timeStr + ":00"
      else timeStr
    }

    val dateSegment = s"$yearSegment-$monthSegement-$daySegment"
    s"${dateSegment}T${timePart}Z"
  }

  // Not implemented
  // def format(format: DateTimeFormatter): String

}

object Instant {
  import Constants._
  import ChronoField._

  private final val iso = IsoChronology.INSTANCE

  final val EPOCH = new Instant(0, 0)

  private val MinSecond = -31557014167219200L
  private val MaxSecond = 31556889864403199L
  private val MaxNanosInSecond = 999999999

  private val MaxYear = 1000000000
  private val MinYear = -1000000000

  /*
   * 146097 days in 400 years
   * 86400 seconds in a day
   * 25 cycles of 400 years
   */
  private val secondsInTenThousandYears = 146097L * SECONDS_IN_DAY * 25L
  private val secondsFromZeroToEpoch = ((146097L * 5L) - (30L * 365L + 7L)) * SECONDS_IN_DAY

  final val MIN = ofEpochSecond(MinSecond)
  final val MAX = ofEpochSecond(MaxSecond, MaxNanosInSecond)

  def now(): Instant = {
    val date = new js.Date()
    ofEpochMilli(date.getTime.toLong)
  }

  // Not implemented
  // def now(zoneId: ZoneId): Instant
  // def now(clock: Clock): Instant

  def ofEpochSecond(epochSecond: Long): Instant =
    ofEpochSecond(epochSecond, 0)

  def ofEpochSecond(epochSecond: Long, nanos: Long): Instant = {
    val adjustedSeconds = Math.addExact(epochSecond,
        Math.floorDiv(nanos, NANOS_IN_SECOND))
    val adjustedNanos = Math.floorMod(nanos, NANOS_IN_SECOND).toInt
    new Instant(adjustedSeconds, adjustedNanos)
  }

  def ofEpochMilli(epochMilli: Long): Instant = {
    val seconds = Math.floorDiv(epochMilli, MILLIS_IN_SECOND)
    val nanos = Math.floorMod(epochMilli, MILLIS_IN_SECOND)
    new Instant(seconds, nanos.toInt * NANOS_IN_MILLI)
  }

  def from(temporal: TemporalAccessor): Instant = temporal match {
    case temporal: Instant => temporal
    case _                 =>
      ofEpochSecond(temporal.getLong(INSTANT_SECONDS), temporal.getLong(NANO_OF_SECOND))
  }

  private def parseSegment(segment: String, classifier: String): Int = {
    try {
      segment.toInt
    } catch {
      case _: NumberFormatException =>
        throw new DateTimeParseException(s"$segment is not a valid $classifier",
            segment, 0)
    }
  }

  private def toEpochDay(year: Int, month: Int, day: Int): Long = {
    val leapYear = iso.isLeapYear(year)

    val extremeLeapYear = 999999996
    val epochDaysToAccountForExtreme = (3 * DAYS_IN_YEAR) + DAYS_IN_LEAP_YEAR

    requireDateTimeParse(year <= MaxYear || year >= MinYear,
        s"$year out of bounds, year > 1000000000 || year < -1000000000",
        year.toString, 0)

    val monthDay = MonthDay.of(month, day)
    if (monthDay.getMonth == Month.FEBRUARY && leapYear) {
      requireDateTimeParse(monthDay.getDayOfMonth <= 29,
          "Day range out of bounds <= 29 for leap years", day.toString, 0)
    }

    if (year == MaxYear)
      LocalDate.of(extremeLeapYear, month, day).toEpochDay + epochDaysToAccountForExtreme
    else if (year == MinYear)
      LocalDate.of(-extremeLeapYear, month, day).toEpochDay - epochDaysToAccountForExtreme
    else
      LocalDate.of(year, month, day).toEpochDay
  }

  def parse(text: CharSequence): Instant = {
    try {
      val pattern = """(^[-+]?)(\d*)-(\d*)-(\d*)T(\d*):(\d*):(\d*).?(\d*)Z""".r
      val pattern(sign, yearSegment, monthSegment, daySegment,
          hourSegment, minutesSegment, secondsSegment, nanosecondsSegment) = text

      val year = parseSegment(sign + yearSegment, "year")
      val month = parseSegment(monthSegment, "month")
      val day = parseSegment(daySegment, "day")
      val nanoPower = 9

      requireDateTimeParse(!((sign != "+") && (year > 9999)),
          s"year > 9999 must be preceded by [+]", text, 0)

      val days = toEpochDay(year, month, day)
      val dayOffset = days

      val hourOffset = parseSegment(hourSegment, "hour")
      val minuteOffset = parseSegment(minutesSegment, "minutes")
      val secondsOffset = parseSegment(secondsSegment, "seconds")

      requireDateTimeParse(hourOffset <= HOURS_IN_DAY,
          s"hours are > $HOURS_IN_DAY", text, 0)

      requireDateTimeParse(minuteOffset <= MINUTES_IN_HOUR,
          s"minutes are > $MINUTES_IN_HOUR", text, 0)

      requireDateTimeParse(secondsOffset <= SECONDS_IN_MINUTE,
          s"seconds are > $SECONDS_IN_MINUTE", text, 0)

      val nanos = if (nanosecondsSegment != "") {
        val scale = Math.pow(10, nanoPower - nanosecondsSegment.length).toInt
        parseSegment(nanosecondsSegment, "nanoseconds") * scale
      } else {
        0
      }

      val epochSecondsOffset = {
        dayOffset * SECONDS_IN_DAY +
        hourOffset * SECONDS_IN_HOUR +
        minuteOffset * SECONDS_IN_MINUTE +
        secondsOffset
      }

      new Instant(epochSecondsOffset, nanos)
    } catch {
      case err: DateTimeParseException =>
        throw err
      case NonFatal(err) =>
        throw new DateTimeParseException(s"Invalid date $text", text, 0)
    }
  }
}
