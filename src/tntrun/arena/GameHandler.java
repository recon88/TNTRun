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
import tntrun.bars.Bars;
import tntrun.messages.Messages;
import tntrun.signs.SignMode;

public class GameHandler {

	private TNTRun plugin;
	private Arena arena;
	public GameHandler(TNTRun plugin, Arena arena)
	{
		this.plugin = plugin;
		this.arena = arena;
		count = arena.getCountdown();
	}
	
	//arena leave handler
	private int leavetaskid;
	public void startArenaAntiLeaveHandler()
	{
		leavetaskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				if (arena.isArenaEnabled())
				{
					for (Player player : Bukkit.getOnlinePlayers())
					{
						if (plugin.pdata.getArenaPlayers(arena).contains(player.getName()))
						{
							if (!arena.isInArenaBounds(player.getLocation()))
							{
								arena.arenaph.leavePlayer(player, Messages.playerlefttoplayer, Messages.playerlefttoothers);
							}
						}
					}
				} else
				{
					Bukkit.getScheduler().cancelTask(leavetaskid);
				}
			}
		},0,1);
	}
	
	
	//arena start handler (running status updater)
	int runtaskid;
	int count;
	protected void runArenaCountdown()
	{
		arena.setStarting(true);
		runtaskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() 
		{
			public void run()
			{
				//check if countdown should be stopped for some various reasons
				HashSet<String> arenaplayers = plugin.pdata.getArenaPlayers(arena);
				if (!arena.isArenaEnabled())
				{
					stopArenaCountdown();
				} else
				if (arenaplayers.size() < arena.getMinPlayers())
				{
					for (Player player : Bukkit.getOnlinePlayers())
					{
						if (arenaplayers.contains(player.getName()))
						{
							Bars.setBar(player, Bars.waiting, arenaplayers.size(), 0, arenaplayers.size()*100/arena.getMinPlayers());
							plugin.signEditor.modifySigns(arena.getArenaName(), SignMode.ENABLED, arenaplayers.size(), arena.getMaxPlayers());
						}
					}
					stopArenaCountdown();
				} else
				//start arena if countdown is 0
				if (count == 0)
				{
					stopArenaCountdown();
					startArena();
				} else
				//countdown
				{
					for (Player player : Bukkit.getOnlinePlayers())
					{
						if (arenaplayers.contains(player.getName()))
						{
							Messages.sendMessage(player, Messages.arenacountdown, count);
							Bars.setBar(player, Bars.starting, 0, count, count*100/arena.getCountdown());
						}
					}
					count--;
				}
			}
		}, 0, 20);
	}
	private void stopArenaCountdown()
	{
		arena.setStarting(false);
		count = arena.getCountdown();
		Bukkit.getScheduler().cancelTask(runtaskid);
	}
	
	//main arena handler
	private int timelimit;
	private int arenahandler;
	private void startArena()
	{
		arena.setRunning(true);
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (plugin.pdata.getArenaPlayers(arena).contains(player.getName()))
			{
				Messages.sendMessage(player, Messages.arenastarted, arena.getTimeLimit());
			}
		}
		plugin.signEditor.modifySigns(arena.getArenaName(), SignMode.GAME_IN_PROGRESS);
		timelimit = arena.getTimeLimit()*20; //timelimit is in ticks
		arenahandler = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				if (plugin.pdata.getArenaPlayers(arena).size() > 0)
				{
					handleArenaTick();
				} else
				{
					Bukkit.getScheduler().cancelTask(arenahandler);
					if (arena.isArenaEnabled())
					{
						startArenaRegen();
					}
				}
			}
		}, 0, 1);
	}
	private void handleArenaTick()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (plugin.pdata.getArenaPlayers(arena).contains(player.getName()))
			{
				if (timelimit < 0)
				{
					//kick player
					arena.arenaph.leavePlayer(player, Messages.arenatimeout, "");
				} else
				{
					//update bar
					Bars.setBar(player, Bars.playing, plugin.pdata.getArenaPlayers(arena).size(), timelimit/20, timelimit*5/arena.getTimeLimit());
					//handle player
					handlePlayer(player);
				}
			}
		}
		//decrease timelimit
		timelimit--;
	}

	//player handlers
	public void handlePlayer(final Player player)
	{
		Location plloc = player.getLocation();
		Location plufloc = plloc.clone().add(0,-1,0);
		//check for game location
		for (final GameLevel gl : arena.getGameLevels())
		{
			//remove block under player feet
			if (gl.isSandLocation(plufloc))
			{
				gl.destroyBlock(plufloc, arena);
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
	
	
	private void startArenaRegen()
	{
		//set arena is regenerating status
		arena.setRegenerating(true);
		//start arena regen
		Thread regen = new Thread()
		{
			public void run()
			{
				try 
				{
					//regen
					for (final GameLevel gl : arena.getGameLevels())
					{
						int regentask =Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
						{
							public void run()
							{
								gl.regen(arena.getWorld());
							}
						});
						while (Bukkit.getScheduler().isCurrentlyRunning(regentask) || Bukkit.getScheduler().isQueued(regentask))
						{
							Thread.sleep(10);
						}
						Thread.sleep(100);
					}
					Thread.sleep(100);
					//update arena status
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
					{
						public void run()
						{
							arena.setRegenerating(false);
							arena.setRunning(false);
						}
					});
				} catch (Exception e) {}
			}
		};
		regen.start();
	}
	
}
