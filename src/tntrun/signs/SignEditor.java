package tntrun.signs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tntrun.TNTRun;

public class SignEditor {
	
	private TNTRun plugin;
	private HashMap<String, HashSet<Block>> signs = new HashMap<String, HashSet<Block>>();

	private File configfile;
	public SignEditor(TNTRun plugin) {
		this.plugin = plugin;
		configfile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator+ "signs.yml");
	}

	public SignEditor addArena(String arena) 
	{
		if(!signs.containsKey(arena)) 
		{
			signs.put(arena, new HashSet<Block>());
		}
		return this;
	}
	public SignEditor removeArena(String arena)
	{
		for (Block block : new HashSet<Block>(getSigns(arena)))
		{
			removeSign(block, arena);
		}
		signs.remove(arena);
		return this;
	}
	public SignEditor addSign(Block block, String arena) 
	{
		addArena(arena).getSigns(arena).add(block);
		return this;
	}
	public SignEditor removeSign(Block block, String arena) 
	{
		if (block.getState() instanceof Sign)
		{
			Sign sign = (Sign) block.getState();
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			sign.setLine(4, "");
			sign.update();
		}
		addArena(arena).getSigns(arena).remove(block);
		return this;
	}
	public HashSet<Block> getSignsCopy(String arena)
	{
		return new HashSet<Block>(getSigns(arena));
	}
	protected HashSet<Block> getSigns(String arena) 
	{
		return addArena(arena).signs.get(arena);
	}

	public void modifySigns(String arena, SignMode mode) 
	{
		modifySigns(arena, mode, 0, 1);
	}
	public void modifySigns(String arena, SignMode mode, int players, int maxPlayers) 
	{
		String text = null;
		if(mode == SignMode.GAME_IN_PROGRESS) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "In game";
		} else if(mode == SignMode.DISABLED) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Disabled";
		} else if (mode == SignMode.REGENERATING) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Regenerating";
		} else if(players == maxPlayers) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + Integer.toString(players) + "/" + Integer.toString(maxPlayers);
		} else {
			text = ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + Integer.toString(players) + "/" + Integer.toString(maxPlayers);
		}
		
		for(Block block : getSigns(arena)) 
		{
			if (block.getState() instanceof Sign)
			{
				Sign sign = (Sign) block.getState();
				sign.setLine(3, text);
				sign.update();
			} else
			{
				removeSign(block, arena);
			}
		}
	}

	public void loadConfiguration() 
	{
		FileConfiguration file = YamlConfiguration.loadConfiguration(configfile);

		for(String arena : file.getKeys(false))
		{
			ConfigurationSection section = file.getConfigurationSection(arena);
			for(String block : section.getKeys(false)) 
			{
				ConfigurationSection blockSection = section.getConfigurationSection(block);
				Location l = new Location(Bukkit.getWorld(blockSection.getString("world")), (double)blockSection.getInt("x"), (double)blockSection.getInt("y"), (double)blockSection.getInt("z"));
				Block sign = l.getBlock();
				addSign(sign, arena);
			}
			modifySigns(arena, SignMode.ENABLED, 0, plugin.pdata.getArenaByName(arena).getMaxPlayers());
		}
	}

	public void saveConfiguration() 
	{
		FileConfiguration file = new YamlConfiguration();
		
		for(String arena : signs.keySet()) 
		{
			ConfigurationSection section = file.createSection(arena);
			int i = 0;
			for(Block b : signs.get(arena)) 
			{
				ConfigurationSection blockSection = section.createSection(Integer.toString(i++));
				blockSection.set("x", b.getX());
				blockSection.set("y", b.getY());
				blockSection.set("z", b.getZ());
				blockSection.set("world", b.getWorld().getName());
			}
		}
		
		try {file.save(configfile);} catch (IOException e) {}
	}

}
