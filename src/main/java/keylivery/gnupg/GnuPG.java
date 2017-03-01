package keylivery.gnupg;

import java.io.IOException;

public interface GnuPG {
    GnuPGKeyID[] listKeys() throws IOException;

    void importKey(String gnuPGKeyString);

    String exportKeyAsString(GnuPGKeyID gnuPGKeyID) throws IOException;

    String exportKeyAsString(String gnuPGKeyID) throws IOException;

}
