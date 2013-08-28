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
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.messages.Messages;

public class PlayerHandler {

	private TNTRun plugin;
	private Arena arena;
	public PlayerHandler(TNTRun plugin, Arena arena)
	{
		this.plugin = plugin;
		this.arena = arena;
	}
	
	//spawn player on arena
	public void spawnPlayer(Player player, String msgtoplayer, String msgtoarenaplayers)
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
		plugin.pdata.storePlayerHunger(player.getName());
		//teleport player to arena
		plugin.pdata.storePlayerLocation(player.getName());
		player.teleport(arena.getSpawnPoint());
		//send message to player
		Messages.sendMessage(player, msgtoplayer);
		//send message to other players
		for (String p : plugin.pdata.getArenaPlayers(arena))
		{
			Messages.sendMessage(Bukkit.getPlayerExact(p), player.getName(), msgtoarenaplayers);
		}
		//set player on arena data
		plugin.pdata.setPlayerArena(player.getName(), arena);
		//send message about arena player count
		Messages.sendMessage(player, Messages.playerscount+plugin.pdata.getArenaPlayers(arena).size());
		//check for game start
		if (plugin.pdata.getArenaPlayers(arena).size() == arena.getMaxPlayers() || plugin.pdata.getArenaPlayers(arena).size() == arena.getMinPlayers())
		{
			arena.arenagh.runArena();
		}
	}
	//remove player from arena
	public void leavePlayer(Player player, String msgtoplayer, String msgtoarenaplayers)
	{
		//remove player on arena data
		plugin.pdata.removePlayerFromArena(player.getName());
		//restore location
		plugin.pdata.restorePlayerLocation(player.getName());
		//restore player status
		plugin.pdata.restorePlayerInventory(player.getName());
		plugin.pdata.restorePlayerArmor(player.getName());
		plugin.pdata.restorePlayerHunger(player.getName());
		plugin.pdata.restorePlayerGameMode(player.getName());
		//send message to player
		Messages.sendMessage(player, msgtoplayer);
		//send message to other players
		for (String p : plugin.pdata.getArenaPlayers(arena))
		{
			Messages.sendMessage(Bukkit.getPlayerExact(p), player.getName(), msgtoarenaplayers);
		}
		//remove vote
		votes.remove(player.getName());
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
