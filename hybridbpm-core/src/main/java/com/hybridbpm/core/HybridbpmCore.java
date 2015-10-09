/*
 * Copyright (c) 2011-2015 Marat Gubaidullin. 
 *
 * This file is part of HYBRIDBPM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.hybridbpm.core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public class HybridbpmCore {

    private static final Logger logger = Logger.getLogger(HybridbpmCore.class.getSimpleName());
    public static final String HYBRIDBPM_DIRECTORY = "hybridbpm";
    public static final String CONFIGURATION_DIRECTORY = "hybridbpm/conf";
    protected static final HazelcastServer hazelcastServer = new HazelcastServer();
    protected static final DatabaseServer databaseServer = new DatabaseServer();

    public void start() {
        try {
            logger.info("HybridbpmCore starting");
            new File(CONFIGURATION_DIRECTORY).mkdirs();
            databaseServer.start();
            hazelcastServer.start();
            logger.info("HybridbpmCore started");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void stop() {
        try {
            logger.info("HybridbpmCore stopping");
            hazelcastServer.stop();
            databaseServer.stop();
            logger.info("HybridbpmCore stopped");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
