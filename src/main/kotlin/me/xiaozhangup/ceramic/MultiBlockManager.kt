package me.xiaozhangup.ceramic

import org.bukkit.Material
import org.bukkit.block.Block
import taboolib.common.platform.function.info

class MultiBlockManager {
    private val twoDimensional = mutableListOf<TwoDimensionalStructure>()

    fun register(structure: TwoDimensionalStructure) {
        twoDimensional.add(structure)
    }

    fun searchIn2D(block: Block): TwoDimensionalStructure? {
        val centered = twoDimensional.filter { it.center == block.type }
        info(centered)
        return centered.firstOrNull { structure ->
            val offset = mutableListOf<Pair<Int, Int>>()
            for (y in structure.availableY()) {
                for (x in structure.availableX()) {
                    if (
                        structure.getRelativeLocation(x, y) != null
                    ) offset.add(Pair(x, y))
                }
            }

            if (
                offset.all {
                    structure.getRelativeLocation(it.first, it.second) == block.getRelative(it.first, it.second, 0).type
                }
            ) return@firstOrNull true
            if (
                offset.all {
                    structure.getRelativeLocation(it.first, it.second) == block.getRelative(0, it.second, it.first).type
                }
            ) return@firstOrNull true

            if (!structure.mirror) {
                if (offset.all {
                        structure.getRelativeLocation(it.first, it.second) == block.getRelative(
                            -it.first,
                            it.second,
                            0
                        ).type
                    }) return@firstOrNull true

                if (offset.all {
                        structure.getRelativeLocation(it.first, it.second) == block.getRelative(
                            0,
                            it.second,
                            -it.first
                        ).type
                    }) return@firstOrNull true
            }

            false
        }
    }
}

class TwoDimensionalStructure(
    private val id: String,
    private val structure: List<String>,
    private val mapping: Map<Char, Material>
) {
    private val locations: Map<Int, Map<Int, Char>> // 先是高度，再是水平
    val mirror: Boolean = structure.all { isSymmetric(it) } // 是否为水平对称 (优化搜寻)
    val range: Int = structure.first().length - 1 / 2
    val center: Material = mapping['x']!!

    init { // 初始化检查工具
        val size = structure.first().length
        if (!mapping.contains('x') || structure.all { !it.contains('x') }) {
            throw IllegalArgumentException("The structure does not contain the center")
        }
        if (
            structure.any {
                it.length != size
            }
        ) {
            throw IllegalArgumentException("The structure is not a square")
        }

        // 构建编译后的结构
        val originY = structure.indexOfFirst { it.contains('x') }
        val originX = structure.first { it.contains('x') }.indexOf('x')
        val locations = mutableMapOf<Int, MutableMap<Int, Char>>()
        structure.forEachIndexed { i, s ->
            val y = originY - i
            s.forEachIndexed { index, c ->
                val x = index - originX
                if (locations[y] == null) {
                    locations[y] = mutableMapOf()
                }
                locations[y]!![x] = c
            }
        }

        this.locations = locations
    }

    fun getRelativeLocation(x: Int, y: Int): Material? {
        return locations[y]?.get(x)?.let { mapping[it] }
    }

    fun availableY(): Set<Int> {
        return locations.keys
    }

    fun availableX(): Set<Int> {
        return locations.map { it.value.keys }.flatten().toSet()
    }

    fun getId(): String {
        return id
    }

    fun getMaterial(char: Char) = mapping[char]

    private fun isSymmetric(s: String): Boolean {
        val chars = s.toCharArray()
        var left = 0
        var right = chars.size - 1
        while (left < right) {
            if (chars[left] != chars[right]) {
                return false
            }
            left++
            right--
        }

        return true
    }
}