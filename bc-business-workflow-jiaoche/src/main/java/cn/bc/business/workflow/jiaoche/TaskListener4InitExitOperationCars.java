/**
 * 
 */
package cn.bc.business.workflow.jiaoche;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		// 设置流程变量 TODO 自动获取本月交车列表
		// List<Map<String, String>> cars = new ArrayList<Map<String,
		// String>>();
		// Map<String, String> car = new HashMap<String, String>();
		// car.put("id", "1001");
		// car.put("plate", "XXXX1");
		// cars.add(car);
		// car = new HashMap<String, String>();
		// car.put("id", "1002");
		// car.put("plate", "XXXX2");
		// cars.add(car);
		// delegateTask.setVariable("cars", cars);
	}
}
