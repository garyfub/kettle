 /**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** It belongs to, is maintained by and is copyright 1999-2005 by     **
 **                                                                   **
 **      i-Bridge bvba                                                **
 **      Fonteinstraat 70                                             **
 **      9400 OKEGEM                                                  **
 **      Belgium                                                      **
 **      http://www.kettle.be                                         **
 **      info@kettle.be                                               **
 **                                                                   **
 **********************************************************************/
 

package be.ibridge.kettle.trans.step;

/**
 * This defines the basic interface for the data used by a thread.
 * This will allow us to stop execution of threads and restart them later on 
 * without loosing track of the situation.
 * Typically the StepDataInterface implementing class will contain resultsets, 
 * temporary data, caching indexes, etc.
 * 
 * @author Matt
 * @since 20-jan-2005
 */
public interface StepDataInterface
{
	public static final int STATUS_EMPTY         = 0;
	public static final int STATUS_INIT          = 1;
	public static final int STATUS_RUNNING       = 2;
	public static final int STATUS_IDLE          = 3;
	public static final int STATUS_FINISHED      = 4;
	public static final int STATUS_DISPOSED      = 5;

	public void setStatus(int status);
	public int getStatus();
	
	public boolean isEmpty();
	public boolean isInitialising();
	public boolean isRunning();
	public boolean isIdle();
	public boolean isFinished();
	public boolean isDisposed();
}
