package me.hammale.epictourney;

import org.bukkit.entity.Player;
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
			 }
		 }
	}
	 
	 public void onEntityDeath(EntityDeathEvent e){
		 if(plugin.active == true){
			 if(e.getEntity() instanceof Player){
				 Player p = (Player) e.getEntity();
				 if(plugin.fiters.contains(p.getName())){
					 plugin.shrinkArena(plugin.fiters.size());
				 }
			 }
		 }
	} 
}
