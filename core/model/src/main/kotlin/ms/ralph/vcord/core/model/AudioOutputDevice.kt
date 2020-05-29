package ms.ralph.vcord.core.model

import javax.sound.sampled.Mixer

class AudioOutputDevice(
    val id: AudioOutputDeviceId,
    val mixerInfo: Mixer.Info
)
