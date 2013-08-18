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
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import tntrun.TNTRun;

public class Arena {

	private TNTRun plugin;
	public GameHandler arenagh;
	public Arena(String name, TNTRun plugin)
	{
		this.arenaname = name;
		this.plugin = plugin;
		arenagh = new GameHandler(plugin,this);
	}
	
	private boolean enabled = false;
	public boolean running = false;
	
	private String arenaname;
	public String getArenaName()
	{
		return arenaname;
	}
	
	private World world;
	protected World getWorld()
	{
		return world;
	}
	private Vector p1 = null;
	protected Vector getP1()
	{
		return p1;
	}
	private Vector p2 = null;
	protected Vector getP2()
	{
		return p2;
	}
	private HashMap<String, GameLevel> gamelevels = new HashMap<String, GameLevel>();
	protected Collection<GameLevel> getGameLevels()
	{
		return gamelevels.values();
	}
	private LoseLevel loselevel = new LoseLevel();
	protected LoseLevel getLoseLevel()
	{
		return loselevel;
	}
	private Location spawnpoint = null;
	protected Location getSpawnPoint()
	{
		return spawnpoint;
	}
	
	public int maxPlayers = 6;
	public int minPlayers = 2;
	public double votesPercent = 0.75;

	
	//arena status handler
	public boolean isArenaEnabled()
	{
		return enabled;
	}
	public boolean enableArena()
	{
		if (isArenaConfigured().equalsIgnoreCase("yes"))
		{
			enabled = true;
			regenGameLevels();
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
			arenagh.leavePlayer(Bukkit.getPlayerExact(player), "Arena is disabling","");
		}
		running = false;
	}

	//arena structure handler
	public String isArenaConfigured()
	{
		if (p1 == null || p2==null || world == null) {return "Arena bounds not set";}
		if (gamelevels.size() == 0) {return "Arena gamelevels not set";}
		if (!loselevel.isConfigured()) {return "Arena looselevel not set";}
		if (spawnpoint == null) {return "Arena spawnpoint not set";}
		return "yes";
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
	public void regenGameLevels()
	{
		for (final GameLevel gl : gamelevels.values())
		{
			gl.regen(world);
		}
	}
	public void setLooseLevel(Location loc1, Location loc2)
	{
		loselevel.setLooseLocation(loc1, loc2, world);
	}
	public void setSpawnPoint(Location loc)
	{
		spawnpoint = loc;
	}
	
	
	//arena config handlers
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
		if (isArenaConfigured().equalsIgnoreCase("yes"))
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
