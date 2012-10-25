/**
 * 
 */
package cn.bc.business.workflow.generalorder.service;

import java.util.Map;

/**
 * 宝城公司公文处理流的Service
 * 
 * @author lbj
 */
public interface GeneralOrderWorkflowService {

	/**
	 * 获取指定流程实例用于格式化Word模板的键值替换参数
	 * <ui>
	 * <li>startUser: {String} 流程发起人姓名</li>
	 * <li>startTime: {Date} 流程发起时间</li>
	 * <li>endTime: {Date} 流程结束时间</li>
	 * <li>duration: {Long} 流转耗时(毫秒)</li>
	 * <li>category: {String} 流程所属分类 </li>
	 * <li>key: {String} 流程编码</li>
	 * <li>name: {String} 流程名称</li>
	 * <li>subject: {String} 流程主题</li>
	 * <li>vs: {Map<String, Object>} 流程变量集(key为流程变量的名称)</li>
	 * <li>comments: {List<FlowAttach> comments} 流程意见集</li>
	 * <li>comments_str: {String} 流程意见字符串连接</li>
	 * <li>[taskCode]: {Map<String, Object>} 流程经办任务数据，每个任务的数据以其任务的编码作为key，其值为Map格式：
	 * 	<ui>
	 * 		<li>owner: {String} </li>
	 * 		<li>assignee: {String} 办理人</li>
	 * 		<li>desc: {String} 附加说明</li>
	 * 		<li>dueDate: {Date} 办理期限</li>
	 * 		<li>priority: {int} 优先级</li>
	 * 		<li>startTime: {Date} 任务发起时间</li>
	 * 		<li>endTime: {Date} 任务结束时间</li>
	 * 		<li>duration: {Long} 任务耗时(毫秒)</li>
	 * 		<li>key: {String} 任务编码</li>
	 * 		<li>name: {String} 任务名称</li>
	 * 		<li>subject: {String} 任务主题</li>
	 * 		<li>vs: {Map<String, Object>} 任务的本地流程变量集(key为流程变量的名称)</li>
	 * 		<li>comments: {List<FlowAttach> comments} 任务的意见集</li>
	 * 		<li>comments_str: {String} 任务的意见字符串连接</li>
	 * 	</ui>
	 * </li>
	 * </ui>
	 * 
	 * @param processInstanceId 流程实例的ID
	 * @param args1 营运总监审批-查看审批意见岗位配置
	 * @param args2 总经理组审批-查看审批意见岗位配置
	 * @param args3 无权限查看此信息时默认显示的信息
	 * @return
	 */
	Map<String, Object> getProcessHistoryParams(String processInstanceId,String t04OperationDirectorCheck_s2groups
			,String t05GeneralManagerGroupCheck_s2groups
			,String noAccess2Inf);

	/**
	 * 获取指定流程实例用于格式化Word模板的键值替换参数
	 * <ui>
	 * <li>owner: {String} </li>
	 * <li>assignee: {String} 办理人</li>
	 * <li>desc: {String} 附加说明</li>
	 * <li>dueDate: {Date} 办理期限</li>
	 * <li>priority: {int} 优先级</li>
	 * <li>startTime: {Date} 任务发起时间</li>
	 * <li>endTime: {Date} 任务结束时间</li>
	 * <li>duration: {Long} 任务耗时(毫秒)</li>
	 * <li>key: {String} 任务编码</li>
	 * <li>name: {String} 任务名称</li>
	 * <li>subject: {String} 任务主题</li>
	 * <li>vs: {Map<String, Object>} 任务的本地流程变量集(key为流程变量的名称)</li>
	 * <li>comments: {List<FlowAttach> comments} 任务的意见集</li>
	 * <li>comments_str: {String} 任务的意见字符串连接</li>
	 * <li>pi: {Map<String, Object>} 流程实例数据，其值为Map格式：
	 * 	<ui>
	 * 		<li>startUser: {String} 流程发起人姓名</li>
	 * 		<li>startTime: {Date} 流程发起时间</li>
	 * 		<li>endTime: {Date} 流程结束时间</li>
	 * 		<li>duration: {Long} 流转耗时(毫秒)</li>
	 * 		<li>category: {String} 流程所属分类 </li>
	 * 		<li>key: {String} 流程编码</li>
	 * 		<li>name: {String} 流程名称</li>
	 * 		<li>subject: {String} 流程主题</li>
	 * 		<li>vs: {Map<String, Object>} 流程变量集(key为流程变量的名称)</li>
	 * 		<li>comments: {List<FlowAttach> comments} 流程意见集</li>
	 * 		<li>comments_str: {String} 流程意见字符串连接</li>
	 * 	</ui>
	 * </li>
	 * </ui>
	 * 
	 * @param taskId 任务实例的ID
	 * @param withProcessInfo 是否返回流程实例的全局数据
	 * 
	 * @return
	 */
	Map<String, Object> getTaskHistoryParams(String taskId, boolean withProcessInfo);
	Map<String, Object> getTaskHistoryParams(String taskId);

}