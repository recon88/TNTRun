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

public class GameLevel {

	private String name;
	public GameLevel(String name)
	{
		this.name = name;
	}
	public String getGameLevelName()
	{
		return name;
	}

	
	private Vector gp1 = null;
	private Vector gp2 = null;
	
	private Vector p1 = null;
	public Vector getP1()
	{
		return p1;
	}
	private Vector p2 = null;
	public Vector getP2()
	{
		return p2;
	}

	
	private GameLevelBlockContainer customblockcontainer = new GameLevelBlockContainer();
	public GameLevelBlockContainer getCustomBlocks()
	{
		return customblockcontainer; 
	}
	
	
	protected boolean isSandLocation(Location loc)
	{
		return loc.toVector().isInAABB(gp1, gp2.clone().add(new Vector(0,1,0)));
	};
	
	
	protected void destroyBlock(Location loc, int delay, final Arena arena)
	{
		final Location blockUnderFeetLocation = getPlayerStandOnBlockLocation(loc);
		Bukkit.getScheduler().scheduleSyncDelayedTask(arena.plugin, new Runnable()
		{
			public void run()
			{
				if (arena.isArenaRunning() && !arena.isArenaRegenerating() && blockUnderFeetLocation != null)
				{
					removeGLBlocks(blockUnderFeetLocation.getBlock());
				}
			}
		},delay);
	}
	private Location getPlayerStandOnBlockLocation(Location locationUnderPlayer)
	{
		locationUnderPlayer.setY(gp1.getY());
		Location b11 = locationUnderPlayer.clone().add(0.3,0,-0.3);
		if (b11.getBlock().getType() != Material.AIR && isInsideGamelevel(b11))
		{
			return b11;
		} 
		Location b12 = locationUnderPlayer.clone().add(-0.3,0,-0.3);
		if (b12.getBlock().getType() != Material.AIR && isInsideGamelevel(b12))
		{
			return b12;
		}
		Location b21 = locationUnderPlayer.clone().add(0.3,0,0.3);
		if (b21.getBlock().getType() != Material.AIR && isInsideGamelevel(b21))
		{
			return b21;
		}
		Location b22 = locationUnderPlayer.clone().add(-0.3,0,+0.3);
		if (b22.getBlock().getType() != Material.AIR && isInsideGamelevel(b22))
		{
			return b22;
		}
		return null;
	}
	private void removeGLBlocks(Block block)
	{
		block.setType(Material.AIR);
		block.getRelative(BlockFace.DOWN).setType(Material.AIR);
	}
	private Vector glb1 = null;
	private Vector glb2 = null;
	private boolean isInsideGamelevel(Location loc)
	{
		if (loc.getBlock().getLocation().toVector().isInAABB(glb1, glb2))
		{
			return true;
		}
		return false;
	}
	
	protected void setGameLocation(Location p1, Location p2, World w)
	{
		unsetCustomGameLevel();
		this.p1 = p1.toVector();
		this.p2 = p2.toVector();
		this.gp1 = p1.add(0, 1, 0).toVector();
		this.gp2 = p2.add(0, 1, 0).toVector();
		this.glb1 = gp1.clone().add(new Vector(1,0,1));
		this.glb2 = gp2.clone().add(new Vector(-1,0,-1));
		fillArea(w);
	}
	protected void setCustomGameLevel(World w)
	{
		customblockcontainer.setCustomGameLevel(w, p1, p2);
	}
	protected void unsetCustomGameLevel()
	{
		customblockcontainer.unsetCustomGameLevel();
	}
	protected void regen(World w)
	{
		fillArea(w);
	}
	private void fillArea(World w)
	{
		if (customblockcontainer.customGameLevelSet())
		{
			customblockcontainer.fillCustomArea(w, p1, p2);
		} else
		{
			fillDefaultArea(w);
		}
	}
	@SuppressWarnings("deprecation")
	private void fillDefaultArea(World w)
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
	

	protected void saveToConfig(FileConfiguration config)
	{
		config.set("gamelevels."+name+".p1", p1);
		config.set("gamelevels."+name+".p2", p2);
		customblockcontainer.saveToConfig("gamelevels."+name, config);
	}
	
	protected void loadFromConfig(FileConfiguration config)
	{
		Vector p1 = config.getVector("gamelevels."+name+".p1", null);
		Vector p2 = config.getVector("gamelevels."+name+".p2", null);
		this.p1 = p1;
		this.p2 = p2;
		this.gp1 = p1.clone().add(new Vector(0,1,0));
		this.gp2 = p2.clone().add(new Vector(0,1,0));
		this.glb1 = gp1.clone().add(new Vector(1,0,1));
		this.glb2 = gp2.clone().add(new Vector(-1,0,-1));
		customblockcontainer.loadFromConfig("gamelevels."+name, config);
	}
	
}
