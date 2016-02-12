package java.time.format

import scala.collection.JavaConverters._

import java.text.DecimalFormatSymbols
import java.{util => ju}

final class DecimalStyle private(zeroDigit: Char, positiveSign: Char,
  negativeSign: Char, decimalSeparator: Char) {

  def getDecimalSeparator(): Char = decimalSeparator

  def getNegativeSign(): Char = negativeSign

  def getPositiveSign(): Char = positiveSign

  def getZeroDigit(): Char = zeroDigit

  def withDecimalSeparator(decimalSeparator: Char): DecimalStyle =
    new DecimalStyle(zeroDigit, positiveSign, negativeSign, decimalSeparator)

  def withNegativeSign(negativeSign: Char): DecimalStyle =
    new DecimalStyle(zeroDigit, positiveSign, negativeSign, decimalSeparator)

  def withPositiveSign(positiveSign: Char): DecimalStyle =
    new DecimalStyle(zeroDigit, positiveSign, negativeSign, decimalSeparator)

  def withZeroDigit(zeroDigit: Char): DecimalStyle =
    new DecimalStyle(zeroDigit, positiveSign, negativeSign, decimalSeparator)

  override def equals(other: Any): Boolean = other match {
    case that: DecimalStyle =>
      zeroDigit == that.getZeroDigit && positiveSign == that.getPositiveSign &&
      negativeSign == that.getNegativeSign && decimalSeparator == that.getDecimalSeparator

    case _ => false
  }

  override def hashCode(): Int =
    31 * (31 * (31 * zeroDigit.hashCode + positiveSign.hashCode) +
      negativeSign.hashCode) + decimalSeparator.hashCode

  override def toString(): String =
    s"DecimalStyle[$zeroDigit, $positiveSign, $negativeSign, $decimalSeparator]"
}

object DecimalStyle {

  val STANDARD: DecimalStyle = new DecimalStyle('0', '+', '-', '.')

  // def getAvailableLocales(): ju.Set[ju.Locale] = ???

  // def ofDefaultLocale(): DecimalStyle = ???

  // def of(locale: ju.Locale): DecimalStyle = ???

}
