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
package eu.larkc.core.management;

/**
 * This exception describes the case when the URL is invalid.
 * @author norlan
 *
 */
public class InvalidURLException extends Exception {

	/**
	 * The UID of the exception.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor that has the message as parameter.
	 * 
	 * @param msg The message.
	 */
	public InvalidURLException(String msg) {
		super(msg);
	}
	
	/**
	 * Construcor that takes the message and the cause as parameter.
	 * 
	 * @param msg The message
	 * @param cause The cause
	 */
	public InvalidURLException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	/**
	 * Constructor that takes the cause as parameter.
	 * 
	 * @param cause The cause
	 */
	public InvalidURLException(Throwable cause) {
		super(cause);
	}
}
