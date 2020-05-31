package ms.ralph.vcord

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import ms.ralph.vcord.core.manager.VcordManager
import ms.ralph.vcord.core.model.AudioOutputDeviceId
import ms.ralph.vcord.core.model.BotStatus
import ms.ralph.vcord.core.model.MidiInputDeviceId
import ms.ralph.vcord.gui.VcordWindow
import ms.ralph.vcord.integration.audio.VcordAudioClient
import ms.ralph.vcord.integration.discord.VcordDiscordClient
import ms.ralph.vcord.integration.discord.VcordDiscordClientFactory
import ms.ralph.vcord.integration.midi.VcordMidiClient
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.ServiceLoader
import java.util.concurrent.CountDownLatch
import kotlin.system.exitProcess

object VcordMain {

    private val logger = LoggerFactory.getLogger(VcordMain::class.java)

    private val latch: CountDownLatch = CountDownLatch(1)

    private var midiClient: VcordMidiClient? = null
    private var alphaAudioClient: VcordAudioClient? = null
    private var alphaDiscordClient: VcordDiscordClient? = null
    private var bravoAudioClient: VcordAudioClient? = null
    private var bravoDiscordClient: VcordDiscordClient? = null

    @JvmStatic
    fun main(args: Array<String>) {
        logger.debug("Starting Application")

        val yaml = Yaml(configuration = YamlConfiguration())
        val configFile = File("config.yml")

        logger.debug("Loading config from '${configFile.absolutePath}'")

        val appConfig = try {
            yaml.parse(AppConfig.serializer(), configFile.readText(Charsets.UTF_8))
        } catch (e: FileNotFoundException) {
            logger.error("Configuration file not found.")
            configFile.createNewFile()
            logger.error("Empty file created. Fill this file and re-run.")
            exitProcess(-1)
        } catch (e: Exception) {
            logger.error("Failed to load configuration.", e)
            exitProcess(-1)
        }

        loadDefaultSelectionFromConfig(appConfig)
        startGui()
        startDiscordBot(appConfig)

        latch.await()
        logger.debug("Shutting down all clients")
        midiClient?.shutdown()
        alphaAudioClient?.shutdown()
        bravoAudioClient?.shutdown()
        alphaDiscordClient?.shutdown()
        bravoDiscordClient?.shutdown()
        logger.debug("All clients finished.")
        exitProcess(0)
    }

    private fun loadDefaultSelectionFromConfig(appConfig: AppConfig) {
        VcordManager.midiDataManager.updateSelectedMidiInputDeviceId(
            appConfig.defaultMidiDeviceId?.let(::MidiInputDeviceId)
        )
        VcordManager.alphaAudioDataManager.updateSelectedAudioOutputDeviceId(
            appConfig.alphaTeamConfig?.defaultAudioDeviceId?.let(::AudioOutputDeviceId)
        )
        VcordManager.bravoAudioDataManager.updateSelectedAudioOutputDeviceId(
            appConfig.bravoTeamConfig?.defaultAudioDeviceId?.let(::AudioOutputDeviceId)
        )
    }

    private fun startGui() {
        logger.debug("Initializing GUI")
        val uiMainThread = Thread {
            VcordWindow.launch()
            logger.debug("Window closed")
            latch.countDown()
        }
        uiMainThread.name = "UI Main Thread"
        uiMainThread.start()
    }

    private fun startDiscordBot(appConfig: AppConfig) {
        logger.debug("Initializing Discord bot")

        midiClient = VcordMidiClient(VcordManager.midiDataManager)

        val factory = ServiceLoader.load(VcordDiscordClientFactory::class.java).findFirst().get()

        val alphaDiscordBotKey = appConfig.alphaTeamConfig?.discordBotApiKey
        if (alphaDiscordBotKey == null) {
            VcordManager.alphaTeamDataManager.updateBotStatus(BotStatus.ERROR_NO_API_KEY)
        } else {
            val localAlphaAudioClient = VcordAudioClient(VcordManager.alphaAudioDataManager)
            alphaAudioClient = localAlphaAudioClient
            alphaDiscordClient = factory.create(
                VcordManager.alphaTeamDataManager,
                VcordManager.alphaAudioDataManager,
                appConfig.alphaTeamConfig.discordBotApiKey,
                localAlphaAudioClient
            )
        }

        val bravoDiscordBotKey = appConfig.alphaTeamConfig?.discordBotApiKey
        if (bravoDiscordBotKey == null) {
            VcordManager.bravoTeamDataManager.updateBotStatus(BotStatus.ERROR_NO_API_KEY)
        } else {
            val localBravoAudioClient = VcordAudioClient(VcordManager.bravoAudioDataManager)
            bravoAudioClient = localBravoAudioClient
            bravoDiscordClient = factory.create(
                VcordManager.bravoTeamDataManager,
                VcordManager.bravoAudioDataManager,
                bravoDiscordBotKey,
                localBravoAudioClient
            )
        }

        alphaAudioClient?.init()
        bravoAudioClient?.init()
        midiClient?.init()
        alphaDiscordClient?.init()
        bravoDiscordClient?.init()
    }
}
