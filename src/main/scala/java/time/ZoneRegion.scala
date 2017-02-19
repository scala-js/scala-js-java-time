package java.time

import java.time.zone.{ZoneRules, ZoneRulesException, ZoneRulesProvider}

import scala.annotation.tailrec

// TODO: ZoneRules
final class ZoneRegion private (id: String, rules: ZoneRules)


object ZoneRegion {
  def ofId(zoneId: String, checkAvailable: Boolean): ZoneRegion = {
    checkName(zoneId)

    // TODO: nullability if checkAvailable is false
    val rules: ZoneRules = try {
      // always attempt load for better behavior after deserialization
      ZoneRulesProvider.getRules(zoneId, true)
    } catch {
      case ex: ZoneRulesException if checkAvailable => throw ex
    }
    new ZoneRegion(zoneId, rules)
  }

  private def checkName(zoneId: String): Unit = {
    val length = zoneId.length()
    @tailrec def go(i: Int): Boolean = (i, zoneId.charAt(i)) match {
      case (n, _) if n == length                    => false
      case (_, c) if c >= 'a' && c <= 'z'           => go(i + 1)
      case (_, c) if c >= 'A' && c <= 'Z'           => go(i + 1)
      case (_, c) if c == '/' && i != 0             => go(i + 1)
      case (_, c) if c >= '0' && c <= '9' && i != 0 => go (i + 1)
      case (_, c) if c == '~' && i != 0             => go (i + 1)
      case (_, c) if c == '.' && i != 0             => go (i + 1)
      case (_, c) if c == '_' && i != 0             => go (i + 1)
      case (_, c) if c == '+' && i != 0             => go (i + 1)
      case (_, c) if c == '-' && i != 0             => go (i + 1)
      case _ => false
    }
    if (length < 2) {
      throw new DateTimeException("Invalid ID for region-based ZoneId, invalid format: " + zoneId)
    } else {
      if (go(0)) ()
      else throw new DateTimeException("Invalid ID for region-based ZoneId, invalid format: " + zoneId)
    }

  }
}
