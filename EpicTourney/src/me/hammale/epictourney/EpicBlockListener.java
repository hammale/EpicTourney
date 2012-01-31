package me.hammale.epictourney;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class EpicBlockListener extends BlockListener {

	 public final epictourney plugin;
		
	 public EpicBlockListener(epictourney plugin){
		 this.plugin = plugin;
	 }
	
	 public void onBlockPlace(BlockPlaceEvent e){
		 if(plugin.active == true && plugin.iv == false){
			 Player p = e.getPlayer();
			 if(plugin.viewers.contains(p.getName())){
				 e.setCancelled(true);
				 p.sendMessage(ChatColor.RED + "No can do partner! You're in viewmode.");
			}
		 }
	}
	 
	 public void onBlockBreak(BlockBreakEvent e){
		 if(plugin.active == true && plugin.iv == false){
			 Player p = e.getPlayer();
			 if(plugin.viewers.contains(p.getName())){
				 e.setCancelled(true);
				 p.sendMessage(ChatColor.RED + "No can do partner! You're in viewmode.");
			}
		 }
	}  
}
