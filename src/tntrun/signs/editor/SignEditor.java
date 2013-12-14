package tntrun.signs.editor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tntrun.TNTRun;
import tntrun.arena.Arena;

public class SignEditor {
	
	private TNTRun plugin;
	private HashMap<String, HashSet<SignInfo>> signs = new HashMap<String, HashSet<SignInfo>>();

	private File configfile;
	public SignEditor(TNTRun plugin) 
	{
		this.plugin = plugin;
		configfile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator+ "signs.yml");
	}

	public void addArena(String arena) 
	{
		if(!signs.containsKey(arena)) 
		{
			signs.put(arena, new HashSet<SignInfo>());
		}
	}
	public void removeArena(String arena)
	{
		for (Block block : getSignsBlocks(arena))
		{
			removeSign(block, arena);
		}
		signs.remove(arena);
	}
	public void addSign(Block block, String arena) 
	{
		SignInfo signinfo = new SignInfo(block);
		addArena(arena);
		getSigns(arena).add(signinfo);
	}
	public void removeSign(Block block, String arena) 
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
		SignInfo signinfo = new SignInfo(block);
		addArena(arena);
		getSigns(arena).remove(signinfo);
	}
	public HashSet<Block> getSignsBlocks(String arena)
	{
		HashSet<Block> signs = new HashSet<Block>();
		for (SignInfo signinfo : getSigns(arena))
		{
			if (signinfo != null)
			{
				signs.add(signinfo.getBlock());
			}
		}
		return signs;
	}

	private void addSignInfo(SignInfo si, String arena)
	{
		addArena(arena);
		getSigns(arena).add(si);
	}
	private HashSet<SignInfo> getSigns(String arena) 
	{
		addArena(arena);
		return signs.get(arena);
	}

	public void modifySigns(String arena) 
	{
		Arena arenainst = plugin.pdata.getArenaByName(arena);
		if (arena == null) {return;}

		String text = null;
		int players = plugin.pdata.getArenaPlayers(arenainst).size();
		int maxPlayers = arenainst.getMaxPlayers();
		if(!arenainst.isArenaEnabled()) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Disabled";
		} else if (arenainst.isArenaRunning()) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "In Game";
		} else if (arenainst.isArenaRegenerating()) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Regenerating";
		}  else if(players == maxPlayers) {
			text = ChatColor.RED.toString() + ChatColor.BOLD.toString() + Integer.toString(players) + "/" + Integer.toString(maxPlayers);
		} else {
			text = ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + Integer.toString(players) + "/" + Integer.toString(maxPlayers);
		}
		
		for(Block block : getSignsBlocks(arena)) 
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
				SignInfo si = new SignInfo(blockSection.getString("world"), blockSection.getInt("x"), blockSection.getInt("y"), blockSection.getInt("z"));
				addSignInfo(si, arena);
			}
			modifySigns(arena);
		}
	}

	public void saveConfiguration() 
	{
		FileConfiguration file = new YamlConfiguration();
		
		for(String arena : signs.keySet()) 
		{
			ConfigurationSection section = file.createSection(arena);
			int i = 0;
			for(SignInfo si : getSigns(arena)) 
			{
				ConfigurationSection blockSection = section.createSection(Integer.toString(i++));
				blockSection.set("x", si.getX());
				blockSection.set("y", si.getY());
				blockSection.set("z", si.getZ());
				blockSection.set("world", si.getWorldName());
			}
		}
		
		try {file.save(configfile);} catch (IOException e) {}
	}

}
