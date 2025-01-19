package org.cosmosmc.trademeaddon.addons;

import java.util.Arrays;
import java.util.HashMap;

import me.Zrips.TradeMe.Containers.*;
import org.bukkit.entity.Player;
import me.Zrips.TradeMe.Locale.LC;
import net.Zrips.CMILib.GUI.GUIManager;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.CMIGui;
import com.olziedev.coincurrency.CoinCurrency;
import java.util.logging.Level;
import net.Zrips.CMILib.Items.CMIMaterial;
import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;
import java.util.List;
import com.olziedev.coincurrency.db.SQLStorage;
import me.Zrips.TradeMe.TradeMe;
import org.cosmosmc.trademeaddon.TradeMeAddonPlugin;


public class CoinCurrencyAddon implements TradeModeInterface {

    private String at;
    private TradeMe tradeMe;
    private TradeMeAddonPlugin plugin;
    private SQLStorage storage;
    List<ItemStack> AmountButtons;
    ItemStack OfferedTradeButton;
    OfferButtons offerButton;
    Amounts amounts;

    public CoinCurrencyAddon(final TradeMeAddonPlugin tradeMeAddon, final TradeMe tradeMe, final String string) {
        this.at = "Coins";
        this.AmountButtons = new ArrayList<ItemStack>();
        this.OfferedTradeButton = CMIMaterial.SUNFLOWER.newItemStack();
        this.offerButton = new OfferButtons();
        this.amounts = new Amounts(1, 10, 100, 1000);
        this.plugin = tradeMeAddon;
        this.tradeMe = tradeMe;
        this.at = string;
        tradeMeAddon.getLogger().log(Level.INFO, "Initializing GamePoints Addon.");
        this.storage = CoinCurrency.getSqlStorage();
    }

    public CMIGui Buttons(final TradeOffer tradeOffer, final CMIGui cMIGui, final int n) {
        final String string = this.tradeMe.getUtil().TrA(this.storage.getCoins(tradeOffer.getP1().getUniqueId()));
        final String string2 = this.tradeMe.getUtil().TrA(tradeOffer.getOffer(this.at));
        final ItemStack itemStack = (tradeOffer.getOffer(this.at) == 0.0) ? this.offerButton.getOfferOff() : this.offerButton.getOfferOn();
        final String string3 = this.tradeMe.getUtil().GetTaxesString(this.at, tradeOffer.getOffer(this.at));
        String string4 = "";
        if (tradeOffer.getButtonList().size() > 4) {
            string4 = "\n" + this.tradeMe.getMessage("MiddleMouse");
        }
        if (tradeOffer.Size == TradeSize.REGULAR) {
            cMIGui.updateButton(new CMIGuiButton(Integer.valueOf(n), this.tradeMe.getUtil().makeSlotItem(itemStack, this.tradeMe.getMessage(this.at, "ToggleButton.Name", new Object[0]), this.tradeMe.getMessageListAsString(this.at, "ToggleButton.Lore", new Object[] { "[amount]", this.tradeMe.getUtil().TrA(tradeOffer.getOffer(this.at)), "[taxes]", string3 }) + string4)) {
                public void click(final GUIManager.GUIClickType gUIClickType) {
                    tradeOffer.toogleMode(CoinCurrencyAddon.this.at, gUIClickType, n);
                }
            });
        }
        if (tradeOffer.getAction() == this.at) {
            final String string5 = this.tradeMe.getMessageListAsString(this.at, "Button.Lore", new Object[] { "[balance]", string, "[offer]", string2, "[taxes]", string3 });
            for (int i = 45; i < 49; ++i) {
                cMIGui.updateButton(new CMIGuiButton(Integer.valueOf(i), this.tradeMe.getUtil().makeSlotItem(this.AmountButtons.get(i - 45), this.tradeMe.getMessage(this.at, "Button.Name", new Object[] { "[amount]", this.tradeMe.getUtil().TrA(this.amounts.getAmount(i - 45)) }), string5)) {
                    public void click(final GUIManager.GUIClickType gUIClickType) {
                        tradeOffer.amountClick(CoinCurrencyAddon.this.at, gUIClickType, this.getSlot() - 45, n);
                    }
                });
            }
        }
        return cMIGui;
    }

