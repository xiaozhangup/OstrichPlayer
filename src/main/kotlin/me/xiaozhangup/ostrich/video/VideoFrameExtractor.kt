package me.xiaozhangup.ostrich.video

import java.io.File

object VideoFrameExtractor {

    /**
     * 提取视频的每个帧并保存到指定文件夹
     *
     * @param videoPath 视频文件路径
     * @param outputDir 输出图片文件夹路径
     */
    fun extractFrames(videoPath: File, outputFolder: File) {
        // 创建输出文件夹
        if (!outputFolder.exists()) {
            outputFolder.mkdirs()
        }

        // FFmpeg 命令：-i 表示输入视频，%d.png 表示输出的帧按数字命名为 PNG 文件
        val command = listOf(
            "ffmpeg", "-i", videoPath.absolutePath, "-vf", "fps=20", "${outputFolder.absolutePath}/%d.png"
        )

        // 使用 ProcessBuilder 执行命令
        val processBuilder = ProcessBuilder(command)
        processBuilder.redirectErrorStream(true)

        try {
            val process = processBuilder.start()
            process.inputStream.bufferedReader().use { it.lines().forEach { line -> println(line) } }
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}