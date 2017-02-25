package com.wxjfkg.dubbo.filter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;

@Activate(group = Constants.PROVIDER, order = 1)
public class MockFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(MockFilter.class);

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation)
			throws RpcException {
		Class clazz = invoker.getInterface();
		String methodName = invocation.getMethodName();
		logger.debug("target interface name: {}", invoker.getInterface());

		RpcResult result = new RpcResult();
		try {

			Method method = clazz.getMethod(methodName,
					invocation.getParameterTypes());
			Class returnType = method.getReturnType();
			logger.debug("target method name: {}", methodName);
			logger.debug("target method return type: {}",
					returnType.getCanonicalName());
			
			if (returnType == Void.class) {
				return result;
			} else {
				
				/*
				 * 挡板文件命名规则为：接口类名+方法名
				 */
				ClassPathResource resource = new ClassPathResource(clazz.getSimpleName() + "." + methodName);
				InputStream stream = null;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[2048];
				if (resource.exists()) {
					stream = resource.getInputStream();
					int bytes = 0;
					try {
						while ((bytes = stream.read(buffer)) != -1) {
							baos.write(buffer, 0, bytes);
						}
					} finally {
						if (stream != null) {
							stream.close();
						}
						baos.close();
					}
					String content = new String(baos.toByteArray(), "UTF-8");
	
					Object value = null;
					if (returnType == String.class) {
						value = content;
					} else {
						value = JSON.parse(content, returnType);
					}
	
					result.setValue(value);
				} else {
					throw new FileNotFoundException(resource.getFilename());
				}
			}
		} catch (Exception ex) {
			logger.error("No such method", ex);
		}

		// Result result = invoker.invoke(invocation);
		return result;
	}

}
