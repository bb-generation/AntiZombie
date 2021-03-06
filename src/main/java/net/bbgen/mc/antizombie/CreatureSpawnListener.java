/*
 * CreatureSpawnListener.java -- main class for AntiZombie
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

import java.util.LinkedList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener
{
    public CreatureSpawnListener()
    {

    }

    public void addDisabledArea(DisabledArea disabledArea)
    {
        disabledAreas.add(disabledArea);
    }
    
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        for(DisabledArea area : disabledAreas)
        {
            if(area.matches(event.getLocation(), event.getEntity().getType()))
            {
                event.setCancelled(true);
                break;
            }
        }
    }

    private List<DisabledArea> disabledAreas = new LinkedList<DisabledArea>();
}
