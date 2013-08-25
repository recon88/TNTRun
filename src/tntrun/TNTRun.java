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

package tntrun;

import java.io.File;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import tntrun.arena.Arena;
import tntrun.commands.GameCommands;
import tntrun.commands.SetupCommands;
import tntrun.datahandler.PlayerDataStore;
import tntrun.eventhandler.PlayerStatusHandler;
import tntrun.eventhandler.QuitHandler;
import tntrun.eventhandler.RestrictionHandler;
import tntrun.messages.Messages;
import tntrun.signs.SignHandler;

public class TNTRun extends JavaPlugin {

	public PlayerDataStore pdata;
	public SetupCommands scommands;
	public GameCommands gcommands;
	public QuitHandler ghandler;
	public PlayerStatusHandler pshandler;
	public RestrictionHandler rhandler;
	public SignHandler signs;
	
	@Override
	public void onEnable()
	{
		Messages.loadMessages();
		pdata = new PlayerDataStore();
		scommands = new SetupCommands(this);
		getCommand("trsetup").setExecutor(scommands);
		gcommands = new GameCommands(this);
		getCommand("tr").setExecutor(gcommands);
		ghandler = new QuitHandler(this);
		getServer().getPluginManager().registerEvents(ghandler, this);
		pshandler = new PlayerStatusHandler(this);
		getServer().getPluginManager().registerEvents(pshandler, this);
		rhandler = new RestrictionHandler(this);
		getServer().getPluginManager().registerEvents(rhandler, this);
		signs = new SignHandler(this);
		getServer().getPluginManager().registerEvents(signs, this);
		//load arenas
		File arenasfolder = new File("plugins/TNTRun/arenas/");
		arenasfolder.mkdirs(); 
		for (String file : arenasfolder.list())
		{
			Arena arena = new Arena(file.split("[.]")[0], this);
			try {
				arena.loadFromConfig();
			} catch (Exception e) {}
		}
	}
	
	@Override
	public void onDisable()
	{
		//save arenas
		for (Arena arena : pdata.getArenas())
		{
			arena.disableArena();
			arena.saveToConfig();
		}
		HandlerList.unregisterAll(this);
		scommands = null;
		gcommands = null;
		ghandler = null;
		pshandler = null;
		rhandler = null;
		signs = null;
		pdata = null;
	}
	
}
