package com.techbank.cqrs.core.commands;

public interface CommandDispatcher {

    <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod <T>command);
    void send(BaseCommand command);
}
