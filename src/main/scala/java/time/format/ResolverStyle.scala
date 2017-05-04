package java.time.format

final class ResolverStyle private(name: String, ordinal: Int)
  extends Enum[ResolverStyle](name, ordinal)

object ResolverStyle {
  final val STRICT = new ResolverStyle("STRICT", 0)

  final val SMART = new ResolverStyle("SMART", 1)

  final val LENIENT = new ResolverStyle("LENIENT", 2)

  private val styles =
    Seq(STRICT, SMART, LENIENT)

  def values(): Array[ResolverStyle] = styles.toArray

  def valueOf(name: String): ResolverStyle = styles.find(_.name == name).getOrElse {
    throw new IllegalArgumentException(s"No such style: $name")
  }
}
