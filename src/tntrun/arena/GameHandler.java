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
import org.bukkit.Location;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.messages.Messages;

public class GameHandler {

	private TNTRun plugin;
	private Arena arena;
	public GameHandler(TNTRun plugin, Arena arena)
	{
		this.plugin = plugin;
		this.arena = arena;
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
				if (plugin.pdata.getArenaPlayers(arena).size() < 2) 
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
					count = 10;
					Bukkit.getScheduler().cancelTask(runtaskid);
					runtaskid = null;
					for (String p : plugin.pdata.getArenaPlayers(arena))
					{
						Messages.sendMessage(Bukkit.getPlayerExact(p), Messages.arenastarted, arena.getTimeLimit());
					}
					runArenaHandler();
				} else
				//countdown
				{
					for (String p : plugin.pdata.getArenaPlayers(arena))
					{
						Messages.sendMessage(Bukkit.getPlayerExact(p), Messages.arenacountdown, count);
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
	
	//main arena handler
	private int timelimit;
	private int arenahandler;
	private void runArenaHandler()
	{
		arena.setRunning(true);
		timelimit = arena.getTimeLimit()*20; //timelimit is in ticks
		arenahandler = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				if (arena.isArenaEnabled())
				{
					handleArenaTick();
				} else
				{
					Bukkit.getScheduler().cancelTask(arenahandler);
				}
			}
		}, 0, 1);
	}
	private void handleArenaTick()
	{
		if (plugin.pdata.getArenaPlayers(arena).size() != 0)
		{
			//check arena time limit
			if (timelimit < 0)
			{
				for (String p : new HashSet<String>(plugin.pdata.getArenaPlayers(arena)))
				{
					//kick all players
					arena.arenaph.leavePlayer(Bukkit.getPlayerExact(p), Messages.arenatimeout, "");
				}
			} else
			{
				//decrease timelimit
				timelimit--;
				//handle players
				for (String p : new HashSet<String>(plugin.pdata.getArenaPlayers(arena)))
				{
					handlePlayer(Bukkit.getPlayerExact(p));
				}
			}
		} else
		{
			//game ended
			Bukkit.getScheduler().cancelTask(arenahandler);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					arena.setRunning(false);
					arena.regenGameLevels();
				}
			});
		}
	}
	//player handlers
	public void handlePlayer(final Player player)
	{
		Location plloc = player.getLocation();
		//check for game location
		for (final GameLevel gl : arena.getGameLevels())
		{
			//remove block under player feet
			if (gl.isSandLocation(plloc.add(0,-1,0)))
			{
				gl.destroyBlock(plloc.add(0,-1,0), arena.getGameLevelDestroyDelay(), arena);
			}
		}
		//check for win
		if (plugin.pdata.getArenaPlayers(arena).size() == 1)
		{
			//last player won
			arena.arenaph.leaveWinner(player, Messages.playerwontoplayer);
			broadcastWin(player);
			return;
		}
		//check for lose
		if (arena.getLoseLevel().isLooseLocation(plloc))
		{
			//player lost
			arena.arenaph.leavePlayer(player, Messages.playerlosttoplayer, Messages.playerlosttoothers);
			return;
		}
	}
	private void broadcastWin(Player player)
	{
		Messages.broadsactMessage(player.getName(), arena.getArenaName(), Messages.playerwonbroadcast);
	}
	
}
