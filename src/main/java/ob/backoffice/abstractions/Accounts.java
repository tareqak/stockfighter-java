package ob.backoffice.abstractions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Accounts {
    private final Map<String, Map<String, Account>> accounts = new HashMap<>();
    private final List<Account> accountList = new LinkedList<>();

    public Account getAccount(final String venue, final String id) {
        if (accounts.containsKey(venue)) {
            final Map<String, Account> accountMap = accounts.get(venue);
            if (accountMap.containsKey(id)) {
                return accountMap.get(id);
            } else {
                final Account account = new Account(venue, id);
                accountMap.put(id, account);
                accountList.add(account);
                return account;
            }
        } else {
            final Account account = new Account(venue, id);
            final Map<String, Account> accountMap = new HashMap<>();
            accountMap.put(id, account);
            accounts.put(venue, accountMap);
            accountList.add(account);
            return account;
        }
    }

    public List<Account> getAccounts() {
        return accountList;
    }

    public class Account {
        private final String id;
        private final String venue;

        private Account(final String venue, final String id) {
            this.id = id;
            this.venue = venue;
        }

        public String getId() {
            return id;
        }

        public String getVenue() {
            return venue;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Account account = (Account) o;

            if (!id.equals(account.id)) return false;
            return venue.equals(account.venue);

        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + venue.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return venue + ":" + id;
        }
    }
}
