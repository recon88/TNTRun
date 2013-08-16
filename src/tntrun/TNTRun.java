package tntrun;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import tntrun.arena.Arena;
import tntrun.commands.GameCommands;
import tntrun.commands.SetupCommands;
import tntrun.datahandler.PlayerDataStore;

public class TNTRun extends JavaPlugin {

	public PlayerDataStore pdata;
	public SetupCommands scommands;
	public GameCommands gcommands;
	
	@Override
	public void onEnable()
	{
		pdata = new PlayerDataStore();
		scommands = new SetupCommands(this);
		getCommand("trsetup").setExecutor(scommands);
		gcommands = new GameCommands(this);
		getCommand("tr").setExecutor(gcommands);
		//load arenas
		new File("plugins/TNTRun/arenas/").mkdirs(); 
		for (String file : new File("plugins/TNTRun/arenas/").list())
		{
			Arena arena = new Arena(file.split("[.]")[0], this);
			arena.loadFromConfig();
			arena.enableArena();
		}
	}
	
	@Override
	public void onDisable()
	{
		//save configured arenas
		for (Arena arena : pdata.getArenas())
		{
			if (arena.isArenaConfigured())
			{
				arena.saveToConfig();
			}
		}
		scommands = null;
		gcommands = null;
		pdata = null;
	}
	
}
