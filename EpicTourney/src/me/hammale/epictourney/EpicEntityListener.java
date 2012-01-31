package me.hammale.epictourney;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class EpicEntityListener extends EntityListener {

	 public final epictourney plugin;
		
	 public EpicEntityListener(epictourney plugin){
		 this.plugin = plugin;
	 }
	
	 public void onEntityDamage(EntityDamageEvent e){
		 if(plugin.active == true){
			 if(e.getEntity() instanceof Player){
				 Player p = (Player) e.getEntity();
				 if(plugin.viewers.contains(p.getName())){
					 e.setCancelled(true);
				 }
				 if(e instanceof EntityDamageByEntityEvent){
					 Entity attacker = ((EntityDamageByEntityEvent) e).getDamager();
			         if (attacker instanceof Player ){
				            Player attack = (Player) attacker;		            
				            if(plugin.viewers.contains(attack.getName())) {
				                e.setCancelled(true);
				            }    
				     }
				}  
			 }
		 }
	}
	 
	 public void onEntityDeath(EntityDeathEvent e){
		 if(plugin.active == true){
			 if(e.getEntity() instanceof Player){
				 Player p = (Player) e.getEntity();
				 if(plugin.fiters.contains(p.getName())){
					 plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + p.getName() + " has been defeated!");
					 plugin.fiters.remove(p.getName());
					 System.out.println(plugin.fiters.size() + ", " + plugin.fiters);
					 if(plugin.fiters.size() > 1){
						 plugin.getServer().broadcastMessage(ChatColor.GREEN + "Arena shrinking...");
						 plugin.shrinkArena();
						 if(plugin.isBan() == 1){
							 	plugin.banned.add(p);
							    p.setBanned(true);
							    p.kickPlayer("You have been defeated! Come back when the tourney is over!");
						 }else{
							 plugin.viewers.add(p.getName());
						 }
					 }else{
						 plugin.getServer().broadcastMessage(ChatColor.GREEN + "" + plugin.fiters + " has won!");
						 plugin.winner = plugin.fiters;
						 plugin.stopTounrey();
					 }
				 }
			 }
		 }
	} 
}
