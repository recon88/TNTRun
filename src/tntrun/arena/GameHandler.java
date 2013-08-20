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

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import tntrun.TNTRun;

public class GameHandler {

	private TNTRun plugin;
	private Arena arena;
	public GameHandler(TNTRun plugin, Arena arena)
	{
		this.plugin = plugin;
		this.arena = arena;
		runArenaHandler();
	}
	
	
	//arena start handler (running status updater)
	Integer runtaskid = null;
	int count = 10;
	protected void runArena()
	{
		Runnable run = new Runnable()
		{
			public void run()
			{
				//cancel countdown if not enough players
				if (plugin.pdata.getArenaPlayers(arena).size() < arena.getMinPlayers()) 
				{
					for (String p : plugin.pdata.getArenaPlayers(arena))
					{
						Bukkit.getPlayerExact(p).sendMessage("Too much players left the arena, wating for some more");
					}
					Bukkit.getScheduler().cancelTask(runtaskid);
					runtaskid = null;
					count = 10;
					return;
				}
				//now arena start sequence
				if (count == 0)
				{
					timelimit = arena.getTimeLimit()*20; //timelimit is in ticks
					arena.running = true;
					count = 10;
					Bukkit.getScheduler().cancelTask(runtaskid);
					runtaskid = null;
					for (String p : plugin.pdata.getArenaPlayers(arena))
					{
						Bukkit.getPlayerExact(p).sendMessage("Arena started. Time limit is "+arena.getTimeLimit()+" seconds");
					}
				} else
				{
					for (String p : plugin.pdata.getArenaPlayers(arena))
					{
						Bukkit.getPlayerExact(p).sendMessage("Arena starts in "+count+" seconds");
					}
					count--;
				}
			}
		};
		//schedule arena run task only if this task is not running
		if (runtaskid == null)
		{
			runtaskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, run, 0, 20);
		}
	}
	
	//main arena handler (start on arena enable)
	private int timelimit;
	private int arenahandler;
	private void runArenaHandler()
	{
		arenahandler = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				try{
					//check arena time limit
					if (checkDraw())
					{
						for (String p : new HashSet<String>(plugin.pdata.getArenaPlayers(arena)))
						{
							//kick all players
							arena.arenaph.leavePlayer(Bukkit.getPlayerExact(p), "Time is out", "");
							//not running
							arena.running = false;
							//regenerate arena
							arena.regenGameLevels();
						}
					} else
					{
						//decrease timelimit
						timelimit-=2;
						//handle players
						for (String p : new HashSet<String>(plugin.pdata.getArenaPlayers(arena)))
						{
							handlePlayer(Bukkit.getPlayerExact(p));
						}
					}
				} catch (Exception e) {
					//if we caught and exception it means that arena is deleted, so we must stop arena handler
					Bukkit.getScheduler().cancelTask(arenahandler);
				}
			}
		}, 0, 2);
	}
	//check for time is out
	private boolean checkDraw()
	{
		if (arena.running && timelimit < 0)
		{
			//it's a draw
			return true;
		}
		return false;
	}
	//player handlers
	public void handlePlayer(final Player player)
	{
		//check if player is in arena
		if (!player.getLocation().toVector().isInAABB(arena.getP1(), arena.getP2()))
		{
			arena.arenaph.leavePlayer(player, "You left the arena", "Player "+player.getName()+" left the arena");
			return;
		}
		//do not handle game if it is not running
		if (!arena.running) {return;}
		//check for game location
		for (final GameLevel gl : arena.getGameLevels())
		{
			if (gl.isSandLocation(player.getLocation().add(0,-1,0)))
			{
				gl.destroyBlock(player.getLocation().clone().add(0,-1,0));
			}
		}
		//check for loose location
		if (plugin.pdata.getArenaPlayers(arena).size() > 1 && arena.getLoseLevel().isLooseLocation(player.getLocation()))
		{
			//player lost
			arena.arenaph.leavePlayer(player, "You lost the arena", "Player "+player.getName()+" lost the arena");
			return;
		}
		//now check for win
		if (plugin.pdata.getArenaPlayers(arena).size() == 1)
		{
			//last player won
			arena.arenaph.leavePlayer(player, "You won the arena", "");
			broadcastWin(player);
			rewardPlayer(player);
			//not running
			arena.running = false;
			//regenerate arena
			arena.regenGameLevels();
		}
	}
	private void broadcastWin(Player player)
	{
		Bukkit.broadcastMessage(ChatColor.BLUE+"[TNTRun] "+ChatColor.GREEN+player.getName()+ChatColor.WHITE+" won the game on arena "+ChatColor.RED+arena.getArenaName()+ChatColor.WHITE);
	}
	private void rewardPlayer(Player player)
	{
		arena.getRewards().rewardPlayer(player);
	}
	
}