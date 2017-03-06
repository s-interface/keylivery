package keylivery.gnupg;

public interface GnuPG {
    GnuPGKeyID[] listKeys();

    boolean importKey(String gnuPGKeyString, boolean dryRun);

    String exportKeyAsString(GnuPGKeyID gnuPGKeyID);

    String exportKeyAsString(String gnuPGKeyID);

}
