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

package tntrun.signs;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;

public class JoinSign {

	private TNTRun plugin;
	public JoinSign(TNTRun plugin)
	{
		this.plugin = plugin;
	}

	
	protected void handleCreation(SignChangeEvent e)
	{
		Arena arena = plugin.pdata.getArenaByName(e.getLine(2));
		if (arena!=null)
		{
			e.setLine(0, ChatColor.BLUE+"[TNTRun]");
			e.getPlayer().sendMessage("Sign succesfully created");
			plugin.signEditor.addSign(e.getBlock(), arena.getArenaName());
			SignMode mode;
			if(!arena.isArenaEnabled()) {
				mode = SignMode.DISABLED;
			} else if(arena.isArenaRunning()) {
				mode = SignMode.GAME_IN_PROGRESS;
			} else if (arena.isArenaRegenerating()) {
				mode = SignMode.REGENERATING;
			} else {
				mode = SignMode.ENABLED;
			}
			plugin.signEditor.modifySigns(arena.getArenaName(), mode, plugin.pdata.getArenaPlayers(arena).size(), arena.getMaxPlayers());
		} else
		{
			e.getPlayer().sendMessage("Arena does not exist");
			e.setCancelled(true);
			e.getBlock().breakNaturally();
		}
	}
	
	protected void handleClick(PlayerInteractEvent e)
	{
		Arena arena = plugin.pdata.getArenaByName(((Sign)e.getClickedBlock().getState()).getLine(2));
		if (arena!=null)
		{
			if (!arena.isArenaEnabled()) {Messages.sendMessage(e.getPlayer(), Messages.arenadisabled); return;}
			if (arena.isArenaRunning()) {Messages.sendMessage(e.getPlayer(), Messages.arenarunning); return;}
			arena.arenaph.spawnPlayer(e.getPlayer(), Messages.playerjoinedtoplayer, Messages.playerjoinedtoothers);
			e.setCancelled(true);
		} else
		{
			e.getPlayer().sendMessage("Arena does not exist");
		}
	}
	
	protected void handleDestroy(Block b) {
		plugin.signEditor.removeSign(b, ((Sign)b.getState()).getLine(2));
	}
	
}
