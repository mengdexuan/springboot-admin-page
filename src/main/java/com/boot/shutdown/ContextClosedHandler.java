package com.boot.shutdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 利用ApplicationListener<ContextClosedEvent> 来监听spring上下文关闭事件，
 * 该事件发生在上下文和spring bean销毁之前，此时手动执行线程池优雅停机
 *
 * @author mengdexuan on 2019/6/18 15:02.
 */
@Component
public class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {
	@Autowired
	ThreadPoolExecutor executor;

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {

		/**
		 * 关闭 线程池
		 *
		 * 使用shutdownNow方法，可能会引起报错，使用shutdown方法可能会导致线程关闭不了。
		 *
		 * 所以当我们使用shutdownNow方法关闭线程池时，一定要对任务里进行异常捕获。
		 *
		 * 当我们使用shutdown方法关闭线程池时，一定要确保任务里不会有永久阻塞等待的逻辑，否则线程池就关闭不了
		 */
		if (executor!=null){
			executor.shutdown();
		}
	}
}
