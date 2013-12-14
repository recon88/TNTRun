package tntrun.signs.editor;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class SignInfo {

	private String worldname;
	private int x;
	private int y;
	private int z;
	public SignInfo(String worldname, int x, int y, int z)
	{
		this.worldname = worldname;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public SignInfo(Block block)
	{
		this.worldname = block.getWorld().getName();
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
	}
	
	public Block getBlock()
	{
		World world = Bukkit.getWorld(worldname);
		if (world != null)
		{
			return world.getBlockAt(x, y, z);
		}
		return null;
	}
	
	protected String getWorldName()
	{
		return worldname;
	}
	protected int getX()
	{
		return x;
	}
	protected int getY()
	{
		return y;
	}
	protected int getZ()
	{
		return z;
	}
	
}
