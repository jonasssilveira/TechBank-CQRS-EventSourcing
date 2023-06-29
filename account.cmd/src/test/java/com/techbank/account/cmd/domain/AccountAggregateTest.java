package com.techbank.account.cmd.domain;

import com.example.account.common.events.AccountClosedEvent;
import com.example.account.common.events.AccountOpenedEvent;
import com.example.account.common.events.FundsDepositedEvent;
import com.techbank.account.cmd.api.commands.OpenAccountCommand;
import com.techbank.cqrs.core.domain.AggregateRoot;
import com.techbank.cqrs.core.events.BaseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static com.example.account.common.dto.AccountType.CURRENT;
import static com.example.account.common.dto.AccountType.SAVINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AccountAggregateTest {

    public static final String ID = "Test";
    private AccountAggregate accountAggregate;
    private AggregateRoot aggregateRoot;
    private AccountOpenedEvent accountOpenedEvent;

    @BeforeEach
    public void setup() {
        this.aggregateRoot = Mockito.mock(AggregateRoot.class);
        OpenAccountCommand openAccountCommand = OpenAccountCommand
                .builder()
                .accountType(CURRENT)
                .accountHolder(ID)
                .accountBalance(100D)
                .build();

        accountOpenedEvent = AccountOpenedEvent
                .builder()
                .accountBalance(100D)
                .accountType(SAVINGS)
                .accountHolder(ID)
                .createdDate(LocalDateTime.now())
                .build();
        this.aggregateRoot.setId(ID);
        this.aggregateRoot.setVersion(1L);
        this.accountAggregate = new AccountAggregate(this.aggregateRoot);
        this.accountAggregate.OpenAggregate(openAccountCommand);
    }

    @Test
    public void shouldDepositeFundsWithSuccess() {
        this.accountAggregate.apply(accountOpenedEvent);
        this.accountAggregate.depositFunds(100D);
        verify(this.aggregateRoot).raiseEvent(any(FundsDepositedEvent.class));
    }

    @Test
    public void shouldNotDepositeFundsWhenTheEventIsNotActive() {
        String expectedMessage = "Funds cannot be deposited into a closed account!";
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
                () -> this.accountAggregate.depositFunds(100));

        assertEquals(expectedMessage, illegalStateException.getMessage());
        assertEquals(IllegalStateException.class, illegalStateException.getClass());

    }

    @Test
    public void shouldNotDepositeFundsWhenAmountIsLowerThan1() {
        this.accountAggregate.apply(accountOpenedEvent);
        String expectedMessage = "The deposit amount mus be greater than 0!";
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
                () -> this.accountAggregate.depositFunds(0));

        assertEquals(expectedMessage, illegalStateException.getMessage());
        assertEquals(IllegalStateException.class, illegalStateException.getClass());

    }

    @Test
    public void shouldCloseAccountWithSuccess() {
        //act
        this.accountAggregate.apply(accountOpenedEvent);
        AccountClosedEvent accountClosedEvent = this.accountAggregate.closeAccount();
        this.accountAggregate.apply(accountClosedEvent);

        //assert
        verify(this.aggregateRoot,times(2)).raiseEvent(any(BaseEvent.class));
        assertEquals(accountOpenedEvent.getId(),this.accountAggregate.getRoot().getId());
        assertFalse(this.accountAggregate.getActive());
    }

    @Test
    public void shouldNotCloseAccountWhenAccountIsNotActive() {
        String expectedMessage = "The bank account has already been closed!";
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
                () -> this.accountAggregate.closeAccount());

        assertEquals(expectedMessage, illegalStateException.getMessage());
        assertEquals(IllegalStateException.class, illegalStateException.getClass());

    }


    @Test
    public void shouldWithdrawAccountWithSuccess() {
        //act
        this.accountAggregate.apply(accountOpenedEvent);
        var fundsWithdrewEvent = this.accountAggregate.withdrawFunds(10D);
        this.accountAggregate.apply(fundsWithdrewEvent);

        //assert
        verify(this.aggregateRoot,times(2)).raiseEvent(any(BaseEvent.class));
        assertEquals(accountOpenedEvent.getId(),this.accountAggregate.getRoot().getId());
        assertEquals(90D,this.accountAggregate.getBalance());

    }


    @Test
    public void shouldNotWithdrawWhenTheEventIsNotActive() {
        String expectedMessage = "Funds cannot be withdrawn from a closed account!";
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
                () -> this.accountAggregate.withdrawFunds(100));

        assertEquals(expectedMessage, illegalStateException.getMessage());
        assertEquals(IllegalStateException.class, illegalStateException.getClass());
    }


}
