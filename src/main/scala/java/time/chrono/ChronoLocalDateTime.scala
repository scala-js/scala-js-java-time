package java.time.chrono

import java.time.LocalTime
import java.time.temporal._

trait ChronoLocalDateTime[D <: ChronoLocalDate]
    extends Temporal with TemporalAdjuster with Comparable[ChronoLocalDateTime[_]] {

//  def query[R](query: TemporalQuery[R]): R = {
//    if (query == TemporalQueries.zoneId() || query == TemporalQueries.zone() || query == TemporalQueries.offset()) {
//      null.asInstanceOf[R]
//    } else if (query == TemporalQueries.localTime()) {
//      toLocalTime().asInstanceOf[R]
//    } else if (query == TemporalQueries.chronology()) {
//      getChronology().asInstanceOf[R]
//    } else if (query == TemporalQueries.precision()) {
//      ChronoUnit.NANOS.asInstanceOf[R]
//    } else {
//      // inline TemporalAccessor.super.query(query) as an optimization
//      // non-JDK classes are not permitted to make this optimization
//      query.queryFrom(this)
//    }
//  }

  def toLocalDate(): D

  def getChronology(): Chronology = toLocalDate().getChronology()

  def toLocalTime(): LocalTime

  def isSupported(field: TemporalField): Boolean

  def isSupported(unit: TemporalUnit): Boolean = unit match {
    case u: ChronoUnit => u != ChronoUnit.FOREVER
    case null          => false
    case _             => unit.isSupportedBy(this)
  }

  def adjustInto(temporal: Temporal): Temporal =
    temporal
      .`with`(ChronoField.EPOCH_DAY, toLocalDate().toEpochDay)
      .`with`(ChronoField.NANO_OF_DAY, toLocalTime().toNanoOfDay())
}

object ChronoLocalDateTime {
  def from(temporal: TemporalAccessor): ChronoLocalDateTime[_] = {
    temporal match {
      case t: ChronoLocalDateTime[_] => t
      case t =>
        val chrono = t.query(TemporalQueries.chronology())
        chrono.localDateTime(temporal)
    }
  }
}
