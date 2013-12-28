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
import tntrun.messages.Messages;

public class GameCommands implements CommandExecutor{

	private TNTRun plugin;
	public GameCommands(TNTRun plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) 
		{
			sender.sendMessage("A player is expected");
			return true;
		}
		Player player = (Player) sender;
		//check permissions
		if (!player.hasPermission("tntrun.game")) 
		{
			Messages.sendMessage(player, Messages.nopermission);
			return true;
		}
		//handle commands
		//help command
		if (args.length == 1 && args[0].equalsIgnoreCase("help"))
		{
			sender.sendMessage("/tr lobby - teleport to lobby");
			sender.sendMessage("/tr list - list all arenas");
			sender.sendMessage("/tr status {arena} - show arena status");
			sender.sendMessage("/tr join {arena} - join arena");
			sender.sendMessage("/tr leave - leave current arena");
			sender.sendMessage("/tr vote - vote for current arena start");
			return true;
		}
		else if (args.length == 1 && args[0].equalsIgnoreCase("lobby"))
		{
			if (plugin.globallobby.isLobbyLocationSet())
			{
				if (plugin.globallobby.isLobbyLocationWorldAvailable())
				{
					player.teleport(plugin.globallobby.getLobbyLocation());
					Messages.sendMessage(player, Messages.teleporttolobby);
				} else
				{
					player.sendMessage("Lobby world is unloaded, can't join lobby");
				}
			} else
			{
				sender.sendMessage("Lobby is not set");

			}
			return true;
		}
		//list arenas
		else if (args.length == 1 && args[0].equalsIgnoreCase("list"))
		{
			StringBuilder message = new StringBuilder(200);
			message.append(Messages.availablearenas);
			for (Arena arena : plugin.pdata.getArenas())
			{
				if (arena.isArenaEnabled())
				{
					message.append("&a"+arena.getArenaName()+" ");
				} else
				{
					message.append("&c"+arena.getArenaName()+" ");
				}
			}
			Messages.sendMessage(player, message.toString());
			return true;
		}
		//status
		else if (args.length == 2 && args[0].equalsIgnoreCase("status"))
		{
			Arena arena = plugin.pdata.getArenaByName(args[1]);
			if (arena != null)
			{
				player.sendMessage("Arena enabled: "+arena.isArenaEnabled());
				player.sendMessage("Arena running: "+arena.isArenaRunning());
				player.sendMessage("Players: "+Arrays.asList(plugin.pdata.getArenaPlayers(arena).toArray()));
				return true;
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//join arena
		else if (args.length == 2 && args[0].equalsIgnoreCase("join"))
		{
			Arena arena = plugin.pdata.getArenaByName(args[1]);
			if (arena != null)
			{
				boolean canJoin = arena.arenaph.tryJoin(player);
				if (canJoin)
				{
					arena.arenaph.spawnPlayer(player, Messages.playerjoinedtoplayer, Messages.playerjoinedtoothers);
				}
				return true;
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		//leave arena
		else if (args.length == 1 && args[0].equalsIgnoreCase("leave"))
		{
				Arena arena = plugin.pdata.getPlayerArena(player.getName());
				if (arena != null)
				{
					arena.arenaph.leavePlayer(player,Messages.playerlefttoplayer,Messages.playerlefttoothers);
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
				if (arena.arenaph.vote(player))
				{
					Messages.sendMessage(player, Messages.playervotedforstart);
				} else
				{
					Messages.sendMessage(player, Messages.playeralreadyvotedforstart);
				}
				return true;
			} else
			{
				sender.sendMessage("You are not in arena");
				return true;
			}
		}
		return false;
	}

}
