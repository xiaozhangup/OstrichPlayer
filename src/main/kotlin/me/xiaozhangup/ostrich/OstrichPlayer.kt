package me.xiaozhangup.ostrich

import me.xiaozhangup.ostrich.video.ImageResizer
import me.xiaozhangup.ostrich.video.ImageToColorBlockConverter
import me.xiaozhangup.ostrich.video.VideoFrameExtractor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.io.newFile
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import java.io.File

@RuntimeDependencies(
    RuntimeDependency(
        "ink.pmc.advkt:core:1.0.0",
        test = "ink.pmc.advkt.sound.SoundKt",
        relocate = ["!kotlin.", "!kotlin1822."],
        repository = "https://maven.nostal.ink/repository/maven-public",
        transitive = false
    )
)
object OstrichPlayer : Plugin() {
    private val videoDir by lazy {
        newFile(
            getDataFolder(),
            "video",
            folder = true
        )
    }
    private val cacheDir by lazy {
        newFile(
            getDataFolder(),
            "cache",
            folder = true
        )
    }
    private val cache: MutableMap<String, List<Component>> = mutableMapOf()

    override fun onEnable() {
        videoDir
        cacheDir.listFiles()?.forEach {
            it.delete()
        }
        command("play") {
            dynamic {
                suggestion<CommandSender> { _, _ ->
                    getDataFolder().listFiles()?.filter {
                        it.name.endsWith(".cideo")
                    }?.map {
                        it.name
                    }?.toList() ?: emptyList()
                }

                execute<Player> { sender, _, arg ->
                    submitAsync {
                        val cached = cache[arg]
                        val frames = if (cached != null) {
                            sender.sendMessage("视频文件已缓存")
                            cached
                        } else {
                            val video = File(getDataFolder(), arg)
                            sender.sendMessage("正在加载视频文件中...")
                            val frames = video.readLines().map {
                                GsonComponentSerializer.gson().deserialize(it)
                            }
                            sender.sendMessage("视频文件加载完成")
                            cache[arg] = frames
                            frames
                        }.toMutableList()

                        submit {
                            val entity = sender.world.spawnEntity(
                                sender.location.apply {
                                    yaw = 0f
                                    pitch = 0f
                                },
                                EntityType.TEXT_DISPLAY
                            ).apply {
                                this as TextDisplay
                                isShadowed = true
                                lineWidth = Int.MAX_VALUE
                                alignment = TextDisplay.TextAlignment.CENTER
                                billboard = Display.Billboard.VERTICAL
                            } as TextDisplay

                            sender.sendMessage("视频开始播放")
                            playerVideo(frames, entity, true)
                        }
                    }
                }
            }
        }

        command("video", permissionDefault = PermissionDefault.TRUE) {
            literal("extractor") {
                dynamic {
                    suggestion<CommandSender> { _, _ ->
                        videoDir.listFiles()?.map { it.name }?.toList() ?: emptyList()
                    }

                    execute<CommandSender> { sender, _, args ->
                        submitAsync {
                            val video = File(videoDir, args)
                            if (video.exists()) {
                                VideoFrameExtractor.extractFrames(
                                    video,
                                    File(cacheDir, video.nameWithoutExtension + "_frames")
                                )
                            } else {
                                sender.sendMessage("视频文件不存在")
                            }
                        }
                    }
                }
            }

            literal("resize") {
                dynamic("x") {
                    dynamic("y") {
                        dynamic("file") {
                            suggestion<CommandSender> { _, _ ->
                                cacheDir.listFiles()?.map { it.name }?.toList() ?: emptyList()
                            }

                            execute<CommandSender> { sender, args, _ ->
                                submitAsync {
                                    val x = args["x"].toInt()
                                    val y = args["y"].toInt()
                                    ImageResizer.resizeImages(
                                        File(cacheDir, args["file"]),
                                        File(cacheDir, args["file"] + "_${x}_${y}_resize"),
                                        x,
                                        y
                                    )

                                    sender.sendMessage("图片已缩放")
                                }
                            }
                        }
                    }
                }
            }

            literal("convert") {
                dynamic("file") {
                    suggestion<CommandSender> { _, _ ->
                        cacheDir.listFiles()?.map { it.name }?.toList() ?: emptyList()
                    }

                    execute<CommandSender> { sender, args, _ ->
                        submitAsync {
                            val file = File(cacheDir, args["file"])
                            ImageToColorBlockConverter.convertImagesToColorBlocksList(
                                file,
                                2,
                                newFile(
                                    getDataFolder(),
                                    "${file.name}.cideo"
                                )
                            )

                            sender.sendMessage("图片已转换")
                        }
                    }
                }
            }
        }
    }

    private fun playerVideo(
        frames: MutableList<Component>,
        entity: TextDisplay,
        repeat: Boolean
    ) {
        var clone = frames.toMutableList()
        submit(period = 1) {
            if (clone.isEmpty()) {
                if (repeat) {
                    clone = frames.toMutableList()
                } else {
                    entity.remove()
                    cancel()
                    return@submit
                }
            }

            entity.text(clone.removeFirst())
        }
    }
}