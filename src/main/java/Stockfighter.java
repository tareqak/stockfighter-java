import levels.TestEx;

import java.util.concurrent.atomic.AtomicBoolean;

public class Stockfighter {
    private static final AtomicBoolean done = new AtomicBoolean(true);
    public static void main(String[] args) {
        new TestEx(done).play();
    }
}
