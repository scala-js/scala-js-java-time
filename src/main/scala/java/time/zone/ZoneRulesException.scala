package java.time.zone

import java.time.DateTimeException

class ZoneRulesException(message: String, cause: Throwable) extends DateTimeException(message, cause) {
  def this(message: String) = this(message, null)
}
