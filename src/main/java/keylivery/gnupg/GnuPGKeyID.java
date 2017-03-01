package keylivery.gnupg;

import java.text.SimpleDateFormat;
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

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return this.getUserID() + " | created: " + dateFormat.format(this.getCreationDate());
    }
}
