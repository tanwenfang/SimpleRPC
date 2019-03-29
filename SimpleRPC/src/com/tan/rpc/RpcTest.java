package com.tan.rpc;

import java.net.InetSocketAddress;

public class RpcTest {

	public static void main(String[] args) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					RpcExporter.exporter("localhost", 9999); // 发布服务
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		RpcImporter<EchoService> importer = new RpcImporter<EchoService>(); // 创建本地代理
		EchoService echo = importer.importer(EchoServiceImpl.class, new InetSocketAddress("localhost", 9999));
		System.out.println(echo.echo("Are you ok?"));
	}
}
