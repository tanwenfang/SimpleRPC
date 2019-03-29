package com.tan.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * RPC服务端服务发布者
 */
public class RpcExporter {

	static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	public static void exporter(String hostName,int port) throws IOException {
		System.out.println("------------RPC服务端服务发布者------RpcExporter------start-");
		ServerSocket server = new ServerSocket();
		server.bind(new InetSocketAddress(hostName, port));
		try {
			while (true) {
				executor.execute(new ExporterTask(server.accept()));
				System.out.println("---------------execute----------------");
			}
		} finally {
			server.close();
		}
	}
	private static class ExporterTask implements Runnable {
		
		Socket client = null;
		public ExporterTask(Socket client) {
			this.client = client;
		}
		@Override
		public void run() {
			System.out.println("-------------ExporterTask-------------");
			ObjectInputStream input = null;
			ObjectOutputStream output = null;
			try {
				input = new ObjectInputStream(client.getInputStream());
				String interfaceName = input.readUTF();
				Class<?> service = Class.forName(interfaceName);
				String methodName = input.readUTF();
				Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
				Object[] arguments = (Object[]) input.readObject();
				Method method = service.getMethod(methodName, parameterTypes);
				Object result = method.invoke(service.newInstance(), arguments);
				output = new ObjectOutputStream(client.getOutputStream());
				output.writeObject(result);
				System.out.println("------------run----------");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				if (input != null) {
					try {
						input.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				if (client != null) {
					try {
						client.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		}
	}
}
