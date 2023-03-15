package com.budderman18.IngotSpleefPlus.Bukkit;

import com.budderman18.IngotMinigamesAPI.Core.Data.ArenaStatus;
import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotMinigamesAPI.Core.Data.IngotPlayer;
import com.budderman18.IngotMinigamesAPI.Core.Data.Leaderboard;
import com.budderman18.IngotMinigamesAPI.Core.Data.LeaderboardType;
import com.budderman18.IngotMinigamesAPI.Core.Data.Spawn;
import com.budderman18.IngotMinigamesAPI.Core.Data.Team;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.BossbarHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.ScoreboardHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TablistHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TimerHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TitleHandler;
import com.budderman18.IngotSpleefPlus.Core.Game;
import com.budderman18.IngotSpleefPlus.Core.GameType;
import com.budderman18.IngotSpleefPlus.Core.Lobby;
import com.budderman18.IngotSpleefPlus.Core.SPArena;
import com.budderman18.IngotSpleefPlus.Core.SPPlayer;
import com.budderman18.IngotSpleefPlus.Main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * 
 * This class handles all the management commands staff will use. 
 * 
 */
public class SPAdminCommand implements TabExecutor {
    //retrive plugin instance
    private static final Plugin plugin = Main.getInstance();
    //used if the given file isnt in another folder
    private static final String ROOT = "";
    //import files
    private static FileConfiguration language = FileManager.getCustomData(plugin, "language", ROOT);
    private static FileConfiguration config = FileManager.getCustomData(plugin, "config", ROOT);
    //global vars
    private int[] pos1 = null;
    private int[] pos2 = null;
    private boolean toggleConfirming = false;
    private double[] tempLoc = new double[5];
    private float[] loc = new float[5];
    private SPArena currentArena = null;
    private Spawn currentSpawn = null;
    private List<Spawn> spawns = new ArrayList<>();
    //language
    private static String prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message") + "");
    private static String noPermissionMessage = ChatColor.translateAlternateColorCodes('&', language.getString("No-Permission-Message") + "");
    private static String playerOnlyMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Player-Only-Message") + "");
    private static String incorrectCommandMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Incorrect-Command-Message") + "");
    private static String arenaInvalidArenaMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Invalid-Arena-Message") + "");
    private static String arenaPos1SetMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Pos1-Set-Message") + "");
    private static String arenaPos2SetMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Pos2-Set-Message") + "");
    private static String arenaCreateInvalidNameMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Create-Invalid-Name-Message") + "");
    private static String arenaCreateInvalidPositionsMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Create-Invalid-Positions-Message") + "");
    private static String arenaCreateWrongPositionsMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Create-Wrong-Positions-Message") + "");
    private static String arenaCreateCreatedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Create-Arena-Created-Message") + "");
    private static String arenaDeleteDeletedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Delete-Arena-Deleted-Message") + "");
    private static String arenaEditNotDisabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Edit-Not-Disabled-Message") + "");
    private static String arenaEditEditedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Edit-Editied-Message") + "");
    private static String arenaEditInvalidArgumentMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Edit-Invalid-Argument-Message") + "");
    private static String arenaSelectSelectedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Select-Arena-Selected-Message") + "");
    private static String arenaSelectInvalidArenaMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Select-Invalid-Arena-Message") + "");
    private static String arenaToggleOnMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Toggle-On-Message") + "");
    private static String arenaToggleOffMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Toggle-Off-Message") + "");
    private static String arenaToggleCantEnableMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Cant-Enable-Message") + "");
    private static String arenaToggleConfirmMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Toggle-Confirm-Message") + "");
    private static String arenaToggleAlreadyEnabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Already-Enabled-Message") + "");
    private static String arenaToggleAlreadyDisabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Already-Disabled-Message") + "");
    private static String arenaSpawnCreateSpawnCreatedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-Create-Spawn-Created-Message") + "");
    private static String arenaSpawnDeleteSpawnDeletedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-Delete-Spawn-Deleted-Message") + "");
    private static String arenaSpawnNotDisabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-Not-Disabled-Message") + "");
    private static String arenaSpawnDeleteNoSpawnsMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-Delete-No-Spawns-Message") + "");
    private static String arenaSpawnListStart1Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-List-Start1-Message") + "");
    private static String arenaSpawnListStart2Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-List-Start2-Message") + "");
    private static String arenaSpawnListEndMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-List-End-Message") + "");
    private static String arenaRegenerateRegenerated1Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Regenerate-Arena-Regenerated-1-Message") + "");
    private static String arenaRegenerateRegenerated2Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Regenerate-Arena-Regenerated-2-Message") + "");
    private static String arenaRegenerateInvalidArenaMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Regenerate-Invalid-Arena-Message") + "");
    private static String arenaSchematicInvalidArenaMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Schematic-Invalid-Arena-Message") + "");
    private static String arenaSchematicGenerated1Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Schematic-Arena-Schematic-1-Message") + "");
    private static String arenaSchematicGenerated2Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Schematic-Arena-Schematic-2-Message") + "");
    private static String hologramCreateCreatedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Create-Hologram-Created-Message") + "");
    private static String hologramAlreadyExistsMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Create-Already-Exists-Message") + "");
    private static String hologramDeleteDeletedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Delete-Hologram-Deleted-Message") + "");
    private static String hologramDoesntExistMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Delete-Doesnt-Message") + "");
    private static String hologramRefreshRefreshedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Refresh-Hologram-Refreshed-Message") + "");
    private static String hologramRecalculateScoreMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Recalculate-Score-Message") + "");
    private static String hologramInvalidHologramMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Refresh-Invalid-Hologram-Message") + "");
    private static String helpStartMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Start-Message") + "");
    private static String helpArenaMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Message") + "");
    private static String helpArenaCreateMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Create-Message") + "");
    private static String helpArenaDeleteMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Delete-Message") + "");
    private static String helpArenaEditMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Edit-Message") + "");
    private static String helpArenaRegenerateMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Regenerate-Message") + "");
    private static String helpArenaSchematicMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Schematic-Message") + "");
    private static String helpArenaSelectMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Select-Message") + "");
    private static String helpArenaSpawnMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Spawn-Message") + "");
    private static String helpArenaSpawnCreateMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Spawn-Create-Message") + "");
    private static String helpArenaSpawnDeleteMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Spawn-Delete-Message") + "");
    private static String helpArenaSpawnListMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Spawn-List-Message") + "");
    private static String helpArenaPos1Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Pos1-Message") + "");
    private static String helpArenaPos2Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Pos2-Message") + "");
    private static String helpHologramMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Hologram-Message") + "");
    private static String helpHologramCreateMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Hologram-Create-Message") + "");
    private static String helpHologramDeleteMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Hologram-Delete-Message") + "");
    private static String helpHologramRefreshMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Hologram-Refresh-Message") + "");
    private static String helpHologramRecalculateScoreMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Hologram-Recalculate-Score-Message") + "");
    private static String helpHelpMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Help-Message") + "");
    private static String helpReloadMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Reload-Message") + "");
    private static String helpVersionMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Version-Message") + "");
    private static String helpEndMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-End-Message") + "");
    private static String reloadMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Reload-Message") + "");
    private static String versionMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Version-Message") + "");
    //this method reloads language and files for spadmincommad
    private static void reload() {
        //files
        language = FileManager.getCustomData(plugin, "language", ROOT);
        config = FileManager.getCustomData(plugin, "config", ROOT);
        //language
        prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message") + "");
        noPermissionMessage = ChatColor.translateAlternateColorCodes('&', language.getString("No-Permission-Message") + "");
        playerOnlyMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Player-Only-Message") + "");
        incorrectCommandMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Incorrect-Command-Message") + "");
        arenaInvalidArenaMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Invalid-Arena-Message") + "");
        arenaPos1SetMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Pos1-Set-Message") + "");
        arenaPos2SetMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Pos2-Set-Message") + "");
        arenaCreateInvalidNameMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Create-Invalid-Name-Message") + "");
        arenaCreateInvalidPositionsMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Create-Invalid-Positions-Message") + "");
        arenaCreateWrongPositionsMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Create-Wrong-Positions-Message") + "");
        arenaCreateCreatedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Create-Arena-Created-Message") + "");
        arenaDeleteDeletedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Delete-Arena-Deleted-Message") + "");
        arenaEditNotDisabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Edit-Not-Disabled-Message") + "");
        arenaEditEditedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Edit-Editied-Message") + "");
        arenaEditInvalidArgumentMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Edit-Invalid-Argument-Message") + "");
        arenaSelectSelectedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Select-Arena-Selected-Message") + "");
        arenaSelectInvalidArenaMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Select-Invalid-Arena-Message") + "");
        arenaToggleOnMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Toggle-On-Message") + "");
        arenaToggleOffMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Toggle-Off-Message") + "");
        arenaToggleConfirmMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Toggle-Confirm-Message") + "");
        arenaToggleCantEnableMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Cant-Enable-Message") + "");
        arenaToggleAlreadyEnabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Already-Enabled-Message") + "");
        arenaToggleAlreadyDisabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Toggle-Already-Disabled-Message") + "");
        arenaSpawnNotDisabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-Not-Disabled-Message") + "");
        arenaSpawnCreateSpawnCreatedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-Create-Spawn-Created-Message") + "");
        arenaSpawnDeleteSpawnDeletedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-Delete-Spawn-Deleted-Message") + "");
        arenaSpawnDeleteNoSpawnsMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-Delete-No-Spawns-Message") + "");
        arenaSpawnListStart1Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-List-Start1-Message") + "");
        arenaSpawnListStart2Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-List-Start2-Message") + "");
        arenaSpawnListEndMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Spawn-List-End-Message") + "");
        arenaRegenerateRegenerated2Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Regenerate-Arena-Regenerated-2-Message") + "");
        arenaSchematicInvalidArenaMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Schematic-Invalid-Arena-Message") + "");
        arenaSchematicGenerated1Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Schematic-Arena-Schematic-1-Message") + "");
        arenaSchematicGenerated2Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Schematic-Arena-Schematic-2-Message") + "");
        arenaRegenerateInvalidArenaMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Arena-Schematic-Invalid-Arena-Message") + "");
        hologramCreateCreatedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Create-Hologram-Created-Message") + "");
        hologramAlreadyExistsMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Create-Already-Exists-Message") + "");
        hologramDeleteDeletedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Delete-Hologram-Deleted-Message") + "");
        hologramDoesntExistMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Delete-Doesnt-Message") + "");
        hologramRefreshRefreshedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Refresh-Hologram-Refreshed-Message") + "");
        hologramRecalculateScoreMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Recalculate-Score-Message") + "");
        hologramInvalidHologramMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Hologram-Refresh-Invalid-Hologram-Message") + "");
        helpStartMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Start-Message") + "");
        helpArenaMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Message") + "");
        helpArenaCreateMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Create-Message") + "");
        helpArenaDeleteMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Delete-Message") + "");
        helpArenaEditMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Edit-Message") + "");
        helpArenaRegenerateMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Regenerate-Message") + "");
        helpArenaSelectMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Select-Message") + "");
        helpArenaSchematicMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Schematic-Message") + "");
        helpArenaSpawnMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Spawn-Message") + "");
        helpArenaSpawnCreateMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Spawn-Create-Message") + "");
        helpArenaSpawnDeleteMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Spawn-Delete-Message") + "");
        helpArenaSpawnListMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Spawn-List-Message") + "");
        helpArenaPos1Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Pos1-Message") + "");
        helpArenaPos2Message = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Arena-Pos2-Message") + "");
        helpHologramMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Hologram-Message") + "");
        helpHologramCreateMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Hologram-Create-Message") + "");
        helpHologramDeleteMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Hologram-Delete-Message") + "");
        helpHologramRefreshMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Hologram-Refresh-Message") + "");
        helpHologramRecalculateScoreMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Hologram-Recalculate-Score-Message") + "");
        helpHelpMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Help-Message") + "");
        helpReloadMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Reload-Message") + "");
        helpVersionMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-Version-Message") + "");
        helpEndMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdminHelp-End-Message") + "");
        reloadMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Reload-Message") + "");
        versionMessage = ChatColor.translateAlternateColorCodes('&', language.getString("SPAdmin-Version-Message") + "");
    }
    //this method sets a given position
    private double[] setLocation(SPArena currentArena, Player player, double[] tempLoc) {
        //local vars
        tempLoc[0] = player.getLocation().getX();
        tempLoc[1] = player.getLocation().getY();
        tempLoc[2] = player.getLocation().getZ();
        tempLoc[3] = player.getLocation().getYaw();
        tempLoc[4] = player.getLocation().getPitch();
        //check if positiones should be centerized
        if (config.getBoolean("centerize-teleport-locations") == true) {
            loc = currentArena.centerizeLocation(tempLoc, true);
            tempLoc[0] = loc[0];
            tempLoc[1] = loc[1];
            tempLoc[2] = loc[2];
            tempLoc[3] = loc[3];
            tempLoc[4] = loc[4];
        }
        loc = new float[5];
        return tempLoc;
    }
    /**
     *
     * This method edits a given option of an arena. 
     *
     * @param sender the sender object
     * @param currentArena the arena to edit
     * @param args the command args
     * @param preSelected true if using selection in commands
     * @return true if edited
     */
    private boolean editOption(CommandSender sender, SPArena currentArena, Player player, String[] args, boolean preSelected) {
        //local vars
        byte index = 4;
        //check for preSelected
        if (preSelected == true) {
            index--;
        }
        tempLoc = new double[5];
        //check if sender is a player
        if (sender instanceof Player) {
            tempLoc[0] = player.getLocation().getX();
            tempLoc[1] = player.getLocation().getY();
            tempLoc[2] = player.getLocation().getZ();
            tempLoc[3] = player.getLocation().getYaw();
            tempLoc[4] = player.getLocation().getPitch();
        }
        try {
            //check if name is being changed
            if (args[2].equalsIgnoreCase("name")) {
                //set arena's name
                currentArena.getArenaEquivelent().setFilePath("/Arenas/" + args[index] + '/');
                currentArena.setName(args[index]);
                currentArena.saveFiles(false);
                sender.sendMessage(prefixMessage + arenaEditEditedMessage + "name");
                //end command
                return true;
            }
            //check if maxPlayers is being changed
            if (args[2].equalsIgnoreCase("minPlayers")) {
                try {
                    //set maxPlayers
                    currentArena.getArenaEquivelent().setMinPlayers(Byte.parseByte(args[index]));
                    currentArena.saveFiles(false);
                    sender.sendMessage(prefixMessage + arenaEditEditedMessage + "minPlayers");
                    //end command
                    return true;
                }
                //run if maxPlayers is invalid
                catch (NumberFormatException ex) {
                    sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                    //end command
                    return true;
                }
            }
            //check if maxPlayers is being changed
            if (args[2].equalsIgnoreCase("skipPlayers")) {
                try {
                    //set maxPlayers
                    currentArena.getArenaEquivelent().setSkipPlayers(Byte.parseByte(args[index]));
                    currentArena.saveFiles(false);
                    sender.sendMessage(prefixMessage + arenaEditEditedMessage + "minPlayers");
                    //end command
                    return true;
                } 
                //run if maxPlayers is invalid
                catch (NumberFormatException ex) {
                    sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                    //end command
                    return true;
                }
            }
            //check if maxPlayers is being changed
            if (args[2].equalsIgnoreCase("maxPlayers")) {
                try {
                    //set arena's maxPlayers
                    currentArena.getArenaEquivelent().setMaxPlayers(Byte.parseByte(args[index]));
                    currentArena.saveFiles(false);
                    sender.sendMessage(prefixMessage + arenaEditEditedMessage + "maxPlayers");
                    //end command
                    return true;
                } 
                //run if maxPlayers is invalid
                catch (NumberFormatException ex) {
                    sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                    //end command
                    return true;
                }
            }
            //check if maxPlayers is being changed
            if (args[2].equalsIgnoreCase("teamSize")) {
                try {
                    //set arena's teamSize
                    currentArena.getArenaEquivelent().setTeamSize(Byte.parseByte(args[index]));
                    currentArena.saveFiles(false);
                    sender.sendMessage(prefixMessage + arenaEditEditedMessage + "teamSize");
                    //end command
                    return true;
                } 
                //run if maxPlayers is invalid
                catch (NumberFormatException ex) {
                    sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                    //end command
                    return true;
                }
            }
            //check if lobby is being changed
            if (args[2].equalsIgnoreCase("lobby") && (sender instanceof Player)) {
                tempLoc = setLocation(currentArena, player, tempLoc);
                currentArena.getArenaEquivelent().setLobby(tempLoc);
                currentArena.getArenaEquivelent().setLobbyWorld(player.getWorld().getName());
                currentArena.saveFiles(false);
                sender.sendMessage(prefixMessage + arenaEditEditedMessage + "lobby");
                return true;
            }
            //check if exit is being changed
            if (args[2].equalsIgnoreCase("exit") && (sender instanceof Player)) {
                tempLoc = setLocation(currentArena, player, tempLoc);
                currentArena.getArenaEquivelent().setExit(tempLoc);
                currentArena.getArenaEquivelent().setExitWorld(player.getWorld().getName());
                currentArena.saveFiles(false);
                sender.sendMessage(prefixMessage + arenaEditEditedMessage + "exit");
                return true;
            }
            //check if spec is being changed
            if (args[2].equalsIgnoreCase("spec") && (sender instanceof Player)) {
                tempLoc = setLocation(currentArena, player, tempLoc);
                currentArena.getArenaEquivelent().setSpectatorPos(tempLoc);
                currentArena.saveFiles(false);
                sender.sendMessage(prefixMessage + arenaEditEditedMessage + "spec");
                return true;
            }
            //check if center is being changed
            if (args[2].equalsIgnoreCase("center") && (sender instanceof Player)) {
                tempLoc = setLocation(currentArena, player, tempLoc);
                currentArena.getArenaEquivelent().setCenter(tempLoc);
                currentArena.saveFiles(false);
                sender.sendMessage(prefixMessage + arenaEditEditedMessage + "center");
                return true;
            }
            //check if lobbyWaitTime is being changed
            if (args[2].equalsIgnoreCase("lobbyWaitTime")) {
                //set arena's name
                try {
                    currentArena.getArenaEquivelent().setLobbyWaitTime(Integer.parseInt(args[index]));
                    currentArena.saveFiles(false);
                    sender.sendMessage(prefixMessage + arenaEditEditedMessage + "lobby-wait-time");
                    //end command
                    return true;
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                    //end command
                    return true;  
                }
            }
            //check if lobbySkipTime is being changed
            if (args[2].equalsIgnoreCase("lobbySkipTime")) {
                //set arena's name
                try {
                    currentArena.getArenaEquivelent().setLobbySkipTime(Integer.parseInt(args[index]));
                    currentArena.saveFiles(false);
                    sender.sendMessage(prefixMessage + arenaEditEditedMessage + "lobby-skip-time");
                    //end command
                    return true;
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                    //end command
                    return true;  
                }
            }
            //check if gameWaitTime is being changed
            if (args[2].equalsIgnoreCase("gameWaitTime")) {
                //set arena's name
                try {
                    currentArena.getArenaEquivelent().setGameWaitTime(Integer.parseInt(args[index]));
                    currentArena.saveFiles(false);
                    sender.sendMessage(prefixMessage + arenaEditEditedMessage + "game-wait-time");
                    //end command
                    return true;
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                    //end command
                    return true;  
                }
            }
            //check if gameLength is being changed
            if (args[2].equalsIgnoreCase("gameLength")) {
                //set arena's name
                try {
                    currentArena.getArenaEquivelent().setGameLengthTime(Integer.parseInt(args[index]));
                    currentArena.saveFiles(false);
                    sender.sendMessage(prefixMessage + arenaEditEditedMessage + "game-length");
                    //end command
                    return true;
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                    //end command
                    return true;  
                }
            }
            //check if gameType is being changed
            if (args[2].equalsIgnoreCase("gameType")) {
                //set arena's name
                try {
                    currentArena.setType(GameType.getFromString(args[index]));
                    currentArena.saveFiles(false);
                    sender.sendMessage(prefixMessage + arenaEditEditedMessage + "game-type");
                    //end command
                    return true;
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                    //end command
                    return true;  
                }
            }
            //check if sender isnt a player
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefixMessage + playerOnlyMessage);
                //end command
                return true;
            }
            else {
                sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                //end command
                return true;
            }
        } 
        //run if arena is invalid
        catch (IllegalArgumentException ex) {
            sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
            //end command
            return true;
        }
    }
    /**
     *
     * This method handles the SPAdmin command. 
     *
     * @param sender the sender object (console, player, block, plugin)
     * @param cmd The command object
     * @param label The actual command name
     * @param args the command input
     * @return true if valid command
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //local vars
        boolean exists = false;
        String version = null;
        String world = null;
        String loby = null;
        String exit = null;
        Lobby lobby = null;
        Game game = null;
        short score = 0;
        Player player = null;
        SPArena tempArena = null;
        byte index = 0;
        Leaderboard tempboard = null;
        List<IngotPlayer> spplayers = new ArrayList<>();
        File hologramf = new File(plugin.getDataFolder(), "hologram.yml");
        FileConfiguration hologram = FileManager.getCustomData(plugin, "hologram", ROOT);
        //check if command is /spadmin
        if (cmd.getName().equalsIgnoreCase("spadmin")) {
            //check for player
            if (sender instanceof Player) {
                player = Bukkit.getPlayer(sender.getName());
            }
            //check if command is empty to prevent errors
            if (args.length > 0) {
                //check if sender has default permission
                if (sender.hasPermission("ingotsp.spadmin")) {
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.spadmin.arena")) {
                        //arena feature
                        if (args[0].equalsIgnoreCase("arena") && args.length > 1) {
                            //check if player has permission(s)
                            if (sender.hasPermission("ingotsp.spadmin.arena.create")) {
                                //create
                                if (args[1].equalsIgnoreCase("create") && (sender instanceof Player) && args.length > 2) {
                                    //cycle trhough existing arenas
                                    for (SPArena key : SPArena.getSPInstances()) {
                                        if (key.getName().equals(args[2])) {
                                            exists = true;
                                        }
                                    }
                                    //check if arenaName is null or the arena already exists
                                    if (args[2] == null || exists == true) {
                                        sender.sendMessage(prefixMessage + arenaCreateInvalidNameMessage);
                                        //end command
                                        return true;
                                    }
                                    //check if pos1 or pos2 is not set
                                    if (pos1 == null || pos2 == null || !(sender instanceof Player)) {
                                        sender.sendMessage(prefixMessage + arenaCreateInvalidPositionsMessage);
                                        //end command
                                        return true;
                                    }
                                    //check if pos1 is not greater than pos2
                                    if (pos1[0] < pos2[0] && pos1[1] < pos2[1] && pos1[2] < pos2[2]) {
                                        //create arena
                                        currentArena = SPArena.createArena(pos1, pos2, player.getWorld().getName(), args[2], (byte) 0, (byte) (0), (byte) 0, (byte) (0), 0, 0, 0, 0, null, "", null, "", null, null, ArenaStatus.DISABLED, null, null, "/Arenas/" + args[2] + '/', true, "ingotsp.arenas." + args[2], plugin);
                                        currentArena.saveFiles(false);
                                        currentArena.getArenaEquivelent().createArenaSchematic();
                                        //clear pos arrays
                                        pos1 = null;
                                        pos2 = null;
                                        //check if lobby doesnt exist
                                        if (Lobby.selectLobby(currentArena) == null) {
                                            //setup lobby
                                            lobby = new Lobby(currentArena);
                                        }
                                        else {
                                            //update existing lobby
                                            Lobby.selectLobby(currentArena).updateArena(currentArena);
                                        }
                                        //check if game doesnt exist
                                        if (Game.selectGame(currentArena) == null) {
                                            //setup game
                                            game = new Game(currentArena);
                                        }
                                        else {
                                            //update existing game
                                            Game.selectGame(currentArena).updateArena(currentArena);
                                        }
                                        sender.sendMessage(prefixMessage + arenaCreateCreatedMessage);
                                        //end command
                                        return true;
                                    }
                                    //run if pos1 is greater than pos1
                                    else {
                                        sender.sendMessage(prefixMessage + arenaCreateWrongPositionsMessage);
                                        //end command
                                        return true;
                                    }
                                }
                            }
                            //check if player has permission(s)
                            if (sender.hasPermission("ingotsp.spadmin.arena.delete")) {
                                //delete
                                if (args[1].equalsIgnoreCase("delete")) {
                                    //using selection
                                    if (args.length == 2) {
                                        try {
                                            //check if arena is disabled
                                            if (currentArena.getArenaEquivelent().getStatus() == ArenaStatus.DISABLED) {
                                                //delete arena
                                                currentArena.deleteArena(false, true);
                                                sender.sendMessage(prefixMessage + arenaDeleteDeletedMessage);
                                                //end command
                                                return true;
                                            }
                                            else {
                                                //run if not disabled
                                                sender.sendMessage(prefixMessage + arenaEditNotDisabledMessage);
                                                return true;
                                            }
                                        }
                                        //run if arena doesnt exist
                                        catch (IllegalArgumentException | NullPointerException ex) {
                                            sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                            //end command
                                            return true;
                                        }
                                    }
                                    //other
                                    if (args.length > 2) {
                                        try {
                                            //load arena
                                            tempArena = SPArena.selectArena(args[2], plugin);
                                            //check if arena is disabled
                                            if (tempArena.getArenaEquivelent().getStatus() == ArenaStatus.DISABLED) {
                                                //delete arena
                                                tempArena.deleteArena(false, true);
                                                sender.sendMessage(prefixMessage + arenaDeleteDeletedMessage);
                                                //end command
                                                return true;
                                            }
                                            else {
                                                //run if not disabled
                                                sender.sendMessage(prefixMessage + arenaEditNotDisabledMessage);
                                                return true;
                                            }
                                        }
                                        //run if arena doesnt exist
                                        catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
                                            sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                            //end command
                                            return true;
                                        }
                                    }
                                }
                            }
                            //check if player has permission(s)
                            if (sender.hasPermission("ingotsp.spadmin.arena.edit")) {
                                //edit
                                if (args[1].equalsIgnoreCase("edit")) {
                                    //check if there is a specifed arena
                                    if (args.length > 4 || (args.length == 4 && (args[2].equalsIgnoreCase("lobby") || args[2].equalsIgnoreCase("exit") || args[2].equalsIgnoreCase("spec") || args[2].equalsIgnoreCase("center")))) {
                                        //load arena
                                        tempArena = SPArena.selectArena(args[3], plugin);
                                        if (tempArena != null) {
                                            //check if arena is disabled
                                            if (tempArena.getArenaEquivelent().getStatus() == ArenaStatus.DISABLED) {
                                                //edit option
                                                return editOption(sender, tempArena, player, args, false);
                                            }
                                            else {
                                                //run if not disabled
                                                sender.sendMessage(prefixMessage + arenaEditNotDisabledMessage);
                                                return true;
                                            }
                                        }
                                        sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                        return true;
                                    }
                                    //check if there's no selected arena
                                    if (currentArena == null) {
                                        sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                        return true;
                                    }
                                    //check if using selected arena
                                    if (args.length == 4 || (args.length == 3 && (args[2].equalsIgnoreCase("lobby") || args[2].equalsIgnoreCase("exit") || args[2].equalsIgnoreCase("spec") || args[2].equalsIgnoreCase("center")))) {
                                        //check if arena is disabled
                                        if (currentArena.getArenaEquivelent().getStatus() == ArenaStatus.DISABLED) {
                                            //edit option
                                            return editOption(sender, currentArena, player, args, true);
                                        } 
                                        else {
                                            //run if not disabled
                                            sender.sendMessage(prefixMessage + arenaEditNotDisabledMessage);
                                            return true;
                                        }
                                    }
                                    //run if edit args are invalid
                                    else {
                                        sender.sendMessage(prefixMessage + arenaEditInvalidArgumentMessage);
                                        return true;
                                    }
                                }
                            }
                            //check if player has permission(s)
                            if (sender.hasPermission("ingotsp.spadmin.arena.select")) {
                                //select
                                if (args[1].equalsIgnoreCase("select")) {
                                    //check if name was entered
                                    if (args.length > 2) {
                                        try {
                                            //load arena
                                            currentArena = SPArena.selectArena(args[2], plugin);
                                            currentArena.getName();
                                            sender.sendMessage(prefixMessage + arenaSelectSelectedMessage);
                                            //end command
                                            return true;
                                        }
                                        //run if arena is invalid
                                        catch (IllegalArgumentException | NullPointerException ex) {
                                            sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                            return true;
                                        }
                                    }
                                    //run if select args are invalid
                                    else {
                                        sender.sendMessage(prefixMessage + arenaSelectInvalidArenaMessage);
                                        //end command
                                        return true;
                                    }
                                }
                            }
                            //check if player has permission(s)
                            if (sender.hasPermission("ingotsp.spadmin.arena.toggle")) {
                                //select
                                if (args[1].equalsIgnoreCase("toggle")) {
                                    index = 2;
                                    if (args.length > 3) {
                                        index++;
                                    }
                                    //check for on
                                    if (args[index].equalsIgnoreCase("on")) {
                                        //get arena
                                        if (index == 3) {
                                            tempArena = SPArena.selectArena(args[index-1], plugin);
                                        }
                                        else {
                                            tempArena = currentArena;
                                        }
                                        //check if arena is null
                                        if (tempArena == null) {
                                            sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                            return true;
                                        }
                                        //check if arena is disabled
                                        if (tempArena.getArenaEquivelent().getStatus() == ArenaStatus.DISABLED) {
                                            try {
                                                world = Bukkit.getWorld(tempArena.getArenaEquivelent().getWorld()).getName();
                                            }
                                            catch (IllegalArgumentException | NullPointerException ex) {
                                                world = "";
                                                sender.sendMessage(prefixMessage + arenaToggleCantEnableMessage);
                                                return true;
                                            }
                                            try {
                                                loby = Bukkit.getWorld(tempArena.getArenaEquivelent().getLobbyWorld()).getName();
                                            }
                                            catch (IllegalArgumentException | NullPointerException ex) {
                                                loby = "";
                                                sender.sendMessage(prefixMessage + arenaToggleCantEnableMessage);
                                                return true;
                                            }
                                            try {
                                                exit = Bukkit.getWorld(tempArena.getArenaEquivelent().getExitWorld()).getName();
                                            }
                                            catch (IllegalArgumentException | NullPointerException ex) {
                                                exit = "";
                                                sender.sendMessage(prefixMessage + arenaToggleCantEnableMessage);
                                                return true;
                                            }
                                            if (!world.equalsIgnoreCase("") && tempArena.getArenaEquivelent().getMinPlayers() > 0 && tempArena.getArenaEquivelent().getMaxPlayers() > tempArena.getArenaEquivelent().getMinPlayers() && tempArena.getArenaEquivelent().getSkipPlayers() > tempArena.getArenaEquivelent().getMinPlayers() && tempArena.getArenaEquivelent().getSkipPlayers() <= tempArena.getArenaEquivelent().getMaxPlayers() && !loby.equalsIgnoreCase("") && !exit.equalsIgnoreCase("") && tempArena.getArenaEquivelent().getLobbyWaitTime() > 0 && tempArena.getArenaEquivelent().getLobbySkipTime() > 0 && tempArena.getArenaEquivelent().getLobbySkipTime() < tempArena.getArenaEquivelent().getLobbyWaitTime() && tempArena.getArenaEquivelent().getGameWaitTime() < tempArena.getArenaEquivelent().getGameLengthTime() && tempArena.getArenaEquivelent().getSpawns().size() >= tempArena.getArenaEquivelent().getMaxPlayers()) {
                                                tempArena.getArenaEquivelent().setStatus(ArenaStatus.WAITING);
                                                tempArena.saveFiles(false);
                                                sender.sendMessage(prefixMessage + arenaToggleOnMessage + tempArena.getName());
                                                return true;
                                            }
                                            else {
                                                sender.sendMessage(prefixMessage + arenaToggleCantEnableMessage);
                                                return true;
                                            }
                                        }
                                        //check if already enabled
                                        if (tempArena.getArenaEquivelent().getStatus() != ArenaStatus.DISABLED) {
                                            sender.sendMessage(prefixMessage + arenaToggleAlreadyEnabledMessage);
                                            return true;
                                        }
                                    }
                                    //check for off
                                    if (args[index].equalsIgnoreCase("off")) {
                                        //get arena
                                        if (index == 3) {
                                            tempArena = SPArena.selectArena(args[index-1], plugin);
                                        }
                                        else {
                                            tempArena = currentArena;
                                        }
                                        if (tempArena == null) {
                                            sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                            return true;
                                        }
                                        //check if arena is waiting
                                        if (tempArena.getArenaEquivelent().getStatus() == ArenaStatus.WAITING) {
                                            for (SPPlayer key : Lobby.selectLobby(tempArena).getPlayers()) {
                                                Lobby.selectLobby(tempArena).leaveLobby(key, true, config.getBoolean("enable-inventories"));
                                            }
                                            tempArena.getArenaEquivelent().setStatus(ArenaStatus.DISABLED);
                                            tempArena.saveFiles(false);
                                            sender.sendMessage(prefixMessage + arenaToggleOffMessage + tempArena.getName());
                                            return true;
                                        }
                                        //check if running and needs confirming
                                        if (tempArena.getArenaEquivelent().getStatus() == ArenaStatus.RUNNING && toggleConfirming == false) {
                                            toggleConfirming = true;
                                            sender.sendMessage(prefixMessage + arenaToggleConfirmMessage);
                                            return true;
                                        }
                                        //check if running and confirmed
                                        if (tempArena.getArenaEquivelent().getStatus() == ArenaStatus.RUNNING && toggleConfirming == true) {
                                            //cycle through all iplayers
                                            for (SPPlayer key : SPPlayer.getSPInstances()) {
                                                //check if in lobby
                                                if (key.getInGame() == true && key.getIsPlaying() == false && key.getGame().equalsIgnoreCase(tempArena.getName())) {
                                                    Lobby.selectLobby(tempArena).leaveLobby(key, true, config.getBoolean("enable-inventories"));
                                                }
                                                //check if in game
                                                if (key.getInGame() == true && key.getIsPlaying() == false && key.getGame().equalsIgnoreCase(tempArena.getName())) {
                                                    Game.selectGame(tempArena).leaveGame(SPPlayer.selectPlayer(key.getUsername(), plugin), true, false, config.getBoolean("enable-inventories"));
                                                }
                                                //check if arena is empty
                                                if (tempArena.getCurrentPlayers() == 0) {
                                                    break;
                                                }
                                            }
                                            tempArena.getArenaEquivelent().setStatus(ArenaStatus.DISABLED);
                                            tempArena.saveFiles(false);
                                            sender.sendMessage(prefixMessage + arenaToggleOffMessage + tempArena.getName());
                                            return true;
                                        }
                                        //check if already disabled
                                        if (tempArena.getArenaEquivelent().getStatus() == ArenaStatus.DISABLED) {
                                            sender.sendMessage(prefixMessage + arenaToggleAlreadyDisabledMessage);
                                            return true;
                                        }
                                    }
                                }
                            }
                            //check if player has permission(s)
                            if (sender.hasPermission("ingotsp.spadmin.arena.regenerate")) {
                                //regenerate
                                if (args[1].equalsIgnoreCase("regenerate")) {
                                    sender.sendMessage(prefixMessage + arenaRegenerateRegenerated1Message);
                                    //select arena
                                    try {
                                        if (args.length > 2) {
                                            tempArena = SPArena.selectArena(args[2], plugin);
                                        } 
                                        else {
                                            tempArena = currentArena;
                                        }
                                    }
                                    catch (IndexOutOfBoundsException ex) {
                                        sender.sendMessage(prefixMessage + arenaRegenerateInvalidArenaMessage);
                                    }
                                    //regenerate arena
                                    if (tempArena != null) {
                                        tempArena.getArenaEquivelent().loadArenaSchematic(true, true, true, true);
                                        sender.sendMessage(prefixMessage + arenaRegenerateRegenerated2Message);
                                        //end command
                                        return true;
                                    } 
                                    else {
                                        sender.sendMessage(prefixMessage + arenaRegenerateInvalidArenaMessage);
                                        //end command
                                        return true;
                                    }
                                }
                            }
                            //check if player has permission(s)
                            if (sender.hasPermission("ingotsp.spadmin.arena.schematic")) {
                                //regenerate
                                if (args[1].equalsIgnoreCase("schematic")) {
                                    sender.sendMessage(prefixMessage + arenaSchematicGenerated1Message);
                                    //select arena
                                    try {
                                        if (args.length > 2) {
                                            tempArena = SPArena.selectArena(args[2], plugin);
                                        } 
                                        else {
                                            tempArena = currentArena;
                                        }
                                    }
                                    catch (IndexOutOfBoundsException ex) {
                                        sender.sendMessage(prefixMessage + arenaSchematicInvalidArenaMessage);
                                    }
                                    //regenerate arena
                                    if (tempArena != null) {
                                        tempArena.getArenaEquivelent().createArenaSchematic();
                                        sender.sendMessage(prefixMessage + arenaSchematicGenerated2Message);
                                        //end command
                                        return true;
                                    } 
                                    else {
                                        sender.sendMessage(prefixMessage + arenaSchematicInvalidArenaMessage);
                                        //end command
                                        return true;
                                    }
                                }
                            }
                            //check if player has permission(s)
                            if (sender.hasPermission("ingotsp.spadmin.arena.spawn")) {
                                //spawn
                                if (args[1].equalsIgnoreCase("spawn")) {
                                    //check if player has permission(s)
                                    if (sender.hasPermission("ingotsp.spadmin.arena.spawn.create") && (sender instanceof Player)) {
                                        //create spawn
                                        if (args[2].equalsIgnoreCase("create")) {
                                            //get files
                                            if (args.length == 3 && currentArena != null) {
                                                tempArena = currentArena;
                                            }
                                            else if (args.length == 3 && currentArena == null) {
                                                sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                                return true;
                                            }
                                            else {
                                                //get arenas
                                                tempArena = SPArena.selectArena(args[3], plugin);
                                            }
                                            //check if arena is still null
                                            if (tempArena == null) {
                                                sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                                return true;
                                            }
                                            //check if arena is disabled
                                            if (tempArena.getArenaEquivelent().getStatus() == ArenaStatus.DISABLED) {
                                                //set positions
                                                tempLoc[0] = player.getLocation().getX();
                                                tempLoc[1] = player.getLocation().getY();
                                                tempLoc[2] = player.getLocation().getZ();
                                                tempLoc[3] = player.getLocation().getYaw();
                                                tempLoc[4] = player.getLocation().getPitch();
                                                //check if positions should be centerized
                                                if (config.getBoolean("centerize-teleport-locations") == true) {
                                                    loc = tempArena.centerizeLocation(tempLoc, true);
                                                }
                                                else {
                                                    loc[0] = (float) tempLoc[0];
                                                    loc[1] = (float) tempLoc[1];
                                                    loc[2] = (float) tempLoc[2];
                                                    loc[3] = (float) tempLoc[3];
                                                    loc[4] = (float) tempLoc[4];
                                                }
                                                //create spawn
                                                tempArena.getArenaEquivelent().setSpawns(Spawn.validateSpawns(tempArena.getArenaEquivelent().getSpawns()));
                                                //add spawn to arena
                                                tempArena.getArenaEquivelent().addSpawn(Spawn.createSpawn(tempArena.getName() + "_Spawn" + Long.toString(tempArena.getArenaEquivelent().getSpawns().size()+1), loc[0], loc[1], loc[2], loc[3], loc[4], plugin));
                                                //save file
                                                tempArena.saveFiles(false);
                                                sender.sendMessage(prefixMessage + arenaSpawnCreateSpawnCreatedMessage + loc[0] + ',' + loc[1] + ',' + loc[2] + ',' + loc[3] + ',' + loc[4]);
                                                //end command
                                                return true;
                                            }
                                            else {
                                                //run if not disabled
                                                sender.sendMessage(prefixMessage + arenaSpawnNotDisabledMessage);
                                                return true;
                                            }
                                        }
                                    }
                                    //check if player has permission(s)
                                    if (sender.hasPermission("ingotsp.spadmin.arena.spawn.delete")) {
                                        //delete spawn
                                        if (args[2].equalsIgnoreCase("delete")) {
                                            //get files
                                            if (args.length == 3 && currentArena != null) {
                                                tempArena = currentArena;
                                            }
                                            else if (args.length == 3 && currentArena == null) {
                                                sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                                return true;
                                            }
                                            else {
                                                //get files
                                                tempArena = SPArena.selectArena(args[3], plugin);
                                            }
                                            if (tempArena.getArenaEquivelent().getSpawns().size()-1 >= 0) {
                                                //check if arena is disabled
                                                if (tempArena.getArenaEquivelent().getStatus() == ArenaStatus.DISABLED) {
                                                    //get spawn
                                                    tempArena.getArenaEquivelent().setSpawns(Spawn.validateSpawns(tempArena.getArenaEquivelent().getSpawns()));
                                                    currentSpawn = tempArena.getArenaEquivelent().getSpawns().get(tempArena.getArenaEquivelent().getSpawns().size()-1);
                                                    //remove spawn
                                                    tempArena.getArenaEquivelent().removeSpawn(currentSpawn);
                                                    currentSpawn.deleteSpawn();
                                                    //save file
                                                    tempArena.saveFiles(false);
                                                    sender.sendMessage(prefixMessage + arenaSpawnDeleteSpawnDeletedMessage);
                                                    //end command
                                                    return true;
                                                }
                                                else {
                                                    //run if not disabled
                                                    sender.sendMessage(prefixMessage + arenaSpawnNotDisabledMessage);
                                                    return true;
                                                }
                                            }
                                            else {
                                                sender.sendMessage(prefixMessage + arenaSpawnDeleteNoSpawnsMessage);
                                                //end command
                                                return true;
                                            }
                                        }
                                    }
                                    //check if player has permission(s)
                                    if (sender.hasPermission("ingotsp.spadmin.arena.spawn.list")) {
                                        //list spawns
                                        if (args[2].equalsIgnoreCase("list")) {
                                            //get files
                                            if (args.length == 3 && currentArena != null) {
                                                tempArena = currentArena;
                                            }
                                            //check for invalid arena
                                            else if (args.length == 3 && currentArena == null) {
                                                sender.sendMessage(prefixMessage + arenaInvalidArenaMessage);
                                                return true;
                                            }
                                            else {
                                                //get files
                                                tempArena = SPArena.selectArena(args[3], plugin);
                                            }
                                            sender.sendMessage(arenaSpawnListStart1Message + tempArena.getName() + arenaSpawnListStart2Message);
                                            //cycle through all spawns
                                            tempArena.getArenaEquivelent().setSpawns(Spawn.validateSpawns(tempArena.getArenaEquivelent().getSpawns()));
                                            //cycle through spawns
                                            for (Spawn key : tempArena.getArenaEquivelent().getSpawns()) {
                                                if (key != null) {
                                                    sender.sendMessage(key.getName() + ": x: " + Double.toString(key.getLocation()[0]) + " y: " + Double.toString(key.getLocation()[1]) + " z: " + Double.toString(key.getLocation()[2]) + " yaw: " + Double.toString(key.getLocation()[3]) + " pitch: " + Double.toString(key.getLocation()[4]));
                                                }
                                            }
                                            sender.sendMessage(arenaSpawnListEndMessage);
                                            //end command
                                            return true;
                                        }
                                    }
                                }
                            }
                            //check if player has permission(s)
                            if (sender.hasPermission("ingotsp.spadmin.arena.pos1") && (sender instanceof Player)) {
                                //pos1
                                if (args[1].equalsIgnoreCase("pos1")) {
                                    //set pos1 from player pos
                                    pos1 = new int[3];
                                    pos1[0] = player.getLocation().getBlockX();
                                    pos1[1] = player.getLocation().getBlockY();
                                    pos1[2] = player.getLocation().getBlockZ();
                                    sender.sendMessage(prefixMessage + arenaPos1SetMessage + pos1[0] + ',' + pos1[1] + ',' + pos1[2]);
                                    //end command
                                    return true;
                                }
                            }
                            //check if player has permission(s)
                            if (sender.hasPermission("ingotsp.spadmin.arena.pos2") && (sender instanceof Player)) {
                                //pos2
                                if (args[1].equalsIgnoreCase("pos2")) {
                                    //set pos2 from player pos
                                    pos2 = new int[3];
                                    pos2[0] = player.getLocation().getBlockX();
                                    pos2[1] = player.getLocation().getBlockY();
                                    pos2[2] = player.getLocation().getBlockZ();
                                    sender.sendMessage(prefixMessage + arenaPos2SetMessage + pos2[0] + ',' + pos2[1] + ',' + pos2[2]);
                                    //end command
                                    return true;
                                }
                            }
                            //no sub-sub permission
                            else {
                                sender.sendMessage(prefixMessage + noPermissionMessage);
                                return true;
                            }
                        }
                    }
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.spadmin.hologram")) {
                        if (args.length >= 3 && !args[1].equalsIgnoreCase("calculateScore")) {
                            //get board
                            tempboard = Leaderboard.selectBoard(args[2], plugin);
                            //holo create feature
                            if (args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("create") && sender.hasPermission("ingotsp.spadmin.hologram.create") && sender instanceof Player) {
                                //check for valid name
                                if (tempboard != null) {
                                    //check if not summoned
                                    if (tempboard.getSummoned() == false && (tempboard.getType() != LeaderboardType.KILLS || tempboard.getType() != LeaderboardType.DEATHS)) {
                                        //summon hologram
                                        tempboard.setHoloLoc(player.getLocation());
                                        tempboard.organizeLeaderboard(true);
                                        tempboard.summonHologram(config.getString("Leaderboard.header"), config.getString("Leaderboard.format"), config.getString("Leaderboard.footer"), true);
                                        tempboard.saveToFile(false);
                                        //tell player and return
                                        sender.sendMessage(prefixMessage + hologramCreateCreatedMessage + player.getLocation().getX() + ", " + player.getLocation().getY() + ", " + player.getLocation().getZ());
                                        return true;
                                    }
                                    //run if summoned
                                    else {     
                                        sender.sendMessage(prefixMessage + hologramAlreadyExistsMessage);
                                        return true;
                                    }
                                }
                                //run if invalid
                                else {     
                                    sender.sendMessage(prefixMessage + hologramInvalidHologramMessage);
                                    return true;
                                }
                            }
                            //holo delete feature
                            if (args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("delete") && sender.hasPermission("ingotsp.spadmin.hologram.delete")) {
                                //check for valid name
                                if (tempboard != null) {
                                    //check if summoned
                                    if (tempboard.getSummoned() == true) {
                                        //summon hologram
                                        tempboard.killHologram(true);
                                        hologram.set(tempboard.getName(), null);
                                        try {
                                            hologram.save(hologramf);
                                        }
                                        catch (IOException ex) {
                                            if (config.getBoolean("enable-debug-mode") == true) {
                                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "COULD NOT SAVE HOLOGRAM.YML");
                                            }
                                        }
                                        //tell player and return
                                        sender.sendMessage(prefixMessage + hologramDeleteDeletedMessage);
                                        return true;
                                    }
                                    //run if not summoned
                                    else {     
                                        sender.sendMessage(prefixMessage + hologramDoesntExistMessage);
                                        return true;
                                    }
                                }
                                //run if invalid
                                else {     
                                    sender.sendMessage(prefixMessage + hologramInvalidHologramMessage);
                                    return true;
                                }
                            }
                            //holo refresh feature
                            if (args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("refresh") && sender.hasPermission("ingotsp.spadmin.hologram.refresh")) {
                                //check for valid name
                                if (tempboard != null) {
                                    //check if summoned
                                    if (tempboard.getSummoned() == true) {
                                        //recalculate the players
                                        spplayers.clear();
                                        for (IngotPlayer key : IngotPlayer.getInstances(plugin)) {
                                            if (key.getPlugin() == plugin) {
                                                spplayers.add(key);
                                            }
                                        }
                                        //refresh hologram
                                        tempboard.setPlayers(spplayers);
                                        tempboard.organizeLeaderboard(true);
                                        tempboard.summonHologram(config.getString("Leaderboard.header"), config.getString("Leaderboard.format"), config.getString("Leaderboard.footer"), true);
                                        //tell player and return
                                        sender.sendMessage(prefixMessage + hologramRefreshRefreshedMessage + tempboard.getName());
                                        return true;
                                    }
                                    //run if summoned
                                    else {     
                                        sender.sendMessage(prefixMessage + hologramDoesntExistMessage);
                                        return true;
                                    }
                                }
                                //run if invalid
                                else {     
                                    sender.sendMessage(prefixMessage + hologramInvalidHologramMessage);
                                    return true;
                                }
                            }
                            //run if not a player (for create)
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(prefixMessage + playerOnlyMessage);
                                return true;
                            }
                        }
                        else if (args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("calculateScore")) {
                            //get new score values
                            config = FileManager.getCustomData(plugin, "config", "");
                            //get leaderboard
                            tempboard = Leaderboard.selectBoard("score", plugin);
                            //recalculate the players
                            spplayers.clear();
                            for (IngotPlayer key : IngotPlayer.getInstances(plugin)) {
                                if (key.getPlugin() == plugin) {
                                    spplayers.add(key);
                                }
                            }
                            //refresh hologram
                            tempboard.setPlayers(spplayers);
                            //cycle through players
                            for (IngotPlayer key : spplayers) {
                                //reset score
                                score = 0;
                                score += key.getWins() * config.getInt("Score.win");
                                score += key.getLosses() * config.getInt("Score.loss");
                                key.setScore(score);
                            }
                            //refresh hologram
                            tempboard.setPlayers(IngotPlayer.getInstances(plugin));
                            tempboard.organizeLeaderboard(true);
                            tempboard.summonHologram(config.getString("Leaderboard.header"), config.getString("Leaderboard.format"), config.getString("Leaderboard.footer"), true);
                            sender.sendMessage(prefixMessage + hologramRecalculateScoreMessage);
                            return true;
                        }
                        //run if invalid
                        else if (args[0].equalsIgnoreCase("hologram")) {
                            sender.sendMessage(prefixMessage + hologramInvalidHologramMessage);
                            return true;
                        }
                    }
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.spadmin.help")) {
                        //help feature
                        if (args[0].equalsIgnoreCase("help")) {
                            //output helplist
                            sender.sendMessage(helpStartMessage);
                            sender.sendMessage(helpArenaMessage);
                            sender.sendMessage(helpArenaCreateMessage);
                            sender.sendMessage(helpArenaDeleteMessage);
                            sender.sendMessage(helpArenaEditMessage);
                            sender.sendMessage(helpArenaRegenerateMessage);
                            sender.sendMessage(helpArenaSchematicMessage);
                            sender.sendMessage(helpArenaSelectMessage);
                            sender.sendMessage(helpArenaSpawnMessage);
                            sender.sendMessage(helpArenaSpawnCreateMessage);
                            sender.sendMessage(helpArenaSpawnDeleteMessage);
                            sender.sendMessage(helpArenaSpawnListMessage);
                            sender.sendMessage(helpArenaPos1Message);
                            sender.sendMessage(helpArenaPos2Message);
                            sender.sendMessage(helpHologramMessage);
                            sender.sendMessage(helpHologramCreateMessage);
                            sender.sendMessage(helpHologramDeleteMessage);
                            sender.sendMessage(helpHologramRefreshMessage);
                            sender.sendMessage(helpHologramRecalculateScoreMessage);
                            sender.sendMessage(helpHelpMessage);
                            sender.sendMessage(helpReloadMessage);
                            sender.sendMessage(helpVersionMessage);
                            sender.sendMessage(helpEndMessage);
                            //end command
                            return true;
                        }
                    }
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.spadmin.version")) {
                        //version feature
                        if (args[0].equalsIgnoreCase("version")) {
                            //get version
                            version = language.getString("version");
                            sender.sendMessage(prefixMessage + versionMessage + version);
                            //end command
                            return true;
                        }
                    }
                    //check if player has permission(s)
                    if (sender.hasPermission("ingotsp.spadmin.reload")) {
                        //reload feature
                        if (args[0].equalsIgnoreCase("reload")) {
                            //cycle throuhg all players. this runs the stuff from the .leave() methods that for some reason fails to run (visual stuff)
                            for (IngotPlayer key : IngotPlayer.getInstances(plugin)) {
                                //check if player is of this plugin and playing
                                if ((key.getGame() != null && !"".equals(key.getGame())) && key.getPlugin() == plugin) {
                                    //get player object
                                    player = Bukkit.getPlayer(key.getUsername());
                                    //check if lopbby has bossbar
                                    if (Lobby.selectLobby(SPArena.selectArena(key.getGame(), plugin)).getBossBar() != null) {
                                        //remove bossbar
                                        BossbarHandler.clearBar(player, Lobby.selectLobby(SPArena.selectArena(key.getGame(), plugin)).getBossBar());
                                    }
                                    //check if game has bossbar
                                    if (Game.selectGame(SPArena.selectArena(key.getGame(), plugin)).getBossBar() != null) {
                                        //remove bossbar
                                        BossbarHandler.clearBar(player, Game.selectGame(SPArena.selectArena(key.getGame(), plugin)).getBossBar());
                                    }
                                    //reset board and list
                                    ScoreboardHandler.clearScoreboard(player);
                                    TablistHandler.reset(player);
                                    //check if titles are enabled
                                    if (config.getBoolean("Title.enable") == true) {
                                        //set title and actionbar
                                        TitleHandler.setTitle(player, config.getString("Title.Leave.title"), config.getString("Title.Leave.subtitle"), config.getInt("Title.Join.fadein"), config.getInt("Title.Leave.length"), config.getInt("Title.Leave.fadeout"));
                                        TitleHandler.setActionBar(player, config.getString("Title.Leave.actionbar"));
                                    }
                                    //check if in lobby
                                    if (Lobby.selectLobby(SPArena.selectArena(key.getGame(), plugin)) != null) {
                                        //teleport to exit
                                        player.teleport(new Location(Bukkit.getWorld(SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExitWorld()), SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExit()[0], SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExit()[1], SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExit()[2], (float) SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExit()[3], (float) SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExit()[4]));
                                    }
                                    //check if in game
                                    if (Game.selectGame(SPArena.selectArena(key.getGame(), plugin)) != null) {
                                        //teleport to exit
                                        player.teleport(new Location(Bukkit.getWorld(SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExitWorld()), SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExit()[0], SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExit()[1], SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExit()[2], (float) SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExit()[3], (float) SPArena.selectArena(key.getGame(), plugin).getArenaEquivelent().getExit()[4]));
                                    }
                                }
                            }
                            //cycle trhough all arenas
                            for (SPArena key : SPArena.getSPInstances()) {
                                //check if lobby has players
                                if (!Lobby.selectLobby(key).getPlayers().isEmpty()) {
                                    //force players to leave lobby
                                    for (byte i=(byte) (Lobby.selectLobby(key).getPlayers().size()-1); i > 0; i--) {
                                        Lobby.selectLobby(key).leaveLobby(Lobby.selectLobby(key).getPlayers().get(i), true, config.getBoolean("enable-inventories"));
                                    }
                                }
                                //check if game has players
                                if (!Game.selectGame(key).getPlayers().isEmpty()) {
                                    //force players to leave game
                                    for (byte i=(byte) (Game.selectGame(key).getPlayers().size()-1); i >= 0; i--) {
                                        Game.selectGame(key).leaveGame(SPPlayer.selectPlayer(Game.selectGame(key).getPlayers().get(i).getUsername(), plugin), true, false, config.getBoolean("enable-inventories"));
                                    }
                                }
                                //delete arena
                                key.deleteArena(false, false);
                            }
                            //cycle through teams
                            for (Team key : Team.getInstances(plugin)) {
                                key.deleteTeam();
                            }
                            //cycle through spawns backwards
                            try {
                                for (short i=(short) (Spawn.getInstances(plugin).size()-1); i >= 0; i--) {
                                    Spawn.getInstances(plugin).get(i).deleteSpawn();
                                }
                            } 
                            catch (IndexOutOfBoundsException ex) {}
                            //cycle through boards
                            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                                key.killHologram(false);
                                key.deleteBoard();
                            }
                            //cancel tasks
                            TimerHandler.cancelAllTimers(plugin);
                            //reload players
                            Main.loadPlayers();
                            //reload arena files
                            Main.loadArenas();
                            //reload death messages
                            Main.loadDeathMessages();
                            //reload classes
                            reload();
                            SPCommand.reload();
                            Events.reload();
                            Lobby.reload();
                            Game.reload();
                            //send message to sender
                            sender.sendMessage(prefixMessage + reloadMessage);
                            //end command
                            return true;
                        }
                    }
                    //no sub permission
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
            //no arguments
            else {
                //end command
                sender.sendMessage(prefixMessage + incorrectCommandMessage);
                return false;
            }
        }
        //should never have to run, but won't compile without it
        sender.sendMessage(prefixMessage + incorrectCommandMessage);
        return false;
    }
    /**
     *
     * This method handles tab-completion when required. 
     *
     * @param sender The sender object
     * @param command The command object
     * @param alias The other commands with the same code
     * @param args the command args
     * @return the tab-completion list
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //create lists
        List<String> arguments = new ArrayList<>();
        List<SPArena> arenas = null;
        //main command args
        if (args.length == 1) {
            arguments.add("arena");
            arguments.add("hologram");
            arguments.add("help");
            arguments.add("version");
            arguments.add("reload");
        }
        //arena main command args
        if (args.length == 2 && args[0].equalsIgnoreCase("arena")) {
            arguments.add("create");
            arguments.add("delete");
            arguments.add("edit");
            arguments.add("select");
            arguments.add("toggle");
            arguments.add("spawn");
            arguments.add("regenerate");
            arguments.add("schematic");
            arguments.add("pos1");
            arguments.add("pos2");
        }
        //hologram main commands
        if (args.length == 2 && args[0].equalsIgnoreCase("hologram")) {
            arguments.add("create");
            arguments.add("delete");
            arguments.add("refresh");
            arguments.add("calculateScore");
        }
        //edit command args
        if (args.length == 3 && args[1].equalsIgnoreCase("edit")) {
            arguments.add("name");
            arguments.add("minPlayers");
            arguments.add("maxPlayers");
            arguments.add("skipPlayers");
            arguments.add("teamSize");
            arguments.add("lobby");
            arguments.add("exit");
            arguments.add("spec");
            arguments.add("center");
            arguments.add("lobbyWaitTime");
            arguments.add("lobbySkipTime");
            arguments.add("gameWaitTime");
            arguments.add("gameLength");
            arguments.add("gameType");
        }
        if (args.length == 3 && args[1].equalsIgnoreCase("toggle")) {
            //get all arenas
            arenas = SPArena.getSPInstances();
            //cycle through al arenas
            for (SPArena key : arenas) {
                //add arena arg
                arguments.add(key.getName());
            }
        }
        //delete, regenerate, and select args
        if (args.length == 3  && !(args[0].equalsIgnoreCase("hologram")) && (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("regenerate") || args[1].equalsIgnoreCase("select") || args[1].equalsIgnoreCase("chests") || args[1].equalsIgnoreCase("schematic"))) {
            //get all arenas
            arenas = SPArena.getSPInstances();
            //cycle through al arenas
            for (SPArena key : arenas) {
                //add arena arg
                arguments.add(key.getName());
            }
        }
        //spawn command args
        if (args.length == 3 && args[1].equalsIgnoreCase("spawn")) {
            arguments.add("create");
            arguments.add("delete");
            arguments.add("list");
        }
        //hologram create args
        if (args.length == 3 && args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("create")) {
            //cycle through leaderboards
            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                //check if not summoned
                if (key.getSummoned() == false) {
                    arguments.add(key.getName());
                }
            }
        }
        //hologram delete args
        if (args.length == 3 && args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("delete")) {
            //cycle through leaderboards
            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                //check if summoned
                if (key.getSummoned() == true) {
                    arguments.add(key.getName());
                }
            }
        }
        //hologram refresh args
        if (args.length == 3 && args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("refresh")) {
            //cycle through leaderboards
            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                //check if summoned
                if (key.getSummoned() == true) {
                    arguments.add(key.getName());
                }
            }
        }
        //toggle args
        if (args.length == 4 && args[1].equalsIgnoreCase("toggle")) {
            arguments.add("on");
            arguments.add("off");
        }
        //edit, spawn args
        if (args.length == 4 && (args[1].equalsIgnoreCase("edit") || args[1].equalsIgnoreCase("spawn"))) {
            //get all arenas
            arenas = SPArena.getSPInstances();
            //cycle through al arenas
            for (SPArena key : arenas) {
                //add arena arg
                arguments.add(key.getName());
            }
        }
        //return tab-completion
        return arguments;
    } 
}