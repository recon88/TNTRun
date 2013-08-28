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
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
	private HashMap<String, Integer> plhunger = new HashMap<String, Integer>();
	private HashMap<String, GameMode> plgamemode = new HashMap<String, GameMode>();
	public void storePlayerInventory(String player)
	{
		PlayerInventory pinv = Bukkit.getPlayerExact(player).getInventory();
		plinv.put(player, pinv.getContents());
		pinv.clear();
	}
	public void storePlayerArmor(String player)
	{
		PlayerInventory pinv = Bukkit.getPlayerExact(player).getInventory();
		plarmor.put(player, pinv.getArmorContents());
		pinv.setArmorContents(null);
	}
	public void storePlayerLocation(String player)
	{
		plloc.put(player, Bukkit.getPlayerExact(player).getLocation());
	}
	public void storePlayerHunger(String player)
	{
		plhunger.put(player, Bukkit.getPlayerExact(player).getFoodLevel());
		Bukkit.getPlayerExact(player).setFoodLevel(20);
	}
	public void storePlayerGameMode(String player)
	{
		plgamemode.put(player, Bukkit.getPlayerExact(player).getGameMode());
		Bukkit.getPlayerExact(player).setGameMode(GameMode.SURVIVAL);
	}
	public void restorePlayerInventory(String player)
	{
		Bukkit.getPlayerExact(player).getInventory().setContents(plinv.get(player));
		plinv.remove(player);
	}
	public void restorePlayerArmor(String player)
	{
		Bukkit.getPlayerExact(player).getInventory().setArmorContents(plarmor.get(player));
		plarmor.remove(player);
	}
	public void restorePlayerLocation(String player)
	{
		Bukkit.getPlayerExact(player).teleport(plloc.get(player));
		plloc.remove(player);
	}
	public void restorePlayerHunger(String player)
	{
		Bukkit.getPlayerExact(player).setFoodLevel(plhunger.get(player));
		plhunger.remove(player);
	}	
	public void restorePlayerGameMode(String player)
	{
		Bukkit.getPlayerExact(player).setGameMode(plgamemode.get(player));
		plgamemode.remove(player);
	}

}
