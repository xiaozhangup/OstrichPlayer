package me.xiaozhangup.ceramic

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.command
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest
import taboolib.platform.util.buildItem

object Ceramic : Plugin() {
    private val manager = MultiBlockManager()

    override fun onEnable() {
        manager.register(
            TwoDimensionalStructure(
                "test",
                listOf(
                    "ixi",
                ),
                mapOf(
                    'x' to Material.STONE,
                    'i' to Material.DIRT
                )
            )
        )
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        if (e.hand != EquipmentSlot.HAND) return
        val clickedBlock = e.clickedBlock ?: return

        info(manager.searchIn2D(clickedBlock)?.getId())
    }
}