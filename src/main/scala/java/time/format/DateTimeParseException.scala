package java.time.format

import java.time.DateTimeException

class UnsupportedTemporalTypeException(message: String, parsedData: CharSequence,
  errorIndex: Int, cause: Throwable) extends DateTimeException(message, cause) {

  def this(message: String, parsedData: CharSequence, errorIndex: Int) = {
    this(message, parsedData, errorIndex, null)
  }
}
