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

package tntrun.datahandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import tntrun.arena.Arena;

public class PlayerDataStore {

	private HashMap<String, Arena> plingame = new HashMap<String, Arena>();
	private HashMap<Arena, HashSet<String>> arenaplayers = new HashMap<Arena, HashSet<String>>();
	public Arena getPlayerArena(String player)
	{
		return plingame.get(player);
	}
	public void setPlayerArena(String player, Arena arena)
	{
		plingame.put(player, arena);
		arenaplayers.get(arena).add(player);
	}
	public void removePlayerFromArena(String player)
	{
		Arena arena = plingame.get(player);
		arenaplayers.get(arena).remove(player);
		plingame.remove(player);
	}
	public HashSet<String> getArenaPlayers(Arena arena)
	{
		return arenaplayers.get(arena);
	}
	public void putArenaInHashMap(Arena arena)
	{
		arenaplayers.put(arena, new HashSet<String>());
	}
	public void removeArenaFromHashMap(Arena arena)
	{
		arenaplayers.remove(arena);
	}
	public Set<Arena> getArenas()
	{
		return arenaplayers.keySet();
	}
	
	private HashMap<String, ItemStack[]> plinv = new HashMap<String, ItemStack[]>();
	private HashMap<String, ItemStack[]> plarmor = new HashMap<String, ItemStack[]>();
	private HashMap<String, Location> plloc = new HashMap<String, Location>();
	public ItemStack[] getPlayerInventory(String player)
	{
		ItemStack[] inv = plinv.get(player);
		plinv.remove(player);
		return inv;
	}
	public void setPlayerInventory(String player)
	{
		plinv.put(player, Bukkit.getPlayerExact(player).getInventory().getContents());
	}
	public ItemStack[] getPlayerArmor(String player)
	{
		ItemStack[] armor = plarmor.get(player);
		plarmor.remove(player);
		return armor;
	}
	public void setPlayerArmor(String player)
	{
		plarmor.put(player, Bukkit.getPlayerExact(player).getInventory().getArmorContents());
	}
	public Location getPlayerLocation(String player)
	{
		Location loc = plloc.get(player);
		plloc.remove(player);
		return loc;
	}
	public void setPlayerLocation(String player)
	{
		plloc.put(player, Bukkit.getPlayerExact(player).getLocation());
	}
}
