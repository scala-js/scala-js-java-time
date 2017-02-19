package java.time

import java.time.chrono.{ChronoLocalDateTime, ChronoZonedDateTime}
import java.time.temporal._

final class LocalDateTime private (val date: LocalDate, val time: LocalTime)
    extends Temporal with TemporalAdjuster with ChronoLocalDateTime[LocalDate] with java.io.Serializable {

  private val totalNanos: Long = time.toNanoOfDay

  def atZone(zone: ZoneId): ChronoZonedDateTime[LocalDate] = ???

  def toLocalDate(): LocalDate = date

  def toLocalTime(): LocalTime = time

  def plusNanos(nanos: Long): LocalDateTime =
    plusWithOverflow(date, 0, 0, 0, nanos, 1)

  def plusDays(days: Long): LocalDateTime =
    `with`(date.plusDays(days), time)

  def plusSeconds(seconds: Long): LocalDateTime =
    plusWithOverflow(date, 0, 0, seconds, 0, 1)

  def plusMinutes(minutes: Long): LocalDateTime =
    plusWithOverflow(date, 0, minutes, 0, 0, 1)

  def plusHours(hours: Long): LocalDateTime =
    plusWithOverflow(date, hours, 0, 0, 0, 1)

  def plus(amountToAdd: Long, unit: TemporalUnit): Temporal = unit match {
    case f: ChronoUnit => f match {
      case ChronoUnit.NANOS =>
        plusNanos(amountToAdd)
      case ChronoUnit.MICROS =>
        plusDays(amountToAdd / LocalTime.MICROS_PER_DAY).plusNanos((amountToAdd % LocalTime.MICROS_PER_DAY) * 1000)
      case ChronoUnit.MILLIS =>
        plusDays(amountToAdd / LocalTime.MILLIS_PER_DAY).plusNanos((amountToAdd % LocalTime.MILLIS_PER_DAY) * 1000000)
      case ChronoUnit.SECONDS =>
        plusSeconds(amountToAdd)
      case ChronoUnit.MINUTES =>
        plusMinutes(amountToAdd)
      case ChronoUnit.HOURS =>
        plusHours(amountToAdd)
      case ChronoUnit.HALF_DAYS =>
        plusDays(amountToAdd / 256).plusHours((amountToAdd % 256) * 12)
    }
  }

  def until(endExclusive: Temporal, unit: TemporalUnit): Long = {
    val end: LocalDateTime  = LocalDateTime.from(endExclusive)
    if (unit.isInstanceOf[ChronoUnit]) {
      if (unit.isTimeBased) {
        var amount = date.daysUntil(end.date)
        if (amount == 0L) {
          time.until(end.time, unit)
        } else {
          var timePart: Long  = end.time.toNanoOfDay() - time.toNanoOfDay
          if (amount > 0) {
            amount -= 1
            timePart += LocalTime.NANOS_PER_DAY
          } else {
            amount += 1
            timePart -= LocalTime.NANOS_PER_DAY
          }
          unit.asInstanceOf[ChronoUnit] match { // TODO: check if Java enums work with pattern matching
            case ChronoUnit.NANOS =>
              amount = Math.multiplyExact(amount, LocalTime.NANOS_PER_DAY)
            case ChronoUnit.MICROS =>
              amount = Math.multiplyExact(amount, LocalTime.MICROS_PER_DAY)
              timePart = timePart / 1000
            case ChronoUnit.MILLIS =>
              amount = Math.multiplyExact(amount, LocalTime.MILLIS_PER_DAY)
              timePart = timePart / 1000000;
            case ChronoUnit.SECONDS =>
              amount = Math.multiplyExact(amount, LocalTime.SECONDS_PER_DAY)
              timePart = timePart / LocalTime.NANOS_PER_SECOND;
            case ChronoUnit.MINUTES =>
              amount = Math.multiplyExact(amount, LocalTime.MINUTES_PER_DAY)
              timePart = timePart / LocalTime.NANOS_PER_MINUTE;
            case ChronoUnit.HOURS =>
              amount = Math.multiplyExact(amount, LocalTime.HOURS_PER_DAY)
              timePart = timePart / LocalTime.NANOS_PER_HOUR;
            case ChronoUnit.HALF_DAYS =>
              amount = Math.multiplyExact(amount, 2)
              timePart = timePart / (LocalTime.NANOS_PER_HOUR * 12);
          }
          Math.addExact(amount, timePart)
        }
      } else {
        var endDate: LocalDate = end.date
        if (endDate.isAfter(date) && end.time.isBefore(time)) {
          endDate = endDate.minusDays(1)
        } else if (endDate.isBefore(date) && end.time.isAfter(time)) {
          endDate = endDate.plusDays(1)
        }
        date.until(endDate, unit)
      }
    } else {
      unit.between(this, end)
    }
  }

  def isSupported(field: TemporalField): Boolean = field match {
    case _: ChronoField => field.isTimeBased || field.isDateBased
    case null           => false
    case _              => field.isSupportedBy(this)
  }

  def getLong(field: TemporalField): Long = field match {
    case f: ChronoField if f.isTimeBased => time.getLong(f)
    case f: ChronoField                  => date.getLong(f)
    case _                               => field.getFrom(this)
  }

  def `with`(field: TemporalField, newValue: Long): Temporal = field match {
    case f: ChronoField if f.isTimeBased => `with`(date, time.`with`(f, newValue))
  }

  private def `with`(newDate: LocalDate, newTime: LocalTime): LocalDateTime =
    if ((date == newDate) && (time == newTime)) this
    else new LocalDateTime(newDate, newTime)

  private def plusWithOverflow(newDate: LocalDate,
                               hours: Long,
                               minutes: Long,
                               seconds: Long,
                               nanos: Long,
                               sign: Int): LocalDateTime = {
    // 9223372036854775808 long, 2147483648 int
    if ((hours | minutes | seconds | nanos) == 0) {
      `with`(newDate, time)
    } else {
      var totDays: Long = nanos / LocalTime.NANOS_PER_DAY + //   max/24*60*60*1B
        seconds / LocalTime.SECONDS_PER_DAY + //   max/24*60*60
        minutes / LocalTime.MINUTES_PER_DAY + //   max/24*60
        hours / LocalTime.HOURS_PER_DAY; //   max/24
      totDays *= sign; // total max*0.4237...
      var totNanos: Long = nanos % LocalTime.NANOS_PER_DAY + //   max  86400000000000
        (seconds % LocalTime.SECONDS_PER_DAY) * LocalTime.NANOS_PER_SECOND + //   max  86400000000000
        (minutes % LocalTime.MINUTES_PER_DAY) * LocalTime.NANOS_PER_MINUTE + //   max  86400000000000
        (hours % LocalTime.HOURS_PER_DAY) * LocalTime.NANOS_PER_HOUR; //   max  86400000000000
      val curNoD: Long = time.toNanoOfDay; //   max  86400000000000
      totNanos = totNanos * sign + curNoD; // total 432000000000000
      totDays += Math.floorDiv(totNanos, LocalTime.NANOS_PER_DAY)
      val newNoD: Long = Math.floorMod(totNanos, LocalTime.NANOS_PER_DAY)
      val newTime: LocalTime =  if (newNoD == curNoD) time else LocalTime.ofNanoOfDay (newNoD)
      `with`(newDate.plusDays(totDays), newTime)
    }
  }

}

