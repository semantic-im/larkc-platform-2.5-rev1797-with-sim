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
package eu.larkc.shared;

import org.openrdf.rio.ParseErrorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple ParseErrorListener implementation which logs every error and warning.
 * 
 * @author Christoph Fuchs
 * 
 */
public class LarkcParseErrorListener implements ParseErrorListener {

	private static Logger logger = LoggerFactory
			.getLogger(LarkcParseErrorListener.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openrdf.rio.ParseErrorListener#error(java.lang.String, int, int)
	 */
	@Override
	public void error(String arg0, int arg1, int arg2) {
		logger.error("Parse error message: {}, {}, {}", new Object[] { arg0,
				arg1, arg2 });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openrdf.rio.ParseErrorListener#fatalError(java.lang.String, int,
	 * int)
	 */
	@Override
	public void fatalError(String arg0, int arg1, int arg2) {
		logger.error("Fatal parse error: {}, {}, {}", new Object[] { arg0,
				arg1, arg2 });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openrdf.rio.ParseErrorListener#warning(java.lang.String, int,
	 * int)
	 */
	@Override
	public void warning(String arg0, int arg1, int arg2) {
		logger.error("Parse warning: {}, {}, {}", new Object[] { arg0, arg1,
				arg2 });
	}

}
