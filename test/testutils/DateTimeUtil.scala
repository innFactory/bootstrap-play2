package testutils

import org.joda.time.{DateTime, DateTimeUtils}

object DateTimeUtil {
  def setToDateTime(dateTime: String): Unit = DateTimeUtils.setCurrentMillisFixed(new DateTime(dateTime).getMillis)

  def resetDateTime(): Unit = DateTimeUtils.setCurrentMillisSystem()
}
