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
package eu.larkc.core.pluginManager.remote.GAT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.Preferences;
import org.gridlab.gat.URI;
import org.gridlab.gat.resources.JavaSoftwareDescription;
import org.gridlab.gat.resources.Job.JobState;
import org.gridlab.gat.resources.JobDescription;
import org.gridlab.gat.resources.ResourceBroker;
import org.gridlab.gat.security.SecurityContext;
import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.core.data.CloseableIterator;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.data.SetOfStatementsImpl;
import eu.larkc.core.data.workflow.WorkflowDescriptionPredicates;
import eu.larkc.core.parallelization.MultiThreading;
import eu.larkc.core.parallelization.MultiThreadingException;
import eu.larkc.core.pluginManager.ControlMessage;
import eu.larkc.core.pluginManager.Message;
import eu.larkc.core.pluginManager.PluginManager;
import eu.larkc.core.queue.Queue;
import eu.larkc.core.queue.QueueListener;
import eu.larkc.plugin.Plugin;

/**
 * GatPluginManager is a particular implementation of LocalPluginManger that
 * executes on a remote {@link GatResourceDescription} by means of JavaGAT.
 * 
 * @author Alexey Cheptsov
 * 
 */
public class GatPluginManager implements PluginManager {

	/**
	 * The queue for storing and accessing control methods sent by other
	 * PluginManagers.
	 * 
	 * @see PluginManager.Message
	 */

	private final Plugin mPlugin;
	private final GatResourceDescription gatResource;
	private final SecurityContext mSecurityContext;

	/**
	 * The queue for storing and accessing control methods sent by other
	 * PluginManagers.
	 * 
	 * @see PluginManager.Message
	 */
	private Queue<ControlMessage> mControlQueue;

	/** The PluginManagers managing the previous plugins in the pipeline. */
	private List<PluginManager> mPreviousPlugins;

	/** The thread that the plugin management goes on within. */
	private PluginThread pluginThread;

	/**
	 * The queues from which input messages will come from the previous plugin
	 * in the pipeline.
	 */
	private Map<String, List<Queue<SetOfStatements>>> inputQueues;

	/**
	 * The queues onto which output messages should be put to send them to the
	 * next plugin in the pipeline.
	 */
	private Map<String, List<Queue<SetOfStatements>>> outputQueues;

	/**
	 * Defines the input behavior of the plugin (if it has multiple inputs).
	 */
	private int inputBehavior;

	/** The logger. */
	protected final Logger logger = LoggerFactory
			.getLogger(GatPluginManager.class);

	/**
	 * Constructor that takes only the plugin and the
	 * {@link GatResourceDescription} as input.
	 * 
	 * @param plugin
	 *            The plugin for which this manager is responsible
	 * @param gatDescription
	 *            The GAT resource description
	 */
	public GatPluginManager(Plugin plugin, GatResourceDescription gatDescription) {
		mSecurityContext = null;

		mPlugin = plugin;
		mPreviousPlugins = new ArrayList<PluginManager>();
		inputQueues = new HashMap<String, List<Queue<SetOfStatements>>>();
		outputQueues = new HashMap<String, List<Queue<SetOfStatements>>>();
		mControlQueue = new Queue<ControlMessage>();
		inputBehavior = plugin.getInputBehavior();

		setThread(new PluginThread());

		gatResource = gatDescription;

		logger.debug("Initialized GAT plugin manager for " + mPlugin.getClass());
	}

	/**
	 * Extended version of the simple constructor that additionally takes
	 * securityContext as input.
	 * 
	 * @param plugin
	 *            The plugin for which this manager is responsible
	 * @param gatDescription
	 *            The GAT resource description
	 * @param securityContext
	 *            The Security Context
	 */
	public GatPluginManager(Plugin plugin,
			GatResourceDescription gatDescription,
			SecurityContext securityContext) {
		mSecurityContext = securityContext;
		mPlugin = plugin;
		mPreviousPlugins = new ArrayList<PluginManager>();
		inputQueues = new HashMap<String, List<Queue<SetOfStatements>>>();
		outputQueues = new HashMap<String, List<Queue<SetOfStatements>>>();
		mControlQueue = new Queue<ControlMessage>();
		inputBehavior = plugin.getInputBehavior();

		setThread(new PluginThread());

		gatResource = gatDescription;

		logger.debug("Initialized GAT plugin manager for " + mPlugin.getClass());
	}

