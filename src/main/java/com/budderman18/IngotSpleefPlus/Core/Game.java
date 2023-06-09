package com.budderman18.IngotSpleefPlus.Core;

import com.budderman18.IngotMinigamesAPI.Core.Data.ArenaStatus;
import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotMinigamesAPI.Core.Data.IngotPlayer;
import com.budderman18.IngotMinigamesAPI.Core.Data.Leaderboard;
import com.budderman18.IngotMinigamesAPI.Core.Data.Spawn;
import com.budderman18.IngotMinigamesAPI.Core.Data.Team;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.BossbarHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.ChatHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.ScoreboardHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TablistHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TimerHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TitleHandler;
import com.budderman18.IngotMinigamesAPI.Core.MissingBukkitMethods;
import com.budderman18.IngotSpleefPlus.Main;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * This class handles game logic
 * Make one of these for every arena
 *
 */
public class Game {
    //plugin
    private static final Plugin plugin = Main.getInstance();
    //files
    private static final String ROOT = "";
    private static FileConfiguration config = FileManager.getCustomData(plugin, "config", ROOT);
    private static FileConfiguration language = FileManager.getCustomData(plugin, "language", ROOT);
    //lobby vars
    private byte currentPlayers = 0;
    private byte tempPlayers = 0;
    private byte alivePlayers = 0;
    private byte maxPlayers = 0;
    private int taskNumber = 0;
    private float time = 0;
    private float lastTime = 0;
    private double barSize = 1;
    private boolean gameWaiting = false;
    private boolean gameStarted = false;
    private SPArena arena = null;
    private BossBar bossbar = null;
    private int index = 0;
    private ArrayList<SPPlayer> players = new ArrayList<>();
    private ArrayList<SPPlayer> spectatingplayers = new ArrayList<>();
    private static int trueIndex = 0;
    private static ArrayList<Game> games = new ArrayList<>();
    //language
    private static String gameWinnerMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Winner-Message")  + "");
    private static String gameDrawMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Draw-Message")  + "");
    /**
     *
     * This constructor sets up a game instance for an arena
     *
     * @param arenaa the arena to attach to
     */
    public Game(SPArena arenaa) {
        //check if arena isnt null
        if (arenaa != null) {
            //set data
            this.arena = arenaa;
            this.maxPlayers = this.arena.getArenaEquivelent().getMaxPlayers();
            this.index = trueIndex;
            trueIndex++;
        }
        //add game
        games.add(this);
    }
    /**
     * 
     * This method reloads the config file
     * 
     */
    public static void reload() {
        config = FileManager.getCustomData(plugin, "config", ROOT);
        language = FileManager.getCustomData(plugin, "language", ROOT);
        //messages
        gameWinnerMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Winner-Message")  + "");
        gameDrawMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Draw-Message")  + "");
    }
    /**
     * 
     * This method updated an existing lobby to a new arena
     * 
     * @param arenaa the arena to set to 
     */
    public void updateArena(SPArena arenaa) {
        //check if arena isnt null
        if (arenaa != null) {
            //set data
            this.arena = arenaa;
            this.maxPlayers = this.arena.getArenaEquivelent().getMaxPlayers();
        }
    }
    /**
     * 
     * This method gets the bossbar for the game
     * 
     * @return the bossbar
     */
    public BossBar getBossBar() {
        //return selection
        if (games.contains(this)) {
            return games.get(this.index).bossbar;
        }
        //return instance list
        return this.bossbar;
    }
    /**
     * 
     * This method gets the list of players in the game
     * 
     * @return the player list
     */
    public ArrayList<SPPlayer> getPlayers() {
        //return selection
        if (games.contains(this)) {
            return games.get(this.index).players;
        }
        //return instance list
        return this.players;
    }
    /**
     * 
     * This method adds a player to the game.
     * 
     * @param iplayer the player to add
     * @param useTeam true to add to team
     */
    public void joinGame(SPPlayer iplayer, boolean useTeam) {
        //local vars
        Player player = Bukkit.getPlayer(iplayer.getUsername());
        Runnable invclear = null;
        //add player
        this.players.add(iplayer);
        this.currentPlayers++;
        this.arena.setCurrentPlayers(this.currentPlayers);
        this.alivePlayers++;
        //set iplayer vars
        iplayer.getIngotPlayerEquivelent().setInGame(true);
        iplayer.getIngotPlayerEquivelent().setIsPlaying(true);
        iplayer.getIngotPlayerEquivelent().setIsAlive(true);
        iplayer.getIngotPlayerEquivelent().setGame(this.arena.getArenaEquivelent().getName());
        //check if bossbar is enabled
        if (config.getBoolean("Bossbar.enable") == true) {
            //set bossbar
            this.bossbar = BossbarHandler.setBarTitle(player, config.getString("Bossbar.title"), this.bossbar);
            BossbarHandler.setBarColor(player, config.getString("Bossbar.color"), this.bossbar);
            BossbarHandler.setBarSize(player, 1, this.bossbar);
            BossbarHandler.displayBar(true, this.bossbar);
        }
        //check if tablist is enabled
        if (config.getBoolean("Tablist.enable") == true) {
            //set header and footer
            TablistHandler.setHeader(player, config.getString("Tablist.header"));
            TablistHandler.setFooter(player, config.getString("Tablist.footer"));
            //check if hiding
            if (config.getBoolean("Tablist.hidePlayers") == true) {
                TablistHandler.removePlayers(this.arena, plugin);
            }
        }
        //check if titles are enabled
        if (config.getBoolean("Title.enable") == true) {
            //set title and actionbar
            TitleHandler.setTitle(player, config.getString("Title.InGameStart.title"), config.getString("Title.InGameStart.subtitle"), config.getInt("Title.InGameStart.fadein"), config.getInt("Title.InGameStart.length"), config.getInt("Title.InGameStart.fadeout"));
            TitleHandler.setActionBar(player, config.getString("Title.InGameStart.actionbar"));
        }
        //join team
        if (useTeam == true) {
            //cycle through teams
            for (Team key : this.arena.getArenaEquivelent().getTeams()) {
                //check if team has room
                if (key.getMembers().size() != key.getMaxSize()) {
                    //add to team
                    key.addPlayer(iplayer.getIngotPlayerEquivelent());
                }
            }
        }
        //clear inventory and run game()
        invclear = () -> {
            iplayer.getIngotPlayerEquivelent().clearInventory(true, true, true);
        };
        TimerHandler.runTimer(plugin, 0, 1, invclear, false, false);
        if (this.currentPlayers == 1) {
            game();
        }
    }
    /**
     * 
     * This method removes a player from the game.
     * 
     * @param iplayer the player to remove
     * @param displayTitle true to display titles
     * @param addLoss weather or not to add a loss to the player
     * @param useInventory true to manage inventories
     * @param gamePlayed true to game played
     */
    public void leaveGame(SPPlayer iplayer, boolean displayTitle, boolean addLoss, boolean useInventory, boolean gamePlayed) {
        //local vars
        Location exitLoc = new Location(Bukkit.getWorld(this.arena.getArenaEquivelent().getExitWorld()), this.arena.getArenaEquivelent().getExit()[0], this.arena.getArenaEquivelent().getExit()[1], this.arena.getArenaEquivelent().getExit()[2], (float) this.arena.getArenaEquivelent().getExit()[3], (float) this.arena.getArenaEquivelent().getExit()[4]); 
        Player player = Bukkit.getPlayer(iplayer.getUsername());
        int losses = iplayer.getIngotPlayerEquivelent().getLosses();
        int score = iplayer.getIngotPlayerEquivelent().getScore();
        int gamesPlayed = iplayer.getIngotPlayerEquivelent().getGamesPlayed();
        //remove player
        this.players.remove(iplayer);
        this.currentPlayers--;
        this.arena.setCurrentPlayers(this.currentPlayers);
        this.alivePlayers--;
        //check if player isnt alive
        if (iplayer.getIngotPlayerEquivelent().getIsAlive() == false) {
            //back to adventure
            player.setGameMode(GameMode.SURVIVAL);
        }
        //set iplayer vars
        iplayer.getIngotPlayerEquivelent().setInGame(false);
        iplayer.getIngotPlayerEquivelent().setIsPlaying(false);
        iplayer.setIsFrozen(false);
        iplayer.getIngotPlayerEquivelent().setIsAlive(true);
        iplayer.setBlocksBroken((short) 0);
        //check if adding a loss
        if (addLoss == true) {
            //add loss and set new score
            losses++;
            score += config.getInt("Score.loss");
            iplayer.getIngotPlayerEquivelent().setLosses(losses);
            iplayer.getIngotPlayerEquivelent().setScore(score);
        }
        //set game
        iplayer.getIngotPlayerEquivelent().setGame(null);
        //teleport to exit
        player.teleport(exitLoc);
        //check if tablist is enabled
        if (config.getBoolean("Tablist.enable") == true) {
            //reset tablist
            TablistHandler.reset(player);
        }
        //check if scoreboard is enabled
        if (config.getBoolean("Scoreboard.enable") == true) {
            //clear scoreboard
            ScoreboardHandler.clearScoreboard(player);
        }
        //check if title is enabled
        if (config.getBoolean("Title.enable") == true && displayTitle == true) {
            //set title and actionbar
            TitleHandler.setTitle(player, config.getString("Title.Leave.title"), config.getString("Title.Leave.subtitle"), config.getInt("Title.Leave.fadein"), config.getInt("Title.Leave.length"), config.getInt("Title.Leave.fadeout"));
            TitleHandler.setActionBar(player, config.getString("Title.Leave.actionbar"));
        }
        //check if bossbar is enabled
        if (config.getBoolean("Bossbar.enable") == true) {
            //clear bossbar
            BossbarHandler.clearBar(player, this.bossbar);
            BossbarHandler.displayBar(false, this.bossbar);
        }
        //leave team
        if (iplayer.getIngotPlayerEquivelent().getTeam() != null) {
            iplayer.getIngotPlayerEquivelent().getTeam().removePlayer(iplayer);
        }
        if (useInventory == true) {
            iplayer.getIngotPlayerEquivelent().applyInventory(true, true, true);
        }
        if (gamePlayed == true) {
            gamesPlayed++;
            iplayer.getIngotPlayerEquivelent().setGamesPlayed(gamesPlayed);
            score = score += config.getInt("Score.gamePlayed");
            iplayer.getIngotPlayerEquivelent().setScore(score);
            //check if adding to leaderboards
            if (iplayer.getIngotPlayerEquivelent().getGamesPlayed() == config.getInt("Leaderboard.min-games")) {
                for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                    key.addPlayer(iplayer.getIngotPlayerEquivelent());
                }
            }
            iplayer.saveToFile();
        }
    }
    /**
     * 
     * This method joins a player as a spectator
     * 
     * @param iplayer the player to spectate
     */
    public void joinAsSpectator(SPPlayer iplayer) {
        //local vars
        Player player = Bukkit.getPlayer(iplayer.getUsername());
        //add player
        this.spectatingplayers.add(iplayer);
        //teleport
        player.teleport(new Location(Bukkit.getWorld(this.arena.getArenaEquivelent().getWorld()), this.arena.getArenaEquivelent().getSpectatorPos()[0],  this.arena.getArenaEquivelent().getSpectatorPos()[1], this.arena.getArenaEquivelent().getSpectatorPos()[2], (float) this.arena.getArenaEquivelent().getSpectatorPos()[3], (float) this.arena.getArenaEquivelent().getSpectatorPos()[4]));
        player.setGameMode(GameMode.SPECTATOR);
        //de to weird gamemode bugs, we need to change gamemode again
        TimerHandler.runTimer(plugin, 0, 1, () -> {player.setGameMode(GameMode.SPECTATOR);}, false, false);
        //set iplayer vars
        iplayer.getIngotPlayerEquivelent().setInGame(true);
        iplayer.getIngotPlayerEquivelent().setIsAlive(false);
        iplayer.getIngotPlayerEquivelent().setGame(this.arena.getArenaEquivelent().getName());
    }
    /**
     * 
     * This method joins a player as a spectator
     * 
     * @param iplayer the player to spectate
     */
    public void leaveAsSpectator(SPPlayer iplayer) {
        //local vars
        Player player = Bukkit.getPlayer(iplayer.getUsername());
        //add player
        this.spectatingplayers.remove(iplayer);
        //teleport
        player.teleport(new Location(Bukkit.getWorld(this.arena.getArenaEquivelent().getExitWorld()), this.arena.getArenaEquivelent().getExit()[0],  this.arena.getArenaEquivelent().getExit()[1], this.arena.getArenaEquivelent().getExit()[2], (float) this.arena.getArenaEquivelent().getExit()[3], (float) this.arena.getArenaEquivelent().getExit()[4]));
        player.setGameMode(GameMode.ADVENTURE);
        //set iplayer vars
        iplayer.getIngotPlayerEquivelent().setInGame(false);
        iplayer.getIngotPlayerEquivelent().setGame(null);
    }
    /**
     *
     * This method selects a game based of of its arena
     *
     * @param arenaa the arena to obtain its game from
     * @return the game object
     */
    public static Game selectGame(SPArena arenaa) {
        //cycle through games
        for (Game key : games) {
            //check if desired arena then return
            if (key.arena == arenaa) {
                return key;
            }
        }
        //log error
        if (config.getBoolean("enable-debug-mode") == true) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, "COULD NOT OBTAIN THE GAME FOR ARENA " + arenaa.getName() + '!');
        }
        return null;
    }
    /**
     *
     * This method runs all game logic
     * It is automatically re-ran
     * 
     */
    private void game() { 
        //run this at the end
        Runnable endTimer = () -> {
            //local vars
            Player player = null;
            short lastBroken = 0;
            short broken = 0;
            int wins = 0;
            int score = 0;
            String message = null;
            SPPlayer keyy = null;
            SPPlayer winner = null;
            boolean tieChecked = false;
            //cycle through all players
            for (SPPlayer key : this.players) {
                try {
                    //get player
                    player = Bukkit.getPlayer(key.getUsername());
                    if (config.getBoolean("DoubleJump.enable") == true) {
                        player.setAllowFlight(false);
                    }
                    //check if player isnt alive
                    if (key.getIngotPlayerEquivelent().getIsAlive() == false) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                    //check if title is enabled
                    if (config.getBoolean("Title.enable") == true) {
                        //set title and actionbar
                        TitleHandler.setTitle(player, config.getString("Title.End.title"), config.getString("Title.End.subtitle"), config.getInt("Title.End.fadein"), config.getInt("Title.End.length"), config.getInt("Title.End.fadeout"));
                        TitleHandler.setActionBar(player, config.getString("Title.End.actionbar"));
                    }
                } 
                catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                    //check for debug mode
                    if (config.getBoolean("enable-debug-mode") == true) {
                        //log error
                        Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, "COULD NOT RETRIVE PLAYER " + key.getUsername() + '!');
                    }
                }
            }
            //check if tiebreaker or winner is needed
            if (tieChecked == false && this.alivePlayers > 1) {
                //cycle though players
                for (byte i=0; i < this.players.size(); i++) {
                    //get player
                    keyy = this.players.get(i);
                    //check if player is alive
                    if (keyy.getIngotPlayerEquivelent().getIsAlive() == true) {
                        //get blocks broken
                        broken = keyy.getBlocksBroken();
                    }
                    //check if this player has more broken blocks than the last
                    if (broken > lastBroken) {
                        //set winner
                        winner = keyy;
                    }
                    lastBroken = broken;
                }
                //check if winner isnt null
                if (winner != null) {
                    message = "&6";
                    for (IngotPlayer key : winner.getIngotPlayerEquivelent().getTeam().getMembers()) {
                        //set message
                        message = message.concat(key.getUsername() + " & ");
                        //update wins and score
                        wins = key.getWins();
                        score = key.getScore();
                        wins++;
                        score += config.getInt("Score.win");
                        key.setWins(wins);
                        key.setScore(score);
                    }
                    message = message.substring(0, message.length()-3);
                    message = message.concat(gameWinnerMessage);
                    //sent message and stop timers
                    ChatHandler.sendMessageToAll(ChatHandler.convertMessage(null, message, config.getString("Chat.format")), true, true, this.arena.getArenaEquivelent(), plugin);
                    TimerHandler.cancelTimer(this.taskNumber);
                }
                //run if a tie
                else {
                    //set message
                    message = gameDrawMessage;
                    //send message and cancel timer
                    ChatHandler.sendMessageToAll(ChatHandler.convertMessage(null, message, config.getString("Chat.format")), true, true, this.arena.getArenaEquivelent(), plugin);
                    TimerHandler.cancelTimer(this.taskNumber);
                }
                tieChecked = true;
                for (SPPlayer key : this.players) {
                    if (key != winner) {
                        key.getIngotPlayerEquivelent().setIsAlive(false);
                    }
                }
            }
            //check if not tied
            else if (tieChecked == false && this.alivePlayers <= 1) {
                //cycle though players
                for (SPPlayer key : this.players) {
                    //check if player is alive
                    if (key.getIngotPlayerEquivelent().getIsAlive() == true) {
                        winner = key;
                        //set message
                        message = "&6" + winner.getUsername() + gameWinnerMessage;
                        //update wins and score
                        wins = winner.getIngotPlayerEquivelent().getWins();
                        score = winner.getIngotPlayerEquivelent().getScore();
                        wins++;
                        score += config.getInt("Score.win");
                        winner.getIngotPlayerEquivelent().setWins(wins);
                        winner.getIngotPlayerEquivelent().setScore(score);
                        //sent message and stop timers
                        ChatHandler.sendMessageToAll(ChatHandler.convertMessage(null, message, config.getString("Chat.format")), true, true, this.arena.getArenaEquivelent(), plugin);
                        TimerHandler.cancelTimer(this.taskNumber);
                        tieChecked = true;
                    }
                }
            }
            //cycle through all players
            for (byte i = (byte) (this.players.size()-1); i >= 0; i--) {
                //force leave
                //check if player is dead
                if (this.players.get(i).getIngotPlayerEquivelent().getIsAlive() == false) {
                    //force leave
                    this.leaveGame(SPPlayer.selectPlayer(this.players.get(i).getIngotPlayerEquivelent().getUsername()), false, true, config.getBoolean("enable-inventories"), true);
                }
                //run to force winner to leave
                else {
                    this.leaveGame(SPPlayer.selectPlayer(this.players.get(i).getIngotPlayerEquivelent().getUsername()), true, false, config.getBoolean("enable-inventories"), true);
                }
            }
            //set arena as inactive
            this.arena.getArenaEquivelent().setStatus(ArenaStatus.WAITING);
            this.barSize = 1.0;
            this.currentPlayers = 0;
            this.arena.getArenaEquivelent().setCurrentPlayers(this.currentPlayers);
            this.players.clear();
            this.tempPlayers = 0;
            this.alivePlayers = 0;
            //update leaderboards
            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                //update leaderboard
                key.setPlayers(SPPlayer.getInstances(plugin));
                key.organizeLeaderboard(true);
                key.killHologram(false);
                try {
                    key.summonHologram(config.getString("Leaderboard.header"), config.getString("Leaderboard.format"), config.getString("Leaderboard.footer"), true);
                }
                catch (NullPointerException ex) {}
            }
        };
        //run for active game logic
        Runnable startTimer = () -> {
            //local vars
            int start = this.arena.getArenaEquivelent().getGameLengthTime();
            int end = 0;
            String[] item = new String[1];
            String type = "";
            Player player = null;
            try {
                //check if game hasnt started
                if (this.gameStarted == false) {
                    //cycle through all players
                    for (SPPlayer keys : this.players) {
                        //unfreeze player
                        player = Bukkit.getPlayer(keys.getUsername());
                        if (config.getBoolean("DoubleJump.enable") == true) {
                            player.setAllowFlight(true);
                        }
                        keys.setIsFrozen(false);
                        //check if titles are enabled
                        if (config.getBoolean("Title.enable") == true) {
                            //set title and actionbar
                            TitleHandler.setTitle(player, config.getString("Title.InGameRelease.title"), config.getString("Title.InGameRelease.subtitle"), config.getInt("Title.InGameRelease.fadein"), config.getInt("Title.InGameRelease.length"), config.getInt("Title.InGameRelease.fadeout"));
                            TitleHandler.setActionBar(player, config.getString("Title.InGameRelease.actionbar"));
                        }
                        //check for spleef
                        if (this.arena.getType() == GameType.SPLEEF) {
                            type = "spleef";
                        }
                        //check for spleef
                        else if (this.arena.getType() == GameType.SPLEGG) {
                            type = "splegg";
                        }
                        //check for spleef
                        else if (this.arena.getType() == GameType.TNTSPLEEF) {
                            type = "tntspleef";
                        }
                        //cycle through item slots
                        for (byte i = 0; i < 40; i++) {
                            //check if item exists
                            if (config.getString("items." + type + ".slot" + i) != null) {
                                item[0] = "0§".concat(config.getString("items." + type + ".slot" + i));
                                player.getInventory().setItem(i, MissingBukkitMethods.convertToInventory(item, false, null, null, 0, 0, 0)[0]);
                            }
                        }
                        //cycle through effects
                        for (String keyss : config.getStringList("items." + type + ".effects")) {
                            try {
                                //add efect
                                player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(keyss.split("§")[0].toUpperCase()), Integer.parseInt(keyss.split("§")[1]), Integer.parseInt(keyss.split("§")[2])));
                            } 
                            catch (IllegalArgumentException ex) {
                                //check for debug mode
                                if (config.getBoolean("enable-debug-mode") == true) {
                                    //log error
                                    Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, "CANNOT APPLY EFFECT " + keyss.split("§")[0].toUpperCase() + " BECAUSE THE TYPE IS NULL!");
                                }
                            }
                        }
                    }
                    //clear spawns
                    for (Spawn keys : Spawn.getInstances(plugin)) {
                        //set occupied
                        keys.setIsOccupied(false);
                    }
                    //set as started
                    this.gameStarted = true;
                    this.taskNumber = TimerHandler.runTimer(plugin, start, end, endTimer, false, false);
                }
            } 
            catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                //check for debug mode
                if (config.getBoolean("enable-debug-mode") == true) {
                    //log error
                    Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, "COULD NOT START GAME!");
                }
            }
        };
        //run for waiting logic
        Runnable action = () -> {
            //runnable vars
            int startTime = this.arena.getArenaEquivelent().getGameWaitTime();
            int start = this.arena.getArenaEquivelent().getGameLengthTime();
            int end = 0;
            byte teamsLeft = 0;
            Player player = null;
            String blankString = " ";
            String lineString = null;
            //cycle through players
            for (SPPlayer key : this.players) {
                try {
                    //get player
                    player = Bukkit.getPlayer(key.getIngotPlayerEquivelent().getUsername());
                    //check if not equal to the players alive or the time has changed
                    if (this.tempPlayers != this.alivePlayers || (short) this.time != (short) this.lastTime) {
                        //check if tablist removal is enabled
                        if (config.getBoolean("Tablist.enable") == true && config.getBoolean("Tablist.hidePlayersNotInGame") == true) {
                            TablistHandler.removePlayers(this.arena, plugin);
                        }
                        //check if scoreboards are enabled
                        if (config.getBoolean("Scoreboard.enable") == true) {
                            //clear scoreboard
                            ScoreboardHandler.clearScoreboard(player);
                            ScoreboardHandler.setTitle(player, ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.title") + ""), config.getBoolean("Scoreboard.importMainScoreboard"));
                            //cycle though scoreboard limit
                            for (byte i = 0; i < config.getInt("Scoreboard.maxLines"); i++) {
                                //check if line is not null
                                if (config.getString("Scoreboard.line" + i) != null) {
                                    //set lines
                                    lineString = config.getString("Scoreboard.line" + i);
                                    lineString = lineString.replaceAll("%currentplayers%", Byte.toString(this.currentPlayers));
                                    lineString = lineString.replaceAll("%aliveplayers%", Byte.toString(this.alivePlayers));
                                    lineString = lineString.replaceAll("%maxplayers%", Byte.toString(this.maxPlayers));
                                    lineString = lineString.replaceAll("%time%", Short.toString((short) this.time));
                                    lineString = lineString.replaceAll("%timeinvert%", Short.toString((short) (this.arena.getArenaEquivelent().getGameLengthTime() - this.time)));
                                    ScoreboardHandler.setLine(player, i, ChatColor.translateAlternateColorCodes('&', lineString));
                                } 
                                //run if line is null
                                else {
                                    //add a spaace to null line
                                    blankString = blankString.concat(" ");
                                    //set line
                                    ScoreboardHandler.setLine(player, i, blankString);
                                }
                            }
                            if (config.getBoolean("Scoreboard.importMainScoreboard") == true) {
                                ScoreboardHandler.updateScoreboard(player);
                            }
                            lineString = "";
                            blankString = " ";
                        }
                    }
                    if (this.tempPlayers != this.alivePlayers) {
                        //check if game is no longer eaiting
                        if (this.gameWaiting == false) {
                            //cycle through all players
                            for (SPPlayer keys : this.players) {
                                //freeze player
                                player = Bukkit.getPlayer(keys.getIngotPlayerEquivelent().getUsername());
                                keys.setIsFrozen(true);
                                //check if titles are enabled
                                if (config.getBoolean("Title.enable")) {
                                    //set title and actionbar
                                    TitleHandler.setTitle(player, config.getString("Title.InGameStart.title") + startTime + " seconds", config.getString("Title.InGameStart.subtitle"), config.getInt("Title.InGameStart.fadein"), config.getInt("Title.InGameStart.length"), config.getInt("Title.InGameStart.fadeout"));
                                    TitleHandler.setActionBar(player, config.getString("Title.InGameStart.actionbar"));
                                }
                            }
                            //run timer and initiate wait
                            TimerHandler.runTimer(plugin, startTime, end, startTimer, false, false);
                            this.gameWaiting = true;
                        }
                    }
                } 
                catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                    //check for debug mode
                    if (config.getBoolean("enable-debug-mode") == true) {
                        //log error
                        Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, "COULD NOT RETRIVE PLAYER " + key.getUsername() + '!');
                    }
                }
            }
            //cycle through all players
            for (SPPlayer key : this.players) {
                //check if player has died
                if (key.getIngotPlayerEquivelent().getIsAlive() == false && key.getIngotPlayerEquivelent().getIsPlaying() == true) {
                    //update alive count
                    this.alivePlayers--;
                    key.getIngotPlayerEquivelent().setIsPlaying(false);
                }
            }
            //cycle through teams
            for (Team key : this.arena.getArenaEquivelent().getTeams()) {
                //check if not empty
                if (!key.getMembers().isEmpty()) {
                    for (IngotPlayer keys : key.getMembers()) {
                        if (keys.getIsAlive() == true) {
                            teamsLeft++;
                            break;
                        }
                    }
                }
            }
            //check if only 1 survivor or out of time
            if (teamsLeft == 1 || (short) this.time >= this.arena.getArenaEquivelent().getGameLengthTime()) {
                endTimer.run(); 
            }
            //check if bossbar is enabled
            if (this.gameStarted == true && config.getBoolean("Bossbar.enable") == true) {
                //get size
                this.barSize -= (((double) 1 / 5) / start);
                //check for invalid bossbar sixe
                if (this.barSize < 0) {
                    this.barSize = 0;
                }
                //cycle through all players
                for (SPPlayer key : this.players) {
                    //set bossbar size
                    player = Bukkit.getPlayer(key.getUsername());
                    BossbarHandler.setBarSize(player, this.barSize, this.bossbar);
                }
            }
            //check if playercount needs increasing
            if (this.tempPlayers < this.currentPlayers || this.tempPlayers < this.alivePlayers) {
                this.tempPlayers++;
            }
            //check if playercount needs decreasing
            if (this.tempPlayers > this.currentPlayers || this.tempPlayers > this.alivePlayers) {
                this.tempPlayers--;
            }
            //set players and re-run game
            this.arena.getArenaEquivelent().setCurrentPlayers(this.currentPlayers);
            this.lastTime = time;
            this.time+=0.2;
            game();
        };
        //check if game needs to re-run
        if (this.currentPlayers != 0) {
            TimerHandler.runTimer(plugin, 0, 4, action, true, false);
        }
        else {
            //reset game instance
            this.currentPlayers = 0;
            this.arena.getArenaEquivelent().setCurrentPlayers(this.currentPlayers);
            this.tempPlayers = 0;
            this.alivePlayers = 0;
            this.time = 0;
            this.barSize = 1;
            this.gameStarted = false;
            this.gameWaiting = false;
            this.players = new ArrayList<>();
            this.bossbar = null;
            //check if arena needs regenerating
            if (config.getBoolean("regenerate-arena-after-finishing") == true) {
                this.arena.getArenaEquivelent().loadArenaSchematic(true, true, true, true);
            }
            else {
                try {
                    this.arena.getArenaEquivelent().loadArenaSchematic(false, false, false, true);
                }
                catch (NullPointerException ex) {}
            }
        }    
    }
}
