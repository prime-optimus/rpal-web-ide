package com.rpal.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class RpalExecutorServlet extends HttpServlet {
	private static final long serialVersionUID = 1826922504350814753L;
	
	private static final String RPAL_EXECUTOR = "rpal/./rpal";
	private static final String OPENSHIFT_DATA_DIR = System.getenv("OPENSHIFT_DATA_DIR");
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String code = request.getParameter("code") + "\n";
		File file = getFileName();
		
		FileUtils.writeStringToFile(file, code, false);
		
		Process process = Runtime.getRuntime().exec(getRpalCommand(request, file));
		InputStream processStream = process.getInputStream();
		
		IOUtils.copy(processStream, response.getOutputStream());
		removeFile(file);
	}

	private void removeFile(final File file) {
		file.delete();
	}

	private File getFileName() {
		return new File(OPENSHIFT_DATA_DIR + "code/" + System.nanoTime());
	}
	
	private String getRpalCommand(HttpServletRequest request, File file){
		StringBuilder options = new StringBuilder(" ");
		checkForParameter(options, request, "ast");
		checkForParameter(options, request, "noout");
		checkForParameter(options, request, "st");
		checkForParameter(options, request, "l");
		
		String command = OPENSHIFT_DATA_DIR  + RPAL_EXECUTOR + options.toString() +
				OPENSHIFT_DATA_DIR  +"code/" + file.getName();
		return command;
	}

	private void checkForParameter(StringBuilder sb,
			HttpServletRequest request, String parameter) {
		if(isParameterEnabled(request, parameter)){
			sb.append("-");
			sb.append(parameter);
			sb.append(" ");
		}
	}

	private boolean isParameterEnabled(HttpServletRequest request, String parameter) {
		return StringUtils.equals(request.getParameter(parameter), "true");
	}
}
