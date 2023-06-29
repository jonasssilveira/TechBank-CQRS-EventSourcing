package com.techbank.account.cmd.api.commands.commandhandler;

import com.techbank.account.cmd.api.commands.CloseAccountCommand;
import com.techbank.account.cmd.api.commands.DepositFundsCommand;
import com.techbank.account.cmd.api.commands.OpenAccountCommand;
import com.techbank.account.cmd.api.commands.WithdrawFundsCommand;

public interface CommandHandler {

    void handle(OpenAccountCommand command);
    void handle(CloseAccountCommand command);
    void handle(WithdrawFundsCommand command);
    void handle(DepositFundsCommand command);

}
