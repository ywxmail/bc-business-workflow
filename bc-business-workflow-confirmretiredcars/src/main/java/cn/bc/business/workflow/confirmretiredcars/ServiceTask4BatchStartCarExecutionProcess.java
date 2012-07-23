package cn.bc.business.workflow.confirmretiredcars;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.util.JsonUtils;

/**
 * 批量发起交车流程的服务任务
 * 
 * @author dragon
 * 
 */
public class ServiceTask4BatchStartCarExecutionProcess implements
		JavaDelegate {
	private static final Log logger = LogFactory
			.getLog(ServiceTask4BatchStartCarExecutionProcess.class);
	private RuntimeService runtimeService;

	@Autowired
	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	public void execute(DelegateExecution execution) throws Exception {
		String carsStr = (String) execution.getVariable("list_lv_cars");
		if (logger.isDebugEnabled()) {
			logger.debug("from=" + execution.getProcessInstanceId());
			logger.debug("exitOperationCars="
					+ carsStr);
		}

		List<Object> carList=(List<Object>) JsonUtils.toCollection(carsStr);
		
		
		Map<String, Object> variables;
		for(Object car:carList){
			// 设置流程变量
			variables = new HashMap<String, Object>();
			variables.put("from", execution.getProcessInstanceId());// 来源信息
			variables.put("car", car);// 车辆信息
			@SuppressWarnings("unchecked")
			Map<String,String> carMap=(Map<String,String>) car;
			if("fireCarRetiredProcesscarMap".equals(carMap.get("executionType"))){
				//发起车辆退出流程
				runtimeService.startProcessInstanceByKey("CarExitOperationProcess", "CarExitOperationProcess:" + car, variables);
			}else if("fireCarRenewProcess".equals(carMap.get("executionType"))){
				//发起车辆续保流程
				runtimeService.startProcessInstanceByKey("CarRenewProcess", "CarRenewProcess:" + car, variables);
			}
		}
	}
}
