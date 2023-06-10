package com.budderman18.IngotSpleefPlus.Core;

import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotMinigamesAPI.Core.Data.IngotPlayer;
import com.budderman18.IngotSpleefPlus.Main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 *
 * This class handles player data specific to IngotSpleef+
 * It is based off of IMAPI's ingotplayer
* 
 */
public class SPPlayer extends IngotPlayer {
    //player vars
    private String name = null;
    private short blocksBroken = 0;
    private boolean canJump = true;
    private boolean isFrozen = false;
    private int index = 0;
    //global vars
    private static Plugin plugin = Main.getInstance();
    private static FileConfiguration config = FileManager.getCustomData(plugin, "config", "");
    private static ArrayList<SPPlayer> players = new ArrayList<>();
    private static int trueIndex = 0;
    /**
     * 
     * This constructor blocks new() usage
     * 
     */
    private SPPlayer() {}
    /**
     *
     * This method creates a new spplayer.It will also save the settings file.Use createPlayerSchematic() for the region file
     *
     * @param iplayer the player name
     * @param inGamee the ingame
     * @param isPlayingg the is playing
     * @param isAlivee the is alive
     * @param deathss the deaths
     * @param winss the wins
     * @param killss the kills
     * @param scoree the score
     * @param gamee the game
     * @param jumpp the isJumping
     * @param lossess the losses
     * @param brokenn the isBroken
     * @param isFrozenn the isFrozen
     * @return The player object that was generated
     */
    public static SPPlayer createPlayer(String iplayer, boolean inGamee, boolean isPlayingg, boolean isAlivee, int killss, short deathss, short winss, short lossess, short scoree, String gamee, short brokenn, boolean jumpp, boolean isFrozenn) {
        //newPlayer
        IngotPlayer extendedPlayer = null;
        SPPlayer player = null;
        //check if unextended player needs creation
        if (IngotPlayer.selectPlayer(iplayer, plugin) == null) {
            //create player
            extendedPlayer = IngotPlayer.createPlayer(iplayer, inGamee, isPlayingg, isAlivee, killss, deathss, winss, lossess, scoree, gamee, plugin);    
        }
        else {
            //select player
            extendedPlayer = IngotPlayer.selectPlayer(iplayer, plugin);
        }
        //convert loaded Player into this SPPlayer
        player = SPPlayer.castToSPPlayer(extendedPlayer);
        //set vars
        player.name = iplayer;
        player.blocksBroken = brokenn;
        player.canJump = jumpp;
        player.isFrozen = isFrozenn;
        player.index = trueIndex;
        //add and return player
        players.add(player);
        trueIndex++;
        return player;
    }
    /**
     *
     * This method deletes the selected spplayer. 
     *
     * @param leaveExtention false to not delete the Player equivelent
     */
    public void deletePlayer(boolean leaveExtention) {
        //check if deleting extension
        if (leaveExtention == false && IngotPlayer.selectPlayer(this.name, this.plugin) != null) {
            IngotPlayer.selectPlayer(this.name, this.plugin).deletePlayer();
        }
        //decrement all higher indexes to prevent bugs
        for (SPPlayer key : players) {
            if (key.index > this.index) {
                players.get(key.index).index--;
            }
        }
        //reset data
        players.remove(this.index);
        this.blocksBroken = 0;
        this.canJump = false;
        this.isFrozen = false;
        this.name = null;
        this.index = 0;
        trueIndex--;
    }
    /**
     *
     * This method selects and returns a given spplayer.
     * Useful for swapping between loaded players. 
     *
     * @param namee the name to use when searching
     * @param pluginn the plugin to use when searching
     * @return the spplayer that was located
     */
    public static SPPlayer selectPlayer(String namee) {
        //cycle between all instances of player
        for (SPPlayer key : players) {
            //check if player incstanc e name isn't null
            if (key.getUsername() != null) {
                //check if player is what is requested
                if (key.getUsername().equals(namee)) {
                    //set selection data
                    return key;
                }
            }
        }
        //send error and return null
        if (config.getBoolean("enable-debug-mode") == true && (namee != null || !"".equals(namee))) {
            Logger.getLogger(SPPlayer.class.getName()).log(Level.SEVERE, "COULD NOT LOAD PLAYER " + namee + '!');
        }
        return null;
    }
    /**
     * 
     * This method gets all loaded spplayer instances
     * Instances with null names are ignored (they shouldn't exist)
     * 
     * @return The list of players
     */
    public static ArrayList<SPPlayer> getSPInstances() {
        //local vars
        ArrayList<SPPlayer> playerss = new ArrayList<>();
        //cycle through players
        for (SPPlayer key : players) {
            //check if name isnt null
            if (key.name != null) {
                //ass player
                playerss.add(key);
            }
            else {
                //delete invalid player
                key.deletePlayer(false);
            }
        }
        return playerss;
    }
    /**
     * 
     * This method saves to the playerdata file
     * 
     */
    @Override
    public void saveToFile() {
        //local vars
        File playerdataf = new File(this.plugin.getDataFolder(), "playerdata.yml");
        FileConfiguration playerdata = FileManager.getCustomData(this.plugin, "playerdata", "");
        //set file
        playerdata.set(this.name + ".wins", this.getIngotPlayerEquivelent().getWins());
        playerdata.set(this.name + ".losses", this.getIngotPlayerEquivelent().getLosses());
        playerdata.set(this.name + ".score", this.getIngotPlayerEquivelent().getScore());
        //save file
        try {
            playerdata.save(playerdataf);
        } 
        catch (IOException ex) {
            //check for debug mode
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(IngotPlayer.class.getName()).log(Level.SEVERE, "COULD NOT SAVE PLAYERDATA.YML!");
            }
        }
    }
    /**
     * 
     * This method casts an Player into an SPPlayer
     * You shouldnt need to use this, but if you're having issues with player
     * vars but not spplayer vars, try this.
     * 
     * @param player the spplayer to cast
     * @return the player
     */
    public static SPPlayer castToSPPlayer(IngotPlayer player) {
        //local vars
        SPPlayer newPlayer = new SPPlayer();
        int indexx = 0;
        //convert data
        newPlayer.setPlugin(player.getPlugin());
        newPlayer.setUsername(player.getUsername());
        newPlayer.setInGame(player.getInGame());
        newPlayer.setIsPlaying(player.getIsPlaying());
        newPlayer.setIsAlive(player.getIsAlive());
        newPlayer.setInventory(player.getInventory());
        newPlayer.setEffects(player.getEffects());
        newPlayer.setXP(player.getXP());
        newPlayer.setTeam(player.getTeam(), false);
        newPlayer.setGame(player.getGame());
        newPlayer.setHealth(player.getHealth());
        newPlayer.setKills(player.getKills());
        newPlayer.setDeaths(player.getDeaths());
        newPlayer.setWins(player.getWins());
        newPlayer.setLosses(player.getLosses());
        newPlayer.setScore(player.getScore());
        //cycle through players
        for (IngotPlayer key : IngotPlayer.getInstances(plugin)) {
            //check if player matches this one
            if (key.getUsername().equalsIgnoreCase(player.getUsername())) {
                //set index
                newPlayer.index = indexx;
                break;
            }
            indexx++;
        }
        return newPlayer;
    }
    /**
     * 
     * This method gets the Player equivelent of the selected SPPlayer
     * If you're having problems with player methods, but not spplayer methods,
     * use this then put the method though this player
     * 
     * @return the player equivelent
     */
    public IngotPlayer getIngotPlayerEquivelent() {
        return IngotPlayer.selectPlayer(this.name, this.plugin);
    }
    /**
     *
     * This method changes the name of the current player.
     * It will prevent setting to null, which breaks selections
     *
     * @param namee the name to set
     */
    @Override
    public void setUsername(String namee) {
        //check if name is null
        if (namee != null) {           
            //check for valid player
            if (IngotPlayer.selectPlayer(this.name, this.plugin) != null) {
                //set unextended plugin
                IngotPlayer.selectPlayer(this.name, this.plugin).setUsername(namee);
            }
            else {
                //check for debug mode
                if (config.getBoolean("enable-debug-mode") == true) {
                    Logger.getLogger(SPPlayer.class.getName()).log(Level.SEVERE, "YOU'RE SETTING AN SWARENA FIELD BUT THERE'S NO ARENA EQUIVELENT!");
                }
            }
            //set instance list
            if (players.contains(this)) {
                players.get(this.index).name = namee;
            }
            //set selection
            this.name = namee;
        }
        else {
            //check for debug mode
            if (config.getBoolean("enable-debug-mode") == true) {
                //log error
                Logger.getLogger(IngotPlayer.class.getName()).log(Level.SEVERE, "COULD NOT SET THE NAME FOR ARENA " + this.name + '!');
            }
        }
    }
    /**
     *
     * This method gets the current player's name. 
     *
     * @return the player name
     */
    @Override
    public String getUsername() {
        //return instance list
        if (players.contains(this)) {
            return players.get(this.index).name;
        }
        //return selection as a fallback
        return this.name;
    }
    /**
     *
     * This method sets the blocks broken in the selected spplayer.
     *
     * @param brokenn the blocks broken to set
     */
    public void setBlocksBroken(short brokenn) {
        //set instance list
        if (players.contains(this)) {
            players.get(this.index).blocksBroken = brokenn;
        }
        //set selection
        this.blocksBroken = brokenn;
    }
    /**
     *
     * This method gets the type in the selected spplayer.
     *
     * @return the type
     */
    public short getBlocksBroken() {
        //return instance array
        if (players.contains(this)) {
            return players.get(this.index).blocksBroken;
        }
        //return selection as a fallback
        return this.blocksBroken;
    }
    /**
     *
     * This method sets the blocks broken in the selected spplayer.
     *
     * @param jumpp weather they can double jump
     */
    public void setCanJump(boolean jumpp) {
        //set instance list
        if (players.contains(this)) {
            players.get(this.index).canJump = jumpp;
        }
        //set selection
        this.canJump = jumpp;
    }
    /**
     *
     * This method gets the canJump in the selected spplayer.
     *
     * @return the canJump
     */
    public boolean getCanJump() {
        //return instance array
        if (players.contains(this)) {
            return players.get(this.index).canJump;
        }
        //return selection as a fallback
        return this.canJump;
    }
    /**
     *
     * This method sets the isfrozen in the selected spplayer.
     *
     * @param frozenn the isFrozen to set
     */
    public void setIsFrozen(boolean frozenn) {
        //set instance list
        if (players.contains(this)) {
            players.get(this.index).isFrozen = frozenn;
        }
        //set selection
        this.isFrozen = frozenn;
    }
    /**
     *
     * This method gets the isFrozen in the selected spplayer.
     *
     * @return the isFrozen
     */
    public boolean getIsFrozen() {
        //return instance array
        if (players.contains(this)) {
            return players.get(this.index).isFrozen;
        }
        //return selection as a fallback
        return this.isFrozen;
    }
}
