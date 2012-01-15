package me.hammale.epictourney;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class epictourney extends JavaPlugin {
	
	int old = 0;
	
	public FileConfiguration config;
	
	private final EpicPlayerListener playerListener = new EpicPlayerListener(this);
	private final EpicEntityListener entityListener = new EpicEntityListener(this);
	private final EpicBlockListener blockListener = new EpicBlockListener(this);
	
	public ArrayList<String> players = new ArrayList<String>();
	public ArrayList<String> fiters = new ArrayList<String>();
	
	public ArrayList<String> viewers = new ArrayList<String>();
	
	public boolean active = false;
	public boolean online = false;
	
	  Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		loadConfiguration();	
		log.info("[EpicTourney] Version: " + pdfFile.getVersion() + " Enabled!");	
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);		
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();	
		log.info("[EpicTourney] Version: " + pdfFile.getVersion() + " Disabled!");	
	}
	
	public void loadConfiguration(){
	    config = getConfig();
	    config.options().copyDefaults(true); 
	    String path = "ViewTime";
	    String path1 = "RadiusPerPlayer";
	    config.addDefault(path, 60);
	    config.addDefault(path1, 100);
	    config.options().copyDefaults(true);
	    saveConfig();
	}
	
	public int getTime(){
	    config = getConfig();
	    int amnt = config.getInt("ViewTime"); 
	    amnt = amnt*20;
	    return amnt;
	}
	
	public int getRadius(){
	    config = getConfig();
	    int amnt = config.getInt("RadiusPerPlayer");
	    return amnt;
	}
	
	public void createWorld(Player p){
		if(getServer().getWorld("EpicTourney") == null){
			p.sendMessage(ChatColor.RED + "Generating world...");
			getServer().createWorld(new WorldCreator("EpicTourney").environment(World.Environment.NORMAL));
			p.sendMessage(ChatColor.GREEN + "World complete!");
			teleportToWorldSpawn(getServer().getWorld("EpicTourney"));
		}else{
			teleportToWorldSpawn(getServer().getWorld("EpicTourney"));
		}
	}
	
	public void unloadWorld(Player p, World w){
		if(getServer().getWorld("EpicTourney") != null){
			p.sendMessage(ChatColor.RED + "Unloading world...");
			Boolean save = true;
			getServer().unloadWorld(w, save);
			p.sendMessage(ChatColor.GREEN + "World unloaded!");
		}	
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("tourney")){
			if(sender instanceof Player){
			if(args[0].equalsIgnoreCase("start")){
					Player p = (Player) sender;
					if(active == false){
						createWorld(p);
						active = true;
					}else{
						p.sendMessage(ChatColor.RED + "Tourney is already started silly!");
					}
				}else if(args[0].equalsIgnoreCase("stop")){
					stopTounrey();
				}else if(args[0].equalsIgnoreCase("test")){
					sender.sendMessage("ACTION!");
					Block b = getServer().getWorld("EpicTourney").getSpawnLocation().getBlock();
					
					Location l1 = b.getRelative(BlockFace.NORTH_WEST, 10).getLocation();
					int x1 = (int) l1.getX();
					int z1 = (int) l1.getZ();
										
					Location l2 = b.getRelative(BlockFace.NORTH_EAST, 10).getLocation();
					
					int x2 = (int) l2.getX();
					int z2 = (int) l2.getZ();
					
					Location lo1 = getServer().getWorld("EpicTourney").getBlockAt(x1, 127, z1).getLocation();
					Location lo2 = getServer().getWorld("EpicTourney").getBlockAt(x2, 1, z2).getLocation();
				
					loopThrough(lo1, lo2, getServer().getWorld("EpicTourney"));
					
					Location l3 = b.getRelative(BlockFace.SOUTH_WEST, 10).getLocation();
					int x3 = (int) l3.getX();
					int z3 = (int) l3.getZ();
										
					Location l4 = b.getRelative(BlockFace.SOUTH_EAST, 10).getLocation();				
					int x4 = (int) l4.getX();
					int z4 = (int) l4.getZ();
					
					Location lo3 = getServer().getWorld("EpicTourney").getBlockAt(x3, 127, z3).getLocation();
					Location lo4 = getServer().getWorld("EpicTourney").getBlockAt(x4, 1, z4).getLocation();
				
					loopThrough(lo3, lo4, getServer().getWorld("EpicTourney"));
					
					Location l5 = b.getRelative(BlockFace.SOUTH_WEST, 10).getLocation();
					int x5 = (int) l5.getX();
					int z5 = (int) l5.getZ();
										
					Location l6 = b.getRelative(BlockFace.NORTH_WEST, 10).getLocation();				
					int x6 = (int) l6.getX();
					int z6 = (int) l6.getZ();
					
					Location lo5 = getServer().getWorld("EpicTourney").getBlockAt(x5, 127, z5).getLocation();
					Location lo6 = getServer().getWorld("EpicTourney").getBlockAt(x6, 1, z6).getLocation();
				
					loopThrough(lo5, lo6, getServer().getWorld("EpicTourney"));
					
					Location l7 = b.getRelative(BlockFace.SOUTH_EAST, 10).getLocation();
					int x7 = (int) l7.getX();
					int z7 = (int) l7.getZ();
										
					Location l8 = b.getRelative(BlockFace.NORTH_EAST, 10).getLocation();				
					int x8 = (int) l8.getX();
					int z8 = (int) l8.getZ();
					
					Location lo7 = getServer().getWorld("EpicTourney").getBlockAt(x7, 127, z7).getLocation();
					Location lo8 = getServer().getWorld("EpicTourney").getBlockAt(x8, 1, z8).getLocation();
				
					loopThrough(lo7, lo8, getServer().getWorld("EpicTourney"));
					
				}
			}	
		}
		return false; 
	}
	
	public void teleportToWorldSpawn(World to) {
		for(World w : getServer().getWorlds()){
			for (Player p : w.getPlayers().toArray(new Player[0])) {
				p.sendMessage(ChatColor.GREEN + "Tourney started! Teleporting...");
				p.teleport(to.getSpawnLocation());
				p.setGameMode(GameMode.CREATIVE);
				p.sendMessage(ChatColor.LIGHT_PURPLE + "Welcome to view mode! Tourney starts in " + getTime()/20 + " seconds!");
			}
		}
		startTimer();
	}
	
	public void startTimer(){
		final int time = getTime();
		final int half = time/2;
			getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
			    public void run() {
			        getServer().broadcastMessage(ChatColor.GOLD + "" + (time/20)/2 + " seconds to tourney!");
			        if(half >= 10){
			        	halfLeft(time);
			        }else{
			        	startTourney();
			        }
			    }
			}, half);
	}
	
	public void halfLeft(final int i){
		final int half = i/4;
    	getServer().broadcastMessage(ChatColor.GOLD + "" + (i/20)/4 + " seconds to tourney!");	
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
		    public void run() {
		    	startTourney();
		    }
		}, half);
	}
	
	public void startTourney(){
		//if(fiters.size() < 2){
		//	getServer().broadcastMessage(ChatColor.RED + "Too few fighters!");
		//	stopTounrey();
		//}else{
			getServer().broadcastMessage(ChatColor.GREEN + "Tourney started!");
			online = true;
			int i = 0;
			for(Player p : getServer().getOnlinePlayers()){
				fiters.add(p.getName());
				p.setGameMode(GameMode.SURVIVAL);
				i++;
		//	}
			old = i;
			World w = getServer().getWorld("EpicTourney");
			makeArena(w, getServer().getPlayer("hammale"));
		}
	}

	public void stopTounrey() {
		getServer().broadcastMessage(ChatColor.RED + "Tourney ended!");
		active = false;
		online = false;
		fiters.clear();	
		viewers.clear();	
	}
	
	public void makeArena(World w, Player p){
		Block b = w.getSpawnLocation().getBlock();
		int radius = getRadius();
		Block ne1 = b.getRelative(BlockFace.NORTH_EAST, radius).getRelative(BlockFace.WEST, 1);
		
		Location ne = ne1.getLocation();

		
		while(ne != null){
			radius++;
			if(ne1.getRelative(BlockFace.UP, radius).getLocation() == null){
				break;
			}
			ne = ne1.getRelative(BlockFace.UP, radius).getLocation();
		}
		
		radius = getRadius();
		
		Block se1 = b.getRelative(BlockFace.SOUTH_EAST, radius);
		
		Location se = se1.getLocation();
		Location nw = null;
		Location sw = null;
		
		while(se != null){
			radius++;
			if(se1.getRelative(BlockFace.UP, radius).getLocation() == null){
				break;
			}
			se = se1.getRelative(BlockFace.DOWN, radius).getLocation();
		}
		
		radius = getRadius();
		
		Block nw1 = b.getRelative(BlockFace.NORTH_WEST, radius);
		
		while(nw1 != null){
			radius++;
			nw1 = nw1.getRelative(BlockFace.DOWN, radius);
		}
		
		radius = getRadius();
		
		Block sw1 = b.getRelative(BlockFace.SOUTH_WEST, radius);
		
		while(sw1 != null){
			radius++;
			sw1 = sw1.getRelative(BlockFace.UP, radius);
		}
		
		//loopThrough(nw1.getRelative(BlockFace.UP, 1).getLocation(), sw1.getRelative(BlockFace.DOWN, 1).getLocation(), w);
		loopThrough(p.getLocation(), b.getLocation(), w);
//		loopThrough(nw, sw, getServer().getWorld("EpicTourney"));
//		
//		ne1 = b.getRelative(BlockFace.NORTH_EAST, radius);
//		
//		while(ne1 != null){
//			radius++;
//			ne = ne1.getRelative(BlockFace.UP, radius).getLocation();
//		}
//		
//		radius = getRadius();
//		
//		se1 = b.getRelative(BlockFace.SOUTH_EAST, radius);
//		
//		while(se1 != null){
//			radius++;
//			se = se1.getRelative(BlockFace.UP, radius).getLocation();
//		}
//		
//		radius = getRadius();
//		
//		nw1 = b.getRelative(BlockFace.NORTH_WEST, radius);
//		
//		while(nw1 != null){
//			radius++;
//			nw = nw1.getRelative(BlockFace.DOWN, radius).getLocation();
//		}
//		
//		radius = getRadius();
//		
//		sw1 = b.getRelative(BlockFace.SOUTH_WEST, radius);
//		
//		while(sw1 != null){
//			radius++;
//			sw = sw1.getRelative(BlockFace.DOWN, radius).getLocation();
//		}
//		
//		loopThrough(ne, sw, getServer().getWorld("EpicTourney"));
//		loopThrough(nw, se, getServer().getWorld("EpicTourney"));
		System.out.println("Starting wall...");
	}
	
	public void loopThrough(Location loc1, Location loc2, World w) {
			int minx = Math.min(loc1.getBlockX(), loc2.getBlockX()),
			miny = Math.min(loc1.getBlockY(), loc2.getBlockY()),
			minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ()),
			maxx = Math.max(loc1.getBlockX(), loc2.getBlockX()),
			maxy = Math.max(loc1.getBlockY(), loc2.getBlockY()),
			maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
			for(int x = minx; x<=maxx;x++){
				for(int y = miny; y<=maxy;y++){
					for(int z = minz; z<=maxz;z++){
					Block b = w.getBlockAt(x, y, z);
						if(b != null){
							b.setTypeIdAndData(53, (byte)4, true);
						}	
					}
				}
			}
	}
	
	public void shrinkArena(int size) {

		
		
	}
}