package com.budderman18.IngotSpleefPlus.Bukkit;

import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotMinigamesAPI.Core.Data.Leaderboard;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.ChatHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.DeathMessageHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.DeathMessageType;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TimerHandler;
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
import org.bukkit.GameMode;
import static org.bukkit.GameMode.ADVENTURE;
import static org.bukkit.GameMode.SPECTATOR;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import static org.bukkit.entity.EntityType.PLAYER;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 *
 * This class handles protection-based events. 
 * You can block building, breaking, and block/entity interactions
 * This contains events and has to be within every plugin, 
 * rather than in IngotMiniGamesAPI. 
 * 
 */
public class Events implements Listener {
    //plugin
    private static Plugin plugin = Main.getInstance();
    //files
    private static final String ROOT = "";
    private static FileConfiguration config = FileManager.getCustomData(plugin,"config",ROOT);
    private static FileConfiguration language = FileManager.getCustomData(plugin,"language",ROOT);
    //messages
    private static String prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message") + "");
    private static String unusableCommandMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unusable-Command-Message") + "");
    /**
     * 
     * This method reloads the config file
     * 
     */
    public static void reload() {
        config = FileManager.getCustomData(plugin,"config",ROOT);
        language = FileManager.getCustomData(plugin,"language",ROOT);
        //messages
        prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message") + "");
        unusableCommandMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unusable-Command-Message") + "");
    }
    /**
     *
     * This method handles block breaking. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        //obtain SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == false) {  
            //block event
            event.setCancelled(true);
        }
        //check if playing
        if (iplayer.getIngotPlayerEquivelent().getIsPlaying() == true) {
            //add broken block
            SPPlayer.selectPlayer(iplayer.getIngotPlayerEquivelent().getUsername()).setBlocksBroken((short) (SPPlayer.selectPlayer(iplayer.getIngotPlayerEquivelent().getUsername()).getBlocksBroken()+1));
            event.setDropItems(false);
        }
    }
    /**
     *
     * This method handles block placing. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        //get SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == false) {  
            //block event
            event.setCancelled(true);
        }
    } 
    /**
     *
     * This method handles item dropping 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        //get SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if protection is enabled and player is inGame
        if (iplayer.getIngotPlayerEquivelent().getInGame() == true || iplayer.getIngotPlayerEquivelent().getIsPlaying() == true) { 
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles block interactions
     *
     * @param event the event ran
     */
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {       
        //get SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == false) {  
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles entity interactions (right click). 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        //get SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == false) {  
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles armor stand interactions. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onStandInteract(PlayerArmorStandManipulateEvent event) {
        //get SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == false) {  
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles armor stand interactions. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onFrameInteract(PlayerInteractAtEntityEvent event) {
        //get SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == false) {  
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles lecturn interactions. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onLecturnInteract(PlayerTakeLecternBookEvent event) {
        //get SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == false) {  
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     * 
     * This method handles player interactions
     * 
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        //local vars
        Egg egg = null;
        //get SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if playing
        if (iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == true) {
            //check if game is splegg and was a right click
            if (SPArena.selectArena(iplayer.getIngotPlayerEquivelent().getGame()).getType() == GameType.SPLEGG && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                //spawn egg
                egg = event.getPlayer().launchProjectile(Egg.class);
                SPArena.selectArena(iplayer.getIngotPlayerEquivelent().getGame()).addEgg(egg);
            }
        }
    }
    /**
     * 
     * This method handled projectile hits
     * 
     * @param event the event ran
     */
    @EventHandler
    public void onProjetileHit(ProjectileHitEvent event) {
        //local vars
        boolean found = false;
        SPArena arena = null;
        //used to stop chicken spawns
        Runnable action = () -> {
            for (Entity key : event.getEntity().getNearbyEntities(2, 2, 2)) {
                if (key.getType() == EntityType.CHICKEN) {
                    key.remove();
                }
            }
        };
        //check if projectile is an egg
        if (event.getEntity().getType() == EntityType.EGG) {
            //cycle through arenas
            for (SPArena key : SPArena.getSPInstances()) {
                //check if egg is in this 
                if (key.getEggs().contains((Egg) event.getEntity())) {
                    //mark as found and stop
                    found = true;
                    arena = key;
                    break;
                }
            }
            //check if found
            if (found == true && event.getHitBlock() != null) {
                //check if hit block is in arena
                if (arena.getArenaEquivelent().isInArena(event.getHitBlock().getLocation()) == true) {
                    //remove egg and set to air
                    arena.removeEgg((Egg) event.getEntity());
                    event.getHitBlock().setType(Material.AIR);
                }
            }
            //remove spawned chicken
            if (found == true) {
                TimerHandler.runTimer(plugin, 0, 1, action, true, false);
            }
        }
    }
    /**
     * 
     * This method handles command processing
     * 
     * @param event the event ran
     */
    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        //get SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if commands are disable
        if (iplayer.getIngotPlayerEquivelent().getInGame() == true && config.getBoolean("Commands.enable") == true && !event.getPlayer().hasPermission("ingotsw.bypass")) {
            //cancel event
            event.setCancelled(true);
            //cycle through allowed commands
            for (String key : config.getStringList("Commands.allowed-commands")) {
                //check if allowed command
                if (event.getMessage().startsWith("/" + key)) {
                    event.setCancelled(false);
                }
            }
            //should never run, but here cause operm check above can fail
            if (event.getPlayer().hasPermission("ingotsw.bypass")) {
                event.setCancelled(false);
            }
            //check if cancelled
            if (event.isCancelled() == true) {
                //tell player
                event.getPlayer().sendMessage(prefixMessage + unusableCommandMessage);
            }
        }
    }
    /**
     * 
     * This method handles flight toggling
     * 
     * @param event the event ran
     */
    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent event) {
        //get SPPlayerdata
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        Runnable actionn = null;
        //check if player is in game and playing
        if (config.getBoolean("DoubleJump.enable") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == true && iplayer.getCanJump() == true) {  
            //launch player
            event.getPlayer().setVelocity(new Vector(event.getPlayer().getVelocity().getX(),config.getInt("DoubleJump.velocity"),event.getPlayer().getVelocity().getZ()));
            event.getPlayer().playSound(event.getPlayer(), Sound.valueOf(config.getString("DoubleJump.sound").toUpperCase()), 10, 1);
            iplayer.setCanJump(false);
            actionn = () -> {
                iplayer.setCanJump(true);
            };
            TimerHandler.runTimer(plugin, 0, config.getInt("DoubleJump.delay"), actionn, false, true);
            event.setCancelled(true);
            event.getPlayer().setVelocity(new Vector(event.getPlayer().getVelocity().getX(),event.getPlayer().getVelocity().getY()-0.1,event.getPlayer().getVelocity().getZ()));
        }
        else if (config.getBoolean("DoubleJump.enable") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == true ) {
            event.getPlayer().setVelocity(new Vector(event.getPlayer().getVelocity().getX(),event.getPlayer().getVelocity().getY()-0.1,event.getPlayer().getVelocity().getZ()));
            event.setCancelled(true);
        }
    }
    /**
     * 
     * This method handles entity damage
     * 
     * @param event the event ran
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {            
        //local vars
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getEntity().getName());
        //check if entity is a player
        if (event.getEntity().getType() == PLAYER && SPPlayer.selectPlayer(event.getEntity().getName()) != null) {
            //check if in lobby with protection or ingame
            if (config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == false) {
                event.setCancelled(true);
            }
            if (iplayer.getIngotPlayerEquivelent().getIsPlaying() == true && (event.getCause() == DamageCause.FALL || event.getCause() == DamageCause.VOID || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.BLOCK_EXPLOSION)) {
                event.setCancelled(false);
            }
        }
    }
    /**
     *
     * This method handles attacking entities. 
     * Players are not protected while playing. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        //local vars
        boolean cancel = false;
        SPPlayer iplayer = null;
        //check if entity is an armor stand
        if (event.getEntity().getType() == EntityType.ARMOR_STAND) {
            //cycle through leaderboards
            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                //cycle through hologram
                for (ArmorStand keys : key.getHologram()) {
                    //check if entity is apart of a hologram
                    if (keys == event.getEntity()) {
                        cancel = true;
                    }
                }
            }
            event.setCancelled(cancel);
        }
        //check if entity is a player and killer is a player
        if (event.getEntityType() == PLAYER && event.getDamager().getType() == PLAYER && SPPlayer.selectPlayer(event.getDamager().getName()) != null) {
            //get SPPlayerdata
            iplayer = SPPlayer.selectPlayer(event.getDamager().getName());
            //check if protection is enabled and player is ingame
            if ((config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true) || iplayer.getIngotPlayerEquivelent().getIsPlaying() == true) {  
                //cancel event
                event.setCancelled(true);
            }
        }
        //check if entity is a player
        else if (event.getDamager().getType() == PLAYER) {
            //reset death vars
            iplayer = SPPlayer.selectPlayer(event.getDamager().getName());
            //check if protection is enable
            if (config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true) {
                //dont damage
                event.setCancelled(true);
            }
        }
        //check if damagee is a player
        else if (event.getEntity().getType() == PLAYER && SPPlayer.selectPlayer(event.getEntity().getName()) != null) {
            //reset death vars
            iplayer = SPPlayer.selectPlayer(event.getEntity().getName());
            //check if protection is enable
            if (config.getBoolean("enable-protection") == true && iplayer.getIngotPlayerEquivelent().getInGame() == true) {
                //dont damage
                event.setCancelled(true);
            }
        }
    }
    /**
     * 
     * This method handles entity deaths
     * 
     * @param event the event ran
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        //local vars
        boolean cancel = false;
        //check if entity is an armor stand
        if (event.getEntity().getType() == EntityType.ARMOR_STAND && config.getBoolean("protect-holograms-from-kill-commands") == true) {
            //cycle through leaderboards
            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                //cycle through hologram
                for (ArmorStand keys : key.getHologram()) {
                    //check if entity is apart of a hologram
                    if (keys == event.getEntity() && key.getSummoned() == true) {
                        cancel = true;
                    }
                }
                //check if cancleed
                if (cancel == true) {
                    //summon hologram
                    key.summonHologram(config.getString("Leaderboard.header"), config.getString("Leaderboard.format"), config.getString("Leaderboard.footer"), true);
                    cancel = false;
                }
            }
        }
    }
    /**
     *
     * This method handles player movement(including rotation changes).
     *
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        //local vars
        Location frozenpos = event.getTo();
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //get arena
        SPArena selectedArena = null;
        if (iplayer.getIngotPlayerEquivelent().getGame() != null || !"".equals(iplayer.getIngotPlayerEquivelent().getGame())) {
            selectedArena = SPArena.selectArena(iplayer.getIngotPlayerEquivelent().getGame());
        }
        //check if player is froxen
        if (iplayer.getIsFrozen() == true) {
            //reset position to current position
            frozenpos.setX(event.getFrom().getX());
            frozenpos.setY(event.getFrom().getBlockY());
            frozenpos.setZ(event.getFrom().getZ());
            //teleport player
            event.getPlayer().teleport(frozenpos);
        }
        //check if player is spectating and in arena
        if (iplayer.getIngotPlayerEquivelent().getGame() != null || !"".equals(iplayer.getIngotPlayerEquivelent().getGame())) {
            if (iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsAlive() == false && event.getPlayer().getGameMode() == SPECTATOR && selectedArena.getArenaEquivelent().isInArena(event.getPlayer().getLocation()) == false) {
                //move player to center
                frozenpos = event.getPlayer().getLocation();
                frozenpos.setX(selectedArena.getArenaEquivelent().getCenter()[0]);
                frozenpos.setY(selectedArena.getArenaEquivelent().getCenter()[1]);
                frozenpos.setZ(selectedArena.getArenaEquivelent().getCenter()[2]);
                frozenpos.setYaw((float) selectedArena.getArenaEquivelent().getCenter()[3]);
                frozenpos.setPitch((float) selectedArena.getArenaEquivelent().getCenter()[4]);
                event.getPlayer().teleport(frozenpos);
            }
        }
    }
    /**
     *
     * This method handles player chatting. 
     *
     * @param event the event ran
     */
    @EventHandler    
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        //local vars
        List<Player> players = new ArrayList<>();
        SPPlayer currentIPlayer = null;
        Player player = null;
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if player is inGame and chat is enabled
        if (iplayer.getIngotPlayerEquivelent().getInGame() == true && config.getBoolean("Chat.enable") == true) {
            //cancel event
            event.setCancelled(true);
            //cycle between all players
            for (Player key : Bukkit.getOnlinePlayers()) {
                //get SPPlayer
                currentIPlayer = SPPlayer.selectPlayer(key.getName());
                //checfk if player is inGame
                if (currentIPlayer.getIngotPlayerEquivelent().getInGame() == true && currentIPlayer.getGame().equals(iplayer.getIngotPlayerEquivelent().getGame())) {
                    //add player to list
                    players.add(key);
                }
            }
            //cycle between all assigned players
            for (short i = 0; i < players.size(); i++) {
                //send message to player
                player = players.get(i);
                player.sendMessage(ChatHandler.convertMessage(Bukkit.getPlayer(iplayer.getIngotPlayerEquivelent().getUsername()), event.getMessage(), config.getString("Chat.format")));
            }
            Bukkit.getServer().getConsoleSender().sendMessage(ChatHandler.convertMessage(Bukkit.getPlayer(iplayer.getIngotPlayerEquivelent().getUsername()), event.getMessage(), config.getString("Chat.format")));
        }
    }
    /**
     * 
     * This method handles player deaths
     * 
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        //local vars
        String message = "";
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getEntity().getName());
        try {
            //check if player is ingame
            if (iplayer.getIngotPlayerEquivelent().getInGame() == true) {
                //check for fall
                if (event.getDeathMessage().contains("high place") || event.getDeathMessage().contains("fell out")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.FALL, plugin);
                }
                //check for explosion
                else if (event.getDeathMessage().contains("blew up")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.EXPLOSION, plugin);
                }
                //check for block
                else if (event.getDeathMessage().contains("squashed")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.CRUSHED, plugin);
                }
                //check for fire
                else if (event.getDeathMessage().contains("flames")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.FIRE, plugin);
                }
                //check for lava
                else if (event.getDeathMessage().contains("lava")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.LAVA, plugin);
                }
                //check for drowned
                else if (event.getDeathMessage().contains("drowned")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.DROWN, plugin);
                }
                //check for sufficate
                else if (event.getDeathMessage().contains("suffocated")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.SUFFICATE, plugin);
                }
                //run for everything else
                else {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.OTHER, plugin);
                }
                //check if message is somehow null
                if (message == null) {
                    //get message
                    message = "invalid message.";
                }
                //set death message
                event.setDeathMessage(DeathMessageHandler.formatMessage(FileManager.getCustomData(plugin, "language", ROOT).getString("Prefix-Message") + message, event.getEntity().getName(), "", ""));
            }
        }
        catch (NullPointerException ex) {}
    }
    /**
     *
     * This method handles player respawns. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        //local vars
        Location spec = null;
        SPArena currentArena = null;
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        //check if player is playing ad is alive
        if (iplayer.getIngotPlayerEquivelent().getIsPlaying() == true && iplayer.getIngotPlayerEquivelent().getIsAlive() == true) {
            //get arena info
            currentArena = SPArena.selectArena(iplayer.getIngotPlayerEquivelent().getGame());
            try {
                spec = new Location(Bukkit.getWorld(currentArena.getArenaEquivelent().getWorld()), currentArena.getArenaEquivelent().getSpectatorPos()[0],  currentArena.getArenaEquivelent().getSpectatorPos()[1], currentArena.getArenaEquivelent().getSpectatorPos()[2], (float) currentArena.getArenaEquivelent().getSpectatorPos()[3], (float) currentArena.getArenaEquivelent().getSpectatorPos()[4]);
            }
            catch (NullPointerException ex) {
                spec = event.getRespawnLocation();
            }
            //set as dead and inst spectator mode, as well as teleport
            iplayer.getIngotPlayerEquivelent().setIsAlive(false);
            event.getPlayer().setGameMode(SPECTATOR);
            event.setRespawnLocation(spec);
        }
    }
    /**
     *
     * This method handles player joining. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //files
        File playerdataf = new File(plugin.getDataFolder(), "playerdata.yml");
        FileConfiguration playerdata = FileManager.getCustomData(plugin, "playerdata", ROOT);
        Player player = event.getPlayer();
        SPPlayer newPlayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        SPPlayer iplayer = null;
        //check if a new player
        if (playerdata.getString(player.getName() + ".uuid") == null) {
            //set data
            playerdata.createSection(player.getName());
            //save to file
            playerdata.set(event.getPlayer().getName() + ".uuid", event.getPlayer().getUniqueId().toString());
            try {
                playerdata.save(playerdataf);
            } 
            catch (IOException ex) {
                if (config.getBoolean("enable-debug-mode") == true) {
                    Logger.getLogger(Events.class.getName()).log(Level.SEVERE, "COULD NOT SAVE PLAYERDATA.YML!");
                }
            }
            //create object
            iplayer = SPPlayer.createPlayer(event.getPlayer().getName(), false, false, false, 0, (short) (0), (short) (0), (short) (0), (short) (0), "", (short) 0, true, false);
            //cycle through leaderboards
            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                //add the new player
                key.addPlayer(iplayer);
                key.organizeLeaderboard(true);
            }
        }
        else {
            newPlayer.setUsername(player.getName());
        }
    }
    /**
     *
     * This method handles player leaving. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        //local vars
        Lobby lobby = null;
        Game game = null;
        SPPlayer iplayer = SPPlayer.selectPlayer(event.getPlayer().getName());
        try {    
            //check if ingame and not playing
            if (iplayer.getIngotPlayerEquivelent().getInGame() == true && iplayer.getIngotPlayerEquivelent().getIsPlaying() == false) {
                //leave lobby
                lobby = Lobby.selectLobby(SPArena.selectArena(iplayer.getIngotPlayerEquivelent().getGame()));
                lobby.leaveLobby(SPPlayer.selectPlayer(event.getPlayer().getName()), true, config.getBoolean("enable-inventories"));
            }
            //check if playing
            else if (iplayer.getIngotPlayerEquivelent().getIsPlaying() == true) {
                //leave game
                game = Game.selectGame(SPArena.selectArena(iplayer.getIngotPlayerEquivelent().getGame()));
                game.leaveGame(SPPlayer.selectPlayer(event.getPlayer().getName()), true, true, config.getBoolean("enable-inventories"));
            }
            //check if spectating
            else if (iplayer.getIngotPlayerEquivelent().getIsPlaying() == true && event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                event.getPlayer().setGameMode(ADVENTURE);
            }
            iplayer.getIngotPlayerEquivelent().saveToFile();
        }
        catch (IndexOutOfBoundsException ex) {
            //check for debug mode
            if (config.getBoolean("enable-debug-mode") == true) {
                //log error
                Logger.getLogger(Events.class.getName()).log(Level.SEVERE, "COULD NOT PROPERLY KICK " + event.getPlayer().getName() + "FROM THE GAME/LOBBY!!!");
            }
        }
    }
}
