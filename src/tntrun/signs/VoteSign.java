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
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import tntrun.TNTRun;
import tntrun.arena.Arena;

public class VoteSign {

	private TNTRun plugin;
	public VoteSign(TNTRun plugin)
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
			arena.arenaph.vote(e.getPlayer());
			e.setCancelled(true);
		} else
		{
			e.getPlayer().sendMessage("Arena does not exist");
		}
	}
	
}
