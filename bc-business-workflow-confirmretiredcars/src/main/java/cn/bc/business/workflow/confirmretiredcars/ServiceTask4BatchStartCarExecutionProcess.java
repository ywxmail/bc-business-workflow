package cn.bc.business.workflow.confirmretiredcars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		String carsVdStr = (String) execution.getVariable("list_vd_cars_gl");
		//车辆更多的信息
		String carsGcStr = (String)execution.getVariable("list_gc_cars");
		
		long verifyUnitId= (Long) execution.getVariable("verifyUnitId");
		
		if (logger.isDebugEnabled()) {
			logger.debug("from=" + execution.getProcessInstanceId());
			logger.debug("exitOperationCars="
					+ carsVdStr);
		}
		List<Object> carVdList=(List<Object>) JsonUtils.toCollection(carsVdStr);
		List<Object> carGcList=(List<Object>) JsonUtils.toCollection(carsGcStr);
		
		List<Map<String,Object>> carList=new ArrayList<Map<String,Object>>();
		
		for(Object carVd: carVdList){
			@SuppressWarnings("unchecked")
			Map<String, Object> carVdMap=(Map<String, Object>) carVd;
			for(Object carGc: carGcList){
				@SuppressWarnings("unchecked")
				Map<String, Object> carGcMap=(Map<String, Object>) carGc;
				if(carVdMap.get("id").equals(carGcMap.get("id"))){
					Set<String> carGcSet=carGcMap.keySet();
					for(String gckey:carGcSet){
						if(!carVdMap.containsKey(gckey)){
							carVdMap.put(gckey, carGcMap.get(gckey));
						}
					}	
				}
			}
			carList.add(carVdMap);
		}		
		
		Map<String, Object> variables;
		for(Map<String, Object> car:carList){
			// 设置流程变量
			variables = new HashMap<String, Object>();
			variables.put("from", execution.getProcessInstanceId());// 来源信息
			variables.put("car", car);// 车辆信息
			variables.put("verifyUnitId", verifyUnitId);//分公司ID
			if("fireCarRetiredProcess".equals(car.get("executionType").toString())){
				//发起车辆退出流程
				runtimeService.startProcessInstanceByKey("CarExitOperationProcess", variables);
			}else if("fireCarRenewProcess".equals(car.get("executionType").toString())){
				//发起车辆续保流程
				runtimeService.startProcessInstanceByKey("CarRenewProcess", variables);
			}
		}
	}
}