	/**
	 * Constructor that takes the input and output queues as well as and the
	 * {@link GatResourceDescription} as input.
	 * 
	 * @param theInputQueues
	 *            The queues from which input messages will come from the
	 *            previous plugin in the pipeline
	 * @param theOutputQueues
	 *            The queues onto which output messages should be put to send
	 *            them to the next plugin in the pipeline
	 * @param plugin
	 *            The plugin for which this manager is responsible
	 * @param gatDescription
	 *            The GAT resource description
	 */
	public GatPluginManager(Plugin plugin,
			Map<String, List<Queue<SetOfStatements>>> theInputQueues,
			Map<String, List<Queue<SetOfStatements>>> theOutputQueues,
			GatResourceDescription gatDescription) {
		mSecurityContext = null;
		inputQueues = theInputQueues;
		outputQueues = theOutputQueues;
		mControlQueue = new Queue<ControlMessage>();
		mPlugin = plugin;
		mPreviousPlugins = new ArrayList<PluginManager>();
		inputBehavior = plugin.getInputBehavior();

		setThread(new PluginThread());

		gatResource = gatDescription;

		logger.debug("Initialized plugin manager for " + mPlugin.getClass());
	}

	/**
	 * This method returns the input queue of the manager.
	 * 
	 * @return the input queue
	 */
	public List<Queue<SetOfStatements>> getInputQueues(String pathId) {
		return inputQueues.get(pathId);
	}

	/**
	 * This method sets the input queue of the manager.
	 * 
	 * @param theInputQueues
	 *            the input queues
	 */
	public void setInputQueues(List<Queue<SetOfStatements>> theInputQueues,
			String pathId) {
		inputQueues.put(pathId, theInputQueues);
	}

	/**
	 * This method returns the output queues of the manager.
	 * 
	 * @return the output queues
	 */
	public List<Queue<SetOfStatements>> getOutputQueues(String pathId) {
		return outputQueues.get(pathId);
	}

	/**
	 * This method sets the output queues of the manager.
	 * 
	 * @param theOutputQueues
	 *            the output queues
	 */
	public void setOutputQueues(List<Queue<SetOfStatements>> theOutputQueues,
			String pathId) {
		outputQueues.put(pathId, theOutputQueues);
	}

	/**
	 * Accept.
	 * 
	 * @param message
	 *            the message
	 */
	public void accept(ControlMessage message) {
		mControlQueue.put(message);
	}

	/**
	 * This method enables the next control message that was sent to this
	 * PluginManager to be retrieved.
	 * 
	 * @return the next control message
	 */
	public ControlMessage getNextControlMessage() {
		return mControlQueue.take();
	}

	/**
	 * Adds the previous plugin manager.
	 * 
	 * @param provider
	 *            the provider
	 * 
	 * @see eu.larkc.core.pluginManager.PluginManager#addPrevious(eu.larkc.core.pluginManager.PluginManager)
	 */
	public void addPrevious(PluginManager provider) {
		mPreviousPlugins.add(provider);
	}

	/**
	 * This method should be called in order to tell the previous plugin in the
	 * pipeline to stop sending input on the input queue.
	 */
	protected void stopPrevious() {
		for (PluginManager manager : mPreviousPlugins) {
			manager.accept(new ControlMessage(Message.STOP, ""));
		}
	}

	/**
	 * This method should be called in order to tell the previous plugin in the
	 * pipeline to send the next piece of input on the input queue.
	 */
	protected void alertPrevious(String pathId) {
		for (PluginManager manager : mPreviousPlugins) {
			manager.accept(new ControlMessage(Message.NEXT, pathId));
		}
	}