    public void Change(final TradeOffer tradeOffer, final int n, final GUIManager.GUIClickType gUIClickType) {
        Double d = this.amounts.getAmount(n);
        final double d2 = this.storage.getCoins(tradeOffer.getP1().getUniqueId());
        final double d3 = this.storage.getCoins(tradeOffer.getP2().getUniqueId());
        final double d4 = tradeOffer.getOffer(this.at);
        if (gUIClickType.isShiftClick()) {
            d *= 10.0;
        }
        if (gUIClickType.isLeftClick()) {
            if (d4 + d + d3 >= 1.0E13) {
                d = 1.0E13 - d4 - d3;
                tradeOffer.getP1().sendMessage(this.tradeMe.getMsg(LC.info_prefix, new Object[0]) + this.tradeMe.getMessage(this.at, "hardLimit", new Object[] { "[playername]", tradeOffer.getP2Name() }));
            }
            if (d4 + d > d2) {
                if (d2 < 0.0) {
                    tradeOffer.setOffer(this.at, 0.0);
                }
                else {
                    tradeOffer.setOffer(this.at, Math.floor(d2));
                }
                tradeOffer.getP1().sendMessage(this.tradeMe.getMsg(LC.info_prefix, new Object[0]) + this.tradeMe.getMessage(this.at, "Limit", new Object[] { "[amount]", this.tradeMe.getUtil().TrA(tradeOffer.getOffer(this.at)) }));
            }
            else {
                tradeOffer.addOffer(this.at, d);
            }
        }
        if (gUIClickType.isRightClick()) {
            if (d4 - d < 0.0) {
                tradeOffer.setOffer(this.at, 0.0);
            }
            else {
                tradeOffer.takeFromOffer(this.at, d);
            }
        }
        final String string = this.tradeMe.getMessage(this.at, "ChangedOffer", new Object[] { "[playername]", tradeOffer.getP1Name(), "[amount]", this.tradeMe.getUtil().TrA(tradeOffer.getOffer(this.at)) });
        TradeMe.getInstance().getUtil().updateInventoryTitle(tradeOffer.getP2(), this.tradeMe.getMessage(this.at, "ChangedOfferTitle", new Object[] { "[playername]", tradeOffer.getP1().getName(), "[amount]", tradeOffer.getOffer(this.at) }), 1000L);
    }

    public ItemStack getOfferedItem(final TradeOffer tradeOffer) {
        if (tradeOffer.getOffer(this.at) > 0.0) {
            final String string = this.tradeMe.getUtil().GetTaxesString(this.at, tradeOffer.getOffer(this.at));
            final ItemStack itemStack = this.tradeMe.getUtil().makeSlotItem(this.OfferedTradeButton, this.tradeMe.getMessage(this.at, "OfferedButton.Name", new Object[] { "[player]", tradeOffer.getP1().getName() }), this.tradeMe.getMessageListAsString(this.at, "OfferedButton.Lore", new Object[] { "[amount]", this.tradeMe.getUtil().TrA(tradeOffer.getOffer(this.at)), "[taxes]", string }));
            return itemStack;
        }
        return null;
    }

    public boolean isLegit(final TradeMap tradeMap) {
        final Player player2 = tradeMap.getP1Trade().getP1();
        final Player player3;
        return this.check(player2, player3 = tradeMap.getP2Trade().getP1(), (int)tradeMap.getP1Trade().getOffer(this.at), (int)tradeMap.getP2Trade().getOffer(this.at));
    }

    public boolean finish(final TradeOffer tradeOffer) {
        final Player player = tradeOffer.getP2();
        final Player player2 = tradeOffer.getP1();
        if (tradeOffer.getOffer(this.at) <= 0.0) {
            return false;
        }
        double d = tradeOffer.getOffer(this.at);
        this.storage.setCoins(player2.getUniqueId(), (int)(this.storage.getCoins(player2.getUniqueId()) - d));
        d = this.tradeMe.getUtil().CheckTaxes(this.at, d);
        tradeOffer.setOffer(this.at, d);
        this.storage.setCoins(player.getUniqueId(), (int)(this.storage.getCoins(player.getUniqueId()) + d));
        if (player != null) {
            player.sendMessage(this.tradeMe.getMsg(LC.info_prefix, new Object[0]) + this.tradeMe.getMessage(this.at, "Got", new Object[] { "[amount]", tradeOffer.getOffer(this.at) }));
        }
        return true;
    }

