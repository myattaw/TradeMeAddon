package org.cosmosmc.trademeaddon;

import me.Zrips.TradeMe.Containers.AmountClickAction;
import me.Zrips.TradeMe.Containers.Amounts;
import me.Zrips.TradeMe.Containers.TradeAction;
import me.Zrips.TradeMe.TradeMe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.cosmosmc.trademeaddon.addons.CoinCurrencyAddon;

public final class TradeMeAddonPlugin extends JavaPlugin {

    public void onEnable() {
        TradeMe.getInstance().addNewTradeMode(
                new TradeAction("Coins", AmountClickAction.Amounts, false),
                new CoinCurrencyAddon(this, TradeMe.getInstance(), "Coins")
        );
        if (!this.getServer().getPluginManager().isPluginEnabled("CoinCurrency")) {
            this.getServer().getPluginManager().disablePlugin(this);
        }
        TradeMe.getInstance().getConfigManager().reload();
        TradeMe.getInstance().get("Coins").setAmounts(new Amounts(1, 10, 100, 1000));
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[TradeMeAddon] " + ChatColor.GOLD + "Injected alternative Coins trade mode");
    }

}
