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

import java.util.HashMap;
import java.util.HashSet;

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
	protected boolean isSandLocation(Location loc)
	{
		return loc.toVector().isInAABB(gp1, gp2.clone().add(new Vector(0,1,0)));
	};

	private HashSet<Block> blockstodestroy = new HashSet<Block>();
	protected void destroyBlock(Location loc, final Arena arena)
	{
		final Location blockUnderFeetLocation = getPlayerStandOnBlockLocation(loc);
		if (blockUnderFeetLocation != null)
		{
			final Block block = blockUnderFeetLocation.getBlock();
			if (!blockstodestroy.contains(block))
			{
				Bukkit.getScheduler().scheduleSyncDelayedTask(arena.plugin, new Runnable()
				{
					public void run()
					{
						blockstodestroy.remove(block);
						if (arena.isArenaRunning() && !arena.isArenaRegenerating() && block.getType() != Material.AIR)
						{
							removeGLBlocks(block);
						}
					}
				},arena.getGameLevelDestroyDelay());
				blockstodestroy.add(blockUnderFeetLocation.getBlock());
			}
		}
	}
	private HashMap<String,String> blockmaterial = new HashMap<String,String>(800);
	private void removeGLBlocks(Block block)
	{
		String locationstring = new StringBuilder().append(block.getX()).append("|").append(block.getY()).append("|").append(block.getZ()).toString();
		String blocksmaterial = new StringBuilder().append(block.getType().toString()).append("|").append(block.getRelative(BlockFace.DOWN).getType().toString()).toString();
		blockmaterial.put(locationstring, blocksmaterial);
		block.setType(Material.AIR);
		block.getRelative(BlockFace.DOWN).setType(Material.AIR);
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
	protected void regen(World w)
	{
		for (String locationstring : blockmaterial.keySet())
		{
			String[] coords = locationstring.split("[|]");
			String [] materials = blockmaterial.get(locationstring).split("[|]");
			Location location = new Location(w,Double.valueOf(coords[0]),Double.valueOf(coords[1]),Double.valueOf(coords[2]));
			location.getBlock().setType(Material.getMaterial(materials[0]));
			location.add(0,-1,0).getBlock().setType(Material.getMaterial(materials[1]));
		}
		blockmaterial.clear();
	}
	
	protected void setGameLocation(Location p1, Location p2, World w)
	{
		this.p1 = p1.toVector();
		this.p2 = p2.toVector();
		this.gp1 = p1.add(0, 1, 0).toVector();
		this.gp2 = p2.add(0, 1, 0).toVector();
		this.glb1 = gp1.clone().add(new Vector(1,0,1));
		this.glb2 = gp2.clone().add(new Vector(-1,0,-1));
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
				if (b.getType() != Material.SAND) 
				{
					b.setType(Material.SAND);
				}
				b = b.getRelative(BlockFace.UP);
				if (b.getType() != Material.TNT) 
				{
					b.setType(Material.TNT);
				}
			}
		}
	}

	protected void saveToConfig(FileConfiguration config)
	{
		config.set("gamelevels."+name+".p1", p1);
		config.set("gamelevels."+name+".p2", p2);
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
	}
	
}
