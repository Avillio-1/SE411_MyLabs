# Lab 12: Threads and locks

## Thread pool comparison

`App` uses a fixed thread pool; `CachedApp` is the second main program and uses
a cached thread pool. Both submit 1,000,000 transactions. The argument selects
the number of accounts. Each account starts with 10,000, and both programs use
the same random seed (411) to generate the same workload.

The fixed pool uses `Runtime.getRuntime().availableProcessors()` workers
(18 on the test machine). The starter used the transaction count as the pool
size, which would request up to one million threads for this exercise.

Measured on Windows with Java 21 on October 25, 2026. Each result is one run,
timing transaction submission through completion, excluding Maven startup.
These are observations, not general benchmark guarantees.

| Implementation | Accounts | Transactions | Fixed pool (ms) | Cached pool (ms) |
| --- | ---: | ---: | ---: | ---: |
| Starter synchronized account methods | 1,000 | 1,000,000 | 350.94 | 2,791.87 |
| Starter synchronized account methods | 10 | 1,000,000 | 265.41 | 1,336.14 |
| Final ordered locks | 1,000 | 1,000,000 | 539.80 | 2,632.22 |
| Final ordered locks | 10 | 1,000,000 | 508.25 | 2,843.61 |

The fixed pool was faster in both account configurations, before and after
the lock change. These short transactions benefit from a bounded set of
workers; a cached pool can create more threads when workers are busy or
waiting for account locks, adding scheduling and thread-management overhead.
With 10 accounts, transactions share accounts more often, so more threads
cannot remove the contention. The smaller account set was not consistently
slower in the measurements: locality and scheduling also affect elapsed time.

All eight runs preserved the total balance: 10,000,000 for 1,000 accounts and
100,000 for 10 accounts (to the six decimal places printed by the program).

## What could go wrong with locks?

If each transaction locks its sender first and receiver second, opposite
transfers can deadlock. A transfer from A to B can hold A's lock while waiting
for B, while a transfer from B to A holds B's lock while waiting for A.
Forgetting to unlock after an exception can also block future transactions.

The solution uses a `ReentrantLock` per account. Transactions compare immutable,
unique account IDs and acquire both locks in ascending lexicographic order,
regardless of transfer direction. The program creates unique IDs; transactions
reject distinct accounts with equal IDs so the ordering remains strict.
Withdrawal and deposit happen while both locks are held. Locks are released
in reverse order in `finally` blocks, including when funds are insufficient.
Account balance methods use the same reentrant lock instead of `synchronized`.

## Run

From `Lab12`, with Java 21 or newer and Maven:

```sh
mvn package
mvn exec:java -Dexec.args=1000
mvn exec:java -Dexec.mainClass=edu.psu.se411.lab12_threads.CachedApp -Dexec.args=1000
mvn exec:java -Dexec.args=10
mvn exec:java -Dexec.mainClass=edu.psu.se411.lab12_threads.CachedApp -Dexec.args=10
```

The checked-in programs contain the final ordered-lock solution. The starter
comparison was measured separately with the supplied synchronized model and
the same workload and pool settings.
