/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zigbee.internal;

import java.util.Dictionary;

import org.openhab.binding.zigbee.zigbeeBindingProvider;
import org.openhab.binding.zigbee.internal.zigbeeGenericBindingProvider.zigbeeBindingConfig;

import org.apache.commons.lang.StringUtils;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapplogic.xbee.api.XBeeException;

/**
 * Implement this class if you are going create an actively polling service
 * like querying a Website/Device.
 * 
 * @author Benji Arnold
 * @since 1.4.0-SNAPSHOT
 */
public class zigbeeBinding extends AbstractActiveBinding<zigbeeBindingProvider> implements ManagedService {

	private static final Logger logger = LoggerFactory.getLogger(zigbeeBinding.class);

	private BroadcastReceiver broadcastReceiver;
	
	/** 
	 * the refresh interval which is used to poll values from the x10
	 * server (optional, defaults to 60000ms)
	 */
	private long refreshInterval = 60000;
	
	public zigbeeBinding() {
	}
		
	
	public void activate() {
	}
	
	public void deactivate() {
		// deallocate resources here that are no longer needed and 
		// should be reset when activating this binding again
		broadcastReceiver.stopThread();
	}

	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected String getName() {
		return "x10 Refresh Service";
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void execute() {
		// the frequently executed code (polling) goes here ...
		logger.debug("execute() method is called!");
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");
		eventPublisher.postCommand(itemName, command);
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");
		
		eventPublisher.postUpdate(itemName, newState);
	}
		
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException {
		if (config != null) {
			
			// to override the default refresh interval one has to add a 
			// parameter to openhab.cfg like <bindingName>:refresh=<intervalInMs>
			String refreshIntervalString = (String) config.get("refresh");
			if (StringUtils.isNotBlank(refreshIntervalString)) {
				refreshInterval = Long.parseLong(refreshIntervalString);
			}
			
			String port = (String) config.get("port");
			if (StringUtils.isNotBlank(port)) {
				broadcastReceiver = new BroadcastReceiver(port, zigbeeBindingConfig.getBaudRate(), zigbeeBindingConfig.getItem(), this);
				broadcastReceiver.start();
				
			}

			setProperlyConfigured(true);
		}
	}
	

}
