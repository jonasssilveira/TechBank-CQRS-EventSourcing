package com.techbank.account.cmd.infrastructure;

import com.techbank.cqrs.core.commands.BaseCommand;
import com.techbank.cqrs.core.commands.CommandDispatcher;
import com.techbank.cqrs.core.commands.CommandHandlerMethod;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AccountCommandDispatcher implements CommandDispatcher {

    private final Map<Class<?extends BaseCommand>, List<CommandHandlerMethod>> routes = new HashMap<>();

    @Override
    public <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> command) {
        var handlers = routes.computeIfAbsent(type,c -> new LinkedList<>());
        handlers.add(command);
    }

    @Override
    public void send(BaseCommand command) {
        var handlers = routes.get(command.getClass());

        if(handlers == null|| handlers.size()==0)
            throw new RuntimeException("No command handler was registered!");
        if(handlers.size()>1)
            throw new RuntimeException("Cannot send command to more then one handler!");

        handlers.get(0).handle(command);
    }
}