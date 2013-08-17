/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package tntrun.arena;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
	

	private Arena thisarena = this;
	private boolean enabled = false;
	public boolean running = false;
	
	private String arenaname;
	public String getArenaName()
	{
		return arenaname;
	}
	private World world;
	private Vector p1 = null;
	private Vector p2 = null;
	public int maxPlayers = 6;
	public int minPlayers = 2;
	public double votesPercent = 0.75;
	private int curPlayers = 0;
	private HashSet<String> votes = new HashSet<String>();
	
	private HashMap<String, GameLevel> gamelevels = new HashMap<String, GameLevel>();
	private LoseLevel loselevel = new LoseLevel();
	private Location spawnpoint = null;
	
	//arena structure handlers
	public boolean isArenaEnabled()
	{
		return enabled;
	}
	public boolean enableArena()
	{
		if (isArenaConfigured())
		{
			enabled = true;
			runArenaHandler();
			return true;
		}
		return false;
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

	public boolean isArenaConfigured()
	{
		if (world == null) {return false;}
		if (p1 == null || p2==null) {return false;}
		if (gamelevels.size() == 0) {return false;}
		if (!loselevel.isConfigured()) {return false;}
		if (spawnpoint == null) {return false;}
		return true;
	}
	public void setArenaPoints(Location loc1, Location loc2)
	{
		this.world = loc1.getWorld();
		this.p1 = loc1.toVector();
		this.p2 = loc2.toVector();
	}
	public void setGameLevel(String glname, Location loc1, Location loc2)
	{
		GameLevel gl = gamelevels.get(glname);
		if (gl == null)
		{
			gl = new GameLevel();
			gamelevels.put(glname, gl);
		}
		gl.setGameLocation(loc1, loc2, world);
	}
	public void setLooseLevel(Location loc1, Location loc2)
	{
		loselevel.setLooseLocation(loc1, loc2, world);
	}
	public void setSpawnPoint(Location loc)
	{
		spawnpoint = loc;
	}
	

	//arena game handlers
	public void spawnPlayer(Player player)
	{
		player.setGameMode(GameMode.SURVIVAL);
		plugin.pdata.setPlayerLocation(player.getName());
		plugin.pdata.setPlayerInventory(player.getName());
		plugin.pdata.setPlayerArmor(player.getName());
		player.getInventory().clear();
		player.teleport(spawnpoint);
		plugin.pdata.setPlayerArena(player.getName(), this);
		curPlayers++;
		if (curPlayers == maxPlayers || curPlayers == minPlayers)
		{
			runArena();
		}
	}
	public void leavePlayer(Player player)
	{
		removePlayerFromArena(player);
		votes.remove(player.getName());
		curPlayers--;
	}
	public void vote(Player player)
	{
		votes.add(player.getName());
		if (votes.size() >= ((int)curPlayers*votesPercent))
		{
			runArena();
		}
	}
	Integer runtaskid = null;
	int count = 10;
	private void runArena()
	{
		Runnable run = new Runnable()
		{
			public void run()
			{
				if (curPlayers < minPlayers) 
				{
					for (String p : plugin.pdata.getArenaPlayers(thisarena))
					{
						Bukkit.getPlayerExact(p).sendMessage("Too much players left the arena, wating for some more");
					}
					Bukkit.getScheduler().cancelTask(runtaskid);
					return;
				}
				if (count == 0)
				{
					running = true;
					curPlayers = 0;
					count = 10;
					Bukkit.getScheduler().cancelTask(runtaskid);
					runtaskid = null;
					for (String p : plugin.pdata.getArenaPlayers(thisarena))
					{
						Bukkit.getPlayerExact(p).sendMessage("Arena started");
					}
				} else
				{
					for (String p : plugin.pdata.getArenaPlayers(thisarena))
					{
						Bukkit.getPlayerExact(p).sendMessage("Arena starts in "+count+" seconds");
					}
					count--;
				}
			}
		};
		if (runtaskid == null)
		{
			runtaskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, run, 0, 20);
		}
	}
	private Integer arenahandler = null;
	private void runArenaHandler()
	{
		if (arenahandler == null)
		{
			arenahandler = 
			Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
			{
				public void run()
				{
					for (String p : new HashSet<String>(plugin.pdata.getArenaPlayers(thisarena)))
					{
						thisarena.handlePlayer(Bukkit.getPlayerExact(p));
					}
				}
			}, 0, 20);
		}
	}
	
	public void handlePlayer(final Player player)
	{
		//check if player is in arena
		if (!player.getLocation().toVector().isInAABB(p1, p2))
		{
			player.sendMessage("You left the arena");
			removePlayerFromArena(player);
			if (curPlayers != 0)
			{
				votes.remove(player.getName());
				curPlayers--;
			}
		}
		//do not handle game if it is not running
		if (!running) {return;}
		//check for game location
		for (final GameLevel gl : gamelevels.values())
		{
			if (gl.isSandLocation(player.getLocation().add(0,-1,0)))
			{
				if (running)
				{
					gl.destroyBlock(player.getLocation().clone().add(0,-1,0), world);
				}
			}
		}
		//check for loose location
		if (loselevel.isLooseLocation(player.getLocation()))
		{
			//player lost
			player.sendMessage("You lost the arena");
			removePlayerFromArena(player);
			return;
		}
		//now check for win
		if (plugin.pdata.getArenaPlayers(this).size() == 1)
		{
			//last player won
			player.sendMessage("You won the arena");
			removePlayerFromArena(player);
			rewardPlayer(player);
			//regenerate arena
			for (final GameLevel gl : gamelevels.values())
			{
				gl.regen(world);
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

	
	//config handlers
	public void saveToConfig()
	{
		FileConfiguration config = new YamlConfiguration();
		config.set("world", world.getName());
		config.set("p1", p1);
		config.set("p2", p2);
		config.set("spawnpoint", spawnpoint.toVector());
		for (String glname : gamelevels.keySet())
		{
			try 
			{
				GameLevel gl = gamelevels.get(glname);
				gl.saveToConfig("gamelevels."+glname, config);
			} catch (Exception e) {}
		}
		loselevel.saveToConfig(config);
		config.set("maxPlayers", maxPlayers);
		config.set("minPlayers", minPlayers);
		config.set("votePercent", votesPercent);
		try {
			config.save(new File("plugins/TNTRun/arenas/"+arenaname+".yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void loadFromConfig()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/TNTRun/arenas/"+arenaname+".yml"));
		this.world = Bukkit.getWorld(config.getString("world", null));
		this.p1 = config.getVector("p1", null);
		this.p2 = config.getVector("p2", null);
		Vector v = config.getVector("spawnpoint", null);
		maxPlayers = config.getInt("maxPlayers",maxPlayers);
		minPlayers = config.getInt("minPlayers",minPlayers);
		votesPercent = config.getDouble("votePercent", votesPercent);
		try {
			this.spawnpoint = new Location(world, v.getX(), v.getY(), v.getZ());
		} catch (Exception e) {}
		this.maxPlayers = config.getInt("maxPlayers");
		ConfigurationSection cs = config.getConfigurationSection("gamelevels");
		if (cs != null)
		{
			for (String key : cs.getKeys(false))
			{
				try{
					GameLevel gl = new GameLevel();
					gl.loadFromConfig("gamelevels."+key, config);
					gamelevels.put(key,gl);
				} catch (Exception e) {}
			}
		}
		loselevel.loadFromConfig(config);
		plugin.pdata.putArenaInHashMap(this);
		if (isArenaConfigured())
		{
			for (GameLevel gl : gamelevels.values())
			{
				gl.regen(world);
			}
			loselevel.regen(world);
			this.enabled = true;
		}
	}
	
}
