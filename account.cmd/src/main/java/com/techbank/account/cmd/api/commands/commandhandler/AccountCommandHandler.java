package com.techbank.account.cmd.api.commands.commandhandler;

import com.techbank.account.cmd.api.commands.CloseAccountCommand;
import com.techbank.account.cmd.api.commands.DepositFundsCommand;
import com.techbank.account.cmd.api.commands.OpenAccountCommand;
import com.techbank.account.cmd.api.commands.WithdrawFundsCommand;
import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.domain.AggregateRoot;
import com.techbank.cqrs.core.handler.EventSourcingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandHandler implements CommandHandler{

    private final EventSourcingHandler<AccountAggregate> eventSourcingHandler;

    public AccountCommandHandler(@Autowired EventSourcingHandler<AccountAggregate> eventSourcingHandler) {
        this.eventSourcingHandler = eventSourcingHandler;
    }

    @Override
    public void handle(OpenAccountCommand command) {
        var aggregate = new AccountAggregate(new AggregateRoot());
        this.eventSourcingHandler.save(aggregate.getRoot());
    }

    @Override
    public void handle(CloseAccountCommand command) {
        var aggregateAccount = this.eventSourcingHandler.getById(command.getId());
        aggregateAccount.closeAccount();
        this.eventSourcingHandler.save(aggregateAccount.getRoot());
    }

    @Override
    public void handle(WithdrawFundsCommand command) {
        var aggregate = this.eventSourcingHandler.getById(command.getId());

        if(command.getAmount() > aggregate.getBalance())
            throw new IllegalStateException("Withdraw declined, insufficient funds!");

     }

    @Override
    public void handle(DepositFundsCommand command) {

    }
}
