package com.untamedears.recivchat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.entity.Player;

import com.untamedears.recivchat.mode.ChatMode;
import com.untamedears.recivchat.mode.NormalChat;
import com.untamedears.recivchat.mode.Replyable;

public class PlayerManager {
	private HashMap<String, ChatMode> chatModes = new HashMap<String, ChatMode>();
	private HashMap<String, Replyable> lastReplyables = new HashMap<String, Replyable>();
	private HashMap<String, ArrayList<String>> ignoreLists = new HashMap<String, ArrayList<String>>();
	
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
	
	public boolean isIgnoring(String player, String ignored) {
		if(ignoreLists.containsKey(player))
			return ignoreLists.get(player).contains(ignored);
		else
			return false;
	}
	
	public void addIgnore(String player, String ignored) {
		ArrayList<String> ignores;
		
		if(ignoreLists.containsKey(player))
			ignores = ignoreLists.get(player);
		else {
			ignores = new ArrayList<String>();
			ignoreLists.put(player, ignores);
		}
		
		ignores.add(ignored);
	}
	
	public void removeIgnored(String player, String ignored) {
		ArrayList<String> ignores;
		
		if(ignoreLists.containsKey(player)) {
			ignores = ignoreLists.get(player);
			
			ignores.remove(ignored);
		}
	}
	
	public ArrayList<String> getIgnoreList(String player) {
		ArrayList<String> ignoreListCopy = new ArrayList<String>();
		
		if(ignoreLists.containsKey(player)) {
			ignoreListCopy.addAll(ignoreLists.get(player));
		}
		
		return ignoreListCopy;
	}
	
	public void setIgnoreList(String player, Collection<String> ignores) {
		ArrayList<String> ignoreList = new ArrayList<String>();
		ignoreList.addAll(ignores);
		
		ignoreLists.put(player, ignoreList);
	}
	
	public void removeIgnoreList(String player) {
		ignoreLists.remove(player);
	}
	
	public Set<String> getIgnoringPlayers() {
		return ignoreLists.keySet();
	}
}
