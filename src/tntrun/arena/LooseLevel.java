package tntrun.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class LooseLevel {

	private Vector p1;
	private Vector p2;
	
	protected boolean isLooseLocation(Location loc)
	{
		if (loc.toVector().isInAABB(p1, p2))
		{
			return true;
		}
		return false;
	};
	
	public void setLooseLocation(Location p1, Location p2)
	{
		//zone points
		this.p1 = p1.toVector();
		this.p2 = p2.toVector();
		//fill area with water
		int y = p1.getBlockY();
		World w = p1.getWorld();
		for (int x = p1.getBlockX(); x<=p2.getBlockX(); x++)
		{
			for (int z = p1.getBlockZ(); z<=p2.getBlockZ(); z++)
			{
				w.getBlockAt(x, y, z).setType(Material.WATER);
			}
		}
	}
	
	
	protected void saveToConfig()
	{
		
	}
	
	protected void loadFromConfig()
	{
		
	}
	
}
