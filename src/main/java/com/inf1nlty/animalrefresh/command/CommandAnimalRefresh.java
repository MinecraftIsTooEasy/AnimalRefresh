package com.inf1nlty.animalrefresh.command;

import com.inf1nlty.animalrefresh.util.AnimalRefreshController;
import net.minecraft.ChatMessageComponent;
import net.minecraft.ICommand;
import net.minecraft.ICommandSender;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CommandAnimalRefresh implements ICommand {

    @Override
    public String getCommandName() {
        return "animalrefresh";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/animalrefresh";
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List getCommandAliases() {
        return Collections.singletonList("ar");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        int resetCount = AnimalRefreshController.resetAllLoadedChunksAnimalSpawnFlags();
        int spawned = AnimalRefreshController.lastSpawnedAnimals;

        String msg = "§a动物刷新：已清除已加载区块的刷新标记，共重置 " + resetCount + " 个区块。此次实际刷新动物: " + spawned + " 只。";
        ChatMessageComponent comp = ChatMessageComponent.createFromText(msg);
        sender.sendChatToPlayer(comp);

        MinecraftServer.getServer().logInfo("[动物刷新] 已由 " + sender.getCommandSenderName() + " 清除区块刷新标记，共 " + resetCount + " 个区块被重置，实际刷新动物 " + spawned + " 只");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        MinecraftServer server = MinecraftServer.getServer();

        if (server == null) return false;

        if (!(sender instanceof ServerPlayer)) return true;

        Set ops = server.getConfigurationManager().getOps();
        return ops != null && ops.contains(sender.getCommandSenderName().toLowerCase());
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List addTabCompletionOptions(ICommandSender sender, String[] args) { return Collections.emptyList(); }
    @Override
    public boolean isUsernameIndex(String[] args, int index) { return false; }
    @Override
    public int compareTo(@NotNull Object object) { return this.getCommandName().compareTo(((ICommand)object).getCommandName()); }
}