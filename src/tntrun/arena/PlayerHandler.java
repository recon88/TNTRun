package tntrun.arena;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import tntrun.TNTRun;

public class PlayerHandler {

	private TNTRun plugin;
	private Arena arena;
	public PlayerHandler(TNTRun plugin, Arena arena)
	{
		this.plugin = plugin;
		this.arena = arena;
	}
	
	//spawn player on arena
	public void spawnPlayer(Player player)
	{
		player.setGameMode(GameMode.SURVIVAL);
		plugin.pdata.setPlayerLocation(player.getName());
		plugin.pdata.setPlayerInventory(player.getName());
		plugin.pdata.setPlayerArmor(player.getName());
		player.getInventory().clear();
		player.teleport(arena.getSpawnPoint());
		for (String p : plugin.pdata.getArenaPlayers(arena))
		{
			Bukkit.getPlayerExact(p).sendMessage("Player "+player.getName()+" joined arena");
		}
		plugin.pdata.setPlayerArena(player.getName(), arena);
		if (plugin.pdata.getArenaPlayers(arena).size() == arena.maxPlayers || plugin.pdata.getArenaPlayers(arena).size() == arena.minPlayers)
		{
			arena.arenagh.runArena();
		}
	}
	//remove player from arena
	public void leavePlayer(Player player, String msgtoplayer, String msgtoarenaplayers)
	{
		plugin.pdata.removePlayerFromArena(player.getName());
		player.teleport(plugin.pdata.getPlayerLocation(player.getName()));
		player.getInventory().setContents(plugin.pdata.getPlayerInventory(player.getName()));
		player.getInventory().setArmorContents(plugin.pdata.getPlayerArmor(player.getName()));
		if (!msgtoplayer.equalsIgnoreCase(""))
		{
			player.sendMessage(msgtoplayer);
		}
		if (!msgtoarenaplayers.equalsIgnoreCase(""))
		{
			for (String p : plugin.pdata.getArenaPlayers(arena))
			{
				Bukkit.getPlayerExact(p).sendMessage(msgtoarenaplayers);
			}
		}
		
		votes.remove(player.getName());
	}
	//vote for game start
	private HashSet<String> votes = new HashSet<String>();
	public boolean vote(Player player)
	{
		if (!votes.contains(player.getName()))
		{
			votes.add(player.getName());
			if (votes.size() >= ((int)plugin.pdata.getArenaPlayers(arena).size()*arena.votesPercent))
			{
				arena.arenagh.runArena();
			}	
			return true;
		}
		return false;
	}
	
	
}
