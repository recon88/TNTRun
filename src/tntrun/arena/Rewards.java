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
			if (rewards != null)
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
