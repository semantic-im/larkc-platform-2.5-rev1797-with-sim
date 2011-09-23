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
package eu.larkc.core.pluginManager.remote.shared;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.shared.Resources;

/**
 * Handling remote host templates
 * 
 * @author hpcochep
 * 
 */
public class HostTemplates {
	private static Logger logger = LoggerFactory.getLogger(HostTemplates.class);

	/**
	 * Returns all templates located in resources/remote_hosts/templates in a
	 * single string, separated with "!"
	 * 
	 * @return String
	 */
	public static String ReturnTemplatesAsString() {

		String alltemplates = "";

		// InputStream inDir = ClassLoader.getSystemClassLoader()
		// .getSystemResourceAsStream(Resources.HOST_TEMPLATES_DIR);
		InputStream inDir = eu.larkc.core.Larkc.class
				.getClassLoader()
				.getResourceAsStream(
						"." + File.separatorChar + Resources.HOST_TEMPLATES_DIR);

		if (inDir != null) {
			BufferedReader rDir = new BufferedReader(new InputStreamReader(
					inDir));
			String line;
			try {
				while ((line = rDir.readLine()) != null) {
					logger.debug("Getting host: " + line);

					String template = getTemplateAsString(Resources.HOST_TEMPLATES_DIR
							+ File.separatorChar + line);
					alltemplates += template + "\n!\n";
				}
				rDir.close();
			} catch (IOException e1) {
				logger.debug("Cannot read host template files");
			}
		} else {

			/*
			 * A workaround for Windows try { BufferedReader rDir = new
			 * BufferedReader(new InputStreamReader( new DataInputStream(new
			 * java.io.FileInputStream( Resources.HOST_TEMPLATES_DIR +
			 * File.separatorChar + "list.txt")))); String line; try { while
			 * ((line = rDir.readLine()) != null) {
			 * logger.debug("Getting host: " + line);
			 * 
			 * String template =
			 * getTemplateAsString(Resources.HOST_TEMPLATES_DIR +
			 * File.separatorChar + line); alltemplates += template + "\n!\n"; }
			 * rDir.close(); } catch (IOException e1) {
			 * logger.debug("Cannot read host template files"); }
			 * 
			 * } catch (Exception ex) { ex.printStackTrace(); }
			 */

			logger.debug("Getting host properties from: "
					+ Resources.HOST_TEMPLATES_FILE);
			inDir = eu.larkc.core.Larkc.class.getClassLoader()
					.getResourceAsStream(Resources.HOST_TEMPLATES_FILE);
			if (inDir != null) {

				BufferedReader rDir = new BufferedReader(new InputStreamReader(
						inDir));
				String line;
				try {
					while ((line = rDir.readLine()) != null) {
						logger.debug("Getting host: " + line);

						String template = getTemplateAsString(Resources.HOST_TEMPLATES_DIR
								+ File.separatorChar + line);
						alltemplates += template + "\n!\n";
					}
					rDir.close();
				} catch (IOException e1) {
					logger.debug("Cannot read host template files");
				}
			} else {
				logger.debug("Attempting to get the host list from the disk files");

				try {
					BufferedReader rDir = new BufferedReader(
							new InputStreamReader(new DataInputStream(
									new java.io.FileInputStream(
											Resources.HOST_TEMPLATES_DIR
													+ File.separatorChar
													+ "list.txt"))));
					String line;
					try {
						while ((line = rDir.readLine()) != null) {
							logger.debug("Getting host: " + line);

							String template = getTemplateAsString(Resources.HOST_TEMPLATES_DIR
									+ File.separatorChar + line);
							alltemplates += template + "\n!\n";
						}
						rDir.close();
					} catch (IOException e1) {
						logger.debug("Cannot read host template files");
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		return alltemplates;
	}

	/**
	 * Method that returns the template from a disk file as a single string
	 * 
	 * @param file
	 *            name of the file with the template
	 * @return String
	 */
	public static String getTemplateAsString(String file) {

		StringBuffer template = new StringBuffer();

		/*
		 * try {
		 * 
		 * A workaround for Windows
		 * 
		 * BufferedReader rFile = new BufferedReader(new InputStreamReader( new
		 * DataInputStream(new java.io.FileInputStream(file)))); String readObj;
		 * 
		 * try { while ((readObj = rFile.readLine()) != null) { try {
		 * template.append(readObj).append(
		 * System.getProperty("line.separator")); } catch (Exception e) { break;
		 * } }
		 * 
		 * } catch (IOException e1) {
		 * logger.debug("Cannot read host template files"); }
		 * 
		 * } catch (Exception ex) { ex.printStackTrace(); }
		 */

		/*
		 * InputStream inFile = ClassLoader.getSystemClassLoader()
		 * .getResourceAsStream(file);
		 */
		InputStream inFile = eu.larkc.core.Larkc.class.getClassLoader()
				.getResourceAsStream(file);

		if (inFile != null) {

			BufferedReader rFile = new BufferedReader(new InputStreamReader(
					inFile));

			String readObj;
			try {
				while ((readObj = rFile.readLine()) != null) {
					try {
						template.append(readObj).append(
								System.getProperty("line.separator"));
					} catch (Exception e) {
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.debug("Unable to read the file: {}", file);
			}
		} else {
			try {

				BufferedReader rFile = new BufferedReader(
						new InputStreamReader(new DataInputStream(
								new java.io.FileInputStream(file))));
				String readObj;

				try {
					while ((readObj = rFile.readLine()) != null) {
						try {
							template.append(readObj).append(
									System.getProperty("line.separator"));
						} catch (Exception e) {
							break;
						}
					}

				} catch (IOException e1) {
					logger.debug("Cannot read host template files");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return template.toString();

	}
}