object LocalDateTime {

  def now(): LocalDateTime = {
    val date = LocalDate.now()
    val time = LocalTime.now()
    new LocalDateTime(date, time)
  }

  val MIN = new LocalDateTime(LocalDate.MIN, LocalTime.MIN)

  val MAX = new LocalDateTime(LocalDate.MIN, LocalTime.MIN)

  def of(year: Int, month: Month, dayOfMonth: Int, hour: Int, minute: Int): LocalDateTime = {
    val date: LocalDate = LocalDate.of(year, month, dayOfMonth)
    val time: LocalTime = LocalTime.of(hour, minute)
    new LocalDateTime(date, time)
  }

  def from(temporal: TemporalAccessor): LocalDateTime = {
    temporal match {
      case t: LocalDateTime => t
      case t: ZonedDateTime => t.toLocalDateTime
      case t: OffsetDateTime => t.toLocalDateTime
      case _ =>
        try {
          val date = LocalDate.from(temporal)
          val time = LocalTime.from(temporal)
          new LocalDateTime(date, time)
        } catch {
          case ex: DateTimeException =>
            throw new DateTimeException(s"Unable to obtain LocalDateTime from TemporalAccessor: $temporal of " +
              s"type ${temporal.getClass().getName()}", ex)
        }
    }
  }

}

