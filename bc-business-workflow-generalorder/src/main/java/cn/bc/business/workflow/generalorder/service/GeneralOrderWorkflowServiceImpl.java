/**
 * 
 */
package cn.bc.business.workflow.generalorder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.identity.web.SystemContextHolder;
import cn.bc.workflow.service.WorkflowService;

/**
 * 宝城公司公文处理流Service的实现
 * 
 * @author lbj
 */
public class GeneralOrderWorkflowServiceImpl implements
		GeneralOrderWorkflowService {
	private static final Log logger = LogFactory
			.getLog(GeneralOrderWorkflowServiceImpl.class);

	private WorkflowService workflowService;

	@Autowired
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getProcessHistoryParams(String processInstanceId
			,String t04OperationDirectorCheck_s2groups
			,String t05GeneralManagerGroupCheck_s2groups
			,String noAccess2Info) {
		//String t04OperationDirectorCheck_s2groups ="yingyunzongjan,zongjingli";
		//String t05GeneralManagerGroupCheck_s2groups ="zongjingli";
		//String noAccess2Info = "(无权查看)";

		Map<String, Object> params = workflowService
				.getProcessHistoryParams(processInstanceId);

		if (SystemContextHolder.get().hasAnyRole("BC_WORKFLOW")) {// 流程管理员
			return params;
		} else if (SystemContextHolder.get().hasAnyGroup("dongshizhang")) {// 董事长
			return params;
		} else {// 其它用户
			// 任务:营运总监审批 t04OperationDirectorCheck_s
			if (params.containsKey("t04OperationDirectorCheck_s")) {
				List<Map<String, Object>> cclist = (List<Map<String, Object>>) params
						.get("t04OperationDirectorCheck_s");
				params.put(
						"t04OperationDirectorCheck_s",
						buildGroupDisplayInfo(cclist,
								t04OperationDirectorCheck_s2groups,
								noAccess2Info));
			}

			// 任务:总经理组审批 t05GeneralManagerGroupCheck_s
			if (params.containsKey("t05GeneralManagerGroupCheck_s")) {
				List<Map<String, Object>> cclist = (List<Map<String, Object>>) params
						.get("t05GeneralManagerGroupCheck_s");
				params.put(
						"t05GeneralManagerGroupCheck_s",
						buildGroupDisplayInfo(cclist,
								t05GeneralManagerGroupCheck_s2groups,
								noAccess2Info));
			}

			// 任务:董事长审批 t06ChairmanCheck_s
			if (params.containsKey("t06ChairmanCheck_s")) {
				List<Map<String, Object>> cclist = (List<Map<String, Object>>) params
						.get("t06ChairmanCheck_s");
				params.put("t06ChairmanCheck_s",
						buildGroupDisplayInfo(cclist, null, noAccess2Info));
			}
		}

		return params;
	}

	// 构建岗位显示的信息
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> buildGroupDisplayInfo(
			List<Map<String, Object>> cclist, String Groups,
			String noAccess2Info) {
		List<Map<String, Object>> new_cclist = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> taskMap : cclist) {
			if (taskMap.get("assigneeCode").equals(
					SystemContextHolder.get().getUser().getCode())) {// 任务办理人
				new_cclist.add(taskMap);
			} else if (Groups != null
					&& SystemContextHolder.get().hasAnyOneGroup(Groups)) {// 配置的岗位权限
				new_cclist.add(taskMap);
			} else if (((Map<String, Object>) taskMap.get("vs"))
					.containsKey("list_readUpperInforUsers")) {// 判断是否为指定人
				new_cclist.add(buildAssignDisplayInfo(noAccess2Info, taskMap));
			} else {// 默认用户
				new_cclist.add(taskMap);
			}
		}
		return new_cclist;
	}

	// 构建指定用户显示的信息
	private Map<String, Object> buildAssignDisplayInfo(String noAccess2Info,
			Map<String, Object> taskMap) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> vsMap = (Map<String, Object>) taskMap.get("vs");
			JSONArray ja = new JSONArray(vsMap.get("list_readUpperInforUsers").toString());
			// 是否为指定中的人变量
			boolean is_assign = false;
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				if (jo.get("code").toString()
						.equals(SystemContextHolder.get().getUser().getCode())) {
					is_assign = true;
					break;
				}
			}
			if (!is_assign) {
				vsMap.put("view", noAccess2Info);
				taskMap.put("vs", vsMap);
			}

		} catch (JSONException e) {
			logger.error(e.getMessage(),e);
		}
		return taskMap;
	}

	public Map<String, Object> getTaskHistoryParams(String taskId) {
		return getTaskHistoryParams(taskId, false);
	}

	public Map<String, Object> getTaskHistoryParams(String taskId,
			boolean withProcessInfo) {

		return this.workflowService.getTaskHistoryParams(taskId,
				withProcessInfo);
	}

}