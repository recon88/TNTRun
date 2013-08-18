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

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;

public class GameCommands implements CommandExecutor{

	private TNTRun plugin;
	public GameCommands(TNTRun plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if (!(sender instanceof Player)) 
		{
			sender.sendMessage("A player is expected");
			return true;
		}
		Player player = (Player) sender;
		//check permissions
		if (!player.hasPermission("tntrun.game")) 
		{
			player.sendMessage("You don't have permission to do this");
			return true;
		}
		//handle commands
		//help command
		if (args.length == 1 && args[0].equalsIgnoreCase("help"))
		{
			sender.sendMessage("/tr list - list all arenas");
			sender.sendMessage("/tr status {arena} - show arena status");
			sender.sendMessage("/tr join {arena} - join arena");
			sender.sendMessage("/tr leave - leave current arena");
			sender.sendMessage("/tr vote - vote for current arena start");
			return true;
		}
		//list arenas
		else if (args.length == 1 && args[0].equalsIgnoreCase("list"))
		{
			StringBuilder message = new StringBuilder(200);
			message.append("Available arenas: ");
			for (Arena arena : plugin.pdata.getArenas())
			{
				message.append(arena.getArenaName()+" ");
			}
			player.sendMessage(message.toString());
			return true;
		}
		//status
		else if (args.length == 2 && args[0].equalsIgnoreCase("status"))
		{
			Arena arena = getArenaByName(args[1]);
			if (arena != null)
			{
				player.sendMessage("Arena enabled: "+arena.isArenaEnabled());
				player.sendMessage("Arena running: "+arena.running);
				player.sendMessage("Current players count: "+plugin.pdata.getArenaPlayers(arena).size());
				player.sendMessage("Players: "+Arrays.asList(plugin.pdata.getArenaPlayers(arena).toArray()));
				return true;
			} else
			{
				sender.sendMessage("Arena not exists");
				return true;
			}
		}
		//join arena
		else if (args.length == 2 && args[0].equalsIgnoreCase("join"))
		{
			Arena arena = getArenaByName(args[1]);
			if (arena != null)
			{
				if (!arena.isArenaEnabled()) {sender.sendMessage("Arena is disabled"); return true;}
				if (arena.running) {sender.sendMessage("Arena already running"); return true;}
				arena.spawnPlayer(player);
				player.sendMessage("You have joined the arena");
				player.sendMessage("Current players count: "+plugin.pdata.getArenaPlayers(arena).size());
				return true;
			} else
			{
				sender.sendMessage("Arena not exists");
				return true;
			}
		}
		//leave arena
		else if (args.length == 1 && args[0].equalsIgnoreCase("leave"))
		{
				Arena arena = plugin.pdata.getPlayerArena(player.getName());
				if (arena != null)
				{
					arena.leavePlayer(player);
					player.sendMessage("You left the arena");
					return true;
				} else
				{
					sender.sendMessage("You are not in arena");
					return true;
				}
		}
		//vote
		else if (args.length == 1 && args[0].equalsIgnoreCase("vote"))
		{
			Arena arena = plugin.pdata.getPlayerArena(player.getName());
			if (arena != null)
			{
				if (arena.vote(player))
				{
					player.sendMessage("You voted for game start");
				} else
				{
					player.sendMessage("You already voted");
				}
			} else
			{
				sender.sendMessage("You are not in arena");
				return true;
			}
		}
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
