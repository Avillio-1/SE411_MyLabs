package edu.spu.se411.lab06_logging;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import edu.spu.se411.lab06_logging.exceptions.InsufficientFundsException;
import edu.spu.se411.lab06_logging.model.WalletAccount;

class LoggingTest {
    private final Logger packageLogger = Logger.getLogger("edu.spu.se411.lab06_logging");
    private final CaptureAppender capture = new CaptureAppender();
    private Level previousLevel;
    private boolean previousAdditivity;

    @BeforeEach
    void captureLogs() {
        previousLevel = packageLogger.getLevel();
        previousAdditivity = packageLogger.getAdditivity();
        packageLogger.setLevel(Level.DEBUG);
        packageLogger.setAdditivity(false);
        packageLogger.addAppender(capture);
    }

    @AfterEach
    void restoreLogging() {
        packageLogger.removeAppender(capture);
        packageLogger.setLevel(previousLevel);
        packageLogger.setAdditivity(previousAdditivity);
    }

    @Test
    void applicationLogsLifecycleAndEachHandledFailureWithOneStackTrace() {
        assertDoesNotThrow(() -> App.main(new String[0]));
        assertEquals("Application is starting...", capture.events.getFirst().getRenderedMessage());
        assertEquals("Application is ending...", capture.events.getLast().getRenderedMessage());
        List<LoggingEvent> errors = capture.events.stream()
                .filter(event -> event.getLevel().equals(Level.ERROR)).toList();
        assertEquals(2, errors.size());
        assertInstanceOf(InsufficientFundsException.class,
                errors.get(0).getThrowableInformation().getThrowable());
        assertInstanceOf(IllegalArgumentException.class,
                errors.get(1).getThrowableInformation().getThrowable());
        assertEquals(2, capture.events.stream()
                .filter(event -> event.getThrowableInformation() != null).count());
        assertTrue(capture.events.stream().noneMatch(event ->
                event.getRenderedMessage().contains("balance is")));
    }

    @ParameterizedTest
    @CsvSource({"ERROR, 0, 0, 0, 2", "WARN, 0, 0, 1, 2",
            "INFO, 0, 2, 1, 2", "DEBUG, 3, 2, 1, 2"})
    void thresholdFiltersExpectedEvents(String threshold, int debug, int info, int warn, int error) {
        packageLogger.setLevel(Level.toLevel(threshold));
        App.main(new String[0]);
        assertEquals(debug, count(Level.DEBUG));
        assertEquals(info, count(Level.INFO));
        assertEquals(warn, count(Level.WARN));
        assertEquals(error, count(Level.ERROR));
        assertEquals(debug + info + warn + error, capture.events.size());
    }

    @Test
    void successfulOperationsLogDebugAndUpdateBalance() throws InsufficientFundsException {
        WalletAccount account = new WalletAccount(1000);
        account.deposit(200);
        account.withdraw(1200);
        assertEquals(3, count(Level.DEBUG));
        assertEquals("Deposit successful: amount=200.0", capture.events.get(1).getRenderedMessage());
        assertEquals("Withdrawal successful: amount=1200.0", capture.events.get(2).getRenderedMessage());
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(1));
    }

    @Test
    void rejectedOperationsPreserveBalanceAndDoNotLogSuccess() throws InsufficientFundsException {
        WalletAccount account = new WalletAccount(1000);
        capture.events.clear();
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(1500));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-100));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-100));
        assertThrows(IllegalArgumentException.class, () -> account.setBalance(-100));
        assertEquals(0, count(Level.DEBUG));
        assertEquals(1, count(Level.WARN));
        account.withdraw(1000);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(1));
    }

    @Test
    void exceptionConstructionWarnsWithoutAStackTrace() {
        InsufficientFundsException exception = new InsufficientFundsException("Test message");
        assertEquals("Test message", exception.getMessage());
        assertEquals(1, capture.events.size());
        assertEquals(Level.WARN, capture.events.getFirst().getLevel());
        assertNull(capture.events.getFirst().getThrowableInformation());
    }

    private long count(Level level) {
        return capture.events.stream().filter(event -> event.getLevel().equals(level)).count();
    }

    private static class CaptureAppender extends AppenderSkeleton {
        private final List<LoggingEvent> events = new ArrayList<>();

        @Override
        protected void append(LoggingEvent event) {
            events.add(event);
        }

        @Override
        public void close() { }

        @Override
        public boolean requiresLayout() {
            return false;
        }
    }
}
