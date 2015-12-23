package ob.backoffice.abstractions;

public class Account {
    private final String id;
    private final String venue;

    public Account(final String id, final String venue) {
        this.id = id;
        this.venue = venue;
    }

    public String getId() {
        return id;
    }

    public String getVenue() {
        return venue;
    }
}
