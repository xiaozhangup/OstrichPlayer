package me.xiaozhangup.ceramic

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

object Ceramic : Plugin() {

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }
}