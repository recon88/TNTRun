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

package tntrun.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

public class LoseLevel {

	private Vector p1 = null;
	private Vector p2 = null;
	
	
	protected boolean isConfigured()
	{
		if (p1 != null && p2 != null)
		{
			return true;
		}
		return false;
	}
	
	
	protected boolean isLooseLocation(Location loc)
	{
		if (loc.toVector().isInAABB(p1, p2))
		{
			return true;
		}
		return false;
	};
	
	protected void setLooseLocation(Location p1, Location p2, World w)
	{
		this.p1 = p1.toVector();
		this.p2 = p2.toVector();
		fillArea(w);
	}	
	public void regen(World w)
	{
		fillArea(w);
	}
	private void fillArea(World w)
	{
		int y = p1.getBlockY();
		for (int x = p1.getBlockX()+1; x<p2.getBlockX(); x++)
		{
			for (int z = p1.getBlockZ()+1; z<p2.getBlockZ(); z++)
			{
				w.getBlockAt(x, y, z).setType(Material.WATER);
			}
		}
	}
	
	
	protected void saveToConfig(FileConfiguration config)
	{
		config.set("loselevel.p1", p1);
		config.set("loselevel.p2", p2);
	}
	
	protected void loadFromConfig(FileConfiguration config)
	{
		p1 = config.getVector("loselevel.p1", null);
		p2 = config.getVector("loselevel.p2", null);
	}
	
}
