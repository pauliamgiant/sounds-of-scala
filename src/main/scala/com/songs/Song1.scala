import com.soundsofscala.models.*
import io.github.iltotore.iron.autoRefine
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.MusicalEvent.*
import com.soundsofscala.models.Pitch
import com.soundsofscala.models.Velocity.*

object Song1:
  val bassline: MusicalEvent= C(Octave(4)) + C(Octave(4)) + C(Octave(4)) + C(Octave(4))