package tntrun.messages;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import tntrun.FormattingCodesParser;
import tntrun.TNTRun;
import tntrun.arena.Rewards;

public class Messages {
	
	private static File messageconfig;
	private static FileConfiguration config;
	
	private static List<String> worldsenabledformessages;
	
	public static String nopermission = "&4You don't have permission to do this";
	
	public static String teleporttolobby = "&6Teleported to lobby";
	
	public static String availablearenas = "&6Available arenas:&r ";
	public static String arenadisabled = "&6Arena is disabled";
	public static String arenarunning = "&6Arena already running"; 
	public static String arenadisabling = "&6Arena is disabling";
	
	public static String playerscount = "&6Current players count:&r ";
	public static String limitreached = "&6Slot limit reached.";
	public static String playerjoinedtoplayer = "&6You joined the arena";
	public static String playerjoinedtoothers = "&6Player {PLAYER} joined the arena";
	public static String playerlefttoplayer = "&6You left the arena";
	public static String playerlefttoothers = "&6Player {PLAYER} left the game";
	public static String playervotedforstart = "&6You voted for game start";
	public static String playeralreadyvotedforstart = "&6You already voted";
	public static String arenastarted = "&6Arena started. Time limit is {TIMELIMIT} seconds";
	public static String arenacountdown = "&6Arena starts in {COUNTDOWN} seconds";
	public static String arenatimeout = "&6Time is out.";
	public static String playerwontoplayer = "&6You won the game";
	public static String playerlosttoplayer = "&6You lost the game";
	public static String playerlosttoothers = "&6Player {PLAYER} lost the game";
	public static String playerwonbroadcast = "&9[TNTRun] &a{PLAYER}&r won the game on arena &c{ARENA}";
	public static String playerreward = "&9[TNTRun] &6You won &c{MONEYREWARD}";
	
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
	public static void sendMessage(Player player, String message, double reward) {
		if (!message.equals("")) {
			message = message.replace("{MONEYREWARD}", Rewards.getEconomy().format(reward));
			player.sendMessage(FormattingCodesParser.parseFormattingCodes(message));
		}
	}
	
	public static void broadsactMessage(String plname, String arena, String message)
	{
		if (!message.equals("")) {
			message = message.replace("{PLAYER}", plname);
			message = message.replace("{ARENA}", arena);
			for (String worlds : worldsenabledformessages) {
				World world = Bukkit.getServer().getWorld(worlds);
				if (world == null) {
					continue;
				}
				for (Player players : world.getPlayers()) {
					players.sendMessage(FormattingCodesParser.parseFormattingCodes(message));
				}
			}
		}
	}

	public static void loadMessages(TNTRun plugin)
	{
		messageconfig = new File(plugin.getDataFolder(),"configmsg.yml");
		config = YamlConfiguration.loadConfiguration(messageconfig);
		worldsenabledformessages = config.getStringList("worldsenabledformessages");
		nopermission = config.getString("nopermission",nopermission);
		teleporttolobby = config.getString("teleporttolobby",teleporttolobby);
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
		playerreward = config.getString("playerreward","playerreward");
		saveMessages(messageconfig);
	}
	private static void saveMessages(File messageconfig)
	{
		FileConfiguration config = new YamlConfiguration();
		List<String> worlds = new ArrayList<String>();
		worlds.add("world");
		worlds.add("example");
		config.set("worldsenabledformessages", worlds);
		config.set("nopermission",nopermission);
		config.set("teleporttolobby",teleporttolobby);
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
