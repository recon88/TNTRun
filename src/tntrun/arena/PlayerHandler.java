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

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.bars.Bars;
import tntrun.messages.Messages;
import tntrun.signs.SignMode;

public class PlayerHandler {

	private TNTRun plugin;
	private Arena arena;
	public PlayerHandler(TNTRun plugin, Arena arena)
	{
		this.plugin = plugin;
		this.arena = arena;
	}

	private HashMap<String,Integer> leavechecker = new HashMap<String,Integer>();
	
	
	//spawn player on arena
	public void spawnPlayer(final Player player, String msgtoplayer, String msgtoarenaplayers)
	{
		if (plugin.pdata.getArenaPlayers(arena).size() == arena.getMaxPlayers())
		{
			Messages.sendMessage(player, Messages.limitreached);
			return;
		}
		//change player status
		plugin.pdata.storePlayerGameMode(player.getName());
		player.setFlying(false);
		player.setAllowFlight(false);
		plugin.pdata.storePlayerInventory(player.getName());
		plugin.pdata.storePlayerArmor(player.getName());
		plugin.pdata.storePlayerPotionEffects(player.getName());
		plugin.pdata.storePlayerHunger(player.getName());
		//teleport player to arena
		plugin.pdata.storePlayerLocation(player.getName());
		player.teleport(arena.getSpawnPoint());
		//send message to player
		Messages.sendMessage(player, msgtoplayer);
		//send message to other players and update bar
		for (String pname : plugin.pdata.getArenaPlayers(arena))
		{
			Messages.sendMessage(Bukkit.getPlayerExact(pname), player.getName(), msgtoarenaplayers);
		}
		//set player on arena data
		plugin.pdata.setPlayerArena(player.getName(), arena);
		//send message about arena player count
		Messages.sendMessage(player, Messages.playerscount+plugin.pdata.getArenaPlayers(arena).size());
		//start leave checker
		int taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run()
			{
				if (!arena.isInArenaBounds(player.getLocation()))
				{
					arena.arenaph.leavePlayer(player, Messages.playerlefttoplayer, Messages.playerlefttoothers);
				}
			}
		}, 0, 1);
		leavechecker.put(player.getName(), taskid);
		//modify signs
		plugin.signEditor.modifySigns(arena.getArenaName(), SignMode.ENABLED, plugin.pdata.getArenaPlayers(arena).size(), arena.getMaxPlayers());
		//modify bars
		if (!arena.isArenaStarting())
		{
			HashSet<String> arenaplayers = plugin.pdata.getArenaPlayers(arena);
			for (String pname : arenaplayers)
			{
				Bars.setBar(Bukkit.getPlayerExact(pname), Bars.waiting, arenaplayers.size(), 0, arenaplayers.size()*100/arena.getMinPlayers());
			}
		}
		//check for game start
		if (!arena.isArenaStarting() && plugin.pdata.getArenaPlayers(arena).size() == arena.getMaxPlayers() || plugin.pdata.getArenaPlayers(arena).size() == arena.getMinPlayers())
		{
			arena.arenagh.runArena();
		}
	}

	//remove player from arena
	public void leavePlayer(Player player, String msgtoplayer, String msgtoarenaplayers)
	{
		//remove player from arena and restore his state
		removePlayerFromArenaAndRestoreState(player, false);
		//send message to player
		Messages.sendMessage(player, msgtoplayer);
		//send message to other players and update bars
		HashSet<String> arenaplayers = plugin.pdata.getArenaPlayers(arena);
		for (String pname : arenaplayers)
		{
			Player p = Bukkit.getPlayerExact(pname);
			Messages.sendMessage(p, player.getName(), msgtoarenaplayers);
			if (!arena.isArenaStarting() && !arena.isArenaRunning())
			{
				Bars.setBar(p, Bars.waiting, arenaplayers.size(), 0, arenaplayers.size()*100/arena.getMinPlayers());
			}
		}
	}
	protected void leaveWinner(Player player, String msgtoplayer)
	{
		//remove player from arena and restore his state
		removePlayerFromArenaAndRestoreState(player, true);
		//send message to player
		Messages.sendMessage(player, msgtoplayer);
	}
	private void removePlayerFromArenaAndRestoreState(Player player, boolean winner)
	{
		//remove leave handler
		int taskid = leavechecker.get(player.getName());
		Bukkit.getScheduler().cancelTask(taskid);
		leavechecker.remove(player.getName());
		//remove player on arena data
		plugin.pdata.removePlayerFromArena(player.getName());
		//restore location
		plugin.pdata.restorePlayerLocation(player.getName());
		//restore player status
		plugin.pdata.restorePlayerHunger(player.getName());
		plugin.pdata.restorePlayerPotionEffects(player.getName());
		plugin.pdata.restorePlayerArmor(player.getName());
		plugin.pdata.restorePlayerInventory(player.getName());
		//reward player before restoring gamemode if player is winner
		if (winner)
		{
			arena.getRewards().rewardPlayer(player);
		}
		plugin.pdata.restorePlayerGameMode(player.getName());
		//remove vote
		votes.remove(player.getName());
		//update signs
		SignMode mode;
		if(winner || plugin.pdata.getArenaPlayers(arena).size() == 0) {
			mode = SignMode.ENABLED;
		} else {
			mode = SignMode.GAME_IN_PROGRESS;
		}
		plugin.signEditor.modifySigns(arena.getArenaName(), mode, plugin.pdata.getArenaPlayers(arena).size(), arena.getMaxPlayers());
		//remove bar
		Bars.removeBar(player);
	}
	

	//vote for game start
	private HashSet<String> votes = new HashSet<String>();
	public boolean vote(Player player)
	{
		if (!votes.contains(player.getName()))
		{
			votes.add(player.getName());
			if (plugin.pdata.getArenaPlayers(arena).size() > 1	&& (votes.size() >= plugin.pdata.getArenaPlayers(arena).size()*arena.getVotePercent()))
			{
				arena.arenagh.runArena();
			}	
			return true;
		}
		return false;
	}
	
	
}
