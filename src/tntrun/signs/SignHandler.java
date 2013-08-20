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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import tntrun.TNTRun;

public class SignHandler implements Listener {

	private JoinSign joinsign;
	private LeaveSign leavesign;
	public SignHandler(TNTRun plugin)
	{
		joinsign = new JoinSign(plugin);
		leavesign = new LeaveSign(plugin);
	}
	
	
	//handle sign change
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void onTNTRunSignCreate(SignChangeEvent e)
	{
		Player player = e.getPlayer();
		if (e.getLine(0).equalsIgnoreCase("[TNTRun]"))
		{
			if (!player.hasPermission("tntrun.setupsigns")) 
			{
				player.sendMessage("You don't have permission to do this");
				e.setCancelled(true);
				e.getBlock().breakNaturally();
				return;
			}
			
			if (e.getLine(1).equalsIgnoreCase("[join]") && e.getLine(2) != null)
			{
				joinsign.handleCreation(e);
			}
			else if (e.getLine(1).equalsIgnoreCase("[leave]"))
			{
				leavesign.handleCreation(e);
			}
			else if (e.getLine(1).equalsIgnoreCase("[vote]"))
			{
				
			}
		}
	}
	
	//handle sign click
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void onSignClick(PlayerInteractEvent e)
	{
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {return;}
		if (!(e.getClickedBlock().getState() instanceof Sign)) {return;}
		Sign sign = (Sign) e.getClickedBlock().getState();
		Player player = e.getPlayer();
		if (sign.getLine(0).equalsIgnoreCase(ChatColor.BLUE+"[TNTRun]"))
		{
			if (!player.hasPermission("tntrun.gamesigns")) 
			{
				player.sendMessage("You don't have permission to do this");
				e.setCancelled(true);
				return;
			}
			
			if (sign.getLine(1).equalsIgnoreCase("[join]") && sign.getLine(2) != null)
			{
				joinsign.handleClick(e);
			}
			else if (sign.getLine(1).equalsIgnoreCase("[leave]"))
			{
				leavesign.handleClick(e);
			}
			else if (sign.getLine(1).equalsIgnoreCase("[vote]"))
			{
				
			}
		}
	}
	
	
	
}
