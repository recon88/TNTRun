package tntrun;

import org.bukkit.ChatColor;

public class FormattingCodesParser {

		public static String parseFormattingCodes(String message) {
			message = message.replaceAll("&0", ChatColor.BLACK + "");
			message = message.replaceAll("&1", ChatColor.DARK_BLUE + "");
			message = message.replaceAll("&2", ChatColor.DARK_GREEN + "");
			message = message.replaceAll("&3", ChatColor.DARK_AQUA + "");
			message = message.replaceAll("&4", ChatColor.DARK_RED + "");
			message = message.replaceAll("&5", ChatColor.DARK_PURPLE + "");
			message = message.replaceAll("&6", ChatColor.GOLD + "");
			message = message.replaceAll("&7", ChatColor.GRAY + "");
			message = message.replaceAll("&8", ChatColor.DARK_GRAY + "");
			message = message.replaceAll("&9", ChatColor.BLUE + "");
			message = message.replaceAll("(?i)&a", ChatColor.GREEN + "");
			message = message.replaceAll("(?i)&b", ChatColor.AQUA + "");
			message = message.replaceAll("(?i)&c", ChatColor.RED + "");
			message = message.replaceAll("(?i)&d", ChatColor.LIGHT_PURPLE + "");
			message = message.replaceAll("(?i)&e", ChatColor.YELLOW + "");
			message = message.replaceAll("(?i)&f", ChatColor.WHITE + "");
			message = message.replaceAll("(?i)&l", ChatColor.BOLD+ "");
			message = message.replaceAll("(?i)&o", ChatColor.ITALIC+ "");
			message = message.replaceAll("(?i)&m", ChatColor.STRIKETHROUGH+ "");
			message = message.replaceAll("(?i)&n", ChatColor.UNDERLINE+ "");
			message = message.replaceAll("(?i)&k", ChatColor.MAGIC+ "");
			message = message.replaceAll("(?i)&r", ChatColor.RESET+ "");
			return message;
		}

}
