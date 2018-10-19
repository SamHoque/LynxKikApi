package net.lynx.client.objects;

public enum Logger {
    PLUS {
        public void log(Object object) {
            System.out.println("[+] " + object);
        }
    }, NEGATIVE {
        public void log(Object object) {
            System.err.println("[-] " + object);
        }
    }, NULL {
        public void log(Object object) {
            System.out.println("[!] " + object);
        }
    };

    public abstract void log(Object object);

    public void log(String format, Object... args) {
        log(String.format(format, args));
    }
}
