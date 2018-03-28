package java.time

import java.time.format.DateTimeParseException

private[time] object Preconditions {
  // Like scala.Predef.require, but throws a DateTimeException.
  def requireDateTime(requirement: Boolean, message: => Any): Unit = {
    if (!requirement)
      throw new DateTimeException(message.toString)
  }

  def requireDateTimeParse(requirement: Boolean, message: => Any,
      charSequence: CharSequence, index: Int): Unit = {
    if (!requirement)
      throw new DateTimeParseException(message.toString, charSequence, index)
  }
}
