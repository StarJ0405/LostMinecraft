package com.StarJ.LM.System;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardStore {
	private final ScoreboardManager sbm;
	private final Scoreboard sb;
	private final Objective health;

	public ScoreboardStore() {
		this.sbm = Bukkit.getScoreboardManager();
		this.sb = sbm.getMainScoreboard();
		this.health = sb.getObjective("health");
		if (health == null) {
			sb.registerNewObjective("health", Criteria.HEALTH, ChatColor.RED + "%");
			health.setDisplaySlot(DisplaySlot.BELOW_NAME);
			health.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}
	}

	public Scoreboard getScoreboard() {
		return sb;
	}
}
