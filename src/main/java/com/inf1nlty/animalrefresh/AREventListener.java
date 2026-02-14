package com.inf1nlty.animalrefresh;

import com.google.common.eventbus.Subscribe;
import net.xiaoyu233.fml.reload.event.CommandRegisterEvent;
import com.inf1nlty.animalrefresh.command.CommandAnimalRefresh;

public class AREventListener {

    @Subscribe
    public static void register(CommandRegisterEvent event) {
        event.register(new CommandAnimalRefresh());
    }
}