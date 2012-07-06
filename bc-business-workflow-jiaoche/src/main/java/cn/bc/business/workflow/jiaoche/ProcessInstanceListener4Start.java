/**
 * 
 */
package cn.bc.business.workflow.jiaoche;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 流程启动事件监听
 * 
 * @author dragon
 * 
 */
public class ProcessInstanceListener4Start implements ExecutionListener {
	private static final Log logger = LogFactory
			.getLog(ProcessInstanceListener4Start.class);

	public void notify(DelegateExecution execution) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("eventName=" + execution.getEventName());
	}
}
