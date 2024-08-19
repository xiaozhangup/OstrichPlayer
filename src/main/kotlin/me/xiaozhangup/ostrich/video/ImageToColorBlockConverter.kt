package me.xiaozhangup.ostrich.video

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.hex
import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import taboolib.common.platform.function.info
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ImageToColorBlockConverter {

    /**
     * 将文件夹内的所有图片按序号顺序转换为色块表示的字符形式，并返回一个 List
     *
     * @param inputDir 输入图片文件夹路径
     * @return 转换后的字符串列表，表示每个图片的色块字符
     */
    fun convertImagesToColorBlocksList(
        inputFolder: File,
        chunk: Int = 20,
        target: File
    ) {
        val imageFiles = inputFolder.listFiles { _, name ->
            name.lowercase().endsWith(".jpg") || name.lowercase().endsWith(".jpeg") ||
                    name.lowercase().endsWith(".png") || name.lowercase().endsWith(".bmp") ||
                    name.lowercase().endsWith(".gif")
        }?.sortedBy { it.nameWithoutExtension.toIntOrNull() ?: Int.MAX_VALUE } ?: return
        val chunked = imageFiles.chunked(chunk)
        val all = chunked.size
        var group = 1
        info("Converting images to color blocks...")

        for (c in chunked){
            val resultList = mutableListOf<Component>()
            for (file in c) {
                try {
                    val image = ImageIO.read(file)
                    val colorBlocks = convertImageToColorBlocks(image)
                    resultList.add(colorBlocks)
                } catch (e: Exception) {
                    println("Failed to process ${file.name}: ${e.message}")
                }
            }
            resultList.forEach {
                target.appendText(
                    GsonComponentSerializer.gson().serialize(
                        it
                    ) + "\n"
                )
            }
            info("Converted ${group++}/$all...")
        }
    }

    /**
     * 将单个图片转换为色块表示的字符形式
     *
     * @param image BufferedImage 对象
     * @return 转换后的字符串，表示图片的色块字符
     */
    private fun convertImageToColorBlocks(image: BufferedImage): Component {
        return component {
            for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    val color = Color(image.getRGB(x, y))
                    val hexColor = String.format("#%02x%02x%02x", color.red, color.green, color.blue)
                    text("█") with hex(hexColor)
                }
                if (y < image.height - 1) {
                    newline()
                }
            }
        }
    }
}