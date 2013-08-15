package tntrun.arena;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import tntrun.TNTRun;

public class Arena {

	private TNTRun plugin;
	public Arena(String name, TNTRun plugin)
	{
		this.arenaname = name;
		this.plugin = plugin;
	}
	
	private String arenaname;
	
	private boolean enabled = false;
	
	private boolean running = false;
	
	private Vector p1 = null;
	private Vector p2 = null;
	private World world;
	
	private HashSet<GameLevel> gamelevels = new HashSet<GameLevel>();
	private LooseLevel looselevel = new LooseLevel();
	private List<Location> spawnpoints = new ArrayList<Location>();
	
	
	public String getArenaName()
	{
		return arenaname;
	}
	public void enableArena()
	{
		enabled = true;
		saveToConfig();
		plugin.pdata.putArenaInHashMap(this);
	}
	public void disableArena()
	{
		enabled = false;
		//drop players
		for (String player : plugin.pdata.getArenaPlayers(this))
		{
			Bukkit.getPlayerExact(player).sendMessage("Arena is disabling");
			removePlayerFromArena(Bukkit.getPlayerExact(player));
		}
	}
	public boolean isArenaEnabled()
	{
		return enabled;
	}
	public boolean isArenaRunning()
	{
		return running;
	}
	public void setArenaRunning(boolean running)
	{
		this.running = running;
	}
	
	
	public boolean isInArena(Location loc)
	{
		if (loc.toVector().isInAABB(p1, p2))
		{
			return true;
		}
		return false;
	}
	
	
	public void spawnPlayer(Player player)
	{
		player.teleport(spawnpoints.get(new Random().nextInt(spawnpoints.size()-1)));
	}
	
	
	public void handlePlayer(final Player player)
	{
		//check for game location
		for (final GameLevel gl : gamelevels)
		{
			if (gl.isSandLocation(player.getLocation().add(0,-1,0)))
			{
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					public void run()
					{ 
						if (running)
						{
							gl.destroyBlock(player.getLocation(), world);
						}
					}
				}, 10);
			}
		}
		//check for loose location
		if (looselevel.isLooseLocation(player.getLocation()))
		{
			//player lost
			player.sendMessage("You lost the arena");
			removePlayerFromArena(player);
			//now check for win
			if (plugin.pdata.getArenaPlayers(this).size() == 1)
			{
				//last player won
				player.sendMessage("You won the arena");
				removePlayerFromArena(player);
				rewardPlayer(player);
				//regenerate arena
				for (final GameLevel gl : gamelevels)
				{
					gl.regenGameLocation(world);
				}
			}
		}
	}
	
	private void removePlayerFromArena(Player player)
	{
		plugin.pdata.removePlayerFromArena(player.getName());
		player.teleport(plugin.pdata.getPlayerLocation(player.getName()));
		player.getInventory().setContents(plugin.pdata.getPlayerInventory(player.getName()));
		player.getInventory().setArmorContents(plugin.pdata.getPlayerArmor(player.getName()));
	}
	
	private void rewardPlayer(Player player)
	{
		
	}

	
	public void saveToConfig()
	{
		
	}
	
	public void loadFromConfig()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/TNTRun/arenas/"+arenaname+".yml"));
	//	ConfigutaionSection cs
		plugin.pdata.putArenaInHashMap(this);
	}
	
}
