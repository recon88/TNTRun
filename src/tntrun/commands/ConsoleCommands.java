package tntrun.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;

public class ConsoleCommands implements CommandExecutor {

	private TNTRun plugin;
	public ConsoleCommands(TNTRun plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender)) 
		{
			sender.sendMessage("Console is expected");
			return true;
		}
		//handle commands
		//disable arena
		else if (args.length == 2 && args[1].equalsIgnoreCase("disable"))
		{
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null)
			{
				arena.disableArena();
				sender.sendMessage("Arena disabled");
			} else
			{
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		//enable arena
		else if (args.length == 2 && args[1].equalsIgnoreCase("enable"))
		{
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null)
			{
				if (arena.isArenaEnabled())
				{
					sender.sendMessage("Arena already enabled.");
				} else
				{
					if (arena.enableArena())
					{
						sender.sendMessage("Arena enabled");
					} else
					{
						sender.sendMessage("Arena is not configured. Reason: "+arena.isArenaConfigured());
					}
				}
			} else
			{
				sender.sendMessage("Arena does not exist");
			}
			return true;
		}
		//reload messages
		else if (args.length == 1 && args[0].equalsIgnoreCase("reloadmsg"))
		{
			Messages.loadMessages(plugin);
			sender.sendMessage("Messages reloaded");
			return true;
		}
		//status
		else if (args.length == 2 && args[1].equalsIgnoreCase("status"))
		{
			Arena arena = plugin.pdata.getArenaByName(args[0]);
			if (arena != null)
			{
				sender.sendMessage("Arena enabled: "+arena.isArenaEnabled());
				sender.sendMessage("Arena running: "+arena.isArenaRunning());
				sender.sendMessage("Players: "+Arrays.asList(plugin.pdata.getArenaPlayers(arena).toArray()));
				return true;
			} else
			{
				sender.sendMessage("Arena does not exist");
				return true;
			}
		}
		return false;
	}
	
}
