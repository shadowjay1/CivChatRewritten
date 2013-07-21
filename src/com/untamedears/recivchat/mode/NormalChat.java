package com.untamedears.recivchat.mode;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.untamedears.recivchat.CivChat;

public class NormalChat implements ChatMode {
	public static NormalChat instance = new NormalChat();
	
	@Override
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Location playerLoc = player.getLocation();
		
		ArrayList<Player> remove = new ArrayList<Player>();
		
		int chatRange = CivChat.getChatRange();
		
		for(Player recipient : event.getRecipients()) {
			double dist = playerLoc.distance(recipient.getLocation());
			
			if(dist > chatRange) {
				remove.add(recipient);
			}
		}
		
		event.getRecipients().removeAll(remove);
		event.setFormat("%s: %s");
	}
	
	@Override
	public void directMessage(CommandSender sender, String message) {
		if(!(sender instanceof Player))
			return;
		
		Player player = (Player) sender;
		Location playerLoc = player.getLocation();
		
		ArrayList<Player> recipients = new ArrayList<Player>();
		
		int chatRange = CivChat.getChatRange();
		
		for(Player recipient : Bukkit.getOnlinePlayers()) {
			if(!player.getWorld().getName().equals(recipient.getWorld().getName()))
				continue;
			
			double dist = playerLoc.distance(recipient.getLocation());
			
			if(dist <= chatRange) {
				recipients.add(recipient);
			}
		}
		
		for(Player recipient : recipients) {
			recipient.sendMessage(sender.getName() + ": " + message);
		}
	}
}
