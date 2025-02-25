package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.api.addStatModifier
import com.willfp.ecoskills.api.modifiers.ModifierOperation
import com.willfp.ecoskills.api.modifiers.StatModifier
import com.willfp.ecoskills.api.removeStatModifier
import com.willfp.ecoskills.stats.Stats
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.RunOrder
import org.bukkit.entity.Player

object EffectAddStat : Effect<NoCompileData>("add_stat") {
    override val runOrder = RunOrder.START

    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("amount", "You must specify the amount to add/remove!")
    }

    override fun onEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val stat = Stats.getByID(config.getString("stat")) ?: return

        player.addStatModifier(
            StatModifier(
                identifiers.uuid,
                stat,
                config.getDoubleFromExpression("amount", player),
                ModifierOperation.ADD
            )
        )
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        player.removeStatModifier(identifiers.uuid)
    }
}