    private boolean check(final Player player, final Player player2, final Integer n, final Integer n2) {
        Integer n3 = this.storage.getCoins(player.getUniqueId());
        if (n3 < n) {
            player.sendMessage(this.tradeMe.getMsg(LC.info_prefix, new Object[0]) + this.tradeMe.getMessage(this.at, "Error", new Object[] { "[playername]", player.getName() }));
            player2.sendMessage(this.tradeMe.getMsg(LC.info_prefix, new Object[0]) + this.tradeMe.getMessage(this.at, "Error", new Object[] { "[playername]", player.getName() }));
            return false;
        }
        n3 = this.storage.getCoins(player2.getUniqueId());
        if (n3 < n2) {
            player.sendMessage(this.tradeMe.getMsg(LC.info_prefix, new Object[0]) + this.tradeMe.getMessage(this.at, "Error", new Object[] { "[playername]", player2.getName() }));
            player2.sendMessage(this.tradeMe.getMsg(LC.info_prefix, new Object[0]) + this.tradeMe.getMessage(this.at, "Error", new Object[] { "[playername]", player2.getName() }));
            return false;
        }
        return true;
    }

    public void setTrade(final TradeOffer tradeOffer, final int n) {
        tradeOffer.getButtonList().add(tradeOffer.getPosibleButtons().get(n));
    }

    public void getResults(final TradeOffer tradeOffer, final TradeResults tradeResults) {
        if (tradeOffer.getOffer(this.at) > 0.0) {
            double d = tradeOffer.getOffer(this.at);
            d -= this.tradeMe.getUtil().CheckFixedTaxes(this.at, d);
            d -= this.tradeMe.getUtil().CheckPercentageTaxes(this.at, d);
            tradeResults.add(this.at, d);
        }
    }

    public HashMap<String, Object> getLocale() {
        final HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Button.Name", "&dCoins increment by &a[amount]");
        hashMap.put("Button.Lore", Arrays.asList("&7Left click to add", "&7Right click to take", "&7Hold shift to increase 10 times", "&7Maximum available: &a[balance]", "&7Current coins offer: &a[offer] [taxes]"));
        hashMap.put("ToggleButton.Name", "&dToggle to coins offer");
        hashMap.put("ToggleButton.Lore", Arrays.asList("&7Curent coins offer: &a[amount] [taxes]"));
        hashMap.put("OfferedButton.Name", "&a[player]'s &7coins offer");
        hashMap.put("OfferedButton.Lore", Arrays.asList("&7Current coins offer: &a[amount] [taxes]"));
        hashMap.put("Error", "&a[playername] &7doesn't have enough coins!");
        hashMap.put("Limit", "&7You dont have enough coins! Amount was set to maximum you can trade: &6[amount]");
        hashMap.put("hardLimit", "&a[playername] &7cant have more than 10,000,000,000,000 coins!");
        hashMap.put("InLoanTarget", "&7Your offered coins amount is to low to get &a[playername] &7out of loan! offer atleast &a[amount]");
        hashMap.put("InLoanYou", "&a[playername] &7offered coins amount is to low to get you out of loan!");
        hashMap.put("Got", "&7You have received &a[amount] &ecoins");
        hashMap.put("CantWidraw", "&7Can't withdraw coins from player! ([playername])");
        hashMap.put("ChangedOffer", "&a[playername] &7has changed their coins offer to: &6[amount]");
        hashMap.put("ChangedOfferTitle", "&7Offered &a[amount] &8coins");
        hashMap.put("log", "&e[amount] &7Coins");
        return hashMap;
    }

    public List<ItemStack> getAmountButtons() {
        this.AmountButtons.add(CMIMaterial.GOLD_NUGGET.newItemStack());
        this.AmountButtons.add(CMIMaterial.GOLD_INGOT.newItemStack());
        this.AmountButtons.add(CMIMaterial.GOLD_BLOCK.newItemStack());
        this.AmountButtons.add(CMIMaterial.DIAMOND.newItemStack());
        return this.AmountButtons;
    }

    public ItemStack getOfferedTradeButton() {
        return this.OfferedTradeButton;
    }

    public void setOfferedTradeButton(final ItemStack itemStack) {
    }

    public OfferButtons getOfferButtons() {
        this.offerButton.addOfferOff(CMIMaterial.SUNFLOWER.newItemStack());
        this.offerButton.addOfferOn(CMIMaterial.SUNFLOWER.newItemStack());
        return this.offerButton;
    }

    @Override
    public void setAmounts(final Amounts amounts) {
        this.amounts = amounts;
    }

    public String Switch(final TradeOffer tradeOffer, final GUIManager.GUIClickType gUIClickType) {
        return null;
    }

}
