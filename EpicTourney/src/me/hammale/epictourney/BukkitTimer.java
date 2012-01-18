package me.hammale.epictourney;

import java.util.TimerTask;

/**
* Cool class. That it is :D
*
* @author nickguletskii200
*/

public class BukkitTimer {
	private int id;
	private epictourney plugin;
	
	public BukkitTimer(epictourney plug) {
		plugin = plug;
	}	
	public void scheduleAtFixedRate(TimerTask tsk, int delay, int step) {
		plugin.sid = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
		plugin, tsk, delay, step);
	}
	
	public void cancel() {
			plugin.getServer().getScheduler().cancelTask(id);
	}
}

