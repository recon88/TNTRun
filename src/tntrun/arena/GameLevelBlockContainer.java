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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

public class GameLevelBlockContainer {

	private HashMap<Integer, Integer> topblocksid = new HashMap<Integer, Integer>();
	public Collection<Integer> getTopBlocksID()
	{
		return topblocksid.values();
	}
	private HashMap<Integer, Byte> topblocksdata = new HashMap<Integer, Byte>();
	public Collection<Byte> getTopBlocksData()
	{
		return topblocksdata.values();
	}
	
	private HashMap<Integer, Integer> bottomblocksid = new HashMap<Integer, Integer>();
	public Collection<Integer> getBottomBlocksID()
	{
		return topblocksid.values();
	}
	private HashMap<Integer, Byte> bottomblocksdata = new HashMap<Integer, Byte>();
	public Collection<Byte> getBottomBlocksData()
	{
		return topblocksdata.values();
	}
	
	@SuppressWarnings("deprecation")
	protected void setCustomGameLevel(World w, Vector p1, Vector p2)
	{
		unsetCustomGameLevel();
		int y = p1.getBlockY();
		int blockcounter = 1;
		for (int x = p1.getBlockX()+1; x<p2.getBlockX(); x++)
		{
			for (int z = p1.getBlockZ()+1; z<p2.getBlockZ(); z++)
			{
				Block b = w.getBlockAt(x, y, z);
				bottomblocksid.put(blockcounter, b.getTypeId());
				bottomblocksdata.put(blockcounter, b.getData());
				b = b.getRelative(BlockFace.UP);
				topblocksid.put(blockcounter, b.getTypeId());
				topblocksdata.put(blockcounter, b.getData());
				blockcounter++;
			}
		}
	}
	protected void unsetCustomGameLevel()
	{
		topblocksid.clear();
		topblocksdata.clear();
		bottomblocksid.clear();
		bottomblocksdata.clear();
	}
	
	@SuppressWarnings("deprecation")
	protected void fillCustomArea(World w, Vector p1, Vector p2)
	{
		int bcounter = 1;
		int y = p1.getBlockY();
		for (int x = p1.getBlockX()+1; x<p2.getBlockX(); x++)
		{
			for (int z = p1.getBlockZ()+1; z<p2.getBlockZ(); z++)
			{
				Block b = w.getBlockAt(x, y, z);
				if (b.getTypeId() != bottomblocksid.get(bcounter)) 
				{
					b.setTypeIdAndData(bottomblocksid.get(bcounter) , bottomblocksdata.get(bcounter) , true);
				}
				b = b.getRelative(BlockFace.UP);
				if (b.getTypeId() != topblocksid.get(bcounter)) 
				{
					b.setTypeIdAndData(topblocksid.get(bcounter) , topblocksdata.get(bcounter), true);
				}
				bcounter++;
			}
		}
	}
	
	
	
	protected boolean customGameLevelSet()
	{
		if (topblocksid.isEmpty() || bottomblocksid.isEmpty()) {return false;}
		return true;
	}
	
	
	
	
	protected void saveToConfig(String levelname, FileConfiguration config)
	{
		int size = topblocksid.size();
		List<String> topblocks = new ArrayList<String>();
		List<String> bottomblocks = new ArrayList<String>();
		for (int i = 1; i <= size; i++)
		{
			String topblockstring = topblocksid.get(i)+":"+topblocksdata.get(i);
			String bottomblockstring = bottomblocksid.get(i)+":"+bottomblocksdata.get(i);
			topblocks.add(topblockstring);
			bottomblocks.add(bottomblockstring);
		}
		config.set(levelname+".topblocks", topblocks);
		config.set(levelname+".bottomblocks", bottomblocks);
	}
	
	protected void loadFromConfig(String levelname, FileConfiguration config)
	{
		unsetCustomGameLevel();
		List<String> topblocks = config.getStringList(levelname+".topblocks");
		List<String> bottomblocks = config.getStringList(levelname+".bottomblocks");
		int bcounter = 1;
		for (String topblock : topblocks)
		{
			String[] topblockinfo = topblock.split("[:]");
			topblocksid.put(bcounter, Integer.valueOf(topblockinfo[0]));
			topblocksdata.put(bcounter, Byte.valueOf(topblockinfo[1]));
			bcounter++;
		}
		bcounter = 1;
		for (String bottomblock : bottomblocks)
		{
			String[] bottomblockinfo = bottomblock.split("[:]");
			bottomblocksid.put(bcounter, Integer.valueOf(bottomblockinfo[0]));
			bottomblocksdata.put(bcounter, Byte.valueOf(bottomblockinfo[1]));
			bcounter++;
		}
	}
	
	
}
