package tntrun.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

public class LooseLevel {

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
	
	public void setLooseLocation(Location p1, Location p2, World w)
	{
		//zone points
		this.p1 = p1.toVector();
		this.p2 = p2.toVector();
		//fill area with water
		fillArea(w);
	}	
	private void fillArea(World w)
	{
		int y = p1.getBlockY();
		for (int x = p1.getBlockX(); x<=p2.getBlockX(); x++)
		{
			for (int z = p1.getBlockZ(); z<=p2.getBlockZ(); z++)
			{
				w.getBlockAt(x, y, z).setType(Material.WATER);
			}
		}
	}
	
	
	protected void saveToConfig(String arenaname, FileConfiguration config)
	{
		config.set(arenaname+".looselevel.p1", p1);
		config.set(arenaname+".looselevel.p2", p2);
	}
	
	protected void loadFromConfig(String arenaname, FileConfiguration config)
	{
		p1 = config.getVector(arenaname+".looselevel.p1", null);
		p2 = config.getVector(arenaname+".looselevel.p2", null);
	}
	
}
