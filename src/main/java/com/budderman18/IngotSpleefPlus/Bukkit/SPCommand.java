package com.budderman18.IngotSpleefPlus.Bukkit;

import com.budderman18.IngotMinigamesAPI.Core.Data.ArenaStatus;
import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotMinigamesAPI.Core.Data.IngotPlayer;
import com.budderman18.IngotMinigamesAPI.Core.Data.Team;
import com.budderman18.IngotSpleefPlus.Core.Game;
import com.budderman18.IngotSpleefPlus.Core.Lobby;
import com.budderman18.IngotSpleefPlus.Core.SPArena;
import com.budderman18.IngotSpleefPlus.Core.SPPlayer;
import com.budderman18.IngotSpleefPlus.Main;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

/**
 * 
 * This class handles all the commands normal players use. 
 * 
 */
public class SPCommand implements TabExecutor {
    //retrive plugin instance
    private static final Plugin plugin = Main.getInstance();
    //used if the given file isnt in another folder
    private static final String ROOT = "";
    //imports classes
    private static Lobby lobby = null;
    private static Game game = null;
    private final Random random = new Random(); 
    //imports files
    private static FileConfiguration config = FileManager.getCustomData(plugin, "config", ROOT);
    private static FileConfiguration language = FileManager.getCustomData(plugin, "language", ROOT);
    //language variables
    private static String prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message") + "");
    private static String noPermissionMessage = ChatColor.translateAlternateColorCodes('&', language.getString("No-Permission-Message") + "");
    private static String incorrectCommandMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Incorrect-Command-Message") + "");
    private static String joinJoinedGameMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Joined-Game-Message") + "");
    private static String joinAlreadyPlayingMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Already-Playing-Message") + "");
    private static String joinArenaMissingMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Arena-Missing-Message") + "");
    private static String joinArenaFullMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Arena-Full-Message") + "");
    private static String joinArenaRunningMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Arena-Running-Message") + "");
    private static String joinArenaDisabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Arena-Disabled-Message") + "");
    private static String teamJoinedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPTeam-Joined-Message") + "");
    private static String teamFullMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPTeam-Full-Message") + "");
    private static String leaveLeftGameMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPLeave-Left-Game-Message") + "");
    private static String leaveNotPlayingMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPLeave-Not-Playing-Message") + "");
    private static String listStartMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPList-Start-Message") + "");
    private static String listStatusMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPList-Arena-Status-Message") + "");
    private static String listPlayersMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPList-Arena-Players-Message") + "");
    private static String listEndMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPList-End-Message") + "");
    private static String helpStartMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-Start-Message") + "");
    private static String helpJoinMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-Join-Message") + "");
    private static String helpRandomJoinMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-RandomJoin-Message") + "");
    private static String helpLeaveMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-Leave-Message") + "");
    private static String helpTeamMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-Team-Message") + "");
    private static String helpListMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-List-Message") + "");
    private static String helpHelpMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-Help-Message") + "");
    private static String helpEndMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-End-Message") + "");
    private static String playerOnlyMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Player-Only-Message") + "");
    
