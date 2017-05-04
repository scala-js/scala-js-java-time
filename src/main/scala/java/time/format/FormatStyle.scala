package java.time.format

final class FormatStyle private(name: String, ordinal: Int)
  extends Enum[FormatStyle](name, ordinal)

object FormatStyle {
  final val FULL = new FormatStyle("FULL", 0)

  final val LONG = new FormatStyle("LONG", 1)

  final val MEDIUM = new FormatStyle("MEDIUM", 2)

  final val SHORT = new FormatStyle("SHORT", 3)

  private val styles =
    Seq(FULL, LONG, MEDIUM, SHORT)

  def values(): Array[FormatStyle] = styles.toArray

  def valueOf(name: String): FormatStyle = styles.find(_.name == name).getOrElse {
    throw new IllegalArgumentException(s"No such style: $name")
  }
}
