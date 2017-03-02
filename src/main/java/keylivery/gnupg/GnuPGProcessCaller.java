package keylivery.gnupg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class GnuPGProcessCaller implements GnuPG {

    private final String gpgCommand;

    public GnuPGProcessCaller(String gpgPath) {
        gpgCommand = gpgPath;
    }

    @Override
    public GnuPGKeyID[] listKeys() throws IOException {
//        ToDO: Add exceptions and checks for null etc...
        ArrayList<String[]> tempTable = new ArrayList<>();
        Process gpgProcess = new ProcessBuilder(gpgCommand, "--armor", "--list-secret-key", "--with-colons").start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(gpgProcess.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            tempTable.add(line.split(":"));
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
    public String exportKeyAsString(GnuPGKeyID gnuPGKeyID) throws IOException {
        String keyID = gnuPGKeyID.getKeyID();
        String result;
        Process gpgProcess = new ProcessBuilder(gpgCommand, "--armor", "--export-secret-key", keyID).start();
        result = new BufferedReader(new InputStreamReader(gpgProcess.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
        return result;
    }

    @Override
    public String exportKeyAsString(String gnuPGKeyID) throws IOException {
        String result;
        Process gpgProcess = new ProcessBuilder(gpgCommand, "--armor", "--export-secret-key", gnuPGKeyID).start();
        result = new BufferedReader(new InputStreamReader(gpgProcess.getInputStream()))
                .lines().collect(Collectors.joining("\n"));
        return result;
    }
}
