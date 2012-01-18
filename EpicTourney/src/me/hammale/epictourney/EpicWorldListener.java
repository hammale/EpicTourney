package me.hammale.epictourney;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

public class EpicWorldListener extends WorldListener {

	 public final epictourney plugin;
		
	 public EpicWorldListener(epictourney plugin){
		 this.plugin = plugin;
	 }
	 
	public void onWorldLoad(WorldLoadEvent e){
		if(e.getWorld() == plugin.getServer().getWorld("EpicTourney")){
			Location l = plugin.getServer().getWorld("EpicTourney").getSpawnLocation();
			e.getWorld().createExplosion(l, 4F);
		    for (Entity en : e.getWorld().getEntities()) {
		        if (en instanceof Item) {
		        	en.remove();
		        }
		    }
//			Block b = plugin.getServer().getWorld("EpicTourney").getSpawnLocation().getBlock();
//			Block n = b.getRelative(BlockFace.NORTH, 1);
//			Block s = b.getRelative(BlockFace.NORTH, 1);
//			Block east = b.getRelative(BlockFace.NORTH, 1);
//			Block w = b.getRelative(BlockFace.NORTH, 1);
//			Block nw = b.getRelative(BlockFace.NORTH, 1);
//			Block ne = b.getRelative(BlockFace.NORTH, 1);
//			Block sw = b.getRelative(BlockFace.NORTH, 1);
//			Block se = b.getRelative(BlockFace.NORTH, 1);
//			int x = 1;
//			while(b.getTypeId() != 0){
//				b.getRelative(BlockFace.UP, x);
//				b.setTypeId(0);
//				x++;
//			}
//			x = 1;
//			while(n.getTypeId() != 0){
//				n.getRelative(BlockFace.UP, x);
//				n.setTypeId(0);
//				x++;
//			}
//			x = 1;
//			while(s.getTypeId() != 0){
//				s.getRelative(BlockFace.UP, x);
//				s.setTypeId(0);
//				x++;
//			}
//			x = 1;
//			while(east.getTypeId() != 0){
//				east.getRelative(BlockFace.UP, x);
//				east.setTypeId(0);
//				x++;
//			}
//			x = 1;
//			while(w.getTypeId() != 0){
//				w.getRelative(BlockFace.UP, x);
//				w.setTypeId(0);
//				x++;
//			}
//			x = 1;
//			while(ne.getTypeId() != 0){
//				b.getRelative(BlockFace.UP, x);
//				b.setTypeId(0);
//				x++;
//			}
//			x = 1;
//			while(nw.getTypeId() != 0){
//				b.getRelative(BlockFace.UP, x);
//				b.setTypeId(0);
//				x++;
//			}
//			x = 1;
//			while(se.getTypeId() != 0){
//				b.getRelative(BlockFace.UP, x);
//				b.setTypeId(0);
//				x++;
//			}
//			x = 1;
//			while(sw.getTypeId() != 0){
//				b.getRelative(BlockFace.UP, x);
//				b.setTypeId(0);
//				x++;
//			}
		}
	}	
}
