package com.tan.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * RPC客户端本地服务代理
 * @author tanwf
 *
 * @param <S>
 */
public class RpcImporter<S> {
	@SuppressWarnings("unchecked")
	public S importer(final Class<?> serviceClass, final InetSocketAddress addr) {
		System.out.println("-------------RPC客户端本地服务代理----RpcImporter------------");
		return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(),
				new Class<?>[] { serviceClass.getInterfaces()[0] }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						Socket socket = null;
						ObjectOutputStream output = null;
						ObjectInputStream input = null;
						try {
							socket = new Socket();
							socket.connect(addr);
							output = new ObjectOutputStream(socket.getOutputStream());
							output.writeUTF(serviceClass.getName());
							output.writeUTF(method.getName());
							output.writeObject(method.getParameterTypes());
							output.writeObject(args);
							input = new ObjectInputStream(socket.getInputStream());
							System.out.println("---------------invoke---------------");
							return input.readObject();
						} finally {
							if (socket != null) {
								socket.close();
							}
							if (output != null) {
								output.close();
							}
							if (input != null) {
								input.close();
							}
						}
					}
				});

	}
}
