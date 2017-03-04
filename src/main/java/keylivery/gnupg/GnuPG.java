package keylivery.gnupg;

public interface GnuPG {
    GnuPGKeyID[] listKeys();

    boolean importKey(String gnuPGKeyString);

    String exportKeyAsString(GnuPGKeyID gnuPGKeyID);

    String exportKeyAsString(String gnuPGKeyID);

}
