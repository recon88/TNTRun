package tntrun.arena;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
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
	

	
	private boolean enabled = false;
	public boolean running = false;
	
	private String arenaname;
	private World world;
	private Vector p1 = null;
	private Vector p2 = null;
	public int maxPlayers = 6;
	private int curPlayers = 0;
	
	private HashSet<GameLevel> gamelevels = new HashSet<GameLevel>();
	private LooseLevel looselevel = new LooseLevel();
	private List<Location> spawnpoints = new ArrayList<Location>();
	
	
	public void enableArena(Player sender)
	{
		if (isArenaConfigured())
		{
			enabled = true;
			sender.sendMessage("Arena "+arenaname+" enabled");
		} else 
		{
			sender.sendMessage("Arena "+arenaname+" is not yet configured");
		}
	}
	public void disableArena(Player sender)
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
	
	
		
	public String getArenaName()
	{
		return arenaname;
	}

	private boolean isArenaConfigured()
	{
		if (world == null) {return false;}
		if (p1 == null || p2==null) {return false;}
		if (gamelevels.size() == 0) {return false;}
		if (!looselevel.isConfigured()) {return false;}
		if (spawnpoints.size() == 0) {return false;}
		return true;
	}
	

	public void spawnPlayer(Player player)
	{
		player.teleport(spawnpoints.get(new Random().nextInt(spawnpoints.size()-1)));
		curPlayers++;
		if (curPlayers == maxPlayers)
		{
			this.running = true;
			curPlayers = 0;
		}
	}
	public void leavePlayer(Player player)
	{
		removePlayerFromArena(player);
		curPlayers--;
	}
	
	public void handlePlayer(final Player player)
	{
		//check if player is in arena
		if (!player.getLocation().toVector().isInAABB(p1, p2))
		{
			player.sendMessage("You left the arena");
			removePlayerFromArena(player);
		}
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
				Player winner = Bukkit.getPlayerExact(plugin.pdata.getArenaPlayers(this).iterator().next());
				winner.sendMessage("You won the arena");
				removePlayerFromArena(winner);
				rewardPlayer(winner);
				//regenerate arena
				for (final GameLevel gl : gamelevels)
				{
					gl.regen(world);
				}
			}
			//not running
			this.running = false;
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
		FileConfiguration config = new YamlConfiguration();
		config.set("world", world);
		config.set("p1", p1);
		config.set("p2", p2);
		config.set("maxPlayers", maxPlayers);
		int i = 1;
		for (GameLevel gl : gamelevels)
		{
			try 
			{
				gl.saveToConfig("gamelevel"+i, config);
			} catch (Exception e) {}
		}
		looselevel.saveToConfig(config);
	}
	public void loadFromConfig()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/TNTRun/arenas/"+arenaname+".yml"));
		this.world = Bukkit.getWorld(config.getString("world"));
		this.p1 = config.getVector("p1");
		this.p2 = config.getVector("p2");
		this.maxPlayers = config.getInt("maxPlayers");
		ConfigurationSection cs = config.getConfigurationSection("");
		if (cs != null)
		{
			for (String key : cs.getKeys(false))
			{
				if (key.contains("gamelevel"))
				{
					try{
						GameLevel gl = new GameLevel();
						gl.loadFromConfig(key, config);
						gamelevels.add(gl);
					} catch (Exception e) {}
				}
			}
		}
		looselevel.loadFromConfig(config);
		plugin.pdata.putArenaInHashMap(this);
		if (isArenaConfigured())
		{
			for (GameLevel gl : gamelevels)
			{
				gl.regen(world);
			}
			looselevel.regen(world);
			this.enabled = true;
		}
	}
	
}
