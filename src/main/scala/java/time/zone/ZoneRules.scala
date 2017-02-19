package java.time.zone

import java.time.{LocalDateTime, ZoneOffset}

// TODO: lastRules
class ZoneRules private (standardOffsets: Seq[ZoneOffset],
                         savingsInstantTransitions: Seq[Long],
                         savingsLocalTransitions: Seq[LocalDateTime],
                         wallOffsets: Seq[ZoneOffset])

