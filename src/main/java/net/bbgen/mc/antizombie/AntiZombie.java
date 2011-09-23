/*
 * AntiZombie.java -- main class for AntiZombie
 * 
 * Copyright (C) 2011 Bernhard Eder
 * 
 * This software is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any damages arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, including commercial applications, and to alter it and redistribute it freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not claim that you wrote the original software. If you use this software in a product, an acknowledgment in the product documentation would be appreciated but is not required. 2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the original software. 3. This notice may not be removed or altered from any source distribution.
 * 
 * Bernhard Eder mc@bbgen.net
 */

package net.bbgen.mc.antizombie;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;


/**
 * AntiZombie is a plugin to prevent spawning creatures in certain areas.
 * 
 * @author Bernhard Eder
 * 
 */
public class AntiZombie extends JavaPlugin
{
    /**
     * Configuration for this plugin.
     * 
     * The following configs are being used: area: area0: entity: [...] (list of entities. if you want to disable all: [*]) x0: lower X boundary (optional) y0: lower Y boundary (optional) z0: lower Z boundary (optional) x1: upper X boundary (optional) y1: upper Y boundary (optional) z1: upper Z boundary (optional) world: world name (optional)
     */
    protected static Configuration config;
    private static Logger logger = Logger.getLogger("AntiZombie");
    private CreatureSpawnListener creatureSpawnListener = new CreatureSpawnListener();

    @Override
    public void onEnable()
    {
        config = getConfiguration();
        logger.setLevel(Level.FINE);

        logger.info("Loading AntiZombie");

        List<String> keys = config.getKeys("area");
        if(keys == null)
            logger.info("No disabled areas found.");
        else
        {
            for(String s : keys)
            {
                try
                {
                    creatureSpawnListener.addDisabledArea(loadArea("area." + s));
                } catch (AntiZombieException e)
                {
                    logger.warning("Could not load area: " + e.getMessage());
                }
            }
        }

        getServer().getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, creatureSpawnListener, Event.Priority.Normal, this);
    }

    private DisabledArea loadArea(String path) throws AntiZombieException
    {
        final World world;
        String worldString = config.getString(path + ".world");
        if(worldString == null)
            world = getServer().getWorlds().get(0);
        else
            world = getServer().getWorld(worldString);

        DisabledArea area = new DisabledArea(world);

        int x0, y0, z0;
        x0 = config.getInt(path + ".x0", Integer.MIN_VALUE);
        y0 = config.getInt(path + ".y0", Integer.MIN_VALUE);
        z0 = config.getInt(path + ".z0", Integer.MIN_VALUE);
        Location location0 = new Location(world, x0, y0, z0);

        int x1, y1, z1;
        x1 = config.getInt(path + ".x1", Integer.MAX_VALUE);
        y1 = config.getInt(path + ".y1", Integer.MAX_VALUE);
        z1 = config.getInt(path + ".z1", Integer.MAX_VALUE);
        Location location1 = new Location(world, x1, y1, z1);


        List<String> entities = config.getStringList(path + ".entities", null);
        if(entities.size() == 1 && entities.get(0).equals("*"))
            area.setAllCreatures();
        for(String entity : entities)
        {
            CreatureType creatureType = CreatureType.fromName(entity);
            if(creatureType == null)
                throw new AntiZombieException("Creature Type " + entity + " not found.");
            area.addCreature(creatureType);
        }

        if(world == null)
            throw new AntiZombieException("World not found. Available worlds: " + getServer().getWorlds());

        logger.fine("Loaded disabled area. World: " + world.getName() + ". Entity: " + entities + ". Location0:" + location0.toString() + " Location1: " + location1.toString());
        area.setLocations(location0, location1);
        return area;
    }

    @Override
    public void onDisable()
    {
        // currently the plugin does not change any config so saving it back is not needed.
        // config.save();
    }

}
