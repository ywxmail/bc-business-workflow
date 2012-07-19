package cn.bc.business.workflow.carrenew;

import org.activiti.engine.test.Deployment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.workflow.activiti.test.AbstractActivitiTest;

/**
 * 车辆续保处理流程
 * 
 * @author dragon
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class CarRetiredProcessTest extends AbstractActivitiTest {
	private static final Log logger = LogFactory
			.getLog(CarRetiredProcessTest.class);

	/**
	 * 常规流程处理
	 * 
	 * @throws Exception
	 */
	@Rollback(true)
	@Test
	@Deployment(resources = { "cn/bc/business/workflow/carrenew/CarRenew.bpmn20.xml" })
	public void testAgreeRequest() throws Exception {
		// 测试固定信息：一分公司的车：分公司合同管理员-曾祥汉zeng、分公司经理-胡志勇hzy
		String initiator = "admin";
		String processKey = "CarRenew";

		// 设置认证用户
		setAuthenticatedUser(initiator);
	}
}