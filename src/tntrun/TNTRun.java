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
import tntrun.eventhandler.DamageHandler;
import tntrun.eventhandler.QuitHandler;
import tntrun.eventhandler.RestrictionHandler;

public class TNTRun extends JavaPlugin {

	public PlayerDataStore pdata;
	public SetupCommands scommands;
	public GameCommands gcommands;
	public QuitHandler ghandler;
	public DamageHandler dhandler;
	public RestrictionHandler rhandler;
	
	@Override
	public void onEnable()
	{
		pdata = new PlayerDataStore();
		scommands = new SetupCommands(this);
		getCommand("trsetup").setExecutor(scommands);
		gcommands = new GameCommands(this);
		getCommand("tr").setExecutor(gcommands);
		ghandler = new QuitHandler(this);
		getServer().getPluginManager().registerEvents(ghandler, this);
		dhandler = new DamageHandler(this);
		getServer().getPluginManager().registerEvents(dhandler, this);
		rhandler = new RestrictionHandler(this);
		getServer().getPluginManager().registerEvents(rhandler, this);
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
		HandlerList.unregisterAll(this);
		scommands = null;
		gcommands = null;
		ghandler = null;
		dhandler = null;
		rhandler = null;
		pdata = null;
	}
	
}
