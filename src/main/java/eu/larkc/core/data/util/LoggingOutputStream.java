/*
   This file is part of the LarKC platform 
   http://www.larkc.eu/

   Copyright 2010 LarKC project consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.larkc.core.data.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

/**
 * An OutputStream that stores each line after a flush(). The stored contents is
 * then written to a Logger by calling one of the printLogAs* methods. The
 * logging messages can not be logged immediately because it is likely that the
 * System.out (stdout) stream is redirected and can thus not be used by the
 * logger (Class derived from <a href=
 * "http://blogs.sun.com/nickstephen/entry/java_redirecting_system_out_and"
 * >here</a> with the permission of the author).
 * 
 * @author Christoph Fuchs
 * 
 */
public class LoggingOutputStream extends ByteArrayOutputStream {

	private String lineSeparator;

	private Logger logger;

	private List<String> loggingLines;

	/**
	 * Constructor
	 * 
	 * @param logger
	 *            Logger to write to
	 */
	public LoggingOutputStream(Logger logger) {
		super();
		this.logger = logger;
		lineSeparator = System.getProperty("line.separator");
		loggingLines = new ArrayList<String>();
	}

	/**
	 * upon flush() write the existing contents of the OutputStream to the
	 * logger as a log record.
	 * 
	 * @throws java.io.IOException
	 *             in case of error
	 */
	public void flush() throws IOException {

		String record;
		synchronized (this) {
			super.flush();
			record = this.toString();
			super.reset();

			if (record.length() == 0 || record.equals(lineSeparator)) {
				// avoid empty records
				return;
			}

			loggingLines.add(record);
		}
	}

	/**
	 * Prints the saved log messages as debug messages.
	 */
	public void printLogAsDebug() {
		for (String line : loggingLines) {
			logger.debug(line);
		}
	}

	/**
	 * Prints the saved log messages as info messages.
	 */
	public void printLogAsInfo() {
		for (String line : loggingLines) {
			logger.info(line);
		}
	}

	/**
	 * Prints the saved log messages as warning messages.
	 */
	public void printLogAsWarn() {
		for (String line : loggingLines) {
			logger.warn(line);
		}
	}

	/**
	 * Prints the saved log messages as error messages.
	 */
	public void printLogAsError() {
		for (String line : loggingLines) {
			logger.error(line);
		}
	}

	/**
	 * Prints the saved log messages as trace messages.
	 */
	public void printLogAsTrace() {
		for (String line : loggingLines) {
			logger.trace(line);
		}
	}
}
