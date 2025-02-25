package com.willfp.ecoskills.gui.components

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.modify
import com.willfp.eco.util.lineWrap
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.skills.Skill
import org.bukkit.entity.Player

class SkillIcon(
    private val skill: Skill,
    config: Config,
    plugin: EcoPlugin
) : PositionedComponent {
    private val baseIcon = Items.lookup(config.getString("icon")).item
        get() = field.clone()

    private val slot = slot({ player, _ ->
        val level = player.getSkillLevel(skill)

        baseIcon.modify {
            setDisplayName(
                plugin.configYml.getFormattedString("gui.skill-icon.name")
                    .replace("%skill%", skill.name)
                    .let { skill.addPlaceholdersInto(it, level) }
            )

            addLoreLines(
                skill.addPlaceholdersInto(
                    plugin.configYml.getStrings("gui.skill-icon.lore"),
                    player
                ).lineWrap(plugin.configYml.getInt("gui.skill-icon.line-wrap"))
            )
        }
    }) {
        onLeftClick { player, _, _, _ ->
            skill.levelGUI.open(player)
        }
    }

    private val unknownSlot = slot(
        Items.lookup(plugin.configYml.getString("gui.unknown-skill-icon.icon")).item.clone().modify {
            setDisplayName(
                plugin.configYml.getFormattedString("gui.unknown-skill-icon.name")
            )

            addLoreLines(
                plugin.configYml.getFormattedStrings("gui.unknown-skill-icon.lore")
            )
        }
    )

    override val isEnabled = config.getBoolOrNull("enabled") ?: true
    override val row = config.getInt("position.row")
    override val column = config.getInt("position.column")

    override fun getSlotAt(row: Int, column: Int, player: Player, menu: Menu): Slot {
        return if (player.getSkillLevel(skill) > 0 || !skill.isHiddenBeforeLevel1) {
            slot
        } else {
            unknownSlot
        }
    }
}
