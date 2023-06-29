package com.techbank.account.cmd.api.commands;

import com.example.account.common.dto.AccountType;
import com.techbank.cqrs.core.commands.BaseCommand;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OpenAccountCommand extends BaseCommand{

    public String accountHolder;
    public AccountType accountType;
    public Double accountBalance;

}
