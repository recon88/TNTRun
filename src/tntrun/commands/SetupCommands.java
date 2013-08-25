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

package tntrun.commands;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;

public class SetupCommands implements CommandExecutor {

	private TNTRun plugin;
	public SetupCommands(TNTRun plugin)
	{
		this.plugin = plugin;
	}
	
	private HashMap<String, Location> loc1 = new HashMap<String, Location>();
	private HashMap<String, Location> loc2 = new HashMap<String, Location>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if (!(sender instanceof Player)) 
		{
			sender.sendMessage("Player is expected");
			return true;
		}
		Player player = (Player) sender;
		//check permissions
		if (!player.hasPermission("tntrun.setup")) 
		{
			Messages.sendMessage(player, Messages.nopermission);
			return true;
		}
		//handle commands
		//locations
		if (args.length == 1 && args[0].equalsIgnoreCase("setp1"))
		{
			loc1.put(sender.getName(), player.getTargetBlock(null, 30).getLocation());
			sender.sendMessage("p1 saved");
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("setp2"))
		{
			loc2.put(sender.getName(), player.getTargetBlock(null, 30).getLocation());
			sender.sendMessage("p2 saved");
			return true;
		}
		//create arena
		else if (args.length == 2 && args[1].equalsIgnoreCase("create"))
		{
			Arena arenac = getArenaByName(args[0]);
			if (arenac != null)
			{
				sender.sendMessage("Arena already exists");
				return true;
			}
			Arena arena = new Arena(args[0], plugin);
			plugin.pdata.putArenaInHashMap(arena);
			sender.sendMessage("Arena created");
			return true;
		}
		//delete arena
		else if (args.length == 2 && args[1].equalsIgnoreCase("delete"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena == null)
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
			if (arena.isArenaEnabled())
			{
				sender.sendMessage("Disable arena first");
				return true;
			}
			plugin.pdata.removeArenaFromHashMap(arena);
			new File("plugins/TNTRun/arenas/"+arena.getArenaName()+".yml").delete();
			sender.sendMessage("Arena deleted");
		}
		//set arena bounds
		else if (args.length == 2 && args[1].equalsIgnoreCase("setarena"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				try {
					Location[] locs = sortLoc(player);
					arena.setArenaPoints(locs[0],locs[1]);
					sender.sendMessage("Arena bounds set");
					return true;
				}
				catch (Exception e) {
					sender.sendMessage("Locations are wrong or not defined");
					return true;
				}
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//set game level
		else if (args.length == 3 && args[1].equalsIgnoreCase("setgamelevel"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				try {
					Location[] locs = sortLoc(player);
					if (isOneBlockHigh(locs))
					{
						if (arena.setGameLevel(args[2], locs[0], locs[1]))
						{
							sender.sendMessage("GameLevel set");
						} else
						{
							sender.sendMessage("GameLevel should be in arena bounds");
						}
						return true;
					} else 
					{
						sender.sendMessage("Bounds should be 1 block high");
						return true;
					}
				}
				catch (Exception e) {
					sender.sendMessage("Locations are wrong or not defined");
					return true;
				}
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//set looselevel
		else if (args.length == 2 && args[1].equalsIgnoreCase("setloselevel"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				try {
					Location[] locs = sortLoc(player);
					if (isOneBlockHigh(locs))
					{
						if (arena.setLooseLevel(locs[0], locs[1]))
						{
							sender.sendMessage("LoseLevel set");
						} else
						{
							sender.sendMessage("LoseLevel should be in arena bounds");
						}
						return true;
					} else 
					{
						sender.sendMessage("Bounds should be 1 block high");
						return true;
					}
				}
				catch (Exception e) {
					sender.sendMessage("Locations are wrong or not defined");
					return true;
				}
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//set spawnpoint
		else if (args.length == 2 && args[1].equalsIgnoreCase("setspawn"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				if (arena.setSpawnPoint(player.getLocation()))
				{
					sender.sendMessage("Spawnpoint set");
				} else 
				{
					sender.sendMessage("Spawnpoint should be in arena bounds");
				}
				return true;
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//set maxPlayers
		else if (args.length == 3 && args[1].equalsIgnoreCase("setmaxplayers"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				arena.setMaxPlayers(Integer.valueOf(args[2]));
				sender.sendMessage("Max Players set");
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//set min players
		else if (args.length == 3 && args[1].equalsIgnoreCase("setminplayers"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				arena.setMinPlayers(Integer.valueOf(args[2]));
				sender.sendMessage("Min Players set");
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//set vote percent
		else if (args.length == 3 && args[1].equalsIgnoreCase("setvotepercent"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				arena.setVotePercent(Double.valueOf(args[2]));
				sender.sendMessage("Vote percent set");
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//set rewards
		else if (args.length == 2 && args[1].equalsIgnoreCase("setrewards"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				arena.setRewards(player.getInventory().getContents());
				sender.sendMessage("Rewards set");
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//set time limit
		else if (args.length == 3 && args[1].equalsIgnoreCase("settimelimit"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				arena.setTimeLimit(Integer.valueOf(args[2]));
				sender.sendMessage("Time limit set");
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//finish arena creation
		else if (args.length == 2 && args[1].equalsIgnoreCase("finish"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				if (!arena.isArenaEnabled())
				{
					if (arena.isArenaConfigured().equalsIgnoreCase("yes"))
					{
						arena.saveToConfig();
						plugin.pdata.putArenaInHashMap(arena);
						arena.enableArena();
						sender.sendMessage("Arena saved and enabled");
						return true;
					} else 
					{
						sender.sendMessage("Arena is not configured. Reason: "+arena.isArenaConfigured());
						return true;
					}
				} else
				{
					sender.sendMessage("Disable arena first");
				}
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//disable arena
		else if (args.length == 2 && args[1].equalsIgnoreCase("disable"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				arena.disableArena();
				sender.sendMessage("Arena disabled");
				return true;
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//enable arena
		else if (args.length == 2 && args[1].equalsIgnoreCase("enable"))
		{
			Arena arena = getArenaByName(args[0]);
			if (arena != null)
			{
				if (arena.isArenaEnabled())
				{
					sender.sendMessage("Arena already enabled.");
					return true;
				} else
				{
					if (arena.enableArena())
					{
						sender.sendMessage("Arena enabled");
					} else
					{
						sender.sendMessage("Arena is not configured. Reason: "+arena.isArenaConfigured());
					}
					return true;
				}
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//reload messages
		else if (args.length == 1 && args[0].equalsIgnoreCase("reloadmsg"))
		{
			Messages.loadMessages();
			sender.sendMessage("Messages reloaded");
			return true;
		}
		return false;
	}

	
	//0 is min, 1 is max
	private Location[] sortLoc(Player player)
	{
		Double xmin = loc1.get(player.getName()).getX();
		Double xmax = loc2.get(player.getName()).getX();
		if (xmin > xmax) 
		{
			Double temp = xmax;
			xmax = xmin;
			xmin = temp;
		}
		Double ymin = loc1.get(player.getName()).getY();
		Double ymax = loc2.get(player.getName()).getY();
		if (ymin > ymax) 
		{
			Double temp = ymax;
			ymax = ymin;
			ymin = temp;
		}
		Double zmin = loc1.get(player.getName()).getZ();
		Double zmax = loc2.get(player.getName()).getZ();
		if (zmin > zmax) 
		{
			Double temp = zmax;
			zmax = zmin;
			zmin = temp;
		}
		
		
		Location[] locs = new Location[2];
		locs[0] = new Location(loc1.get(player.getName()).getWorld(),xmin,ymin,zmin);
		locs[1] = new Location(loc1.get(player.getName()).getWorld(),xmax,ymax,zmax);
		locs[0].distanceSquared(locs[1]);
		return locs;
		
	}
	
	
	private boolean isOneBlockHigh(Location[] locs)
	{
		int y1 = locs[0].getBlockY();
		int y2 = locs[1].getBlockY();
		if (y1==y2) {return true;}
		return false;
	}
	
	
	private Arena getArenaByName(String name)
	{
		for (Arena arena : plugin.pdata.getArenas())
		{
			if (arena.getArenaName().equals(name))
			{
				return arena;
			}
		}
		return null;
	}
	
}
