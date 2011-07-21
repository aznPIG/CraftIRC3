package com.ensifera.animosity.craftirc;

import java.lang.Exception;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerListener;

public class CraftIRCListener extends PlayerListener {

    private CraftIRC plugin = null;

    public CraftIRCListener(CraftIRC plugin) {
        this.plugin = plugin;
    }
    
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        try {
            String[] split = event.getMessage().split(" ");
            // ACTION/EMOTE can't be claimed, so use onPlayerCommandPreprocess
            if (split[0].equalsIgnoreCase("/me")) {
                RelayedMessage msg = plugin.newMsg(plugin.getEndPoint(plugin.cMinecraftTag()), null, "action");
                msg.setField("sender", event.getPlayer().getDisplayName());
                msg.setField("message", Util.combineSplit(1, split, " "));
                msg.setField("world", event.getPlayer().getWorld().getName()); 
                msg.post();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    
    public void onPlayerChat(PlayerChatEvent event) {
        if (this.plugin.isHeld(CraftIRC.HoldType.CHAT)) return;
        try {
            if (this.plugin.isDebug()) CraftIRC.dolog(String.format("onPlayerChat(): <%s> %s", event.getMessage(), event.getPlayer()));
            RelayedMessage msg;
            if (event.isCancelled())
                msg = plugin.newMsg(plugin.getEndPoint(plugin.cCancelledTag()), null, "chat");
            else 
                msg = plugin.newMsg(plugin.getEndPoint(plugin.cMinecraftTag()), null, "chat");
            msg.setField("sender", event.getPlayer().getDisplayName());
            msg.setField("message", event.getMessage());
            msg.setField("world", event.getPlayer().getWorld().getName()); 
            msg.post();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.plugin.isHeld(CraftIRC.HoldType.JOINS)) return;
        try {
            RelayedMessage msg = plugin.newMsg(plugin.getEndPoint(plugin.cMinecraftTag()), null, "join");
            msg.setField("sender", event.getPlayer().getDisplayName());
            msg.post();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        if (this.plugin.isHeld(CraftIRC.HoldType.QUITS)) return;
        try {
            RelayedMessage msg = plugin.newMsg(plugin.getEndPoint(plugin.cMinecraftTag()), null, "quit");
            msg.setField("sender", event.getPlayer().getDisplayName());
            msg.post();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPlayerKick(PlayerKickEvent event) {
        if (this.plugin.isHeld(CraftIRC.HoldType.KICKS)) return;
        RelayedMessage msg = plugin.newMsg(plugin.getEndPoint(plugin.cMinecraftTag()), null, "kick");
        msg.setField("sender", event.getPlayer().getDisplayName());
        msg.setField("message", (event.getReason().length() == 0) ? "no reason given" : event.getReason());
        msg.setField("moderator", "Admin"); //there is no moderator context in CBukkit, oh no.
        msg.post();
    }

}
