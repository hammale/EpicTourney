package me.hammale.epictourney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EpicPlayerListener extends PlayerListener {

	 public final epictourney plugin;
		
	  public HashMap<String, ArrayList<String>> playerHideTree = new HashMap<String, ArrayList<String>>();
	  public HashSet<String> commonPlayers = new HashSet<String>();
	  public final HashMap<String, Integer> schedulers = new HashMap<String, Integer>();	  
	  public HashMap<String, BukkitTimer> timers = new HashMap<String, BukkitTimer>();
	  public ArrayList<String> players = new ArrayList<String>();
	 
	 public EpicPlayerListener(epictourney plugin){
		 this.plugin = plugin;
	 }
	 
	 public void onPlayerJoin(PlayerJoinEvent e){
		 if(plugin.active == true){
			 if(plugin.online == false){
				 e.setJoinMessage(ChatColor.YELLOW + "" + e.getPlayer().getName() + " joined and entered view mode.");
				 Player p = e.getPlayer();
				 plugin.viewers.add(e.getPlayer().getName());
				 p.sendMessage(ChatColor.RED + "Tourney in progress! Welcome to view mode!");
				 p.sendMessage(ChatColor.GREEN + "Tourney started! Teleporting...");
				 p.teleport(plugin.getServer().getWorld("EpicTourney").getSpawnLocation());
				 vanishPlayer(p);
			 }else{
				 plugin.fiters.add(e.getPlayer().getName());
				 e.getPlayer().setGameMode(GameMode.SURVIVAL);
			 }
		 }
	 }

	 public void onPlayerChat(PlayerChatEvent e){
		 if(plugin.active == true){
			 if(plugin.viewers.contains(e.getPlayer().getName())){
				 e.setCancelled(true);
				 e.getPlayer().sendMessage(ChatColor.RED + "Shhh you're in viewmode!");
			 }
		 }
	 }
	 
	 public void onPlayerDropItem(PlayerDropItemEvent e){
		 if(plugin.active == true){
			 if(plugin.viewers.contains(e.getPlayer().getName())){
				 e.setCancelled(true);
			 }
		 }
	 }
	 
	 public void onPlayerQuit(PlayerQuitEvent e){
		 if(plugin.active == true){
			 Player p = e.getPlayer();
			 if(plugin.viewers.contains(p.getName())){
				 plugin.viewers.remove(p.getName());
			 }
			 if(plugin.fiters.contains(p.getName())){
				 plugin.fiters.remove(p.getName());
			 }
		 }
	 }
	 
	private void vanishPlayer(final Player player) {
		final String name = player.getName();
		  schedulers.put(player.getName(), plugin.getServer().getScheduler()
		  .scheduleAsyncRepeatingTask(plugin, new Runnable() {
		  @Override
		  public void run() {
		  try {
			  if (!player.isOnline()) {
			  plugin.getServer().getScheduler()
			  .cancelTask(schedulers.get(name));
			  schedulers.remove(name);
			  commonPlayers.remove(player.getName());
			  playerHideTree.remove(player.getName());
			  return;
			  }
			  if(!(plugin.viewers.contains(name))){
				  reappear(player);
				  return;
			  }
		  } catch (Exception e) {
			  }
			  }
			  }, 0, 500L));
			  // }
			  Player[] playerList = plugin.getServer().getOnlinePlayers();
			  for (Player p : playerList) {
			  invisible(player, p);
		  }

	}
	 
	  public void invisible(Player p1, Player p2) {
		  if (outsideSight(p1.getLocation(), p2.getLocation())) {
		  return;
		  }
		  CraftPlayer hide = (CraftPlayer) p1;
		  CraftPlayer hideFrom = (CraftPlayer) p2;

		  if (!playerHideTree.containsKey(p1.getName())) {
		  playerHideTree.put(p1.getName(), new ArrayList<String>());
		  }
		  //if ((!playerHideTree.get(p1.getName()).contains(p2.getName()) || force)){
		  //.&& !plugin.getSettings().isSeeAll(p2.getName())) {
		  if (p1 != p2) {
		  try {
		  hideFrom.getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(hide.getEntityId()));
		  playerHideTree.get(p1.getName()).add(p2.getName());
		  } catch (Exception e) {
		  // Why would I care about some networking exceptions? Ha ha ha...
		  //}
		  }
		  }
	}
	  
	  public boolean outsideSight(Location loc1, Location loc2) {
		  World w1 = loc1.getWorld();
		  World w2 = loc2.getWorld();
		  if (!w1.getName().equals(w2.getName())) {
		  // We don't need to hide people from different worlds! Woohoo, multiworld friendly!
		  return false;
		  }
		  Chunk chG = w2.getChunkAt(loc2.getBlock());
		  Chunk ch = w1.getChunkAt(loc1.getBlock());
		  int maxX = chG.getX() + 16; // Just making sure nobody will still be
		  // visible
		  int minX = chG.getX() - 16;
		  int maxZ = chG.getZ() + 16;
		  int minZ = chG.getZ() - 16;
		  if ((ch.getX() <= maxX || ch.getX() >= minX)
		  || (ch.getZ() <= maxZ || ch.getZ() >= minZ)) {
		  return false;
		  } else {
		  return true;
		  }
	}

	  private void uninvisible(Player p1, Player p2) {
	  CraftPlayer unHide = (CraftPlayer) p1;
	  CraftPlayer unHideFrom = (CraftPlayer) p2;
	  if (p1 != p2 && playerHideTree.containsKey(p1.getName())) {
	  if (playerHideTree.get(p1.getName()).contains(p2.getName())) {
	  unHideFrom.getHandle().netServerHandler
	  .sendPacket(new Packet20NamedEntitySpawn(unHide
	  .getHandle()));
	  playerHideTree.get(p1.getName()).remove(p2.getName());
	  }
	  }
	  }


	  public void cleanuptimers(Player player) {
	  //if (timers.containsKey(player.getName())) {
		//timers.get(player.getName()).cancel();
	  	//timers.remove(player.getName());
	  //}
	  if (schedulers.containsKey(player.getName())) {
		  plugin.getServer().getScheduler()
	  	.cancelTask(schedulers.get(player.getName()));
	  	schedulers.remove(player.getName());
	  }
	  }
	  
	  public void reappear(Player player) {
		  if (!commonPlayers.contains(player.getName())) {
			  return;
		  }
		  commonPlayers.remove(player.getName());
		  cleanuptimers(player);
		  Player[] playerList = plugin.getServer().getOnlinePlayers();
		  for (Player p : playerList) {
		  if (!p.getName().equals(player.getName())) {
		  uninvisible(player, p);
		  }
		  }
		  playerHideTree.remove(player.getName());
	}
	  
}
