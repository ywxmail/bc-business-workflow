/**
 * 
 */
package cn.bc.business.workflow.jiaoche;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自动初始化本月要交车的车辆列表信息
 * 
 * @author dragon
 * 
 */
public class TaskListener4InitExitOperationCars implements TaskListener {
	private static final Log logger = LogFactory
			.getLog(TaskListener4InitExitOperationCars.class);
	private RuntimeService runtimeService;

	@Autowired
	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	public void notify(DelegateTask delegateTask) {
		if (logger.isDebugEnabled())
			logger.debug("eventName=" + delegateTask.getEventName());
		
		// 获取本月交车列表

		// 设置流程变量
		delegateTask.setVariable("cars", "c1,c2");
	}
}
