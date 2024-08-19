package me.xiaozhangup.ostrich.video

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ImageResizer {

    /**
     * 将文件夹下的所有图片统一缩放到指定尺寸
     *
     * @param inputDir 输入图片文件夹路径
     * @param outputDir 输出图片文件夹路径
     * @param width 目标宽度
     * @param height 目标高度
     */
    fun resizeImages(inputFolder: File, outputFolder: File, width: Int, height: Int) {
        // 创建输出文件夹
        if (!outputFolder.exists()) {
            outputFolder.mkdirs()
        }

        // 获取输入文件夹下的所有图片文件
        val imageFiles = inputFolder.listFiles { _, name ->
            name.lowercase().endsWith(".jpg") || name.lowercase().endsWith(".jpeg") ||
                    name.lowercase().endsWith(".png") || name.lowercase().endsWith(".bmp") ||
                    name.lowercase().endsWith(".gif")
        } ?: return

        // 遍历每个图片文件并进行缩放
        for (file in imageFiles) {
            try {
                val originalImage = ImageIO.read(file)
                val resizedImage = resizeImage(originalImage, width, height)
                val outputFile = File(outputFolder.absolutePath, file.name)
                ImageIO.write(resizedImage, getFileExtension(file.name), outputFile)
                println("Resized and saved: ${outputFile.absolutePath}")
            } catch (e: Exception) {
                println("Failed to process ${file.name}: ${e.message}")
            }
        }
    }

    /**
     * 缩放图片到指定尺寸
     *
     * @param originalImage 原始图片
     * @param width 目标宽度
     * @param height 目标高度
     * @return 缩放后的图片
     */
    private fun resizeImage(originalImage: BufferedImage, width: Int, height: Int): BufferedImage {
        val resizedImage = BufferedImage(width, height, originalImage.type)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null)
        graphics2D.dispose()
        return resizedImage
    }

    /**
     * 获取文件的扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名（不包括点号）
     */
    private fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "png")
    }
}