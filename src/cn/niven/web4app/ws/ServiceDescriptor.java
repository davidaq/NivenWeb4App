package cn.niven.web4app.ws;

import java.io.PrintWriter;
import java.util.HashMap;

public class ServiceDescriptor {

	private final HashMap<String, ActionItem> serviceMap;

	private IndentedPrintWriter writer;

	public ServiceDescriptor(HashMap<String, ActionItem> serviceMap) {
		this.serviceMap = serviceMap;
	}

	public void output(PrintWriter printWriter, String baseUrl) {
		writer = new IndentedPrintWriter(printWriter);
		writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		writer.println("<service baseurl=\"" + baseUrl + "\">");
		writer.indent++;
		writer.println("<types>");
		ServiceTypesDescriptor typeDesc = new ServiceTypesDescriptor(writer,
				serviceMap);
		typeDesc.output();
		writer.println("</types>");
		writer.println("<actions>");
		new ServiceActionsDescriptor(writer, serviceMap, typeDesc).output();
		writer.println("</actions>");
		writer.indent--;
		writer.println("</service>");
		writer.println();
	}
}
