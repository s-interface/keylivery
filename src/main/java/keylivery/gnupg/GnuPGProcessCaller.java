package keylivery.gnupg;

import keylivery.AppPreferences;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GnuPGProcessCaller implements GnuPG {

    private final String gpgCommand;

    public GnuPGProcessCaller() {
        gpgCommand = AppPreferences.getInstance().getString(AppPreferences.Preference.GPGPATH_STR);
    }

    @Override
    public GnuPGKeyID[] listKeys() {
        GpGProcess gpGProcess = new GpGProcess("--armor", "--list-secret-key", "--with-colons").start();
        gpGProcess.waitFor();
        List<String> keyList = gpGProcess.getInput();
        ArrayList<GnuPGKeyID> keys = new ArrayList<>();
        Date creationDate = null;
        String keyID = null;
        String userID;
        for (String recordRow : keyList) {
            String[] recordRowSplit = recordRow.split(":");
            String recordType = recordRowSplit[0];
            switch (recordType) {
                case "sec":
                    keyID = recordRowSplit[4];
                    creationDate = parseDate(recordRowSplit[5]);
                    break;
                case "uid":
                    if (keyID != null) {
                        userID = recordRowSplit[9];
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
    public boolean importKey(String gnuPGKeyString) {
        // use "--dry-run" for testing
        GpGProcess gpGProcess = new GpGProcess("--import").start();
        gpGProcess.write(gnuPGKeyString);
        int exitValue = gpGProcess.waitFor();
        if (exitValue == 0) {
            return true;
        } else if (exitValue == 2) {
            System.out.println("Error (Import Key):\n" + gpGProcess.getErr().stream().collect(Collectors.joining("\n")));
        }
        return false;
    }

    @Override
    public String exportKeyAsString(GnuPGKeyID gnuPGKeyID) {
        return exportKeyAsString(gnuPGKeyID.getKeyID());
    }

    @Override
    public String exportKeyAsString(String gnuPGKeyID) {
        GpGProcess gpGProcess = new GpGProcess("--armor", "--export-secret-key", gnuPGKeyID).start();
        gpGProcess.waitFor();
        String result = gpGProcess.getInput().stream().collect(Collectors.joining("\n"));
        return result;
    }

    private Date parseDate(String dateString) {
        Date resultDate;
        try {
            long dateL = Long.valueOf(dateString);
            resultDate = new Date(dateL * 1000L);
            return resultDate;
        } catch (NumberFormatException e) {
        }
        try {
            resultDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            return resultDate;
        } catch (ParseException e) {
        }
        throw new IllegalStateException("Reading Keys from gpg-process: Date Conversion failed");
    }

    private class GpGProcess {
        private String name;
        private List<String> commands;
        private Process gpgProcess;
        private ThreadedStreamReader inReader;
        private ThreadedStreamReader errReader;

        GpGProcess(String... args) {
            this.commands = new ArrayList<>();
            this.commands.add(gpgCommand);
            Collections.addAll(this.commands, args);
            this.name = commands.stream().collect(Collectors.joining(" "));
        }

        GpGProcess start() {
            try {
                gpgProcess = new ProcessBuilder(commands).start();
                inReader = new ThreadedStreamReader(gpgProcess.getInputStream());
                errReader = new ThreadedStreamReader(gpgProcess.getErrorStream());
                inReader.start();
                errReader.start();
            } catch (IOException e) {
                System.out.println("Exception: " + e.getMessage() + "\tFull Command: \"" + name + "\":");
            }
            return this;
        }

        void write(String outString) {
            if (gpgProcess == null) {
                try {
                    throw new IOException("Could not write to gpgProcess: gpgProcess is null");
                } catch (IOException e) {
                    System.out.println("Exception: " + e.getMessage());
                }
                return;
            }
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(gpgProcess.getOutputStream()));
            try {
                out.write(outString);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int waitFor() {
            try {
                if (gpgProcess != null) {
                    return gpgProcess.waitFor();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return -1;
        }

        List<String> getInput() {
            if (gpgProcess != null) {
                return inReader.getInput();
            }
            return new ArrayList<>();
        }

        List<String> getErr() {
            if (gpgProcess != null) {
                return errReader.getInput();
            }
            return new ArrayList<>();
        }
    }

    private class ThreadedStreamReader extends Thread {

        private InputStream inputStream;
        private List<String> stringBuffer;

        ThreadedStreamReader(InputStream inputStream) {
            this.inputStream = inputStream;
            this.stringBuffer = new ArrayList<>();
        }

        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    stringBuffer.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<String> getInput() {
            return stringBuffer;
        }
    }
}
