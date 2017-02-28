package keylivery.gnupg;

public interface GnuPG {
    GnuPGKeyID[] listKeys();

    void importKey(String gnuPGKeyString);

    String exportKeyAsString(GnuPGKeyID gnuPGKeyID);
}
