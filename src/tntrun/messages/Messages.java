package tntrun.messages;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import tntrun.FormattingCodesParser;

public class Messages {

	public static String nopermission = "&4You don't have permission to do this";
	
	public static String availablearenas = "&9Available arenas:&r ";
	public static String arenadisabled = "&9Arena is disabled";
	public static String arenarunning = "&9Arena already running"; 
	public static String arenadisabling = "&9Arena is disabling";
	
	public static String playerscount = "&9Current players count:&r ";
	public static String limitreached = "&9Slot limit reached.";
	public static String playerjoinedtoplayer = "&9You joined the arena";
	public static String playerjoinedtoothers = "&9Player {PLAYER} joined the arena";
	public static String playerlefttoplayer = "&9You left the arena";
	public static String playerlefttoothers = "&9Player {PLAYER} left the game";
	public static String playervotedforstart = "&9You voted for game start";
	public static String playeralreadyvotedforstart = "&9You already voted";
	public static String arenastarted = "&9Arena started. Time limit is {TIMELIMIT} seconds";
	public static String arenacountdown = "&9Arena starts in {COUNTDOWN} seconds";
	public static String arenatimeout = "&9Time is out.";
	public static String playerwontoplayer = "&9You won the game";
	public static String playerlosttoplayer = "&9You lost the game";
	public static String playerlosttoothers = "&9Player {Player} lost the game";
	public static String playerwonbroadcast = "&9[TNTRun] &a{PLAYER}&r won the game on arena &c{ARENA}";
	
	public static void sendMessage(Player player, String message) {
		if (!message.equals("")) {
			player.sendMessage(FormattingCodesParser.parseFormattingCodes(message));
		}
	}
	public static void sendMessage(Player player, String plname, String message) {
		if (!message.equals("")) {
			message = message.replace("{PLAYER}", plname);
			player.sendMessage(FormattingCodesParser.parseFormattingCodes(message));
		}
	}
	public static void sendMessage(Player player, String message, int c) {
		if (!message.equals("")) {
			message = message.replace("{TIMELIMIT}", String.valueOf(c));
			message = message.replace("{COUNTDOWN}", String.valueOf(c));
			player.sendMessage(FormattingCodesParser.parseFormattingCodes(message));
		}
	}
	
	public static void broadsactMessage(String plname, String arena, String message)
	{
		if (!message.equals("")) {
			message = message.replace("{PLAYER}", plname);
			message = message.replace("{ARENA}", arena);
			Bukkit.broadcastMessage(FormattingCodesParser.parseFormattingCodes(message));
		}
	}

	private static File messageconfig = new File("plugins/TNTRun/configmsg.yml");
	public static void loadMessages()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(messageconfig);
		nopermission = config.getString("nopermission",nopermission);
		availablearenas = config.getString("availablearenas",availablearenas);
		arenadisabled = config.getString("arenadisabled",arenadisabled);
		arenarunning = config.getString("arenarunning",arenarunning);
		arenadisabling = config.getString("arenadisabling",arenadisabling);
		playerscount = config.getString("playerscount",playerscount);
		limitreached = config.getString("limitreached",limitreached );
		playerjoinedtoplayer = config.getString("playerjoinedtoplayer",playerjoinedtoplayer);
		playerjoinedtoothers = config.getString("playerjoinedtoothers",playerjoinedtoothers);
		playerlefttoplayer = config.getString("playerlefttoplayer",playerlefttoplayer);
		playerlefttoothers = config.getString("playerlefttoothers",playerlefttoothers);
		playervotedforstart = config.getString("playervotedforstart",playervotedforstart);
		playeralreadyvotedforstart = config.getString("playeralreadyvotedforstart",playeralreadyvotedforstart);
		arenastarted = config.getString("arenastarted",arenastarted);
		arenacountdown = config.getString("arenacountdown",arenacountdown);
		arenatimeout = config.getString("arenatimeout",arenatimeout);
		playerwontoplayer = config.getString("playerwontoplayer",playerwontoplayer);
		playerlosttoplayer = config.getString("playerlosttoplayer",playerlosttoplayer);
		playerlosttoothers = config.getString("playerlosttoothers",playerlosttoothers);
		playerwonbroadcast = config.getString("playerwonbroadcast",playerwonbroadcast);
		saveMessages();
	}
	private static void saveMessages()
	{
		FileConfiguration config = new YamlConfiguration();
		config.set("nopermission",nopermission);
		config.set("availablearenas",availablearenas);
		config.set("arenadisabled",arenadisabled);
		config.set("arenarunning",arenarunning);
		config.set("arenadisabling",arenadisabling);
		config.set("playerscount",playerscount);
		config.set("limitreached",limitreached );
		config.set("playerjoinedtoplayer",playerjoinedtoplayer);
		config.set("playerjoinedtoothers",playerjoinedtoothers);
		config.set("playerlefttoplayer",playerlefttoplayer);
		config.set("playerlefttoothers",playerlefttoothers);
		config.set("playervotedforstart",playervotedforstart);
		config.set("playeralreadyvotedforstart",playeralreadyvotedforstart);
		config.set("arenastarted",arenastarted);
		config.set("arenacountdown",arenacountdown);
		config.set("arenatimeout",arenatimeout);
		config.set("playerwontoplayer",playerwontoplayer);
		config.set("playerlosttoplayer",playerlosttoplayer);
		config.set("playerlosttoothers",playerlosttoothers);
		config.set("playerwonbroadcast",playerwonbroadcast);
		try {
			config.save(messageconfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
