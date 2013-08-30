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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import tntrun.TNTRun;
import tntrun.messages.Messages;

public class Arena {

	private TNTRun plugin;
	public GameHandler arenagh;
	public PlayerHandler arenaph;
	public Arena(String name, TNTRun plugin)
	{
		this.arenaname = name;
		this.plugin = plugin;
		arenagh = new GameHandler(plugin,this);
		arenaph = new PlayerHandler(plugin,this);
		arenafile = new File("plugins/TNTRun/arenas/"+arenaname+".yml");
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
	private int gameleveldestroydelay = 2;
	protected int getGameLevelDestroyDelay()
	{
		return gameleveldestroydelay;
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
	
	private int maxPlayers = 6;
	protected int getMaxPlayers()
	{
		return maxPlayers;
	}
	private int minPlayers = 2;
	protected int getMinPlayers()
	{
		return minPlayers;
	}
	private double votesPercent = 0.75;
	protected double getVotePercent()
	{
		return votesPercent;
	}
	private int timelimit = 180;
	protected int getTimeLimit()
	{
		return timelimit;
	}	
	private Rewards rewards = new Rewards();
	protected Rewards getRewards()
	{
		return rewards;
	}

	
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
		running = false;
		//drop players
		for (String player : plugin.pdata.getArenaPlayers(this))
		{
			arenaph.leavePlayer(Bukkit.getPlayerExact(player), Messages.arenadisabling,"");
		}
	}

	//arena structure handler
	//main
	public boolean isInArenaBounds(Location loc)
	{
		if (loc.toVector().isInAABB(p1, p2)) {return true;}
		return false;
	}
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
	public boolean setGameLevel(String glname, Location loc1, Location loc2)
	{
		if (isInArenaBounds(loc1) && isInArenaBounds(loc2))
		{
			GameLevel gl = gamelevels.get(glname);
			if (gl == null)
			{
				gl = new GameLevel();
				gamelevels.put(glname, gl);
			}
			gl.setGameLocation(loc1, loc2, world);
			return true;
		}
		return false;
	}
	public void setGameLevelDestroyDelay(int delay)
	{
		gameleveldestroydelay = delay;
	}
	public void regenGameLevels()
	{
		for (final GameLevel gl : gamelevels.values())
		{
			gl.regen(world);
		}
	}
	public boolean setLooseLevel(Location loc1, Location loc2)
	{
		if (isInArenaBounds(loc1) && isInArenaBounds(loc2))
		{
			loselevel.setLooseLocation(loc1, loc2, world);			
			return true;
		} 
		return false;
	}
	public boolean setSpawnPoint(Location loc)
	{
		if (isInArenaBounds(loc))
		{
			spawnpoint = loc;
			return true;
		}
		return false;
	}
	//additional
	public void setMaxPlayers(int maxplayers)
	{
		this.maxPlayers = maxplayers;
	}
	public void setMinPlayers(int minplayers)
	{
		this.minPlayers = minplayers;
	}
	public void setVotePercent(double votepercent)
	{
		this.votesPercent = votepercent;
	}
	public void setTimeLimit(int timelimit)
	{
		this.timelimit = timelimit;
	}
	public void setRewards(ItemStack[] rewards)
	{
		this.rewards.setRewards(rewards);
	}
	
	
	//arena config handlers
	private File arenafile;
	public void saveToConfig()
	{
		FileConfiguration config = new YamlConfiguration();
		//save arena bounds
		try {
			config.set("world", world.getName());
			config.set("p1", p1);
			config.set("p2", p2);
		} catch (Exception e) {}
		//save gamelevels
		for (String glname : gamelevels.keySet())
		{
			try {
				GameLevel gl = gamelevels.get(glname);
				gl.saveToConfig("gamelevels."+glname, config);
			} catch (Exception e) {}
		}
		//save gamelevel destroy delay
		config.set("gameleveldestroydelay",gameleveldestroydelay);
		//save looselevel
		try {
			loselevel.saveToConfig(config);
		} catch (Exception e) {}
		//save spawnpoint
		try {
			config.set("spawnpoint", spawnpoint.toVector());
		} catch (Exception e) {}
		//save maxplayers
		config.set("maxPlayers", maxPlayers);
		//save minplayers
		config.set("minPlayers", minPlayers);
		//save vote percent
		config.set("votePercent", votesPercent);
		//save timelimit
		config.set("timelimit", timelimit);
		//save rewards
		rewards.saveToConfig(config);
		try {
			config.save(arenafile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void loadFromConfig()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(arenafile);
		//load arena bounds
		try {
			world = Bukkit.getWorld(config.getString("world", null));
			p1 = config.getVector("p1", null);
			p2 = config.getVector("p2", null);
		} catch (Exception e) {}
		//load gamelevels
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
		//load gamelevel destroy delay
		gameleveldestroydelay = config.getInt("gameleveldestroydelay",gameleveldestroydelay);
		//load looselevel
		loselevel.loadFromConfig(config);
		//load spawnpoint
		try {
			Vector v = config.getVector("spawnpoint", null);
			spawnpoint = new Location(world, v.getX(), v.getY(), v.getZ());
		} catch (Exception e) {}
		//load maxplayers
		maxPlayers = config.getInt("maxPlayers",maxPlayers);
		//load minplayers
		minPlayers = config.getInt("minPlayers",minPlayers);
		//load vote percent
		votesPercent = config.getDouble("votePercent", votesPercent);
		//load timelimit
		timelimit = config.getInt("timelimit",timelimit);
		//load rewards
		rewards.loadFromConfig(config);
		//register arena
		plugin.pdata.putArenaInHashMap(this);
		//enable if fully configured
		if (isArenaConfigured().equalsIgnoreCase("yes"))
		{
			for (GameLevel gl : gamelevels.values())
			{
				gl.regen(world);
			}
			loselevel.regen(world);
			enabled = true;
		}
	}
	
}
