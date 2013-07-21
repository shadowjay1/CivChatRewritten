package com.untamedears.recivchat.mode;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.untamedears.citadel.Citadel;
import com.untamedears.citadel.GroupManager;
import com.untamedears.citadel.entity.Faction;
import com.untamedears.recivchat.CivChat;

public class GroupChat implements ChatMode, Replyable {
	private String groupName;
	
	public GroupChat(String groupName) {
		this.groupName = groupName;
	}
	
	@Override
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player sender = event.getPlayer();
		
		GroupManager groupManager = Citadel.getGroupManager();
		
		if(!groupManager.isGroup(groupName)) {
			sender.sendMessage(ChatColor.YELLOW + "The group you were chatting with no longer exists. You have been moved to normal chat.");
			
			CivChat.instance.getPlayerManager().removePlayerChatMode(sender.getName());
			
			event.setCancelled(true);
			
			return;
		}
		
		Faction group = groupManager.getGroup(groupName);
		
		if(!group.isMember(sender.getName()) && !group.isModerator(sender.getName()) && !group.isFounder(sender.getName())) {
			sender.sendMessage(ChatColor.YELLOW + "You are no longer a part of the group you were chatting with. You have been moved to normal chat.");
			
			CivChat.instance.getPlayerManager().removePlayerChatMode(sender.getName());
			
			event.setCancelled(true);
			
			return;
		}
		
		ArrayList<Player> remove = new ArrayList<Player>();
		
		for(Player recipient : event.getRecipients()) {
			String recipientName = recipient.getName();
			
			if(CivChat.instance.getPlayerManager().isIgnoring(recipientName, sender.getName())) {
				remove.add(recipient);
				
				continue;
			}
			
			if(!group.isMember(recipientName) && !group.isModerator(recipientName) && !group.isFounder(recipientName)) {
				remove.add(recipient);
			}
		}
		
		event.getRecipients().removeAll(remove);
		event.getRecipients().remove(sender);
		
		event.setFormat(ChatColor.LIGHT_PURPLE + "From %s: %s");
		
		CivChat.instance.getPlayerManager().updateLastReplyables(event.getRecipients(), this);
		
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "To " + groupName + ": " + event.getMessage());
	}

	@Override
	public void directMessage(CommandSender sender, String message) {
		GroupManager groupManager = Citadel.getGroupManager();
		
		if(!groupManager.isGroup(groupName)) {
			sender.sendMessage(ChatColor.YELLOW + "The specified group does not exist!");
			
			return;
		}
		
		Faction group = groupManager.getGroup(groupName);
		
		if((sender instanceof Player) && (!group.isMember(sender.getName()) && !group.isModerator(sender.getName()) && !group.isFounder(sender.getName()))) {
			sender.sendMessage(ChatColor.YELLOW + "You are not a part of the specified group.");
			
			return;
		}
		
		ArrayList<Player> recipients = new ArrayList<Player>();
		
		for(Player recipient : Bukkit.getOnlinePlayers()) {
			String recipientName = recipient.getName();
			
			if(recipient.getName().equals(sender.getName()))
				continue;
			
			if(CivChat.instance.getPlayerManager().isIgnoring(recipient.getName(), sender.getName())) {
				continue;
			}
			
			if(group.isMember(recipientName) || group.isModerator(recipientName) || group.isFounder(recipientName)) {
				recipients.add(recipient);
			}
		}
		
		for(Player recipient : recipients) {
			recipient.sendMessage(ChatColor.LIGHT_PURPLE + "From " + sender.getName() + ": " + message);
		}
		
		CivChat.instance.getPlayerManager().updateLastReplyables(recipients, this);
		
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "To " + group.getName() + ": " + message);
	}

	@Override
	public ChatMode getReplyChat() {
		return this;
	}
}
