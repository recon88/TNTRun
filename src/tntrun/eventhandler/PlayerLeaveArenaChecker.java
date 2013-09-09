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

package tntrun.eventhandler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;

public class PlayerLeaveArenaChecker implements Listener {

	private TNTRun plugin;
	public PlayerLeaveArenaChecker(TNTRun plugin)
	{
		this.plugin = plugin;
	}
	
	//remove player from arena if he walked to location outside the arena bounds
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	public void onPlayerMoveEvent(PlayerMoveEvent e)
	{
		Player player = e.getPlayer();
		Arena arena = plugin.pdata.getPlayerArena(player.getName());
		//ignore player not in arena
		if (arena == null) {return;}
		if (!arena.isInArenaBounds(player.getLocation()))
		{
			arena.arenaph.leavePlayer(player, Messages.playerlefttoplayer, Messages.playerlefttoothers);
		}
	}
	
	//remove player from arena if he teleported to location outside the arena bounds
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent e)
	{
		Player player = e.getPlayer();
		Arena arena = plugin.pdata.getPlayerArena(player.getName());
		//ignore player not in arena
		if (arena == null) {return;}
		if (!arena.isInArenaBounds(player.getLocation()))
		{
			arena.arenaph.leavePlayer(player, Messages.playerlefttoplayer, Messages.playerlefttoothers);
		}
	}
	
	//remove player from arena on quit
	@EventHandler(priority=EventPriority.LOWEST,ignoreCancelled = true)
	public void onPlayerQuitEvent(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		Arena arena = plugin.pdata.getPlayerArena(player.getName());
		//ignore if player is not in arena
		if (arena == null) {return;}
		arena.arenaph.leavePlayer(player, "", Messages.playerlefttoothers);
	}
	
	//remove player from arena if he died (/kill command sux)
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled = true)
	public void onPlayerDeathEvent(PlayerDeathEvent e)
	{
		Player player = e.getEntity();
		Arena arena = plugin.pdata.getPlayerArena(player.getName());
		//ignore if player is not in arena
		if (arena == null) {return;}
		arena.arenaph.leavePlayer(player, "", Messages.playerlefttoothers);
	}
	
}
