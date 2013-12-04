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

package tntrun.lobby;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import tntrun.TNTRun;

public class GlobalLobby {

	private File lobbyFile;
	public GlobalLobby(TNTRun plugin)
	{
		lobbyFile = new File(plugin.getDataFolder()+File.separator+"lobby.yml");
	}
	
	private Location lobbyLocation = null;
	public boolean isLobbyLocationSet()
	{
		return lobbyLocation != null;
	}
	public Location getLobbyLocation()
	{
		return lobbyLocation.clone();
	}
	
	public void setLobbyLocation(Location lobbyLocation)
	{
		this.lobbyLocation = lobbyLocation;
	}
	
	
	public void saveToConfig()
	{
		FileConfiguration config = new YamlConfiguration();
		try {
			config.set("lobby.world", lobbyLocation.getWorld());
			config.set("lobby.vector", lobbyLocation.toVector());
			config.set("lobby.pitch", lobbyLocation.getPitch());
			config.set("lobby.yaw", lobbyLocation.getYaw());
			config.save(lobbyFile);
		} catch (Exception e) {}
	}
	
	public void loadFromConfig()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(lobbyFile);
		try {
			World world = Bukkit.getWorld(config.getString("lobby.world",null));
			Vector vector = config.getVector("lobby.vector", null);
			float pitch = (float) config.getDouble("lobby.pitch", 0.0);
			float yaw = (float) config.getDouble("lobby.yaw",0.0);
			lobbyLocation = new Location(world,vector.getX(),vector.getY(),vector.getZ(),yaw,pitch);
		} catch (Exception e) {}
	}
	
}
