package com.techbank.account.cmd.api.commands;

import com.techbank.cqrs.core.commands.BaseCommand;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WithdrawFundsCommand extends BaseCommand {

    private double amount;

}
