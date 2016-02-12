package java.time.format

final class SignStyle private(name: String, ordinal: Int)
  extends Enum[SignStyle](name, ordinal)

object SignStyle {
  final val NORMAL = new SignStyle("NORMAL", 0)

  final val ALWAYS = new SignStyle("ALWAYS", 1)

  final val NEVER = new SignStyle("NEVER", 2)

  final val NOT_NEGATIVE = new SignStyle("NOT_NEGATIVE", 3)

  final val EXCEEDS_PAD = new SignStyle("EXCEEDS_PAD", 4)

  private val styles =
    Seq(NORMAL, ALWAYS, NEVER, NOT_NEGATIVE, EXCEEDS_PAD)

  def values(): Array[SignStyle] = styles.toArray

  def valueOf(name: String): SignStyle = styles.find(_.name == name).getOrElse {
    throw new IllegalArgumentException(s"No such style: $name")
  }
}
