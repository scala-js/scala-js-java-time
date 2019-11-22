package java.time.chrono

import java.time.temporal.{Temporal, TemporalAmount}
import java.time.temporal.TemporalUnit

trait ChronoPeriod extends TemporalAmount {
  def getChronology(): Chronology

  def isZero(): Boolean = forallUnits(get(_) == 0)

  def isNegative(): Boolean = !forallUnits(get(_) >= 0)

  def plus(amount: TemporalAmount): ChronoPeriod

  def minus(amount: TemporalAmount): ChronoPeriod

  def multipliedBy(scalar: Int): ChronoPeriod

  def negated(): ChronoPeriod = multipliedBy(-1)

  def normalized(): ChronoPeriod

  def addTo(temporal: Temporal): Temporal

  def subtractFrom(temporal: Temporal): Temporal

  private def forallUnits(f: TemporalUnit => Boolean): Boolean = {
    // scalastyle:off return
    val iter = getUnits().iterator()
    while (iter.hasNext()) {
      if (!f(iter.next()))
        return false
    }
    true
    // scalastyle:on return
  }
}

object ChronoPeriod {
  def between(start: ChronoLocalDate, end: ChronoLocalDate): ChronoPeriod =
    start.until(end)
}
