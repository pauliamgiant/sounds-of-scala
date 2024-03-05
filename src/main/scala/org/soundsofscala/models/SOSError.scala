package org.soundsofscala.models

sealed abstract class SOSError(val message: String, cause: Option[Throwable] = None)
    extends Exception(message, cause.orNull)

final case class FileLoadingError(original: String)
    extends SOSError(s"The was a file loading error: $original", None)
object SOSError {
  // TODO - Add more error types
}
