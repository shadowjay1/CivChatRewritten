package com.untamedears.recivchat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.untamedears.citadel.Citadel;
import com.untamedears.citadel.GroupManager;
import com.untamedears.citadel.entity.Faction;
import com.untamedears.recivchat.mode.ChatMode;
import com.untamedears.recivchat.mode.GroupChat;
import com.untamedears.recivchat.mode.MessageChat;
import com.untamedears.recivchat.mode.NormalChat;
import com.untamedears.recivchat.mode.Replyable;

public class CivChat extends JavaPlugin implements Listener {
	public static CivChat instance;
	
	private static int chatRange = 1000;
	
	private PlayerManager playerManager = new PlayerManager();
	
	public void onEnable() {
		instance = this;
		
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equals("msg")) {
			if(args.length == 0) {
				ChatMode mode = playerManager.getChatMode(sender.getName());
				
				if(!(mode instanceof NormalChat)) {
					playerManager.removePlayerChatMode(sender.getName());
					
					sender.sendMessage(ChatColor.YELLOW + "You have been moved to normal chat.");
				}
				else {
					sender.sendMessage(ChatColor.YELLOW + "Usage: " + command.getUsage());
				}
			}
			else if(args.length == 1) {
				Player player = Bukkit.getPlayer(args[0]);
				
				if(player != null) {
					playerManager.setChatMode(sender.getName(), new MessageChat(player.getName()));
					
					sender.sendMessage(ChatColor.YELLOW + "You are now in a conversation with " + player.getName() + ".");
				}
				else {
					sender.sendMessage(ChatColor.YELLOW + "The specified player is not online.");
				}
			}
			else if(args.length > 1) {
				Player player = Bukkit.getPlayer(args[0]);
				
				if(player != null) {
					MessageChat chat = new MessageChat(player.getName());
					
					String message = "";
					
					for(int i = 1; i < args.length; i++) {
						message += args[i];
						
						if(i < args.length - 1) {
							message += " ";
						}
					}
					
					chat.directMessage(sender, message);
				}
				else {
					sender.sendMessage(ChatColor.YELLOW + "The specified player is not online.");
				}
			}
		}
		else if(command.getName().equals("groupchat")) {
			if(args.length == 0) {
				ChatMode mode = playerManager.getChatMode(sender.getName());
				
				if(!(mode instanceof NormalChat)) {
					playerManager.removePlayerChatMode(sender.getName());
					
					sender.sendMessage(ChatColor.YELLOW + "You have been moved to normal chat.");
				}
				else {
					sender.sendMessage(ChatColor.YELLOW + "Usage: " + command.getUsage());
				}
			}
			else if(args.length == 1) {
				GroupManager groupManager = Citadel.getGroupManager();
				
				if(groupManager.isGroup(args[0])) {
					Faction group = groupManager.getGroup(args[0]);
					
					playerManager.setChatMode(sender.getName(), new GroupChat(group.getName()));
					
					sender.sendMessage(ChatColor.YELLOW + "You are now in a conversation with " + group.getName() + ".");
				}
				else {
					sender.sendMessage(ChatColor.YELLOW + "The specified group does not exist.");
				}
			}
			else if(args.length > 1) {
				GroupManager groupManager = Citadel.getGroupManager();
				
				if(groupManager.isGroup(args[0])) {
					Faction group = groupManager.getGroup(args[0]);
					
					MessageChat chat = new MessageChat(group.getName());
					
					String message = "";
					
					for(int i = 1; i < args.length; i++) {
						message += args[i];
						
						if(i < args.length - 1) {
							message += " ";
						}
					}
					
					chat.directMessage(sender, message);
				}
				else {
					sender.sendMessage(ChatColor.YELLOW + "The specified group does not exist.");
				}
			}
		}
		else if(command.getName().equals("reply")) {
			Replyable replyable = playerManager.getLastReplyable(sender.getName());
			
			if(replyable != null) {
				String message = "";
				
				for(int i = 0; i < args.length; i++) {
					message += args[i];
					
					if(i < args.length - 1) {
						message += " ";
					}
				}
				
				replyable.getReplyChat().directMessage(sender, message);
			}
			else {
				sender.sendMessage(ChatColor.YELLOW + "You do not have anything to reply to!");
			}
		}
		else if(command.getName().equals("ignore")) {
			
		}
		else if(command.getName().equals("civchat")) {
			
		}
		
		return true;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		ChatMode chatMode = playerManager.getChatMode(event.getPlayer().getName());
		
		chatMode.onPlayerChat(event);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		playerManager.removePlayerChatMode(event.getPlayer().getName());
		playerManager.removeLastReplyable(event.getPlayer().getName());
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public static int getChatRange() {
		return chatRange;
	}
}
