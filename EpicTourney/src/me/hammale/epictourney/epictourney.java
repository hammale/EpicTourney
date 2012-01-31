package me.hammale.epictourney;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class epictourney extends JavaPlugin {
	
	int old = 0;
	
	public FileConfiguration config;
	
	private final EpicPlayerListener playerListener = new EpicPlayerListener(this);
	private final EpicEntityListener entityListener = new EpicEntityListener(this);
	private final EpicBlockListener blockListener = new EpicBlockListener(this);
	
	  public HashMap<String, ArrayList<String>> playerHideTree = new HashMap<String, ArrayList<String>>();
	  public HashSet<String> commonPlayers = new HashSet<String>();
	  public final HashMap<String, Integer> schedulers = new HashMap<String, Integer>();	  
	  public HashMap<String, BukkitTimer> timers = new HashMap<String, BukkitTimer>();
	  public HashSet<String> spying = new HashSet<String>();
	  public HashSet<String> total = new HashSet<String>();
	  public HashSet<String> winner = null;	
	  
	public ArrayList<String> players = new ArrayList<String>();
	public HashSet<String> fiters = new HashSet<String>();
	public HashSet<Player> banned = new HashSet<Player>();
	public ArrayList<String> viewers = new ArrayList<String>();
	
	public boolean active = false;
	public boolean online = false;
	public boolean cancelled = false;
	public boolean fixWorld = false;
	public boolean na = true;;
	public boolean iv = true;
	public boolean real = false;
	
	int id = 0;
	int sid = -1;
	int size = -1;
	
	Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		loadConfiguration();	
		log.info("[EpicTourney] Version: " + pdfFile.getVersion() + " Enabled!");	
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		getServer().getScheduler().cancelTasks(this);
		log.info("[EpicTourney] Version: " + pdfFile.getVersion() + " Disabled!");	
	}
	
	public void loadConfiguration(){
	    config = getConfig();
	    config.options().copyDefaults(true); 
	    String path = "ViewTime";
	    String path1 = "RadiusPerPlayer";
	    String path2 = "MinPlayers";
	    String path3 = "BanOnDeath";
	    String path4 = "AutoRestart";
	    String path5 = "AllowInvisibility";
	    String path6 = "Log";
	    config.addDefault(path, 60);
	    config.addDefault(path1, 30);
	    config.addDefault(path2, 2);
	    config.addDefault(path3, 0);
	    config.addDefault(path4, 0);
	    config.addDefault(path5, 1);
	    config.addDefault(path6, 1);
	    config.options().copyDefaults(true);
	    saveConfig();
	}
	
	public int getTime(){
	    config = getConfig();
	    int amnt = config.getInt("ViewTime"); 
	    amnt = amnt*20;
	    return amnt;
	}
	
	public int allowSpout(){
	    config = getConfig();
	    int amnt = config.getInt("AllowInvisibility"); 
	    return amnt;
	}
	
	public int getLog(){
	    config = getConfig();
	    int amnt = config.getInt("Log"); 
	    return amnt;
	}
	
	public int getRadius(){
	    config = getConfig();
	    int amnt = config.getInt("RadiusPerPlayer");
	    return amnt;
	}
	
	public int isBan(){
	    config = getConfig();
	    int amnt = config.getInt("BanOnDeath");
	    return amnt;
	}
	
	public int isAuto(){
	    config = getConfig();
	    int amnt = config.getInt("AutoRestart");
	    return amnt;
	}
	
	public int getMinPlayers(){
	    config = getConfig();
	    int amnt = config.getInt("MinPlayers");
	    return amnt;
	}
	
	public void createWorld(){
		if(getServer().getWorld("EpicTourney") == null){
			getServer().createWorld(new WorldCreator("EpicTourney").environment(World.Environment.NORMAL));
			getServer().broadcastMessage(ChatColor.GREEN + "World complete!");
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
			if(args[0].equalsIgnoreCase("reload")){
				reloadConfig();
				sender.sendMessage("EpicTourney Reloaded!");
			}
			if(sender instanceof Player){
			if(args[0].equalsIgnoreCase("start")){
					Player p = (Player) sender;
					if(active == false){
						fiters.clear();
						getServer().broadcastMessage((ChatColor.RED + "Generating world..."));
						createWorld();
						active = true;
					}else{
						p.sendMessage(ChatColor.RED + "Tourney is already started silly!");
						return true;
					}
				}else if(args[0].equalsIgnoreCase("stop")){
					real = true;
					stopTounrey();
				}
			}	
		}
		return false; 
	}
	
	public void teleportToWorldSpawn(World to) {
		for(final World w : getServer().getWorlds()){
			for (final Player p : w.getPlayers().toArray(new Player[0])) {
				p.sendMessage(ChatColor.GREEN + "Teleporting...");
				saveInventory(p);
				p.getInventory().clear();
				p.setGameMode(GameMode.SURVIVAL);

				getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				    public void run() {			    	
						int i = 0;
						for (final Player p : w.getPlayers().toArray(new Player[0])) {
				    	p.teleport(getServer().getWorld("EpicTourney").getSpawnLocation().getBlock().getRelative(BlockFace.NORTH, i).getLocation());
				    	i++;
						}
						viewers.add(p.getName());
						if(allowSpout() == 1){
							vanishPlayer(p);
							spying.add((p).getName());
						}
						p.sendMessage(ChatColor.LIGHT_PURPLE + "Welcome to view mode! Tourney starts in " + getTime()/20 + " seconds!");
						na = false;
						startTimer();
				    }
				}, 60L);
					
			}
		}		
	}
	
	public void startTimer(){
		if(cancelled == false){
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
		}else{
			cancelled = false;
		}
	}
	
	public void halfLeft(final int i){
		if(cancelled == false){
			final int half = i/4;
			getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
			    public void run() {
			    	startTourney();
			    }
			}, half);
		}else{
			cancelled = false;
		}
	}
	
	public void startTourney(){
		//markBorder();
		stopViewmode();
		int i = 0;
		viewers.clear();
		for(Player p : getServer().getOnlinePlayers()){
			p.setHealth(20);
			total.add(p.getName());
			fiters.add(p.getName());
			p.setGameMode(GameMode.SURVIVAL);
			if(allowSpout() == 1){
				reappear(p);
			}
			p.getInventory().clear();
			i++;
		old = i;			
		}
		if(allowSpout() == 1){
			spying.clear();
		}
		players.clear();
		getServer().getScheduler().cancelTask(sid);
		if(fiters.size() < getMinPlayers()){
			getServer().broadcastMessage(ChatColor.RED + "Too few fighters!");
			stopTounrey();
		}else{
		if(cancelled == false){
				fixWorld = true;
				getServer().broadcastMessage(ChatColor.GREEN + "Tourney started!");
				online = true;
		}else{
			cancelled = false;
		}
	}
	}
	
	public void stopTounrey() {
		if(real == true){
			real = false;
			for(Player p:banned){
				p.setBanned(false);
			}
			getServer().broadcastMessage(ChatColor.RED + "Tourney ended!");
			getServer().getScheduler().cancelTasks(this);
			active = false;
			online = false;
			cancelled = false;
			iv = true;
			id = -1;
			fixWorld = false;
			players.clear();
			fiters.clear();	
			viewers.clear();
			spying.clear();
			World w = getServer().getWorlds().get(0);
			for(Player p : getServer().getWorld("EpicTourney").getPlayers()){
				reappear(p);
				p.sendMessage(ChatColor.YELLOW + "Welcome home " + p.getName() + "!");
				p.teleport(w.getSpawnLocation());
				try {
					restoreInventory(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	       	getServer().unloadWorld(getServer().getWorld("EpicTourney"), false);
			File dir = new File("EpicTourney");
			deleteWorld(dir);
		}else{
		real = false;
		for(Player p:banned){
			p.setBanned(false);
		}
		getServer().broadcastMessage(ChatColor.RED + "Tourney ended!");
		getServer().getScheduler().cancelTasks(this);
		active = false;
		online = false;
		cancelled = false;
		iv = true;
		id = -1;
		fixWorld = false;
		players.clear();
		fiters.clear();	
		viewers.clear();
		spying.clear();
		World w = getServer().getWorlds().get(0);
		for(Player p : getServer().getWorld("EpicTourney").getPlayers()){
			reappear(p);
			p.sendMessage(ChatColor.YELLOW + "Welcome home " + p.getName() + "!");
			p.teleport(w.getSpawnLocation());
			try {
				restoreInventory(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
       	getServer().unloadWorld(getServer().getWorld("EpicTourney"), false);
		File dir = new File("EpicTourney");
		deleteWorld(dir);
		if(isAuto() == 1){
			fiters.clear();
			getServer().broadcastMessage((ChatColor.RED + "Generating world..."));
			createWorld();
			active = true;
		}
		}
		logTourney();
		winner.clear();
		total.clear();
	}
	
	public void logTourney(){
		if(getLog() == 1){
			makeFolder();
			addLog();
		}
	}
	
	public void makeFolder(){
			File file = new File("plugins/EpicTourney/logs");
			boolean exists = file.exists();
			if (!exists) {
				try{
					if(file.mkdir()){
						System.out.println("[EpicTourney] Directory created!");
					}else{
						System.out.println("[EpicTourney] ERROR! Directory not created!");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		  }
		  
		public void addLog() {
		try{
			
		java.util.Date date= new java.util.Date();
		File file = new File("plugins/EpicTourney/logs/" + new Timestamp(date.getTime()) + ".log");

		        Scanner scan = null;
		        String str = null;
		  
		        if (file.exists()) {
		            scan = new java.util.Scanner(file);
		            str = scan.nextLine();
		            while (scan.hasNextLine()) {
		                str = str.concat("\n" + scan.nextLine());
		            }
		        }
		        
		        PrintWriter out = new PrintWriter(new FileWriter(file, true));
				String time = ("Time: " + new Timestamp(date.getTime()));
		        String o = ("Winner: " + winner);
				String t = ("Entrants: ");
				out.println(time);
				out.println(o);
				out.print(t);
		        for(String s : total){
					str = (s);	        
					out.println(str);
		        }	
		        
		        out.close();
		        scan.close();
		}catch (Exception e){
		System.err.println("Error: " + e.getMessage());
		}

		}
	
	
	public void stopViewmode() {
		iv = false;
		getServer().getScheduler().cancelTasks(this);
		id = -1;
		spying.clear();
		for(Player p : getServer().getWorld("EpicTourney").getPlayers()){
			reappear(p);
			cleanuptimers(p);
			for(Player p1 : getServer().getWorld("EpicTourney").getPlayers()){
				uninvisible(p, p1);
			}
		}
	}  
	
		@SuppressWarnings("deprecation")
		public void restoreInventory(Player player) throws IOException{
			FileInputStream fstream = null;
			DataInputStream in = null;
			BufferedReader br = null;
			try{
			      Inventory inventaire = player.getInventory();
			      inventaire.clear();
			      ItemStack[] contenuInventaire = new ItemStack[36];
			      
				  fstream = new FileInputStream("EpicTourney/" + player.getName() + ".dat");
				  in = new DataInputStream(fstream);
				  br = new BufferedReader(new InputStreamReader(in));
				  String ligne;
				  int i = 0;
				  while ((ligne = br.readLine()) != null){
					  Pattern p = Pattern.compile("(.*):(.*):(.*):(.*);");
				        Matcher m = p.matcher(ligne);
				        while (m.find())
				        {
				          int itemPos = Integer.valueOf(m.group(1).trim()).intValue();
				          int itemId = Integer.valueOf(m.group(2).trim()).intValue();
				          int itemAmount = Integer.valueOf(m.group(3).trim()).intValue();
				          short itemDurability = (short)Integer.valueOf(m.group(4).trim()).intValue();
				          if (itemPos == i){
				            contenuInventaire[i] = new ItemStack(itemId, itemAmount, itemDurability);
				          }else{
				            contenuInventaire[i] = new ItemStack(0);
				          }		          
				        }
				        i++;
				  }
				  in.close();
				  br.close();
				  fstream.close();
				  if(contenuInventaire != null){
					  inventaire.addItem(contenuInventaire);
				  }
				  player.updateInventory();
				    }catch (Exception e){
						  in.close();
						  br.close();
						  fstream.close();
				    	System.err.println("Error 1: " + e.getMessage());
				  }	
		}
	  
	  
		public void saveInventory(Player p) {		
			try{
			File file = new File("EpicTourney/" + p.getName() + ".dat");

	        String str = null;  
	  
	        Inventory inventaire = p.getInventory();
	        ItemStack[] contenuInventaire = inventaire.getContents();
	        
	        PrintWriter out = new PrintWriter(new FileWriter(file, true));  
	        
	        for (int i = 0; i < contenuInventaire.length; i++)
	        {
	          if (contenuInventaire[i] != null){
	        	  str = i + ":" + contenuInventaire[i].getTypeId() + ":" + contenuInventaire[i].getAmount() + ":" + contenuInventaire[i].getDurability() + ";\n";
	  	          out.println(str);
	          }
	        }       
 
	        out.close();
			}catch (Exception e){
			  System.err.println("Error 2: " + e.getMessage());
			}	
			
		}
	  
	  
	   public boolean deleteWorld(File dir) {
	        if (dir.isDirectory()) {
	            String[] children = dir.list();
	            for (int i=0; i<children.length; i++) {
	                boolean success = deleteWorld(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        } 
	        return dir.delete();
	    } 
	  
	   
	   public void vanishPlayer(final Player player) {
			final String name = player.getName();
			  schedulers.put(player.getName(), getServer().getScheduler()
			  .scheduleAsyncRepeatingTask(this, new Runnable() {
			  @Override
			  public void run() {
			  try {
				  if (!player.isOnline()) {
					  getServer().getScheduler()
					  .cancelTask(schedulers.get(name));
					  schedulers.remove(name);
					  commonPlayers.remove(player.getName());
					  playerHideTree.remove(player.getName());				  
					  if(!(viewers.contains(name))){
						  reappear(player);
						  return;
					  }
				  }
				  return;
			  } catch (Exception e) {
				  e.printStackTrace();
			  }
				  }
				  }, 0, 500L));
				  // }
				  Player[] playerList = getServer().getOnlinePlayers();
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
				  e.printStackTrace();
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
		  unHideFrom.getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(unHide.getHandle()));
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
			  getServer().getScheduler()
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
			  Player[] playerList = getServer().getOnlinePlayers();
			  for (Player p : playerList) {
			  if (!p.getName().equals(player.getName())) {
			  uninvisible(player, p);
			  }
			  }
			  playerHideTree.remove(player.getName());
		}

		public boolean checkBoarder(Player p) {
			if(size == -1){
				size = fiters.size()*getRadius();
			}else if(size == 0){
				size = viewers.size()*getRadius();
			}
			double dx = p.getLocation().getX();
			double dz = p.getLocation().getZ();
			
			int x = (int)dx;
			int z = (int)dz;
			
			double dx2 = getServer().getWorld("EpicTourney").getSpawnLocation().getX();
			double dz2 = getServer().getWorld("EpicTourney").getSpawnLocation().getZ();
			
			int x2 = (int)dx2;
			int z2 = (int)dz2;
			
			Location l1 = getServer().getWorld("EpicTourney").getBlockAt(x, 127, z).getLocation();
			Location l2 = getServer().getWorld("EpicTourney").getBlockAt(x2, 127, z2).getLocation();
			int dis = (int) l1.distance(l2);
			
			if(dis > size){
				outOfBounds(p);
				return false;
			}else if(dis+5 > size){
				p.sendMessage(ChatColor.RED + "" + p.getName() + " you're near the boarder!");
				return true;
			}
			return true;
		}
		
//		public void markBorder(){
//			int size = (getRadius()*fiters.size())-1;
//			Location ne = getServer().getWorld("EpicTourney").getSpawnLocation().getBlock().getRelative(BlockFace.NORTH_EAST, size).getLocation();
//			Location se = getServer().getWorld("EpicTourney").getSpawnLocation().getBlock().getRelative(BlockFace.SOUTH_EAST, size).getLocation();
//			Location nw = getServer().getWorld("EpicTourney").getSpawnLocation().getBlock().getRelative(BlockFace.NORTH_WEST, size).getLocation();
//			Location sw = getServer().getWorld("EpicTourney").getSpawnLocation().getBlock().getRelative(BlockFace.SOUTH_WEST, size).getLocation();
//			
//			loopThrough(ne, se);
//			loopThrough(nw, sw);
//			loopThrough(ne, se);
//			loopThrough(nw, sw);
//			
//		}
//		
//		  public void loopThrough(Location loc1, Location loc2) {
//			  int minx = Math.min(loc1.getBlockX(), loc2.getBlockX()),
//			  miny = Math.min(loc1.getBlockY(), loc2.getBlockY()),
//			  minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ()),
//			  maxx = Math.max(loc1.getBlockX(), loc2.getBlockX()),
//			  maxy = Math.max(loc1.getBlockY(), loc2.getBlockY()),
//			  maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
//			  for(int x = minx; x<=maxx;x++){
//				  for(int y = miny; y<=maxy;y++){
//					  for(int z = minz; z<=maxz;z++)
//					  {
//						  Block b = getServer().getWorld("EpicTourney").getBlockAt(x, y, z);
////						  if(b.getRelative(BlockFace.UP,1).getTypeId() == 0 && b.getRelative(BlockFace.DOWN,1).getTypeId() != 0){
////							  if(b.getRelative(BlockFace.DOWN,1).getTypeId() == 8 || b.getRelative(BlockFace.DOWN,1).getTypeId() == 9){
////								  b.setType(Material.ICE);
////							  }else if(b.getRelative(BlockFace.DOWN,1).getTypeId() == 10 || b.getRelative(BlockFace.DOWN,1).getTypeId() == 11){
////								  b.setType(Material.OBSIDIAN);
////							  }else{
////								  b.setType(Material.YELLOW_FLOWER);
////							  }
////						  }
//						  if(b.getTypeId() == 0){
//							  b.setTypeId(102);
//						  }
//					  }
//				  }
//			  }
//		  }
		
		public void outOfBounds(Player p){
			if(na == false){
				p.sendMessage(ChatColor.RED + "" + p.getName() + " you're out of bounds! Get back in the fight!");
				p.damage(1);
			}
		}

		public void shrinkArena() {
			//markBorder();
			final int quit = (fiters.size()*getRadius())+getRadius();
			final int check = fiters.size()*getRadius();
			id = getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			    public void run() {
			    	int i = check;
			    while(i != quit){
			    	size = i;
			    	i++;
			    }
			    if(i == quit){
			    	getServer().getScheduler().cancelTask(id);
			    }
			    }
			}, 60L);
		}		
}