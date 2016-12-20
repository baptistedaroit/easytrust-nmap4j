package org.nmap4j.core;

import org.apache.karaf.util.tracker.BaseActivator;
import org.apache.karaf.util.tracker.annotation.ProvideService;
import org.apache.karaf.util.tracker.annotation.Services;

import org.apache.karaf.util.tracker.BaseActivator;
import org.apache.karaf.util.tracker.annotation.ProvideService;
import org.apache.karaf.util.tracker.annotation.Services;

import org.nmap4j.Nmap4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by stephanericci on 20/12/2016.
 *
 * @author StephaneRicci
 */
@Services(provides = @ProvideService(Nmap4j.class))
public class Activator extends BaseActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    Nmap4j nmap4j;

    @Override
    protected void doStart() throws Exception {
        LOGGER.debug("Starting Nmap4j service");
        super.doStart();

        // TODO : dirty just for testing !!!
        nmap4j = new Nmap4j("/usr/local/bin/");
        register(Nmap4j.class, nmap4j);
    }

    @Override
    protected void doStop() {
        LOGGER.debug("Stopping Nmap4j service");
        super.doStop();
    }

}