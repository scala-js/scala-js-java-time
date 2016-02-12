package java.time.format

import java.time.DateTimeException
import java.util.Calendar

final class TextStyle private(name: String, ordinal: Int, calendarStyle: Int, zoneNameStyleIndex: Int)
  extends Enum[TextStyle](name, ordinal) {

  def asNormal(): TextStyle = TextStyle.of(ordinal & ~1)

  def asStandalone(): TextStyle = TextStyle.of(ordinal | 1)

  def isStandalone(): Boolean = (ordinal & 1) == 1

  private[format] def toCalendarStyle(): Int = calendarStyle

  private[format] def zoneNameStyleIndex(): Int = zoneNameStyleIndex
}

object TextStyle {
  final val FULL = new TextStyle("FULL", 0, Calendar.LONG_FORMAT, 0)

  final val FULL_STANDALONE = new TextStyle("FULL_STANDALONE", 1, Calendar.LONG_STANDALONE, 0)

  final val SHORT = new TextStyle("SHORT", 2, Calendar.SHORT_FORMAT, 1)

  final val SHORT_STANDALONE = new TextStyle("SHORT_STANDALONE", 3, Calendar.SHORT_STANDALONE, 1)

  final val NARROW = new TextStyle("NARROW", 4, Calendar.NARROW_FORMAT, 1)

  final val NARROW_STANDALONE = new TextStyle("NARROW_STANDALONE", 5, Calendar.NARROW_STANDALONE, 1)

  private val styles =
    Seq(FULL, FULL_STANDALONE, SHORT, SHORT_STANDALONE, NARROW, NARROW_STANDALONE)

  def values(): Array[TextStyle] = styles.toArray

  def valueOf(name: String): TextStyle = styles.find(_.name == name).getOrElse {
    throw new IllegalArgumentException(s"No such style: $name")
  }

  private def of(style: Int): TextStyle = styles.lift(style).getOrElse {
    throw new DateTimeException(s"Invalid value for style: $style")
  }
}
