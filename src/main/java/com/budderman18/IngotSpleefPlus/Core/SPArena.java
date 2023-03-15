package com.budderman18.IngotSpleefPlus.Core;

import com.budderman18.IngotMinigamesAPI.Core.Data.Arena;
import com.budderman18.IngotMinigamesAPI.Core.Data.ArenaStatus;
import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotSpleefPlus.Main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Egg;
import org.bukkit.plugin.Plugin;

/**
 *
 * This class handles arena data specific to ingotSkywars
 * It is based off of IMAPI's arena
* 
 */
public class SPArena extends Arena {
    //arena vars
    private String name = "";
    private Plugin plugin = null;
    private GameType type = null;
    List<Egg> launchedEggs = null;
    private int index = 0;
    //global vars
    private static Plugin staticPlugin = Main.getInstance();
    private static FileConfiguration config = FileManager.getCustomData(staticPlugin, "config", "");
    private static List<SPArena> arenas = new ArrayList<>();
    private static int trueIndex = 0;
    /**
     * 
     * This constructor blocks new() usage
     * 
     */
    private SPArena() {}
    /**
     *
     * This method creates a new sparena.It will also save the settings file.
     * Use createArenaSchematic() for the region file
     *
     * @param pos1 the negative-most corner of the arena
     * @param pos2 the positive-most corner of the arena
     * @param worldd the world name the arena is in
     * @param arenaName the arena's name
     * @param minPlayerss the minimum amount of players that can play
     * @param skipPlayerss the amount of players needed to shorten the wait timer
     * @param maxPlayerss the max amount of players that can play
     * @param teamSizee the max team size
     * @param lobbyWaitTimee the amount of time the lobby lasts
     * @param gameWaitTimee the amount of time needed for the game to start once entered
     * @param gameLengthh the length of the game
     * @param lobbySkipTimee the amount of time set to when skipPLayers is reached
     * @param lobbyWorldd the world name for the lobby
     * @param filePathh the filePath for the arena's files
     * @param exitWorldd the world name players get moved to when leaving
     * @param exitPoss the position players get moved to when leaving
     * @param centerPoss the center of the arena
     * @param lobbyPoss the position players get teleported to when joining
     * @param statuss the arena status
     * @param type the type of game this is
     * @param eggs the eggs launched
     * @param specPoss the position players are teleported to when spectating
     * @param saveFilee weather or not to save a settings file
     * @param permName name of the permission to add
     * @param pluginn the plugin to attach this arena to
     * @return The arena object that was generated
     */
    public static SPArena createArena(int[] pos1, int[] pos2, String worldd, String arenaName, byte minPlayerss, byte skipPlayerss, byte maxPlayerss, byte teamSizee, int lobbyWaitTimee, int lobbySkipTimee, int gameWaitTimee, int gameLengthh, double[] lobbyPoss, String lobbyWorldd, double[] exitPoss, String exitWorldd, double[] specPoss, double[] centerPoss, ArenaStatus statuss, GameType type, List<Egg> eggs, String filePathh, boolean saveFilee, String permName, Plugin pluginn) {
        //newArena
        Arena extendedArena = null;
        SPArena arena = new SPArena();
        //files
        File arenaDataf = new File(pluginn.getDataFolder() + filePathh, "settings.yml");
        FileConfiguration arenaData = FileManager.getCustomData(pluginn, "settings", filePathh);
        //check if unextended arena needs creation
        if (Arena.selectArena(arenaName, pluginn) == null) {
            //create arena
            extendedArena = Arena.createArena(pos1, pos2, worldd, arenaName, minPlayerss, skipPlayerss, maxPlayerss, teamSizee, lobbyWaitTimee, lobbySkipTimee, gameWaitTimee, gameLengthh, lobbyPoss, lobbyWorldd, exitPoss, exitWorldd, specPoss, centerPoss, filePathh, saveFilee, statuss, permName, pluginn);    
        }
        else {
            //select arena
            extendedArena = Arena.selectArena(arenaName, pluginn);
        }
        //set selection vars
        arena.name = arenaName;
        arena.plugin = pluginn;
        //convert loaded Arena into this SPArena
        arena = SPArena.castToSPArena(extendedArena);
        //set vars
        if (type == null) {
            type = GameType.SPLEEF;
        }
        arena.type = type;
        if (eggs == null) {
            eggs = new ArrayList<>();
        }
        arena.launchedEggs = eggs;
        arena.index = trueIndex;
        //check if saving file
        if (saveFilee == true && type != null) {
            //drop vars
            arenaData.set("game-type", type.name());
            //save file
            try {
                arenaData.save(arenaDataf);
            } 
            catch (IOException ex) {
                if (config.getBoolean("enable-debug-mode") == true) {
                    Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, "COULD NOT SAVE SETTINGS.YML FOR ARENA " + arenaName + '!');
                }
            }
        }
        //add and return arena
        arenas.add(arena);
        trueIndex++;
        return arena;
    }
    /**
     *
     * This method deletes the selected sparena. 
     *
     * @param leaveExtention false to not delete the Arena equivelent
     * @param deleteFiles true to delete the file folder
     */
    public void deleteArena(boolean leaveExtention, boolean deleteFiles) {
        //check if deleting extension without saving
        if (leaveExtention == false && deleteFiles == false) {
            Arena.selectArena(this.name, this.plugin).deleteArena(false);
        }
        //check if deleting extension with saving
        else if (leaveExtention == false && deleteFiles == true) {
            Arena.selectArena(this.name, this.plugin).deleteArena(true);
        }
        //check if deleting files
        else if (deleteFiles == true) {
            new File(plugin.getDataFolder() + "/" + this.getFilePath()).delete();
        }
        //decrement all higher indexes to prevent bugs
        for (SPArena key : arenas) {
            if (key.index > this.index) {
                arenas.get(key.index).index--;
            }
        }
        //reset data
        arenas.remove(this.index);
        this.type = null;
        this.launchedEggs = null;
        this.name = null;
        this.plugin = null;
        this.index = 0;
        trueIndex--;
    }
    /**
     *
     * This method selects and returns a given sparena.
     * Useful for swapping between loaded arenas. 
     *
     * @param namee the name to use when searching
     * @param pluginn the plugin to use when searching
     * @return the sparena that was located
     */
    public static SPArena selectArena(String namee, Plugin pluginn) {
        //cycle between all instances of arena
        for (SPArena key : arenas) {
            //check if arena incstanc e name isn't null
            if (key.getName() != null) {
                //check if arena is what is requested
                if (key.getName().equals(namee) && key.getPlugin() == pluginn) {
                    //set selection data
                    return key;
                }
            }
        }
        //send error and return null
        if (config.getBoolean("enable-debug-mode") == true && (namee != null || !"".equals(namee))) {
            Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, "COULD NOT LOAD ARENA " + namee + '!');
        }
        return null;
    }
    /**
     * 
     * This method gets all loaded sparena instances
     * Instances with null names are ignored (they shouldn't exist)
     * 
     * @return The list of arenas
     */
    public static List<SPArena> getSPInstances() {
        //local vars
        List<SPArena> arenass = new ArrayList<>();
        //cycle through arenas
        for (SPArena key : arenas) {
            //check if name isnt null
            if (key.name != null) {
                //ass arena
                arenass.add(key);
            }
            else {
                //delete invalid arena
                key.deleteArena(false, false);
            }
        }
        return arenass;
    }
    /**
     * 
     * This method saves the settings files
     * 
     * @param onlyArena false to exclude SPArena specific vars
     */
    public void saveFiles(boolean onlyArena) {
        //local vars
        File arenaDataf = new File(this.plugin.getDataFolder() + this.getArenaEquivelent().getFilePath(), "settings.yml");
        FileConfiguration arenaData = null;
        //save arena vars
        Arena.selectArena(this.name, this.plugin).saveFiles();
        //check if using only Arena vars
        if (onlyArena == false) {
            //reload file
            arenaData = FileManager.getCustomData(this.plugin, "settings", this.getArenaEquivelent().getFilePath());//set file
            //set file
            if (this.type == null) {
                this.type = GameType.SPLEEF;
            }
            arenaData.set("game-type", this.type.name());
            //save file
            try {
                arenaData.save(arenaDataf);
            } 
            catch (IOException ex) {
                //check for debug mode
                if (config.getBoolean("enable-debug-mode") == true) {
                    //log error
                    Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, "COULD NOT SAVE SETTINGS.YML FOR ARENA " + this.name + '!');
                }
            }
        }
    }
    /**
     * 
     * This method casts an Arena into an SPArena
     * You shouldnt need to use this, but if you're having issues with arena
     * vars but not sparena vars, try this.
     * 
     * @param arena the sparena to cast
     * @return the arena
     */
    public static SPArena castToSPArena(Arena arena) {
        //local vars
        SPArena newArena = new SPArena();
        int indexx = 0;
        //convert data
        newArena.setPlugin(arena.getPlugin());
        newArena.setName(arena.getName());
        newArena.setPos1(arena.getPos1());
        newArena.setPos2(arena.getPos2());
        newArena.setWorld(arena.getWorld());
        newArena.setMinPlayers(arena.getMinPlayers());
        newArena.setSkipPlayers(arena.getSkipPlayers());
        newArena.setMaxPlayers(arena.getMaxPlayers());
        newArena.setSpawns(arena.getSpawns());
        newArena.setTeams(arena.getTeams());
        newArena.setTeamSize(arena.getTeamSize());
        newArena.setLobby(arena.getLobby());
        newArena.setLobbyWorld(arena.getLobbyWorld());
        newArena.setExit(arena.getExit());
        newArena.setExitWorld(arena.getExitWorld());
        newArena.setCenter(arena.getCenter());
        newArena.setSpectatorPos(arena.getSpectatorPos());
        newArena.setLobbyWaitTime(arena.getLobbyWaitTime());
        newArena.setLobbySkipTime(arena.getLobbySkipTime());
        newArena.setGameWaitTime(arena.getGameWaitTime());
        newArena.setGameLengthTime(arena.getGameLengthTime());
        newArena.setStatus(arena.getStatus());
        newArena.setPermission(arena.getPermission());
        newArena.setCurrentPlayers(arena.getCurrentPlayers());
        newArena.setFilePath(arena.getFilePath());
        //cycle through arenas
        for (Arena key : Arena.getInstances(staticPlugin)) {
            //check if arena matches this one
            if (key.getName().equalsIgnoreCase(arena.getName())) {
                //set index
                newArena.index = indexx;
                break;
            }
            indexx++;
        }
        return newArena;
    }
    /**
     * 
     * This method gets the Arena equivelent of the selected SPArena
     * If you're having problems with arena methods, but not sparena methods,
     * use this then put the method though this arena
     * 
     * @return the arena equivelent
     */
    public Arena getArenaEquivelent() {
        return Arena.selectArena(this.name, this.plugin);
    }
    /**
     *
     * This method changes the name of the current arena.
     * It will prevent setting to null, which breaks selections
     *
     * @param namee the name to set
     */
    @Override
    public void setName(String namee) {
        //check if name is null
        if (namee != null) {           
            //check for valid arena
            if (Arena.selectArena(this.name, this.plugin) != null) {
                //set unextended plugin
                Arena.selectArena(this.name, this.plugin).setName(namee);
            }
            else {
                //check for debug mode
                if (config.getBoolean("enable-debug-mode") == true) {
                    Logger.getLogger(SPArena.class.getName()).log(Level.SEVERE, "YOU'RE SETTING AN SWARENA FIELD BUT THERE'S NO ARENA EQUIVELENT!");
                }
            }
            //set instance list
            if (arenas.contains(this)) {
                arenas.get(this.index).name = namee;
            }
            //set selection
            this.name = namee;
        }
        else {
            //check for debug mode
            if (config.getBoolean("enable-debug-mode") == true) {
                //log error
                Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, "COULD NOT SET THE NAME FOR ARENA " + this.name + '!');
            }
        }
    }
    /**
     *
     * This method gets the current arena's name. 
     *
     * @return the arena name
     */
    @Override
    public String getName() {
        //return instance list
        if (arenas.contains(this)) {
            return arenas.get(this.index).name;
        }
        //return selection as a fallback
        return this.name;
    }
    /**
     *
     * This method sets the type in the selected arena.
     *
     * @param typee the type to set
     */
    public void setType(GameType typee) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).type = typee;
        }
        //set selection
        this.type = typee;
    }
    /**
     *
     * This method gets the type in the selected arena.
     *
     * @return the type
     */
    public GameType getType() {
        //return instance array
        if (arenas.contains(this)) {
            return arenas.get(this.index).type;
        }
        //return selection as a fallback
        return this.type;
    }
    
    /**
     *
     * This method adds an egg to the selected arena. 
     *
     * @param eggg the egg object to add
     */
    public void addEgg(Egg eggg) {
        //add instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).launchedEggs.add(eggg);
        }
        //add selection
        this.launchedEggs.add(eggg);
    }
    /**
     *
     * This method deletes the given egg from the selected arena. 
     *
     * @param eggg the egg object to remove
     */
    public void removeEgg(Egg eggg) {
        //remove instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).launchedEggs.remove(eggg);
        }
        //remove selection
        this.launchedEggs.remove(eggg);
    }
    /**
     *
     * This method sets the eggs for the selected arena. 
     *
     * @param eggs the list to set
     */
    public void setEggs(List<Egg> eggs) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).launchedEggs = eggs;
        }
        //set selection
        this.launchedEggs = eggs;
    }
    /**
     *
     * This method gets all the spawns from the selected arena
     *
     * @return the team list
     */
    public List<Egg> getEggs() {
        //return instance list
        if (arenas.contains(this)) {
            return arenas.get(this.index).launchedEggs;
        }
        //return selection as a fallback
        return this.launchedEggs;
    }
    /**
     *
     * This method sets the plugin for the selected arena. 
     *
     * @param pluginn the plugin to set
     */
    @Override
    public void setPlugin(Plugin pluginn) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).plugin = pluginn;
        }
        //set selection
        this.plugin = pluginn;
        //check fir valid arena
        if (Arena.selectArena(this.name, pluginn) != null) {
            //set unextended plugin
            Arena.selectArena(this.name, this.plugin).setPlugin(pluginn);
        }
        else {
            //check for debug mode
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(SPArena.class.getName()).log(Level.SEVERE, "YOU'RE SETTING AN SWARENA FIELD BUT THERE'S NO ARENA EQUIVELENT!");
            }
        }
    }
    /**
     *
     * This method gets the plugin for the selected arena. 
     *
     * @return the plugin object
     */
    @Override
    public Plugin getPlugin() {
        //return instance list
        if (arenas.contains(this)) {
            return arenas.get(this.index).plugin;
        }
        //return selection as a fallback
        return this.plugin;
    }
}