	/**
	 * Start.
	 * 
	 * @see eu.larkc.core.pluginManager.PluginManager#start()
	 */
	public void start() {
		if (pluginThread != null) {
			pluginThread.start();
		}
	}

	/**
	 * Stop.
	 * 
	 * @see eu.larkc.core.pluginManager.PluginManager#stopWaiting()
	 */
	public void stopWaiting() {
		if (pluginThread != null) {
			pluginThread.stopWaiting();
		}
	}

	/**
	 * This method is used to specify the thread in which the plugin management
	 * occurs.
	 * 
	 * @param theThread
	 *            The Thread in which the plugin management occurs
	 */
	protected void setThread(PluginThread theThread) {
		this.pluginThread = theThread;
	}

	class PluginThread extends Thread implements QueueListener<SetOfStatements> {

		private int neededInputs;
		private ArrayList<Statement> statements;
		private boolean isWaiting;

		public PluginThread() {
			super("GatPluginManager");
			neededInputs = inputBehavior;
			statements = new ArrayList<Statement>();
			isWaiting = false;
		}

		public void run() {
			// the plugin has to be initialized before this point (by the
			// executor)
			// mPlugin.initialise();
			String pathId;

			for (;;) {
				ControlMessage controlMessage = getNextControlMessage();

				if (controlMessage.getMessage().equals(Message.NEXT)) {
					pathId = controlMessage.getPathId();
					alertPrevious(pathId);

					SetOfStatements input = getNextInput(pathId);

					if (input == null) {
						putNextOutput(null, pathId);
						break;
					}

					SetOfStatements output = null;

					CloseableIterator<Statement> statements = mPlugin
							.getPluginParameters().getStatements();

					Statement stmt = null;
					Statement tmpStmt = null;

					while (statements.hasNext()) {
						tmpStmt = statements.next();
						if (tmpStmt
								.getPredicate()
								.stringValue()
								.equals(WorkflowDescriptionPredicates.IS_INPUT_SPLITTABLE_URI
										.stringValue())) {
							stmt = tmpStmt;
							break;
						}
					}

					if (stmt != null) {
						HashMap<List<Plugin>, SetOfStatements> map = new HashMap<List<Plugin>, SetOfStatements>();
						List<Plugin> list = new ArrayList<Plugin>();
						list.add(mPlugin);
						map.put(list, input);
						try {
							MultiThreading.INSTANCE.invokeThreadPool(map);
							output = MultiThreading.INSTANCE
									.getSynchronizedResults();
						} catch (MultiThreadingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						// The following operations are equivalent to the
						// original call: output = mPlugin.invoke(input);
						// Prestaging
						doPrestage(input);

						// Main execution
						runJob(mPlugin.getClass().getCanonicalName());

						// Poststaging
						output = doPoststage();
					}

					putNextOutput(output, pathId);
				} else if (controlMessage.getMessage().equals(Message.STOP)) {
					break;
				}
			}
			logger.debug("Plugin is shutting down ...");
			stopPrevious();
			mPlugin.shutdown();
		}

		/**
		 * This method should be called to get the next input from the input
		 * queue.
		 * 
		 * @return the next element on the input queue
		 */
		private synchronized SetOfStatements getNextInput(String pathId) {
			statements = new ArrayList<Statement>();
			CloseableIterator<Statement> it;

			if (inputQueues.get(pathId) != null) {
				int numberOfInputs = inputQueues.get(pathId).size();

				if (inputBehavior < 0 || inputBehavior > numberOfInputs) {
					neededInputs = numberOfInputs;
					logger.debug("Use standard input behavior for {}: {}",
							mPlugin.toString(), neededInputs);
				} else {
					neededInputs = inputBehavior;
					logger.debug("Found input behavior for {}: {}",
							mPlugin.toString(), neededInputs);
				}

				for (Queue<SetOfStatements> queue : inputQueues.get(pathId)) {
					if (!queue.isEmpty()) {
						SetOfStatements queueElement = queue.take();
						if (queueElement == null) {
							logger.warn("No data in input queue. Found a NULL element in the input queue, probably some plug-in did not compute any results.");
						} else {
							logger.debug(
									"Used input queue on {} for plugin {}",
									pathId, mPlugin.getIdentifier());
							it = queueElement.getStatements();
							while (it.hasNext()) {
								statements.add(it.next());
							}
						}
						neededInputs--;
					} else {
						queue.addListener(this);
					}
				}
			} else {
				logger.debug("No input queues defined for {} ({})",
						mPlugin.toString(), pathId);
			}

			try {
				while (neededInputs > 0) {
					isWaiting = true;
					wait();
				}
				isWaiting = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return new SetOfStatementsImpl(statements);
		}

		/**
		 * This method should be called to put an output on the output queue.
		 * 
		 * @param theF
		 *            The element to put on the output queue
		 */
		private void putNextOutput(SetOfStatements theF, String pathId) {
			logger.debug("Wrote data to output queues ...");
			for (Queue<SetOfStatements> queue : outputQueues.get(pathId)) {
				queue.put(theF);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see eu.larkc.core.queue.QueueListener#elementAdded(java.lang.Object)
		 */
		@Override
		public synchronized void elementAdded(SetOfStatements queueElement) {
			if (isWaiting) {
				if (queueElement == null) {
					logger.warn("No data in input queue. Found a NULL element in the input queue, probably some plug-in did not compute any results.");
				} else {
					CloseableIterator<Statement> it = queueElement
							.getStatements();
					while (it.hasNext()) {
						statements.add(it.next());
					}
				}
				neededInputs--;
				this.notify();
			}
		}

		/**
		 * This method instructs the thread to not longer wait for inputs (if it
		 * is waiting).
		 */
		public synchronized void stopWaiting() {
			if (isWaiting) {
				neededInputs = 0;
				this.notify();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.pluginManager.PluginManager#addInputQueue(eu.larkc.core
	 * .pluginManager.local.queue.Queue)
	 */
	@Override
	public void addInputQueue(Queue<SetOfStatements> inputQueue, String pathId) {
		if (inputQueues.containsKey(pathId)) {
			inputQueues.get(pathId).add(inputQueue);
		} else {
			ArrayList<Queue<SetOfStatements>> arrayList = new ArrayList<Queue<SetOfStatements>>();
			arrayList.add(inputQueue);
			inputQueues.put(pathId, arrayList);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.pluginManager.PluginManager#addOuputQueue(eu.larkc.core
	 * .pluginManager.local.queue.Queue)
	 */
	@Override
	public void addOutputQueue(Queue<SetOfStatements> outputQueue, String pathId) {
		if (outputQueues.containsKey(pathId)) {
			outputQueues.get(pathId).add(outputQueue);
		} else {
			ArrayList<Queue<SetOfStatements>> arrayList = new ArrayList<Queue<SetOfStatements>>();
			arrayList.add(outputQueue);
			outputQueues.put(pathId, arrayList);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "PluginManager for" + mPlugin.getIdentifier();
	}

	/**
	 * Uploads the plug-in's input data (a SetOfStatements) to the remote host
	 * with GAT
	 * 
	 * @param input
	 *            the SetOfStatements to be uploaded.
	 */
	protected void doPrestage(SetOfStatements input) {

		String input_name = "input" + "_" + mPlugin.toString();
		String param_name = "input" + "_" + mPlugin.toString() + "_param";

		// Input serialization
		java.io.FileOutputStream fos;
		java.io.ObjectOutputStream oos;
		try {
			// input
			fos = new java.io.FileOutputStream("input");
			oos = new java.io.ObjectOutputStream(fos);

			oos.writeObject(input);

			oos.close();
			fos.close();

			// params
			fos = new java.io.FileOutputStream("input_param");
			oos = new java.io.ObjectOutputStream(fos);

			oos.writeObject(mPlugin.getPluginParameters());

			oos.close();
			fos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Upload
		GATContext context = GAT.getDefaultGATContext();
		context.addSecurityContext(mSecurityContext);

		Preferences gatprefs = new Preferences();
		gatprefs.put("File.adaptor.name", gatResource.FileAdaptor);
		context.addPreferences(gatprefs);

		try {
			GAT.createFile(context, "input").copy(
					new URI(gatResource.URI + File.separatorChar
							+ gatResource.WorkDir + File.separatorChar
							+ input_name));
			GAT.createFile(context, "input_param").copy(
					new URI(gatResource.URI + File.separatorChar
							+ gatResource.WorkDir + File.separatorChar
							+ param_name));
			logger.debug("Data uploaded");
		} catch (GATInvocationException e) {
			logger.error("GATInvocationException: " + e.getMessage());
			e.printStackTrace();
		} catch (GATObjectCreationException e) {
			logger.error("GATObjectCreationException: " + e.getMessage());
			e.printStackTrace();
		} catch (URISyntaxException e) {
			logger.error("URISyntaxException: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Downloads the plug-in's output data to the local host
	 * 
	 * @return The SetOfStatements produced by the remotely executed plug-in
	 */
	protected SetOfStatements doPoststage() {
		SetOfStatements result = new SetOfStatementsImpl();
		String output_name = "output" + "_" + mPlugin.toString();

		try {
			// Download
			GATContext context = GAT.getDefaultGATContext();

			context.addSecurityContext(mSecurityContext);

			Preferences gatprefs = new Preferences();
			gatprefs.put("File.adaptor.name", gatResource.FileAdaptor);
			context.addPreferences(gatprefs);
			GAT.createFile(
					context,
					gatResource.URI + File.separatorChar + gatResource.WorkDir
							+ File.separatorChar + output_name).copy(
					new URI("output"));

			// Deserialization
			java.io.FileInputStream inputStream = new java.io.FileInputStream(
					"output");
			java.io.ObjectInputStream oin = new java.io.ObjectInputStream(
					inputStream);
			while (true) {
				try {
					Object readObj = (Object) oin.readObject();
					result = (SetOfStatements) readObj;
					logger.debug("Data downloaded");
					break;
				} catch (java.io.EOFException e) {
					logger.debug("EOFException: {}", e.getMessage());
					e.printStackTrace();
					break;
				} catch (ClassNotFoundException e) {
					logger.debug("ClassNotFoundException: {}", e.getMessage());
					e.printStackTrace();
					break;
				}
			}
		} catch (FileNotFoundException e) {
			logger.debug("FileNotFoundException: {}", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.debug("IOException: {}", e.getMessage());
			e.printStackTrace();
		} catch (GATInvocationException e) {
			logger.debug("GATInvocationException: {}", e.getMessage());
			e.printStackTrace();
		} catch (GATObjectCreationException e) {
			logger.debug("GATObjectCreationException: {}", e.getMessage());
			e.printStackTrace();
		} catch (URISyntaxException e) {
			logger.debug("URISyntaxException: {}", e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Runs the plug-in on a remote host.
	 * 
	 * @param params
	 *            the sting containing the name of the plug-in to be executed.
	 */
	protected void runJob(String pluginName) {

		String inputName = "input" + "_" + mPlugin.toString();
		String outputName = "output" + "_" + mPlugin.toString();

		try {

			// Step 1. Submission preparation

			GATContext context = GAT.getDefaultGATContext();

			context.addSecurityContext(mSecurityContext);

			Preferences gatprefs = new Preferences();

			/*
			 * the next setting is required for some cluster deploying GT4.2
			 * middleware
			 */
			gatprefs.put("wsgt4new.factory.type", "PBS");

			gatprefs.put("ResourceBroker.adaptor.name", gatResource.Broker);
			context.addPreferences(gatprefs);

			JavaSoftwareDescription sd = new JavaSoftwareDescription();
			String managedPluginURI = pluginName.split("\\.")[pluginName
					.split("\\.").length - 1];

			// constructing classpath
			String classpath = gatResource.LarKCDir + File.separatorChar
					+ "platform-2.0.0-SNAPSHOT-LarkcAssembly.jar" + ":"
					+ gatResource.LarKCDir + File.separatorChar + "target"
					+ File.separatorChar
					+ "platform-2.0.0-SNAPSHOT-LarkcAssembly.jar" + ":"
					+ gatResource.LarKCDir + File.separatorChar + "plugins"
					+ File.separatorChar + managedPluginURI;

			java.io.File pluginLibDir = new java.io.File("plugins"
					+ File.separatorChar + managedPluginURI
					+ File.separatorChar + "lib");
			if (pluginLibDir.exists())
				for (java.io.File file : pluginLibDir.listFiles())
					if (file.isFile())
						if (file.getAbsolutePath().endsWith(".jar"))
							classpath += ":" + gatResource.LarKCDir
									+ File.separatorChar + "plugins"
									+ File.separatorChar + managedPluginURI
									+ File.separatorChar + "lib"
									+ File.separatorChar + file.getName();

			sd.setJavaClassPath(classpath);
			sd.setJavaMain("eu.larkc.core.pluginManager.remote.GAT.GatRemoteAgent");

			// Constructing properties
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("larkc.job.input", gatResource.WorkDir + "/"
					+ inputName);
			properties.put("larkc.job.output", gatResource.WorkDir + "/"
					+ outputName);
			properties.put("larkc.location", gatResource.LarKCDir);
			properties.put("larkc.plugin", mPlugin.getIdentifier()
					.stringValue().substring(4));
			sd.setJavaSystemProperties(properties);

			// Optional properties
			if (gatResource.JavaDir != null)
				sd.setExecutable(gatResource.JavaDir + "/java");
			if (gatResource.JavaArgs != null)
				sd.setJavaArguments(gatResource.JavaArgs);

			sd.setStdout(GAT.createFile("stdout"));
			sd.setStderr(GAT.createFile("stderr"));

			sd.addAttribute("sandbox.delete", "false");

			// Step 2 - Job submission

			JobDescription jd = new JobDescription(sd);
			ResourceBroker broker = GAT.createResourceBroker(new URI(
					gatResource.URI));

			org.gridlab.gat.resources.Job job = broker.submitJob(jd);

			logger.debug("Remote job submitted");

			String jobStatus = null;
			while (job.getState() != JobState.STOPPED
					&& job.getState() != JobState.SUBMISSION_ERROR) {
				try {
					Thread.sleep(50);
					String actualJobStatus = job.getState().toString();

					if (actualJobStatus != jobStatus) {
						jobStatus = actualJobStatus;
						logger.debug("Jos status is: {}", actualJobStatus);
					}
				} catch (InterruptedException e) {
				}
			}

			logger.debug("Remote job completed");

			// Step 3 - analysis of the job execution

			logger.debug("Stdout from the remote Plug-in: ");
			logger.debug("--------------------------------");
			java.io.File stdoutFile = new java.io.File("stdout");
			java.io.FileInputStream stdoutFis = null;
			java.io.BufferedInputStream stdoutBis = null;
			java.io.DataInputStream stdoutDis = null;

			stdoutFis = new java.io.FileInputStream(stdoutFile);
			stdoutBis = new java.io.BufferedInputStream(stdoutFis);
			stdoutDis = new java.io.DataInputStream(stdoutBis);

			while (stdoutDis.available() != 0)
				logger.debug(stdoutDis.readLine());
			logger.debug("--------------------------------");
			stdoutFis.close();
			stdoutBis.close();
			stdoutDis.close();

			logger.debug("Stderr from the remote Plug-in: ");
			logger.debug("--------------------------------");
			stdoutFile = new java.io.File("stderr");
			stdoutFis = null;
			stdoutBis = null;
			stdoutDis = null;

			stdoutFis = new java.io.FileInputStream(stdoutFile);
			stdoutBis = new java.io.BufferedInputStream(stdoutFis);
			stdoutDis = new java.io.DataInputStream(stdoutBis);

			while (stdoutDis.available() != 0)
				logger.debug(stdoutDis.readLine());
			logger.debug("--------------------------------");
			stdoutFis.close();
			stdoutBis.close();
			stdoutDis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}