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

public class GameLevel {

	private Vector sandp1 = null;
	private Vector sandp2 = null;

	private Vector tntp1 = null;
	private Vector tntp2 = null;
	
	
	protected boolean isSandLocation(Location loc)
	{
		if (loc.toVector().isInAABB(sandp1, sandp2))
		{
			return true;
		}
		return false;
	};
	
	protected void destroyBlock(Location loc)
	{
		Location blockUnderFeetLocation = getPlayerStandOnBlockLocation(loc);
		removeGLBlocks(blockUnderFeetLocation);
	}
	private Location getPlayerStandOnBlockLocation(Location locationUnderPlayer)
	{
		Location b11 = locationUnderPlayer.clone().add(0.3,0,-0.3);
		Location b12 = locationUnderPlayer.clone().add(-0.3,0,-0.3);
		Location b21 = locationUnderPlayer.clone().add(0.3,0,0.3);
		Location b22 = locationUnderPlayer.clone().add(-0.3,0,+0.3);
		if (b11.getBlock().getType() != Material.AIR)
		{
			return b11;
		} else 
		if (b12.getBlock().getType() != Material.AIR)
		{
			return b12;
		} else 
		if (b21.getBlock().getType() != Material.AIR)
		{
			return b21;
		} else
		if (b22.getBlock().getType() != Material.AIR)
		{
			return b22;
		}
		return locationUnderPlayer;
	}
	private void removeGLBlocks(Location blockLocation)
	{
		blockLocation.getBlock().setType(Material.AIR);
		blockLocation.clone().add(0,-1,0).getBlock().setType(Material.AIR);
	}
	
	
	protected void setGameLocation(Location p1, Location p2, World w)
	{
		this.tntp1 = p1.toVector();
		this.tntp2 = p2.toVector();
		this.sandp1 = p1.add(0, 1, 0).toVector();
		this.sandp2 = p2.add(0, 1, 0).toVector();
		fillArea(w);
	}
	protected void regen(World w)
	{
		fillArea(w);
	}
	private void fillArea(World w)
	{
		int y = tntp1.getBlockY();
		for (int x = tntp1.getBlockX(); x<=tntp2.getBlockX(); x++)
		{
			for (int z = tntp1.getBlockZ(); z<=tntp2.getBlockZ(); z++)
			{
				w.getBlockAt(x, y, z).setType(Material.TNT);
			}
		}
		y = sandp1.getBlockY();
		for (int x = sandp1.getBlockX(); x<=sandp2.getBlockX(); x++)
		{
			for (int z = sandp1.getBlockZ(); z<=sandp2.getBlockZ(); z++)
			{
				w.getBlockAt(x, y, z).setType(Material.SAND);
			}
		}
	}
	
	

	protected void saveToConfig(String levelname, FileConfiguration config)
	{
		config.set(levelname+".p1", tntp1);
		config.set(levelname+".p2", tntp2);
	}
	
	protected void loadFromConfig(String levelname, FileConfiguration config)
	{
		Vector p1 = config.getVector(levelname+".p1", null);
		Vector p2 = config.getVector(levelname+".p2", null);
		this.tntp1 = p1;
		this.tntp2 = p2;
		this.sandp1 = p1.clone().add(new Vector(0,1,0));
		this.sandp2 = p2.clone().add(new Vector(0,1,0));
	}
	
}
