package keylivery.gnupg;

import java.util.Date;

public class GnuPGKeyID {

    private final Date creationDate;
    private final String keyID;
    private final String userID;

    GnuPGKeyID(Date creationDate, String keyID, String userID) {
        this.creationDate = creationDate;
        this.keyID = keyID;
        this.userID = userID;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getKeyID() {
        return keyID;
    }

    public String getUserID() {
        return userID;
    }
}
