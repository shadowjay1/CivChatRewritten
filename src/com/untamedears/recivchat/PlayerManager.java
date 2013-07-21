package com.untamedears.recivchat;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.untamedears.recivchat.mode.ChatMode;
import com.untamedears.recivchat.mode.NormalChat;
import com.untamedears.recivchat.mode.Replyable;

public class PlayerManager {
	private HashMap<String, ChatMode> chatModes = new HashMap<String, ChatMode>();
	private HashMap<String, Replyable> lastReplyables = new HashMap<String, Replyable>();
	
	public PlayerManager() {
		
	}
	
	public void setChatMode(String player, ChatMode mode) {
		chatModes.put(player, mode);
	}
	
	public ChatMode getChatMode(String player) {
		if(chatModes.containsKey(player)) {
			return chatModes.get(player);
		}
		else {
			return NormalChat.instance;
		}
	}
	
	public void removePlayerChatMode(String player) {
		chatModes.remove(player);
	}
	
	public void updateLastReplyables(Collection<Player> players, Replyable replyable) {
		for(Player player : players) {
			updateLastReplyable(player.getName(), replyable);
		}
	}
	
	public void updateLastReplyable(String player, Replyable replyable) {
		lastReplyables.put(player, replyable);
	}
	
	public Replyable getLastReplyable(String player) {
		if(lastReplyables.containsKey(player)) {
			return lastReplyables.get(player);
		}
		else {
			return null;
		}
	}
	
	public void removeLastReplyable(String player) {
		lastReplyables.remove(player);
	}
}
