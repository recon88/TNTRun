package tntrun.arena;

import java.util.HashSet;

import org.bukkit.Location;

public class Arena {

	private Location p1 = null;
	private Location p2 = null;
	
	private HashSet<GameLevel> gamelevels = new HashSet<GameLevel>();
	private LooseLevel looselevel = new LooseLevel();
	
}
