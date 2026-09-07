package edu.psu.se411.model;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import edu.psu.se411.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WalletTest {
    private final Wallet wallet = new Wallet(new BigDecimal("100.00"));
    private final BankAccount bank = new BankAccount();

    private static void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }

    @Test
    void transfersToExistingBankBalance() throws InsufficientFundsException {
        bank.deposit(new BigDecimal("10.00"));
        wallet.withdraw(new BigDecimal("30.00"), bank);
        assertAmount("70.00", wallet.getBalance());
        assertAmount("40.00", bank.getBalance());
    }

    @Test
    void canWithdrawExactBalanceWithDifferentScale() throws InsufficientFundsException {
        wallet.withdraw(new BigDecimal("100"), bank);
        assertAmount("0", wallet.getBalance());
        assertAmount("100", bank.getBalance());
    }

    @Test
    void insufficientFundsPreservesBothBalancesAndAllowsRetry() throws InsufficientFundsException {
        bank.deposit(new BigDecimal("5.00"));
        InsufficientFundsException exception = assertThrowsExactly(
                InsufficientFundsException.class,
                () -> wallet.withdraw(new BigDecimal("100.01"), bank));
        assertEquals("Cannot withdraw 100.01; wallet balance is 100.00", exception.getMessage());
        assertAmount("100", wallet.getBalance());
        assertAmount("5", bank.getBalance());
        wallet.withdraw(new BigDecimal("10"), bank);
        assertAmount("90", wallet.getBalance());
        assertAmount("15", bank.getBalance());
    }

    @Test
    void emptyWalletRejectsWithdrawal() {
        Wallet empty = new Wallet(BigDecimal.ZERO);
        assertThrowsExactly(InsufficientFundsException.class,
                () -> empty.withdraw(new BigDecimal("0.01"), bank));
        assertAmount("0", empty.getBalance());
        assertAmount("0", bank.getBalance());
    }

    @Test
    void decimalTransfersHaveNoFloatingPointRoundingError() throws InsufficientFundsException {
        Wallet small = new Wallet(new BigDecimal("0.30"));
        small.withdraw(new BigDecimal("0.10"), bank);
        small.withdraw(new BigDecimal("0.20"), bank);
        assertAmount("0", small.getBalance());
        assertAmount("0.30", bank.getBalance());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-0.01", "-100"})
    void rejectsNonpositiveWithdrawals(String amount) {
        assertThrowsExactly(IllegalArgumentException.class,
                () -> wallet.withdraw(new BigDecimal(amount), bank));
        assertAmount("100", wallet.getBalance());
        assertAmount("0", bank.getBalance());
    }

    @Test
    void rejectsNullWithdrawalInputsWithoutChangingBalances() {
        assertThrowsExactly(NullPointerException.class, () -> wallet.withdraw(null, bank));
        assertThrowsExactly(NullPointerException.class,
                () -> wallet.withdraw(BigDecimal.ONE, null));
        assertAmount("100", wallet.getBalance());
        assertAmount("0", bank.getBalance());
    }

    @Test
    void rejectsInvalidOpeningBalance() {
        assertThrowsExactly(NullPointerException.class, () -> new Wallet(null));
        assertThrowsExactly(IllegalArgumentException.class,
                () -> new Wallet(new BigDecimal("-0.01")));
    }

    @Test
    void rejectsInvalidBankDepositsWithoutChangingBalance() {
        assertThrowsExactly(NullPointerException.class, () -> bank.deposit(null));
        assertThrowsExactly(IllegalArgumentException.class, () -> bank.deposit(BigDecimal.ZERO));
        assertThrowsExactly(IllegalArgumentException.class,
                () -> bank.deposit(new BigDecimal("-1")));
        assertAmount("0", bank.getBalance());
    }
}
