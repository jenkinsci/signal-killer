package hudson.plugins.signal_killer;

import hudson.Extension;
import hudson.util.ProcessKiller;
import hudson.util.ProcessTree;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static hudson.util.jna.GNUCLibrary.LIBC;

@Extension
public class SignalKiller extends ProcessKiller {

    //standard signals, see signum.h (or man 7 signal)
    public static final int SIGABRT = 6; //Abnormal termination.
    public static final int SIGKILL = 9; //Kill (cannot be blocked, caught, or ignored).
    public static final int SIGTERM = 15; //Termination request.

    /**
     * Default constructor.
     * Does nothing, just logs that plugin instance was created on Hudson master.
     */
    public SignalKiller() {
        LOGGER.fine("SignalKiller initialized");
    }

    @Override
    public boolean kill(ProcessTree.OSProcess process) throws IOException, InterruptedException {
        // Don't fail build while core doesn't handle non IOException error. On Windows
        // java.lang.NoClassDefFoundError: Could not initialize class hudson.util.jna.GNUCLibrary
        // error may happen.
        try {
            int retVal = sendSignal(process.getPid(), SIGKILL);
            if (retVal == 0) {
                return true;
            }
        } catch (Error ex) {
            LOGGER.log(Level.SEVERE, "Can't kill process!", ex);
        }

        return false;
    }

    /**
     * Call GNU libc kill function to send kill signal to process,
     * see http://www.gnu.org/s/libc/manual/html_mono/libc.html#Signaling-Another-Process
     *
     * @param pid
     * @param signal
     * @return zero if the signal can be sent successfully. Otherwise, no signal is sent, and a value of -1 is returned.
     */
    private int sendSignal(int pid, int signal) {
        LOGGER.fine("Sending signal " + signal + " to process " + pid);
        return LIBC.kill(pid, signal);
    }

    private static final Logger LOGGER = Logger.getLogger(SignalKiller.class.getName());
}
