package ms.ralph.vcord.util

import java.util.Optional

internal fun <T : Any> T?.asOptional(): Optional<T> = Optional.ofNullable(this)
