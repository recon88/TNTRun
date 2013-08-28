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
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Rewards {

	private List<ItemStack> rewards = new ArrayList<ItemStack>();
	
	public void setRewards(ItemStack[] rewards)
	{
		this.rewards.clear();
		for (ItemStack reward : rewards)
		{
			if (reward != null)
			{
				this.rewards.add(reward);
			}
		}
	}
	
	
	protected void rewardPlayer(Player player)
	{
		for (ItemStack reward : rewards)
		{
			if (player.getInventory().firstEmpty() != -1)
			{
				player.getInventory().addItem(reward);
			}
			else
			{
				player.getWorld().dropItemNaturally(player.getLocation(), reward);
			}
		}
	}
	
	
	protected void saveToConfig(FileConfiguration config)
	{
		int rc = 1;
		for (ItemStack reward : rewards)
		{
			config.set("rewards."+rc, reward);
			rc++;
		}
	}
	
	protected void loadFromConfig(FileConfiguration config)
	{
		rewards.clear();
		ConfigurationSection cs = config.getConfigurationSection("rewards");
		if (cs != null)
		{
			for (String key : cs.getKeys(false))
			{
				rewards.add(config.getItemStack("rewards."+key));
			}
		}
	}
	
}
