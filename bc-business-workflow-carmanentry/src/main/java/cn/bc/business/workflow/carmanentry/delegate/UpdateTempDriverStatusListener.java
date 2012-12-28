/**
 * 
 */
package cn.bc.business.workflow.carmanentry.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 更新根据流程信息更新司机招聘状态的监听器
 * 
 * @author lbj
 * 
 */
public class UpdateTempDriverStatusListener implements ExecutionListener {
	private static final Log logger = LogFactory
			.getLog(UpdateTempDriverStatusListener.class);

	public void notify(DelegateExecution execution) throws Exception {

		
	}

}
