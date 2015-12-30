package ob.backoffice.abstractions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Accounts {
    INSTANCE;

    private final Map<String, Map<String, Account>> accounts =
            new HashMap<>();

    public static Account getAccount(final String venue, final String id) {
        if (INSTANCE.accounts.containsKey(venue)) {
            final Map<String, Account> accountMap =
                    INSTANCE.accounts.get(venue);
            if (accountMap.containsKey(id)) {
                return accountMap.get(id);
            } else {
                final Account account = new Account(venue, id);
                accountMap.put(id, account);
                return account;
            }
        } else {
            final Account account = new Account(venue, id);
            final Map<String, Account> accountMap = new HashMap<>();
            accountMap.put(id, account);
            INSTANCE.accounts.put(venue, accountMap);
            return account;
        }
    }

    public static List<Account> getAccounts() {
        final List<Account> accountList = new ArrayList<>();
        for (final Map<String, Account> map : INSTANCE.accounts.values()) {
            accountList.addAll(map.values());
        }
        return accountList;
    }

    public static class Account {
        private final String id;
        private final String venue;

        public Account(final String venue, final String id) {
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
    }
}
