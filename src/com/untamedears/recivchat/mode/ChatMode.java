package com.untamedears.recivchat.mode;

import org.bukkit.command.CommandSender;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface ChatMode {
	public void onPlayerChat(AsyncPlayerChatEvent event);
	public void directMessage(CommandSender sender, String message);
}
