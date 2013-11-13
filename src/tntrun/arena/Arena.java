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
import java.util.HashSet;
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
import tntrun.signs.SignMode;

public class Arena {

	protected TNTRun plugin;
	public GameHandler arenagh;
	public PlayerHandler arenaph;
	public Arena(String name, TNTRun plugin)
	{
		this.arenaname = name;
		this.plugin = plugin;
		arenagh = new GameHandler(plugin,this);
		arenaph = new PlayerHandler(plugin,this);
		arenafile = new File(plugin.getDataFolder()+File.separator+"arenas"+File.separator+arenaname+".yml");
		plugin.pdata.putArenaInHashMap(this);
	}
	
	private boolean enabled = false;
	private boolean starting = false;
	private boolean running = false;
	private boolean regenerating = false;
	
	private String arenaname;
	public String getArenaName()
	{
		return arenaname;
	}
	
	private String world;
	public World getWorld()
	{
		return Bukkit.getWorld(world);
	}
	private Vector p1 = null;
	public Vector getP1()
	{
		return p1;
	}
	private Vector p2 = null;
	public Vector getP2()
	{
		return p2;
	}
	private HashSet<GameLevel> gamelevels = new HashSet<GameLevel>();
	public HashSet<GameLevel> getGameLevels()
	{
		return gamelevels;
	}
	private int gameleveldestroydelay = 2;
	public int getGameLevelDestroyDelay()
	{
		return gameleveldestroydelay;
	}
	private LoseLevel loselevel = new LoseLevel();
	public LoseLevel getLoseLevel()
	{
		return loselevel;
	}
	private Location spawnpoint = null;
	public Location getSpawnPoint()
	{
		return spawnpoint;
	}
	
	private int maxPlayers = 6;
	public int getMaxPlayers()
	{
		return maxPlayers;
	}
	private int minPlayers = 2;
	public int getMinPlayers()
	{
		return minPlayers;
	}
	private double votesPercent = 0.75;
	public double getVotePercent()
	{
		return votesPercent;
	}
	private int timelimit = 180;
	public int getTimeLimit()
	{
		return timelimit;
	}	
	private int countdown = 10;
	public int getCountdown()
	{
		return countdown;
	}
	private Rewards rewards = new Rewards();
	public Rewards getRewards()
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
			plugin.signEditor.modifySigns(getArenaName(), SignMode.ENABLED, plugin.pdata.getArenaPlayers(this).size(), getMaxPlayers());
			arenagh.startArenaAntiLeaveHandler();
			enabled = true;
			return true;
		}
		return false;
	}
	public void disableArena()
	{
		enabled = false;
		running = false;
		//drop players
		for (String player : new HashSet<String>(plugin.pdata.getArenaPlayers(this)))
		{
			arenaph.leavePlayer(Bukkit.getPlayerExact(player), Messages.arenadisabling,"");
		}
		//regen gamelevels
		for (GameLevel gl : gamelevels)
		{
			gl.regen(getWorld());
		}
		plugin.signEditor.modifySigns(getArenaName(), SignMode.DISABLED);
	}
	public boolean isArenaStarting()
	{
		return starting;
	}
	protected void setStarting(boolean starting)
	{
		this.starting = starting;
	}
	public boolean isArenaRunning()
	{
		return running;
	}
	protected void setRunning(boolean running)
	{
		this.running = running;
	}
	public boolean isArenaRegenerating()
	{
		return regenerating;
	}
	protected void setRegenerating(boolean regenerating)
	{
		this.regenerating = regenerating;
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
		this.world = loc1.getWorld().getName();
		this.p1 = loc1.toVector();
		this.p2 = loc2.toVector();
	}
	public boolean setGameLevel(String glname, Location loc1, Location loc2)
	{
		if (isInArenaBounds(loc1) && isInArenaBounds(loc2))
		{
			GameLevel gl = getGameLevelByName(glname);
			if (gl == null)
			{
				gl = new GameLevel(glname);
				gamelevels.add(gl);
			}
			gl.setGameLocation(loc1, loc2, Bukkit.getWorld(world));
			return true;
		}
		return false;
	}
	private GameLevel getGameLevelByName(String name)
	{
		for (GameLevel gl : gamelevels)
		{
			if (gl.getGameLevelName().equals(name)) 
			{
				return gl;
			}
		}
		return null;
	}
	public void setGameLevelDestroyDelay(int delay)
	{
		gameleveldestroydelay = delay;
	}
	public boolean setLooseLevel(Location loc1, Location loc2)
	{
		if (isInArenaBounds(loc1) && isInArenaBounds(loc2))
		{
			loselevel.setLooseLocation(loc1, loc2, Bukkit.getWorld(world));			
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
	public void setCountdown(int countdown)
	{
		this.countdown = countdown;
	}
	public void setRewards(ItemStack[] rewards)
	{
		this.rewards.setRewards(rewards);
	}
	public void setRewards(int money)
	{
		this.rewards.setRewards(money);
	}
	
	
	//arena config handlers
	private File arenafile;
	public void saveToConfig()
	{
		FileConfiguration config = new YamlConfiguration();
		//save arena bounds
		try {
			config.set("world", world);
			config.set("p1", p1);
			config.set("p2", p2);
		} catch (Exception e) {}
		//save gamelevels
		for (GameLevel gl : gamelevels)
		{
			try {
				gl.saveToConfig(config);
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
		//save countdown
		config.set("countdown", countdown);
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
		//load arena world location
		world = config.getString("world", null);
		//stop arena loading if world is not loaded
		if (Bukkit.getWorld(world) == null)
		{
			plugin.logSevere("World "+world+" is not loaded. Stopping arena "+arenaname+" loading");
		}
		//load arena bounds
		p1 = config.getVector("p1", null);
		p2 = config.getVector("p2", null);
		//load gamelevels
		ConfigurationSection cs = config.getConfigurationSection("gamelevels");
		if (cs != null)
		{
			for (String glname : cs.getKeys(false))
			{
				try{
					GameLevel gl = new GameLevel(glname);
					gl.loadFromConfig(config);
					gamelevels.add(gl);
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
			spawnpoint = new Location(Bukkit.getWorld(world), v.getX(), v.getY(), v.getZ());
		} catch (Exception e) {}
		//load maxplayers
		maxPlayers = config.getInt("maxPlayers",maxPlayers);
		//load minplayers
		minPlayers = config.getInt("minPlayers",minPlayers);
		//load vote percent
		votesPercent = config.getDouble("votePercent", votesPercent);
		//load timelimit
		timelimit = config.getInt("timelimit",timelimit);
		//load countdown
		countdown = config.getInt("countdown", countdown);
		//load rewards
		rewards.loadFromConfig(config);
		//enable if fully configured
		enableArena();
	}
	
}