    /**
     * 
     * This method reloads all files and language strings
     * 
     */
    public static void reload() {
        //files
        config = FileManager.getCustomData(plugin, "config", ROOT);
        language = FileManager.getCustomData(plugin, "language", ROOT);
        //langauge
        prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message") + "");
        noPermissionMessage = ChatColor.translateAlternateColorCodes('&', language.getString("No-Permission-Message") + "");
        incorrectCommandMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Incorrect-Command-Message") + "");
        joinJoinedGameMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Joined-Game-Message") + "");
        joinAlreadyPlayingMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Already-Playing-Message") + "");
        joinArenaMissingMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Arena-Missing-Message") + "");
        joinArenaFullMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Arena-Full-Message") + "");
        joinArenaRunningMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Arena-Running-Message") + "");
        joinArenaDisabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPJoin-Arena-Disabled-Message") + "");
        leaveLeftGameMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPLeave-Left-Game-Message") + "");
        teamJoinedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPTeam-Joined-Message") + "");
        teamFullMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPTeam-Full-Message") + "");
        leaveNotPlayingMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPLeave-Not-Playing-Message") + "");
        listStartMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPList-Start-Message") + "");
        listStatusMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPList-Arena-Status-Message") + "");
        listPlayersMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPList-Arena-Players-Message") + "");
        listEndMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPList-End-Message") + "");
        helpStartMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-Start-Message") + "");
        helpJoinMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-Join-Message") + "");
        helpRandomJoinMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-RandomJoin-Message") + "");
        helpLeaveMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-Leave-Message") + "");
        helpTeamMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-Team-Message") + "");
        helpListMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-List-Message") + "");
        helpHelpMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-Help-Message") + "");
        helpEndMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPHelp-End-Message") + "");
        playerOnlyMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Player-Only-Message") + "");
    }
    /**
     * 
     * This method verifies that a player can join the arena.
     * 
     * @param sender the sender object
     * @param loadedArena the arena to verify
     * @param player the player to check
     */
    private boolean verifyJoinable(CommandSender sender, SPArena loadedArena, Player player) {
        try {
            //check if arena is not full
            if ((loadedArena.getArenaEquivelent().getCurrentPlayers() < loadedArena.getArenaEquivelent().getMaxPlayers()) && loadedArena.getArenaEquivelent().getMaxPlayers() != 0) {
                //check if arena is somehow running with nobody in it
                if (loadedArena.getArenaEquivelent().getCurrentPlayers() == 0 && loadedArena.getArenaEquivelent().getStatus() == ArenaStatus.RUNNING) {
                    //set waiting
                    loadedArena.getArenaEquivelent().setStatus(ArenaStatus.WAITING);
                }
                //check if arena isnt active
                if (loadedArena.getArenaEquivelent().getStatus() == ArenaStatus.WAITING && loadedArena.getArenaEquivelent().getSpawns().size() >= loadedArena.getArenaEquivelent().getMaxPlayers()) {
                    //check if player has permissions
                    if (sender.hasPermission(loadedArena.getArenaEquivelent().getPermission()) || sender.hasPermission("ingotsp.arenas.*")) {
                        //join lobby
                        lobby = Lobby.selectLobby(loadedArena);
                        lobby.joinLobby(SPPlayer.selectPlayer(player.getName(), plugin), config.getBoolean("enable-inventories"));
                        sender.sendMessage(prefixMessage + joinJoinedGameMessage + loadedArena.getName());
                        return true;
                    } 
                    else {
                        sender.sendMessage(prefixMessage + noPermissionMessage);
                    }
                }
                //check if disabled
                else if (loadedArena.getArenaEquivelent().getStatus() == ArenaStatus.DISABLED) {
                    sender.sendMessage(prefixMessage + joinArenaDisabledMessage);
                    return true;
                }
                //run if arena is active
                else {
                    sender.sendMessage(prefixMessage + joinArenaRunningMessage);
                    return true;
                }
            } 
            //run if game is full
            else {
                sender.sendMessage(prefixMessage + joinArenaFullMessage);
                return true;
            }
        } 
        //run if there is no game specified
        catch (NullPointerException | IndexOutOfBoundsException ex) {
            sender.sendMessage(prefixMessage + joinArenaMissingMessage);
            //end command          
            return true;       
        }
        return false;
    }
    /**
     *
     * This method handles the SP command. 
     *
     * @param sender the command sender
     * @param cmd the command object
     * @param label the command name
     * @param args the command args
     * @return true if valid
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //ingot player
        IngotPlayer iplayer = null;
        boolean inGame = false;
        //get player
        Player player = Bukkit.getPlayer(sender.getName());
        //local vars
        List<SPArena> arenas = null;
        byte randEnd = 0;
        byte randNum = 0;
        byte currentPlayers = 0;
        SPArena loadedArena = null;
        Team team = null;
        String arenaName = null;
        List<String> templist = null;
        PotionEffect[] effects = null;
        List<PotionEffect> tempeffects = new ArrayList<>();
        //check if command is /sp
        if (cmd.getName().equalsIgnoreCase("sp")) {
            //obtain ingotplayer
            iplayer = IngotPlayer.selectPlayer(sender.getName(), plugin);
            //check if command is empty to prevent errors
            if (args.length > 0) {
                //check if player has main permission
                if (sender.hasPermission("ingotsp.sp")) {
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.sp.join") && (sender instanceof Player)) {
                        inGame = iplayer.getInGame();
                        //join feature
                        if (args[0].equalsIgnoreCase("join")) {
                            //check if player isnt ingame
                            if (args.length > 1) {
                                //check if player is ingame
                                if (inGame == false) {
                                    //try to join game
                                    loadedArena = SPArena.selectArena(args[1], plugin);
                                    return verifyJoinable(sender, loadedArena, player);
                                }
                                //run if player is in game
                                else {
                                    //run if player is already playing
                                    sender.sendMessage(prefixMessage + joinAlreadyPlayingMessage);
                                    //end command
                                    return true;
                                }
                            }
                            //run if arena doesn't exist
                            else {
                                sender.sendMessage(prefixMessage + joinArenaMissingMessage);
                                //end command
                                return true;
                            }
                        }
                    }
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.sp.randomJoin")) {
                        //randomJoin feature
                        if (args[0].equalsIgnoreCase("randomJoin") && (sender instanceof Player)) {
                            inGame = iplayer.getInGame();
                            //check if random tables are enabled
                            if (args.length > 1 && config.getBoolean("RandomJoin.enable") == true) {
                                //check if player isnt ingame
                                if (inGame == false) {
                                    try {
                                        //cycle through all options
                                        for (byte h=1; h < 127; h++) {
                                            //check if table is valid
                                            if (!config.getStringList("RandomJoin.tables.Table" + h + ".arenas").isEmpty()) {
                                                templist = config.getStringList("RandomJoin.tables.Table" + h + ".arenas");
                                                //cycle through all tables
                                                for (byte i=0; i < templist.size(); i++) {
                                                    //check if name and table are equal
                                                    if (templist.get(i) != null) {
                                                        //increment end value
                                                        randEnd++;
                                                        if (SPArena.selectArena(templist.get(i), plugin) != null) {
                                                            if (SPArena.selectArena(templist.get(i), plugin).getArenaEquivelent().getCurrentPlayers() > currentPlayers) {
                                                                currentPlayers = SPArena.selectArena(templist.get(i), plugin).getArenaEquivelent().getCurrentPlayers();
                                                                loadedArena = SPArena.selectArena(templist.get(i), plugin);
                                                            }
                                                        }
                                                    }
                                                }
                                                //randomize arena
                                                randNum = (byte) random.nextInt(0, randEnd);
                                                //load arena
                                                for (byte i=0; i < config.getStringList("RandomJoin.tables.Table" + h + ".arenas").size(); i++) {
                                                    if (i == randNum) {
                                                        arenaName = templist.get(i);
                                                    }
                                                }
                                                if (loadedArena == null) {
                                                    loadedArena = SPArena.selectArena(arenaName, plugin);
                                                }
                                                //try to join game
                                                return verifyJoinable(sender, loadedArena, player);
                                            }
                                            else {
                                                break;
                                            }
                                        }
                                    }
                                    //run is no table is specified
                                    catch (NullPointerException | IndexOutOfBoundsException ex) {
                                        sender.sendMessage(prefixMessage + joinArenaMissingMessage);
                                        //end command
                                        return true;
                                    }
                                }
                                //run if player is in game
                                else {
                                    sender.sendMessage(prefixMessage + joinAlreadyPlayingMessage);
                                    //end command
                                    return true;
                                }
                            }
                            //check if random tables are disabled
                            if (args.length == 1) {
                                //check if player isnt ingame
                                if (inGame == false) {
                                    try {
                                        //get files 
                                        arenas = SPArena.getSPInstances();
                                        //cycle through all tables
                                        for (SPArena key : arenas) {
                                            //increment random end value
                                            randEnd++;
                                            if (key.getCurrentPlayers() > currentPlayers) {
                                                currentPlayers = key.getArenaEquivelent().getCurrentPlayers();
                                                loadedArena = key;
                                            }
                                        }
                                        //randomize spawn
                                        randNum = (byte) random.nextInt(0, randEnd);
                                        //load arena
                                        if (loadedArena == null) {
                                            loadedArena = arenas.get(randNum);
                                        }
                                        //try to join game
                                        return verifyJoinable(sender, loadedArena, player);
                                    }
                                    //run if there's invalid args
                                    catch (NullPointerException | IndexOutOfBoundsException ex) {
                                        sender.sendMessage(prefixMessage + joinArenaMissingMessage);
                                        //end command
                                        return true;
                                    }
                                }
                                //run if player is in game
                                else {
                                    sender.sendMessage(prefixMessage + joinAlreadyPlayingMessage);
                                    //end command
                                    return true;
                                }
                            }
                            //run if arena doesn't exist
                            else {
                                sender.sendMessage(prefixMessage + joinArenaMissingMessage);
                                //end command
                                return true;
                            }
                        }
                    }
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.sp.team") && (sender instanceof Player)) {
                        //join feature
                        if (args[0].equalsIgnoreCase("team")) {
                            inGame = iplayer.getInGame();
                            team = Team.selectTeam(args[1], plugin);
                            //check if player isnt ingame
                            if (args.length > 1 && inGame == true && iplayer.getIsPlaying() == false) {
                                if (team != null && team.getMembers().size() < team.getMaxSize()) {
                                    iplayer.setTeam(team, true);
                                    sender.sendMessage(prefixMessage + teamJoinedMessage + team.getName());
                                    return true;
                                }
                                else {
                                    sender.sendMessage(prefixMessage + teamFullMessage);
                                    return true;
                                }
                            }
                            //run if no team specified
                            else {
                                sender.sendMessage(prefixMessage + incorrectCommandMessage);
                                //end command
                                return true;
                            }
                        }
                    }
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.sp.leave") && (sender instanceof Player)) {
                        //get ingame
                        inGame = iplayer.getInGame();
                        //leave feature
                        if (args[0].equalsIgnoreCase("leave")) {
                            //check if player isn't ingame
                            if (inGame == true) {
                                //load arena
                                loadedArena = SPArena.selectArena(iplayer.getGame(), plugin);
                                //leave lobby
                                if (iplayer.getIsPlaying() == false) {
                                    lobby = Lobby.selectLobby(loadedArena);
                                    lobby.leaveLobby(SPPlayer.selectPlayer(player.getName(), plugin), true, config.getBoolean("enable-inventories"));
                                } //leave game
                                else if (iplayer.getIsPlaying() == true) {
                                    game = Game.selectGame(loadedArena);
                                    game.leaveGame(SPPlayer.selectPlayer(iplayer.getUsername(), plugin), true, true, config.getBoolean("enable-inventories"));
                                }
                                //notify player
                                sender.sendMessage(prefixMessage + leaveLeftGameMessage);
                            }
                            //run if player is inGame
                            else {
                                //notify player
                                sender.sendMessage(prefixMessage + leaveNotPlayingMessage);
                            }
                            return true;
                        }
                    }
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.sp.list")) {
                        //list feature
                        if (args[0].equalsIgnoreCase("list")) {
                            //header
                            sender.sendMessage(listStartMessage);
                            //get loaded arenas
                            arenas = SPArena.getSPInstances();
                            //cycle through loaded arenas
                            for (SPArena key : arenas) {
                                //arena
                                sender.sendMessage(key.getName() + listStatusMessage + key.getArenaEquivelent().getStatus() + listPlayersMessage + key.getArenaEquivelent().getCurrentPlayers() + '/' + key.getArenaEquivelent().getMaxPlayers());
                            }
                            //footer
                            sender.sendMessage(listEndMessage);
                            //end command
                            return true;
                        }
                    }
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.sp.help") && (sender instanceof Player)) {
                        //help feature
                        if (args[0].equalsIgnoreCase("help")) {
                            //send help menu
                            sender.sendMessage(helpStartMessage);
                            sender.sendMessage(helpJoinMessage);
                            sender.sendMessage(helpRandomJoinMessage);
                            sender.sendMessage(helpLeaveMessage);
                            sender.sendMessage(helpTeamMessage);
                            sender.sendMessage(helpListMessage);
                            sender.sendMessage(helpHelpMessage);
                            sender.sendMessage(helpEndMessage);
                            //end command
                            return true;
                        }
                    } 
                    //player only
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(prefixMessage + playerOnlyMessage);
                        //end command
                        return true;
                    }
                    //no subpremission
                    else {
                        sender.sendMessage(prefixMessage + noPermissionMessage);
                        //end command
                        return true;
                    }
                }
                //no permission
                else {
                    sender.sendMessage(prefixMessage + noPermissionMessage);
                    //end command
                    return true;
                }
            }
            //no argument specified
            else {
                sender.sendMessage(prefixMessage + incorrectCommandMessage);
                //end command
                return false;
            }
        }
        //should never have to run, but won't compile without it
        return false;
    }
    /**
     *
     * This method handles tabcompletion when required. 
     *
     * @param sender the sender object
     * @param command the command object
     * @param alias the other commands that run the same
     * @param args thw command args
     * @return the tabcompletion list
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //local vars
        List<String> arguments = new ArrayList<>();
        List<SPArena> arenas = null;
        SPArena arena = null;
        IngotPlayer iplayer = null;
        //main command args
        if (args.length == 1) {
            arguments.add("join");
            arguments.add("randomJoin");
            arguments.add("team");
            arguments.add("leave");
            arguments.add("list");
            arguments.add("help");
        }
        //join command args
        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            //get all arenas
            arenas = SPArena.getSPInstances();
            //cycle through all arenas
            for (SPArena key : arenas) {
                //add argument
                arguments.add(key.getArenaEquivelent().getName());
            }
        }
        //randomjoin args
        if (args.length == 2 && args[0].equalsIgnoreCase("randomJoin") && config.getBoolean("RandomJoin.enable") == true) {
            //cycle through all tables
            for (byte i=1; i < 127; i++) {
                //check for table
                if (config.getString("RandomJoin.tables.Table" + i + ".name") != null) {
                    //add table
                    arguments.add(config.getString("RandomJoin.tables.Table" + i + ".name") + "");
                }
                else {
                    break;
                }
            }
        }
        //team command args
        if (args.length == 2 && args[0].equalsIgnoreCase("team") && sender instanceof Player) {
            //get all arenas
            iplayer = IngotPlayer.selectPlayer(sender.getName(),plugin);
            arena = SPArena.selectArena(iplayer.getGame(), plugin);
            //cycle through all arenas
            if (arena != null) {
                //cycle through teams
                for (Team key : arena.getArenaEquivelent().getTeams()) {
                    //add argument
                    arguments.add(key.getName());
                }
            }
        }
        //return tab-completion
        return arguments;
    } 
}