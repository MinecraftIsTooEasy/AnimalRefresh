package com.inf1nlty.animalrefresh;

import net.fabricmc.api.ModInitializer;
import net.xiaoyu233.fml.reload.event.MITEEvents;

public class AnimalRefreshMod implements ModInitializer {

    public void onInitialize() {
        MITEEvents.MITE_EVENT_BUS.register(new AREventListener());
    }
}