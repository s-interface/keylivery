package keylivery.gnupg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class GnuPGProcessCaller implements GnuPG {
    @Override
    public GnuPGKeyID[] listKeys() {
//        ToDO: Add exceptions and checks for null etc...
        ArrayList<String[]> tempTable = new ArrayList<>();
        try {
            Process gpgProcess = new ProcessBuilder("gpg", "--armor", "--list-secret-key", "--with-colons").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(gpgProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                tempTable.add(line.split(":"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[][] keyListTable = tempTable.toArray(new String[tempTable.size()][]);

        ArrayList<GnuPGKeyID> keys = new ArrayList<>();
        Date creationDate = null;
        String keyID = null;
        String userID;
        for (String[] recordRow : keyListTable) {
            String recordType = recordRow[0];
            switch (recordType) {
                case "sec":
                    keyID = recordRow[4];
                    long date = Long.valueOf(recordRow[5]);
                    creationDate = new Date(date * 1000L);
                    break;

                case "uid":
                    if (keyID != null) {
                        userID = recordRow[9];
                        keys.add(new GnuPGKeyID(creationDate, keyID, userID));
                        keyID = null;
                    }
                    break;

                case "ssb":
                    break;

                default:
                    break;
            }
        }
        return keys.toArray(new GnuPGKeyID[keys.size()]);
    }

    @Override
    public void importKey(String gnuPGKeyString) {
        System.out.println("WARNING: pgp key import not yet implemented!");
    }

    @Override
    public String exportKeyAsString(GnuPGKeyID gnuPGKeyID) {
        String keyID = gnuPGKeyID.getKeyID();
        String result = null;
        try {
            Process gpgProcess = new ProcessBuilder("gpg", "--armor", "--export-secret-key", keyID).start();
            result = new BufferedReader(new InputStreamReader(gpgProcess.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
