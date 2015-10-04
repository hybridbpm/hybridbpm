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
package com.hybridbpm.core.connector;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marat Gubaidullin
 */
public abstract class BpmConnector {

    private static final Logger BPM_CONNECTOR_LOGGER = Logger.getLogger(BpmConnector.class.getCanonicalName());

    public abstract void execute();

    public void setInParameter(String name, Object value) {
        try {
            Field f = this.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(this, value);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            BPM_CONNECTOR_LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public Object getOutParameter(String name) {
        try {
            Field f = this.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(this);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            BPM_CONNECTOR_LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
}
