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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import tntrun.TNTRun;

public class GameLevel {

	private Vector gp1 = null;
	private Vector gp2 = null;

	private Vector p1 = null;
	private Vector p2 = null;
	
	
	protected boolean isSandLocation(Location loc)
	{
		if (loc.toVector().isInAABB(gp1, gp2))
		{
			return true;
		}
		return false;
	};
	
	protected void destroyBlock(Location loc, int delay, TNTRun plugin)
	{
		final Location blockUnderFeetLocation = getPlayerStandOnBlockLocation(loc);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			public void run()
			{
				removeGLBlocks(blockUnderFeetLocation.getBlock());
			}
		},delay);
	}
	private Location getPlayerStandOnBlockLocation(Location locationUnderPlayer)
	{
		Location b11 = locationUnderPlayer.clone().add(0.3,0,-0.3);
		if (b11.getBlock().getType() != Material.AIR)
		{
			return b11;
		} 
		Location b12 = locationUnderPlayer.clone().add(-0.3,0,-0.3);
		if (b12.getBlock().getType() != Material.AIR)
		{
			return b12;
		}
		Location b21 = locationUnderPlayer.clone().add(0.3,0,0.3);
		if (b21.getBlock().getType() != Material.AIR)
		{
			return b21;
		}
		Location b22 = locationUnderPlayer.clone().add(-0.3,0,+0.3);
		if (b22.getBlock().getType() != Material.AIR)
		{
			return b22;
		}
		return locationUnderPlayer;
	}
	private void removeGLBlocks(Block block)
	{
		block.setType(Material.AIR);
		block.getRelative(BlockFace.DOWN).setType(Material.AIR);
	}
	
	
	protected void setGameLocation(Location p1, Location p2, World w)
	{
		this.p1 = p1.toVector();
		this.p2 = p2.toVector();
		this.gp1 = p1.add(0, 1, 0).toVector();
		this.gp2 = p2.add(0, 1, 0).toVector();
		fillArea(w);
	}
	protected void regen(World w)
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
				Block b = w.getBlockAt(x, y, z);
				if (b.getTypeId() != 46) 
				{
					b.setTypeIdAndData(46 , (byte) 0, true);
				}
				b = b.getRelative(BlockFace.UP);
				if (b.getTypeId() != 12) 
				{
					b.setTypeIdAndData(12 , (byte) 0, true);
				}
			}
		}
	}
	
	

	protected void saveToConfig(String levelname, FileConfiguration config)
	{
		config.set(levelname+".p1", p1);
		config.set(levelname+".p2", p2);
	}
	
	protected void loadFromConfig(String levelname, FileConfiguration config)
	{
		Vector p1 = config.getVector(levelname+".p1", null);
		Vector p2 = config.getVector(levelname+".p2", null);
		this.p1 = p1;
		this.p2 = p2;
		this.gp1 = p1.clone().add(new Vector(0,1,0));
		this.gp2 = p2.clone().add(new Vector(0,1,0));
	}
	
}
