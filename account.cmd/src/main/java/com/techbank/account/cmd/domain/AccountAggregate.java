package com.techbank.account.cmd.domain;

import com.example.account.common.events.AccountClosedEvent;
import com.example.account.common.events.AccountOpenedEvent;
import com.example.account.common.events.FundsDepositedEvent;
import com.example.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.cmd.api.commands.OpenAccountCommand;
import com.techbank.cqrs.core.domain.AggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class AccountAggregate{

    private boolean active;
    @Getter
    private double balance;
    @Getter
    private AggregateRoot root;
    public AccountAggregate(AggregateRoot root) {
        this.root = root;
    }
    public void OpenAggregate(OpenAccountCommand command) {

        this.root.raiseEvent(AccountOpenedEvent.builder()
                .id(command.getId())
                .accountHolder(command.getAccountHolder())
                .createdDate(LocalDateTime.now())
                .accountType(command.getAccountType())
                .accountBalance(command.getAccountBalance())
                .build());
    }

    public void apply(AccountOpenedEvent event) {
        this.root.setId(event.getId());
        this.active = true;
        this.balance = event.getAccountBalance();
    }

    public void depositFunds(double amount) {
        if (!this.active)
            throw new IllegalStateException("Funds cannot be deposited into a closed account!");
        if (amount < 1)
            throw new IllegalStateException("The deposit amount mus be greater than 0!");
        this.root.raiseEvent(FundsDepositedEvent
                .builder()
                .id(this.root.getId())
                .amount(amount)
                .build());
    }

    public void apply(FundsDepositedEvent event){
        this.root.setId(event.getId());
        this.balance += event.getAmount();
    }

    public AccountClosedEvent closeAccount() {

        if(!this.active)
            throw new IllegalStateException("The bank account has already been closed!");
        var accountCloseEvent = AccountClosedEvent
                .builder()
                .id(this.root.getId())
                .build();
        this.root.raiseEvent(accountCloseEvent);

        return accountCloseEvent;
    }

    public void apply(AccountClosedEvent event){
        this.root.setId(event.getId());
        this.active = false;
    }

    public FundsWithdrawnEvent withdrawFunds(double amount) {
        if (!this.active) {
            throw new IllegalStateException("Funds cannot be withdrawn from a closed account!");
        }
        FundsWithdrawnEvent fundsWithdrawnEvent = FundsWithdrawnEvent.builder()
                .id(this.root.getId())
                .amount(amount)
                .build();
        this.root.raiseEvent(fundsWithdrawnEvent);
        return fundsWithdrawnEvent;
    }

    public void apply(FundsWithdrawnEvent event) {
        this.root.setId(event.getId());
        this.balance -= event.getAmount();
    }

    public boolean getActive() {
        return active;
    }
}
