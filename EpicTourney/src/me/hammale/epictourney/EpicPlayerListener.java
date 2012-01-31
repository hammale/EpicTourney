package me.hammale.epictourney;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EpicPlayerListener extends PlayerListener {

	 public final epictourney plugin;
	 
	 public EpicPlayerListener(epictourney plugin){
		 this.plugin = plugin;
	 }
	 
	 public void onPlayerJoin(PlayerJoinEvent e){
		 if(plugin.active == true){
			 if(plugin.online == true){
				 if(plugin.isBan() != 1){
				 e.setJoinMessage(ChatColor.YELLOW + "" + e.getPlayer().getName() + " joined and entered viewmode.");
				 Player p = e.getPlayer();
				 plugin.viewers.add(e.getPlayer().getName());
				 p.sendMessage(ChatColor.RED + "Tourney in progress! Welcome to viewmode!");
				 p.sendMessage(ChatColor.GREEN + "Tourney started! Teleporting...");
				 p.teleport(plugin.getServer().getWorld("EpicTourney").getSpawnLocation());
				 p.setGameMode(GameMode.CREATIVE);
				 if(plugin.allowSpout() == 1){
					 plugin.vanishPlayer(p);
				 }
				 }else{
					 	Player p = e.getPlayer();
					 	plugin.banned.add(p);
					    p.setBanned(true);
					    p.kickPlayer("Tourney in progress! Come back when the tourney is over!");
				 }
			 }else{
				 plugin.fiters.add(e.getPlayer().getName());
				 e.getPlayer().setGameMode(GameMode.SURVIVAL);
			 }
		 }
	 }

	 public void onPlayerRespawn(PlayerRespawnEvent e){
		 if(plugin.active == true){
			 	 e.getPlayer().sendMessage("Going back...");
				 e.getPlayer().teleport(plugin.getServer().getWorld("EpicTourney").getSpawnLocation());
				 e.getPlayer().setGameMode(GameMode.CREATIVE);
		 }
	 }
	 
	@Override
	public void onPlayerKick(PlayerKickEvent e)  {
	   	 if(plugin.active == true){
	         if(e.getReason().startsWith("You moved too") || e.getReason().startsWith("Flying is not")) {
	                 e.setCancelled(true);
	         }
	   	 }
	 }
	 
	 public void onPlayerMove(PlayerMoveEvent e){
		 if(plugin.active == true){
			if((plugin.checkBoarder(e.getPlayer()) == false && plugin.viewers.contains(e.getPlayer().getName())) || plugin.na == true){
				e.setCancelled(true);
			}else{
				plugin.checkBoarder(e.getPlayer());
			}
		 }
	 }
	 
	 public void onPlayerDropItem(PlayerDropItemEvent e){
		 if(plugin.active == true  && plugin.iv == false){
			 if(plugin.viewers.contains(e.getPlayer().getName())){
				 e.setCancelled(true);
			 }
		 }
	 }
	 
	 public void onPlayerQuit(PlayerQuitEvent e){
		 if(plugin.active == true){
			 Player p = e.getPlayer();
			 plugin.reappear(p);
			 if(plugin.viewers.contains(p.getName())){
				 plugin.viewers.remove(p.getName());
			 }
			 if(plugin.fiters.contains(p.getName())){
				 plugin.fiters.remove(p.getName());
			 }
			 if(plugin.spying.contains(p.getName())){
				 plugin.spying.remove(p.getName());
			 }
			 if(plugin.fiters.size() < plugin.getMinPlayers()){
				 plugin.stopTounrey();
			 }
		 }
	 }
	 
}
