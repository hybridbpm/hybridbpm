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
package com.hybridbpm.core.data.chart;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Marat Gubaidullin
 */
public class DbResponse {

    private final Map<String, Class> header = new HashMap<>();
    private final Map<Integer, Map<String, Object>> data = new HashMap<>();

    public DbResponse() {
    }

    public Map<String, Class> getHeader() {
        return header;
    }

    public Map<Integer, Map<String, Object>> getData() {
        return data;
    }
    
}
