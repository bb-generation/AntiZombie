/*
 * DisabledArea.java -- main class for AntiZombie
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

import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

public class DisabledArea
{

    public DisabledArea(World world)
    {
        location0 = new Location(world, 0, 0, 0);
        location1 = new Location(world, 0, 0, 0);
    }
    
    public void addMaterial(Material material)
    {
        materials.add(material);
    }

    public void addCreature(EntityType creature)
    {
        creatures.add(creature);
    }

    public DisabledArea(Location location0, Location location1, EntityType creatureType)
    {
        this.location0 = location0;
        this.location1 = location1;

        addCreature(creatureType);
    }

    public void setLocations(Location location0, Location location1)
    {
        this.location0 = location0;
        this.location1 = location1;
    }

    public boolean matches(Location location, EntityType creatureType)
    {
        if(checkCreatures && !creatures.contains(creatureType))
            return false;
        
        if(!location0.getWorld().equals(location.getWorld()))
            return false;

        if(!isIn(location0.getBlockX(), location1.getBlockX(), location.getBlockX()))
            return false;

        if(!isIn(location0.getBlockZ(), location1.getBlockZ(), location.getBlockZ()))
            return false;

        if(!isIn(location0.getBlockY(), location1.getBlockY(), location.getBlockY()))
            return false;
        
        if(!matchesMaterial(location))
            return false;

        return true;
    }

    private boolean matchesMaterial(Location location)
    {
        if(materials.isEmpty())
            return true;
        
        if(checkLocation(location))
            return true;
        if(checkLocation(location.add(0, -1.0, 0)))
            return true;

        
        return false;
    }
    
    private boolean checkLocation(Location location)
    {
        Block blockBelow = location.getBlock();
        
        if(materials.contains(blockBelow.getType()))
            return true;
        
        return false;
    }

    public void setAllCreatures()
    {
        checkCreatures = false;
    }

    /**
     * Checks whether x2 is between x0 and x1
     * 
     * @param x0
     * @param x1
     * @param x2
     * @return
     */
    private boolean isIn(int x0, int x1, int x2)
    {
        if(x0 < x1)
            return (x0 <= x2) && (x2 <= x1);
        else
            return (x1 <= x2) && (x2 <= x0);
    }

    private boolean checkCreatures = true;

    private Location location0;
    private Location location1;
    private Set<Material> materials = new TreeSet<Material>();

    private Set<EntityType> creatures = new TreeSet<EntityType>();

}
