package com.budderman18.IngotSpleefPlus;

import com.budderman18.IngotMinigamesAPI.Core.Data.ArenaStatus;
import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotMinigamesAPI.Core.Data.IngotPlayer;
import com.budderman18.IngotMinigamesAPI.Core.Data.Leaderboard;
import com.budderman18.IngotMinigamesAPI.Core.Data.LeaderboardType;
import com.budderman18.IngotMinigamesAPI.Core.Data.Spawn;
import com.budderman18.IngotMinigamesAPI.Core.Data.Team;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.DeathMessageHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.DeathMessageType;
import com.budderman18.IngotSpleefPlus.Bukkit.Events;
import com.budderman18.IngotSpleefPlus.Bukkit.SPAdminCommand;
import com.budderman18.IngotSpleefPlus.Bukkit.SPCommand;
import com.budderman18.IngotSpleefPlus.Core.Game;
import com.budderman18.IngotSpleefPlus.Core.GameType;
import com.budderman18.IngotSpleefPlus.Core.Lobby;
import com.budderman18.IngotSpleefPlus.Core.SPArena;
import com.budderman18.IngotSpleefPlus.Core.SPPlayer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * This class enables and disables the plugin. 
 * It also imports commands and handles events. 
 * 
 */
public class Main extends JavaPlugin implements Listener { 
    //retrive plugin instance
    private static Main plugin = null;
    //global vars
    private final String ROOT = "";
    private final ConsoleCommandSender sender = getServer().getConsoleSender();
    /**
     *
     * This method retrieves the current plugin data
     *
     * @return the plugin
     */
    public static Main getInstance() {
        return plugin;
    }
    /**
     * 
     * This method loads all leaderboards and players
     * 
     */
    public static void loadPlayers() {
        //local vars
        Plugin pluginn = getInstance();
        FileConfiguration config = FileManager.getCustomData(pluginn, "config", "");
        FileConfiguration playerdata = FileManager.getCustomData(pluginn, "playerdata", "");
        FileConfiguration hologram = FileManager.getCustomData(pluginn, "hologram", "");
        Leaderboard board = null;
        ArrayList<String> boardsToCheck = new ArrayList<>();
        ArrayList<ArmorStand> hologramm = null;
        ArrayList<IngotPlayer> players = new ArrayList<>();
        ArrayList<SPPlayer> spplayers = new ArrayList<>();
        byte maxSize = (byte) config.getInt("Leaderboard.max-size");
        boolean wins = false;
        boolean losses = false;
        boolean wl = false;
        boolean score = false;
        //delete all currently loaded players
        for (SPPlayer key : SPPlayer.getSPInstances()) {
            key.deletePlayer(false);
        }
        //cycle through file
        for (String key : playerdata.getKeys(false)) {
            //check if not on version
            if (!(key.equalsIgnoreCase("version"))) {
                //add player
                SPPlayer.createPlayer(key, false, false, false, playerdata.getInt(key + ".kills"), (short) (playerdata.getInt(key + ".deaths")), (short) (playerdata.getInt(key + ".wins")), (short) (playerdata.getInt(key + ".losses")), (short) (playerdata.getInt(key + ".score")), "", (short) 0, true, false);
            }
        }
        //store players
        spplayers = SPPlayer.getSPInstances();
        //cycle through spplayers
        for (SPPlayer key : spplayers) {
            players.add(key.getIngotPlayerEquivelent());
        }
        //cycle through leaderboards
        for (String key : hologram.getKeys(false)) {
            //check if not on version
            if (!(key.equalsIgnoreCase("version"))) {
                //create leaderboard
                boardsToCheck.add(key);
            }
        }
        //cycle through checked boarfs
        for (String key : boardsToCheck) {
            //get board
            board = Leaderboard.selectBoard(key, plugin);
            //check if board isnt null
            if (board != null) {
                //delete board and get hologram
                hologramm = board.getHologram();
                board.deleteBoard();
            }
            else {
                //create new board
                Leaderboard.createBoard(key, LeaderboardType.getFromString(hologram.getString(key + ".type")), players, hologramm, new Location(Bukkit.getWorld(hologram.getString(key + ".location.world")), hologram.getDouble(key + ".location.x"), hologram.getDouble(key + ".location.y"), hologram.getDouble(key + ".location.z")), (byte) hologram.getInt(key + ".max-size"), Boolean.parseBoolean(hologram.getString(key + ".invert-list")), Boolean.parseBoolean(hologram.getString(key + ".summoned")), plugin);
            }
        }
        //cycle through current leaderboards
        for (Leaderboard key : Leaderboard.getInstances(plugin)) {
            //check for win
            if (key.getName().equalsIgnoreCase("wins")) {
                wins = true;
            }
            //check for loss
            else if (key.getName().equalsIgnoreCase("losses")) {
                losses = true;
            }
            //check for win/loss
            else if (key.getName().equalsIgnoreCase("wlratio")) {
                wl = true;
            }
            //check for score
            else if (key.getName().equalsIgnoreCase("score")) {
                score = true;
            }
        }
        //cycle through leaderboards
        for (Leaderboard key : Leaderboard.getInstances(plugin)) {
            try {
                //check if there is at least 1 player
                if (key.getPlayers().get(0).getUsername() != null) {
                    //organize board
                    key.organizeLeaderboard(true);
                    //check if summoned
                    if (key.getSummoned() == true) {
                        //summon hologram
                        key.summonHologram(config.getString("Leaderboard.header"), config.getString("Leaderboard.format"), config.getString("Leaderboard.footer"), true);
                    }
                }
            }
            catch (IndexOutOfBoundsException ex) {}
        }
        //check for wins
        if (wins == false) {
            //set board to wins
            board = Leaderboard.createBoard("wins", LeaderboardType.WINS, players, null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
        //check for losses
        if (losses == false) {
            //set board to losses
            board = Leaderboard.createBoard("losses", LeaderboardType.LOSSES, players, null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
        //check for wl
        if (wl == false) {
            //set board to wl
            board = Leaderboard.createBoard("wlratio", LeaderboardType.WLRATIO, players, null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
        //check for score
        if (score == false) {
            //set to board
            board = Leaderboard.createBoard("score", LeaderboardType.SCORE, players, null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
    }
    /**
     * 
     * This method loads in all the death messages
     * 
     */
    public static void loadDeathMessages() {
        //local vars
        FileConfiguration language = FileManager.getCustomData(plugin, "language", "");
        //add messages
        DeathMessageHandler.replaceMessage(language.getString("Death-Fall-Message"), DeathMessageType.FALL, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Explosion-Message"), DeathMessageType.EXPLOSION, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Crushed-Message"), DeathMessageType.CRUSHED, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Fire-Message"), DeathMessageType.FIRE, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Lava-Message"), DeathMessageType.LAVA, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Drown-Message"), DeathMessageType.DROWN, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Sufficate-Message"), DeathMessageType.SUFFICATE, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Other-Message"), DeathMessageType.OTHER, plugin);
    }
    /**
     *
     * This method loads in all arenas from the arenas folder 
     * Useful for startup and reloading
     *
     */
    public static void loadArenas() {
        //files
        FileConfiguration arenaData = null;
        File loadArenas = new File(plugin.getDataFolder() + "/Arenas/");
        File temparenaf = null;
        //arena
        SPArena temparena = null;
        Lobby lobbyy = null;
        Game gamee = null;
        int[] pos1 = new int[3];
        int[] pos2 = new int[3];
        String world = null;
        String name = null;
        byte minPlayers = 0;
        byte skipPlayers = 0;
        byte maxPlayers = 0;
        byte teamSize = 0;
        byte loopSize = 0;
        int lobbyWaitTime = 0;
        int lobbySkipTime = 0;
        int gameWaitTime = 0;
        int gameLengthTime = 0;
        ArenaStatus status = null;
        GameType type = null;
        //Spawn
        Spawn tempspawn = null;
        String namee = null;
        double x = 0;
        double y = 0;
        double z = 0;
        double yaw = 0;
        double pitch = 0;
        //positions
        double[] lobby = new double[6];
        String lobbyWorld = null;
        double[] exit = new double[6];
        String exitWorld = null;
        double[] spec = new double[6];
        double[] center = new double[6];
        //language
        FileConfiguration language = FileManager.getCustomData(plugin, "language", "");
        String prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message") + ""); 
        String arenaLoadedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Arena-Loaded-Message") + ""); 
        ArrayList<Team> teams = new ArrayList<>();
        //check if arena folder isnt made yet
        if (!loadArenas.exists()) {
            loadArenas.mkdirs();
        }
        //cycle through arena directory
        for (String key : loadArenas.list()) {
            //get file
            temparenaf = new File(plugin.getDataFolder() + "/Arenas/" + key + '/', "settings.yml");
            arenaData = FileManager.getCustomData(plugin, "settings", "/Arenas/" + key + '/');
            //check if arena has a settings file
            if (temparenaf.exists()) {
                //reset arrays
                pos1 = new int[3];
                pos2 = new int[3];
                lobby = new double[6];
                exit = new double[6];
                center = new double[6];
                spec = new double[6];
                //get positions
                pos1[0] = arenaData.getInt("pos1.x");
                pos1[1] = arenaData.getInt("pos1.y");
                pos1[2] = arenaData.getInt("pos1.z");
                pos2[0] = arenaData.getInt("pos2.x");
                pos2[1] = arenaData.getInt("pos2.y");
                pos2[2] = arenaData.getInt("pos2.z");
                lobbyWorld = arenaData.getString("Lobby.world");
                lobby[0] = arenaData.getDouble("Lobby.x");
                lobby[1] = arenaData.getDouble("Lobby.y");
                lobby[2] = arenaData.getDouble("Lobby.z");
                lobby[3] = arenaData.getDouble("Lobby.yaw");
                lobby[4] = arenaData.getDouble("Lobby.pitch");
                exitWorld = arenaData.getString("Exit.world");
                exit[0] = arenaData.getDouble("Exit.x");
                exit[1] = arenaData.getDouble("Exit.y");
                exit[2] = arenaData.getDouble("Exit.z");
                exit[3] = arenaData.getDouble("Exit.yaw");
                exit[4] = arenaData.getDouble("Exit.pitch");
                spec[0] = arenaData.getDouble("Spec.x");
                spec[1] = arenaData.getDouble("Spec.y");
                spec[2] = arenaData.getDouble("Spec.z");
                spec[3] = arenaData.getDouble("Spec.yaw");
                spec[4] = arenaData.getDouble("Spec.pitch");
                center[0] = arenaData.getDouble("Center.x");
                center[1] = arenaData.getDouble("Center.y");
                center[2] = arenaData.getDouble("Center.z");
                center[3] = arenaData.getDouble("Center.yaw");
                center[4] = arenaData.getDouble("Center.pitch");
                //get world
                world = arenaData.getString("world");
                //get name
                name = arenaData.getString("name");
                //get player vars
                minPlayers = (byte) arenaData.getInt("minPlayers");
                skipPlayers = (byte) arenaData.getInt("skipPlayers");
                maxPlayers = (byte) arenaData.getInt("maxPlayers");
                teamSize = (byte) arenaData.getInt("teamSize");
                //get timer vars
                lobbyWaitTime = arenaData.getInt("lobby-wait-time");
                lobbySkipTime = arenaData.getInt("lobby-skip-time");
                gameWaitTime = arenaData.getInt("game-wait-time");
                gameLengthTime = arenaData.getInt("game-length-time");
                //get drop vars
                type = GameType.getFromString(arenaData.getString("game-type") + "");
                status = ArenaStatus.getFromString(arenaData.getString("status"));
                //check for perm
                if (Bukkit.getPluginManager().getPermission("ingotsp.arenas." + name) != null) {
                    Bukkit.getPluginManager().removePermission("ingotsp.arenas." + name);
                }
                //create arena
                temparena = SPArena.createArena(pos1, pos2, world, name, minPlayers, skipPlayers, maxPlayers, teamSize, lobbyWaitTime, lobbySkipTime, gameWaitTime, gameLengthTime, lobby, lobbyWorld, exit, exitWorld, spec, center, status, type, null, "/Arenas/" + key + '/', true, "ingotsp.arenas." + name);
                //set lobby again
                temparena.setLobby(lobby);
                temparena.setLobbyWorld(lobbyWorld);//cycle spawns
                for (short i = 1; i < 32766; i++) {
                    //load from file
                    namee = arenaData.getString("Spawnpoints.Spawn" + i + ".name");
                    x = arenaData.getDouble("Spawnpoints.Spawn" + i + ".x");
                    y = arenaData.getDouble("Spawnpoints.Spawn" + i + ".y");
                    z = arenaData.getDouble("Spawnpoints.Spawn" + i + ".z");
                    yaw = arenaData.getDouble("Spawnpoints.Spawn" + i + ".yaw");
                    pitch = arenaData.getDouble("Spawnpoints.Spawn" + i + ".pitch");
                    if (namee != null) {
                        //create spawn
                        tempspawn = Spawn.createSpawn(namee, x, y, z, yaw, pitch, plugin);
                        temparena.getArenaEquivelent().addSpawn(tempspawn);
                    }
                    else {
                        i = 32766;
                        temparena.getArenaEquivelent().setSpawns(Spawn.validateSpawns(temparena.getArenaEquivelent().getSpawns()));
                    }
                }
                //load teams
                if (teamSize != 0) {
                    loopSize = (byte) (maxPlayers/teamSize);
                }
                else {
                    loopSize = maxPlayers;
                }
                teams.clear();
                for (byte i=0; i < loopSize; i++) {
                    teams.add(Team.createTeam(temparena.getArenaEquivelent().getName() + "_Team" + (i+1), null, 0, null, null, null, teamSize, false,false, false, plugin));
                }
                temparena.getArenaEquivelent().setTeams(teams);
                //load lobby + game
                lobbyy = new Lobby(temparena);
                gamee = new Game(temparena);
                //notify arena is loaded
                Bukkit.getServer().getConsoleSender().sendMessage(prefixMessage + arenaLoadedMessage + temparena.getArenaEquivelent().getName());
            }
        }
    }
    /**
     *
     * Enables the plugin. 
     * Checks if MC version isn't the latest. 
     * If its not, warn the player about lacking support 
     * Checks if server is running offline mode 
     * If it is, disable the plugin 
     * Also loads commands and events
     * Also loads in arenas and spawns
     *
     */
    @Override
    public void onEnable() {
        //create plugin instance
        plugin = this;
        //files
        FileConfiguration chest = FileManager.getCustomData(plugin, "chest", ROOT);
        FileConfiguration config = FileManager.getCustomData(plugin, "config", ROOT);
        FileConfiguration hologram = FileManager.getCustomData(plugin, "hologram", ROOT);
        FileConfiguration playerdata = FileManager.getCustomData(plugin, "playerdata", ROOT);
        ArrayList<String> comments = new ArrayList<>();
        //language variables
        FileConfiguration language = FileManager.getCustomData(plugin,"language",ROOT);
        String prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message") + ""); 
        String unsupportedVersionAMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsupported-VersionA-Message") + ""); 
        String unsupportedVersionBMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsupported-VersionB-Message") + ""); 
        String unsupportedVersionCMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsupported-VersionC-Message") + ""); 
        String unsecureServerAMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsecure-ServerA-Message") + ""); 
        String unsecureServerBMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsecure-ServerB-Message") + ""); 
        String unsecureServerCMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsecure-ServerC-Message") + ""); 
        String pluginEnabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Plugin-Enabled-Message") + ""); 
        //check for correct version
        if (!(Bukkit.getVersion().contains("1.20"))) {
            sender.sendMessage(prefixMessage + unsupportedVersionAMessage);
            sender.sendMessage(prefixMessage + unsupportedVersionBMessage);
            sender.sendMessage(prefixMessage + unsupportedVersionCMessage); 
        }
        //check for online mode and not bungee mode
        if (getServer().getOnlineMode() == false && FileManager.getCustomData(null, "spigot", ROOT).getBoolean("settings.bungeecord") == false) {
            sender.sendMessage(prefixMessage + unsecureServerAMessage);
            sender.sendMessage(prefixMessage + unsecureServerBMessage);
            sender.sendMessage(prefixMessage + unsecureServerCMessage);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //check for 1.0->1.1
        if (config.getString("version").contains("1.0")) {
            sender.sendMessage(prefixMessage + "&4YOUR FILES ARE OUTDATED!!! &eUpdating...");
            //config
            config.set("version", "1.1");
            config.set("enable-inventories", true);
            comments.add("Use this to manage inventories when leaving and joining");
            comments.add("If you have plugins that do this (multiverse-inventories), use those instead");
            config.setComments("enable-inventories", comments);
            comments = new ArrayList<>();
            config.set("Tablist.hidePlayersNotInGame", null);
            config.set("Tablist.hidePlayersInGameFromServer", null);
            config.set("Tablist.hidePlayers", true);
            config.set("Commands.enable", true);
            comments.add("sg");
            config.set("Commands.allowed-commands", comments);
            comments = new ArrayList<>();
            comments.add("Enable command blocking features");
            comments.add("Useful for preventing unintended actions (like warping in-game)");
            comments.add("allowed-commands allows these commands to bypass blocking)");
            config.setComments("Commands", comments);
            try {
                config.save(new File(plugin.getDataFolder() + "/config.yml"));
            }
            catch (IOException ex) {}
            //language
            language.set("Outdated-Files-Message", "&4YOUR FILES ARE OUTDATED!!! &eUpdating...");
            language.set("Unusable-Command-Message", "&bYou can't use that command while playing!");
            language.set("SPStats-Start-Message-1", "&f=======================&6&l");
            language.set("SPStats-Start-Message-2", "&3's stats&r&f=======================");
            language.set("SPStats-Kills-Message", "- &aKills: &c");
            language.set("SPStats-Deaths-Message", "- &aDeaths: &c");
            language.set("SPStats-KDRatio-Message", "- &aKDRatio: &c");
            language.set("SPStats-Wins-Message", "- &aWins: &c");
            language.set("SPStats-Losses-Message", "- &aLosses: &c");
            language.set("SPStats-WLRatio-Message", "- &aWLRatio: &c");
            language.set("SPStats-Score-Message", "- &aScore: &c");
            language.set("SPStats-End-Message", "&f=============================================================");
            language.set("SPHelp-Stats-Message", "&9/sp stats (player) &f- &eView your or another player's stats");
            try {
                language.save(new File(plugin.getDataFolder() + "/language.yml"));
            }
            catch (IOException ex) {}
        }
        //commands
        this.getCommand("sp").setExecutor(new SPCommand());
        this.getCommand("spadmin").setExecutor(new SPAdminCommand());
        //events
        getServer().getPluginManager().registerEvents(new Events(),this);
        //load arenas
        loadArenas();
        //load death messages
        loadDeathMessages();
        //load players+holograms
        loadPlayers();
        //enable plugin
        getServer().getPluginManager().enablePlugin(this);
        sender.sendMessage(prefixMessage + pluginEnabledMessage);
    }
    /**
     *
     * This method disables the plugin. 
     * It also saves all files and 
     * clears all loaded player data. 
     *
     */
    @Override
    public void onDisable() {
        plugin = this;
        //import files
        File configf = new File(plugin.getDataFolder(),"config.yml");
        File languagef = new File(plugin.getDataFolder(),"language.yml");
        File playerdataf = new File(plugin.getDataFolder(),"playerdata.yml");
        File hologramf = new File(plugin.getDataFolder(),"hologram.yml");
        FileConfiguration config = FileManager.getCustomData(plugin, "config", ROOT);
        FileConfiguration language = FileManager.getCustomData(plugin, "language", ROOT);
        FileConfiguration pd = FileManager.getCustomData(plugin, "playerdata", ROOT);
        FileConfiguration hologram = FileManager.getCustomData(plugin, "hologram", ROOT);
        FileConfiguration chest = FileManager.getCustomData(plugin, "chest", ROOT);
        //language
        String prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message")); 
        String pluginDisabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Plugin-Disabled-Message")); 
        //local vars
        IngotPlayer currentIPlayer = null;
        //reset iplayer data
        for (Player key : Bukkit.getOnlinePlayers()) {
            try {
                //get iplayerdata
                currentIPlayer = IngotPlayer.selectPlayer(key.getName(), plugin);
                //reset vars
                currentIPlayer.deletePlayer();
            }
            catch (IndexOutOfBoundsException ex) {}
        }
        //save holograms
        for (Leaderboard key : Leaderboard.getInstances(plugin)) {
            key.saveToFile(false);
            key.killHologram(false);
        }
        //saves files
        try {
            config.save(configf);
        } 
        catch (IOException ex) {
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "COULD NOT SAVE CONFIG.YML!");
            }
        }
        try {
            language.save(languagef);
        } 
        catch (IOException ex) {
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "COULD NOT SAVE LANGUAGE.YML!");
            }
        }
        try {
            pd.save(playerdataf);
        } 
        catch (IOException ex) {
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "COULD NOT SAVE PLAYERDATA.YML");
            }
        }
        try {
            hologram.save(hologramf);
        } 
        catch (IOException ex) {
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "COULD NOT SAVE HOLOGRAM.YML");
            }
        }
        //disables plugin
        getServer().getPluginManager().disablePlugin(this);
        sender.sendMessage(prefixMessage + pluginDisabledMessage);
    }
}