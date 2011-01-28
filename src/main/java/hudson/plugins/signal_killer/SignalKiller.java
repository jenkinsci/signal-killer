package hudson.plugins.signal_killer;

import static hudson.util.jna.GNUCLibrary.LIBC;
import hudson.Extension;
import hudson.util.ProcessKiller;
import hudson.util.ProcessTree;

import java.io.IOException;
import java.util.logging.Logger;

@Extension
public class SignalKiller extends ProcessKiller{
	
	//standard signals, see signum.h (or man 7 signal)
	public static final int SIGABRT = 6; //Abnormal termination.
	public static final int SIGKILL = 9; //Kill (cannot be blocked, caught, or ignored).
	public static final int SIGTERM = 15; //Termination request.
	
	/**
	 * Default constructor.
	 * Does nothing, just logs that plugin instance was created on Hudson master.
	 */
	public SignalKiller(){
		LOGGER.fine("SignalKiller initializted");
	}
	
	@Override
	public boolean kill(ProcessTree.OSProcess process) throws IOException, InterruptedException{
		int retVal = sendSignal(process.getPid(), SIGKILL);
		if(retVal == 0)
			return true;
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
	private int sendSignal(int pid, int signal){
		LOGGER.fine("Sending signal " + signal + " to process " + pid);
		return LIBC.kill(pid,signal);
	}
	
	private static final Logger LOGGER = Logger.getLogger(SignalKiller.class.getName());
}
