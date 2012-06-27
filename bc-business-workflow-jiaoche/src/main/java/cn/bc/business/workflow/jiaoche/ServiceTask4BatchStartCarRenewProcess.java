package cn.bc.business.workflow.jiaoche;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * 批量发起车辆续保流程的服务任务
 * 
 * @author dragon
 * 
 */
public class ServiceTask4BatchStartCarRenewProcess implements JavaDelegate {
	private static final Log logger = LogFactory
			.getLog(ServiceTask4BatchStartCarRenewProcess.class);
	private RuntimeService runtimeService;

	@Autowired
	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	public void execute(DelegateExecution execution) throws Exception {
		// 获取交车车辆列表 TODO 获取更多信息
		Object[] cars = (Object[]) execution.getVariable("renewCars");
		if(logger.isDebugEnabled()){
			logger.debug("from=" + execution.getProcessInstanceId());
			logger.debug("renewCars=" + StringUtils.arrayToCommaDelimitedString(cars));
		}

		// 循环每一台车发起续保流程
		Map<String, Object> variables;
		for (Object car : cars) {
			// 设置流程变量
			variables = new HashMap<String, Object>();
			variables.put("from", execution.getProcessInstanceId());// 来源信息
			variables.put("car", car);// 车辆信息

			// TODO 设置更多变量信息

			// 启动流程
//			runtimeService.startProcessInstanceByKey("CarRenewProcess", from
//					+ ":" + car, variables);
		}
	}
}
